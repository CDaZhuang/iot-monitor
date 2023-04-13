package org.cdaz.monitor.provider.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.dubbo.config.annotation.DubboReference;
import org.cdaz.alarm.api.service.AlarmService;
import org.cdaz.monitor.api.entity.CountingResult;
import org.cdaz.monitor.api.entity.DefaultResult;
import org.cdaz.monitor.api.entity.MonitorResult;
import org.cdaz.monitor.provider.entity.PendingData;
import org.cdaz.monitor.provider.entity.ProcessingData;
import org.cdaz.monitor.provider.event.MqSizeArrivedEvent;
import org.cdaz.monitor.provider.listener.ParameterMessageListener;
import org.cdaz.monitor.provider.service.DistributeService;
import org.cdaz.monitor.provider.service.MonitorService0;
import org.cdaz.monitor.provider.service.MqttClientService;
import org.cdaz.monitor.provider.util.JsonUtils;
import org.cdaz.rule.api.entity.Rule;
import org.cdaz.rule.api.service.RuleService;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicStampedReference;


@Service
public class MonitorService0Impl implements MonitorService0, ApplicationListener<MqSizeArrivedEvent> {
    private int size;
    private List<String> mqTopics;

    @Autowired
    private DistributeService distributeService;
    @Autowired
    private MqttClientService mqttClientService;
    @DubboReference
    private RuleService ruleService;
    @DubboReference
    private AlarmService alarmService;
    @Autowired
    private BlockingDeque<List<PendingData>> pendingBatchQueue;
    @Autowired
    private Map<Integer, BlockingDeque<List<ProcessingData>>> processingBatchQueues;
    private final Logger LOG = LoggerFactory.getLogger(MonitorService0Impl.class);


    private List<MonitorWorker> monitorWorkers = new CopyOnWriteArrayList<>();


    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private ScheduledExecutorService alarmExecutor = new ScheduledThreadPoolExecutor(1);


    @PostConstruct
    public void init() {
        MqSizeArrivedEvent event = new MqSizeArrivedEvent(this, 1, "Mq");
        eventPublisher.publishEvent(event);
    }

    @Override
    public void onApplicationEvent(MqSizeArrivedEvent event) {
        this.size = event.getSize();
        this.mqTopics = event.getMqTopics();

        start();
    }

    private void receiveMessage() {
        CyclicBarrier cb = new CyclicBarrier(size);
        AtomicStampedReference<List<PendingData>> globalBatchWrap = new AtomicStampedReference<>(new ArrayList<>(), Integer.MIN_VALUE);

        String[] topicFilters = mqTopics.toArray(new String[size]);
        for (int i = 0; i < size; i++) {
            IMqttMessageListener listener = new ParameterMessageListener(cb, pendingBatchQueue, globalBatchWrap);
            mqttClientService.subscribe(topicFilters[i], 0, listener);
        }
    }

