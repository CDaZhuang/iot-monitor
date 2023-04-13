package org.cdaz.monitor.provider.listener;


import org.cdaz.monitor.provider.entity.PendingData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicStampedReference;



public class ParameterMessageTask implements Runnable {
    private final Logger LOG = LoggerFactory.getLogger(ParameterMessageTask.class);

    private List<PendingData> localBatch;
    private CyclicBarrier cyclicBarrier;
    private AtomicStampedReference<List<PendingData>> globalBatchWrap;
    private LinkedBlockingDeque<Map<String, Object>> pendingParameters;
    private BlockingDeque<List<PendingData>> batchQueue;

    public ParameterMessageTask(CyclicBarrier cyclicBarrier, BlockingDeque<List<PendingData>> batchQueue, AtomicStampedReference<List<PendingData>> globalBatchWrap) {
        this.cyclicBarrier = cyclicBarrier;
        this.batchQueue = batchQueue;
        this.globalBatchWrap = globalBatchWrap;

        localBatch = new ArrayList<>();
        pendingParameters = new LinkedBlockingDeque<>();
    }

    public void submit(Map<String, Object> parameter) {
        try {
            LOG.info("Receive parameter，client-id: {}", parameter.get("client-id").toString());
            pendingParameters.put(parameter);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        LOG.info("ParameterMessageTask is running, t-id = {}", Thread.currentThread().getId());

        final Map<String, Object> EMPTY_MAP = new HashMap<>();

        // new global stamp
        int stamp = globalBatchWrap.getStamp();
        globalBatchWrap.compareAndSet(null, new ArrayList<>(), stamp, stamp + 1);

        long deadline = System.currentTimeMillis() + 1000L;
        while (true) {
            Map<String, Object> parameter = null;
            try {
                parameter = pendingParameters.poll(deadline - System.currentTimeMillis(), TimeUnit.MICROSECONDS);
                if (parameter == null) {
                    if (localBatch.isEmpty()) {
                        deadline += 1000;
                        continue;
                    } else {
                        parameter = EMPTY_MAP;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Long timestamp = System.currentTimeMillis();
            String clientId = parameter.getOrDefault("client-id", "").toString();
            parameter.remove("client-id");
            for (Map.Entry<String, Object> entry : parameter.entrySet()) {
                localBatch.add(new PendingData(clientId, entry.getKey(), (Double) entry.getValue(), timestamp));

                LOG.info("Transform parameter to pending data, client-id = {}, prop = {}, data = {}",
                        clientId, entry.getKey(), entry.getValue());
            }

            if (System.currentTimeMillis() >= deadline) {
                LOG.info("Merge local batch to global batch, t-id = {}", Thread.currentThread().getId());

                List<PendingData> globalBatch = globalBatchWrap.getReference();
                globalBatch.addAll(localBatch);
                localBatch.clear();

                try {
                    // wait the other executor transform parameter map to pending data
                    cyclicBarrier.await(1, TimeUnit.SECONDS);

                    // submit global batch and new global batch
                    stamp = globalBatchWrap.getStamp();
                    if (globalBatchWrap.compareAndSet(globalBatch, new CopyOnWriteArrayList<>(), stamp, stamp + 1)) {
                        batchQueue.put(globalBatch);
                        LOG.info("Submit the batch to pending batch queue, batch-size = {}, t-id = {}",
                                globalBatch.size(), Thread.currentThread().getId());
                        LOG.info("Batch queue size = {}", batchQueue.size());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                } catch (TimeoutException e) {
                    submitGlobalBatch();
                    cyclicBarrier.reset();
                } catch (BrokenBarrierException e) {
                    cyclicBarrier.reset();
                    e.printStackTrace();
                }

                deadline += 1000L;
            }
        }
    }

    private void submitGlobalBatch() {
        int stamp = globalBatchWrap.getStamp();
        List<PendingData> globalBatch = globalBatchWrap.getReference();
        if (globalBatch == null || globalBatch.isEmpty()) {
            return;
        }

        if (globalBatchWrap.compareAndSet(globalBatch, new CopyOnWriteArrayList<>(), stamp, stamp + 1)) {
            try {
                batchQueue.put(globalBatch);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            LOG.info("Submit the batch to pending batch queue, batch-size = {}, t-id = {}",
                    globalBatch.size(), Thread.currentThread().getId());
            LOG.info("Batch queue size = {}", batchQueue.size());
        }
    }
}
