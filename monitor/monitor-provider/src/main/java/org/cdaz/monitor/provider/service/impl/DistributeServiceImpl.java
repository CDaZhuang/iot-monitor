package org.cdaz.monitor.provider.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.cdaz.monitor.provider.entity.PendingData;
import org.cdaz.monitor.provider.entity.ProcessingData;
import org.cdaz.monitor.provider.service.DistributeService;
import org.cdaz.rule.api.entity.Rule;
import org.cdaz.rule.api.service.RuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class DistributeServiceImpl implements DistributeService {
    private final Logger LOG = LoggerFactory.getLogger(DistributeServiceImpl.class);

    @Autowired
    private Map<Integer, BlockingDeque<List<ProcessingData>>> processingBatchQueues;
    @Autowired
    private BlockingDeque<List<PendingData>> pendingBatchQueue;
    @DubboReference
    private RuleService ruleService;

    public void start(int coreThreads, int maxThreads, int keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> workerQueue) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreThreads, maxThreads, keepAliveTime, timeUnit, workerQueue);
        for (int i = 0; i < coreThreads; i++) {
            executor.execute(this::distribute);
        }
    }

    private void distribute() {
        LOG.info("Distribute task is running, t-id= {}", Thread.currentThread().getId());

        while (true) {
            List<ProcessingData> defaultProcessingBatch = new ArrayList<>();
            List<ProcessingData> counting5ProcessingBatch = new ArrayList<>();
            List<ProcessingData> counting10ProcessingBatch = new ArrayList<>();
            List<ProcessingData> counting20ProcessingBatch = new ArrayList<>();
            List<ProcessingData> counting50ProcessingBatch = new ArrayList<>();
            List<ProcessingData> counting100ProcessingBatch = new ArrayList<>();
            List<ProcessingData> counting1000ProcessingBatch = new ArrayList<>();

            List<PendingData> pendingBatch = null;
            try {
                pendingBatch = pendingBatchQueue.take();
                if (pendingBatch.isEmpty()) {
                    Thread.sleep(200);
                    continue;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (PendingData pendingData : pendingBatch) {
                Rule rule = ruleService.getRule(pendingData.getClientId(), pendingData.getProp());
                if (rule == null) {
                    rule = new Rule();
                }
                ProcessingData data = new ProcessingData(pendingData, rule);

                LOG.info("Transform pending data to processing data, prop = {}, data = {}, rule-exp = {}",
                        rule.getProp(), pendingData.getData(), rule.getRuleExp());

                if (rule.getCountingRule()) {
                    if (rule.getTimeWindow() == 5L) {
                        counting5ProcessingBatch.add(data);
                    } else if (rule.getTimeWindow() == 10L) {
                        counting10ProcessingBatch.add(data);
                    } else if (rule.getTimeWindow() == 20L) {
                        counting20ProcessingBatch.add(data);
                    } else if (rule.getTimeWindow() == 50L) {
                        counting50ProcessingBatch.add(data);
                    } else if (rule.getTimeWindow() == 100L) {
                        counting100ProcessingBatch.add(data);
                    } else if (rule.getTimeWindow() == 1000L) {
                        counting1000ProcessingBatch.add(data);
                    }
                } else {
                    defaultProcessingBatch.add(data);
                }
            }

            for (Map.Entry<Integer, BlockingDeque<List<ProcessingData>>> entry : processingBatchQueues.entrySet()) {
                try {
                    if (entry.getKey() == 0) {
                        entry.getValue().put(defaultProcessingBatch);
                    } else if (entry.getKey() == 5) {
                        entry.getValue().put(counting5ProcessingBatch);
                    } else if (entry.getKey() == 10) {
                        entry.getValue().put(counting10ProcessingBatch);
                    } else if (entry.getKey() == 20) {
                        entry.getValue().put(counting20ProcessingBatch);
                    } else if (entry.getKey() == 50) {
                        entry.getValue().put(counting50ProcessingBatch);
                    } else if (entry.getKey() == 100) {
                        entry.getValue().put(counting100ProcessingBatch);
                    } else if (entry.getKey() == 1000) {
                        entry.getValue().offer(counting1000ProcessingBatch);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