    private void start() {
        receiveMessage();

        distributeService.start(1, 2, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1000,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.execute(new DefaultMonitorWorker(processingBatchQueues.get(0), alarmExecutor));
    }

    @Override
    public List<MonitorResult> findMonitoringResultByClientId(String clientId) {
        List<MonitorResult> monitorResults = new ArrayList<>();
        for (MonitorWorker worker : monitorWorkers) {
            LOG.info("Processing the worker result, worker-id: {}", worker);
            monitorResults.addAll(worker.getResultMap().get(clientId, k -> new HashSet<>()));
        }

        return monitorResults;
    }

    abstract class MonitorWorker implements Runnable {
        private BlockingDeque<List<ProcessingData>> batchQueue;
        private Map<String, Set<MonitorResult>> noPassingResult;
        //private Map<String, Set<MonitorResult>> resultMap;

        private Cache<String, Set<MonitorResult>> resultMap;

        private ScheduledExecutorService alarmExecutor;

        public MonitorWorker(BlockingDeque<List<ProcessingData>> batchQueue, ScheduledExecutorService alarmExecutor) {
            this.batchQueue = batchQueue;
            this.noPassingResult = new HashMap<>();
            this.resultMap = Caffeine.newBuilder()
                    .expireAfterWrite(20L, TimeUnit.SECONDS)
                    .maximumSize(1024 * 100)
                    .build();
            this.alarmExecutor = alarmExecutor;
            this.alarmExecutor.scheduleAtFixedRate(this::processNoPassingResult, 0, 20, TimeUnit.MINUTES);
        }

        public BlockingDeque<List<ProcessingData>> getBatchQueue() {
            return batchQueue;
        }

        public void setBatchQueue(BlockingDeque<List<ProcessingData>> batchQueue) {
            this.batchQueue = batchQueue;
        }

        public Map<String, Set<MonitorResult>> getNoPassingResult() {
            return noPassingResult;
        }

        public void setNoPassingResult(Map<String, Set<MonitorResult>> noPassingResult) {
            this.noPassingResult = noPassingResult;
        }

        public Cache<String, Set<MonitorResult>> getResultMap() {
            return resultMap;
        }

        public abstract void run();

        public void processNoPassingResult() {
            if (getNoPassingResult().isEmpty()) {
                return;
            }

            alarmService.alarm("cdaz032608@163.com", "2092982064@qq.com", "Alarm", JsonUtils.toJson(getNoPassingResult()));
        }

        public void saveResult(MonitorResult result) {
            Set<MonitorResult> set = getResultMap().get(result.getClientId(), k -> new HashSet<>());
            if (set.contains(result)) {
                set.remove(result);
            }
            set.add(result);
            getResultMap().put(result.getClientId(), set);
        }
    }

    class DefaultMonitorWorker extends MonitorWorker {
        private ExpressionParser parser = new SpelExpressionParser();

        private EvaluationContext context = new StandardEvaluationContext();
        private final Logger LOG = LoggerFactory.getLogger(DefaultMonitorWorker.class);

        public DefaultMonitorWorker(BlockingDeque<List<ProcessingData>> batchQueue, ScheduledExecutorService alarmExecutor) {
            super(batchQueue, alarmExecutor);
        }

        @Override
        public void run() {
            LOG.info("DefaultMonitorWorker id = {} is running", Thread.currentThread().getId());

            // add this worker to monitorWorkers, we can use it get the monitor result conveniently.
            MonitorService0Impl.this.monitorWorkers.add(this);

            while (true) {
                List<ProcessingData> batch = null;
                try {
                    batch = getBatchQueue().take();
                    if (batch.isEmpty()) {
                        Thread.sleep(200);
                        continue;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                for (ProcessingData data : batch) {
                    MonitorResult result = null;
                    PendingData pendingData = data.getData();
                    Rule rule = data.getRule();
                    if (rule == null) {
                        LOG.warn("There is a null rule, client-id: {}, prop: {}, timestamp: {}",
                                pendingData.getClientId(), pendingData.getProp(), pendingData.getTimestamp());
                        continue;
                    }
                    if (data.isError(parser, context)) {
                        LOG.info("Client error, client-id = {}, prop = {}, data = {}",
                                rule.getClientId(), rule.getProp(), data.getData().getData());
                        result = new MonitorResult(rule.getClientId(), rule.getProp(), data.getData().getTimestamp(),
                                new DefaultResult(false, data.getData().getData()));

                        Set<MonitorResult> set = getNoPassingResult().getOrDefault(result.getClientId(), new HashSet<>());
                        set.add(result);
                        getNoPassingResult().put(result.getClientId(), set);
                    } else {
                        result = new MonitorResult(rule.getClientId(), rule.getProp(), data.getData().getTimestamp(),
                                new DefaultResult(true, data.getData().getData()));
                    }

                    LOG.info("client-id: {}, rule-exp: {}, prop: {}, data: {}",
                            rule.getClientId(), rule.getRuleExp(), rule.getProp(), data.getData().getData());

                    saveResult(result);
                }
            }
        }
    }

    class CountingMonitorWorker extends MonitorWorker {
        private final ExpressionParser parser = new SpelExpressionParser();
        private final EvaluationContext context = new StandardEvaluationContext();
        private final Logger LOG = LoggerFactory.getLogger(CountingMonitorWorker.class);
        private final int timeWindow;
        private final Deque<Map<String, MonitorResult>> slideWindowQueue;
        private Map<String, MonitorResult> slideWindowResult;

        public CountingMonitorWorker(BlockingDeque<List<ProcessingData>> batchQueue, ScheduledExecutorService alarmExecutor, int timeWindow) {
            super(batchQueue, alarmExecutor);
            this.timeWindow = timeWindow;
            this.slideWindowQueue = new LinkedList<>();
            this.slideWindowResult = new HashMap<>();
        }

        @Override
        public void run() {
            LOG.info("CountingMonitorWorker is running, t-id = {}", Thread.currentThread().getId());

            MonitorService0Impl.this.monitorWorkers.add(this);

            while (true) {
                List<ProcessingData> batch = null;
                try {
                    batch = getBatchQueue().take();
                    if (batch.isEmpty()) {
                        Thread.sleep(200);
                        continue;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Map<String, MonitorResult> newWindowUnit = new HashMap<>();
                for (ProcessingData data : batch) {
                    Rule rule = data.getRule();
                    PendingData pendingData = data.getData();
                    if (rule == null) {
                        LOG.warn("There is null rule, client-id: {}, prop: {}, timestamp: {}",
                                pendingData.getClientId(), pendingData.getProp(), pendingData.getTimestamp());
                        continue;
                    }
                    LOG.info("client-id: {}, rule-exp: {}, prop: {}, data: {}",
                            rule.getClientId(), rule.getRuleExp(), rule.getProp(), data.getData().getData());

                    if (data.isError(parser, context)) {
                        LOG.info("Client error, client-id = {}, prop = {}, data = {}",
                                rule.getClientId(), rule.getProp(), pendingData.getData());

                        String key = rule.getClientId() + "#" + rule.getProp();

                        // new window unit
                        CountingResult cntResult = new CountingResult(rule.getCount());
                        MonitorResult result = new MonitorResult(rule.getClientId(), rule.getProp(),
                                        data.getData().getTimestamp(), cntResult);
                        cntResult.error();
                        newWindowUnit.put(key, result);

                    }
                }

                updateSlideWindowQueue(newWindowUnit);
                updateResult();
            }
        }

        private void updateResult() {
            for (Map.Entry<String, MonitorResult> e : slideWindowResult.entrySet()) {
                String key = e.getKey();
                MonitorResult result = e.getValue();
                assert result.getResult() instanceof CountingResult;
                CountingResult cntRes = (CountingResult) result.getResult();
                if (cntRes.getCnt() == 0) {
                    slideWindowResult.remove(key);
                    continue;
                }

                // update result, contains the passing and the no passing
                Set<MonitorResult> set = getResultMap().get(result.getClientId(), k -> new HashSet<>());
                if (set.contains(result)) {
                    set.remove(result);
                }
                set.add(result);
                getResultMap().put(result.getClientId(), set);

                // update no passing result
                if (!cntRes.isPassing()) {
                    set = getNoPassingResult().getOrDefault(result.getClientId(), new HashSet<>());
                    set.add(result);
                    getNoPassingResult().put(result.getClientId(), set);
                }
            }
        }

        private void updateSlideWindowQueue(Map<String, MonitorResult> newWindowUnit) {
            if (slideWindowQueue.size() >= timeWindow) {
                Map<String, MonitorResult> oldWindowUnit = slideWindowQueue.pollLast();
                for (Map.Entry<String, MonitorResult> e : oldWindowUnit.entrySet()) {
                    String key = e.getKey();
                    MonitorResult value = e.getValue();
                    slideWindowResult.computeIfPresent(key, (k, v) -> {
                        CountingResult finalCntRes = null;
                        assert v.getResult() instanceof CountingResult;
                        finalCntRes = (CountingResult) v.getResult();

                        CountingResult leftCntRes = null;
                        assert value.getResult() instanceof CountingResult;
                        leftCntRes = (CountingResult) value.getResult();

                        if (finalCntRes.getCnt() >= leftCntRes.getCnt()) {
                            finalCntRes.setCnt(finalCntRes.getCnt() - leftCntRes.getCnt());
                            finalCntRes.setPassing(finalCntRes.getCnt() < finalCntRes.getLimitErrorCnt());
                        }
                        return v;
                    });
                }
            }

            for (Map.Entry<String, MonitorResult> e : newWindowUnit.entrySet()) {
                String key = e.getKey();
                MonitorResult value = e.getValue();
                slideWindowResult.compute(key, (k, v) -> {
                    if (v == null) {
                        return value;
                    }

                    CountingResult finalCntRes = null;
                    assert v.getResult() instanceof CountingResult;
                    finalCntRes = (CountingResult) v.getResult();

                    CountingResult rightCntRes = null;
                    assert value.getResult() instanceof CountingResult;
                    rightCntRes = (CountingResult) value.getResult();

                    if (finalCntRes.getCnt() >= rightCntRes.getCnt()) {
                        finalCntRes.setCnt(finalCntRes.getCnt() + rightCntRes.getCnt());
                        finalCntRes.setPassing(finalCntRes.getCnt() < finalCntRes.getLimitErrorCnt());
                    }
                    return v;
                });
            }
        }
    }
}
