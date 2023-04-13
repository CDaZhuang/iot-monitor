package org.cdaz.monitor.provider.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public interface DistributeService {
    void start(int coreThreads, int maxThreads, int keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workerQueue);
}
