package org.cdaz.mq.service;


import org.cdaz.mq.entity.TopicMetricsResult;

public interface TopicMetricsService {
    boolean createMetrics(String topic);
    TopicMetricsResult getMetrics(String topic);
    TopicMetricsResult[] getAllMetrics();
}
