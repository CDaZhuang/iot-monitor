package org.cdaz.mq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    @Value("${mq.size}")
    private int size;
    @Value("${mq.topic-prefix}")
    private String topicPrefix;
    @Value("${mq.metrics-topic}")
    private String metricsTopic;
    @Value("${mq.metrics-http-auth-username}")
    private String metricsHttpAuthUsername;
    @Value("${mq.metrics-http-auth-password}")
    private String metricsHttpAuthPassword;
    @Value("${mq.monitor-host}")
    private String monitorHost;

    @Value("${mq.size-topic}")
    private String sizeTopic;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public String getMetricsTopic() {
        return metricsTopic;
    }

    public void setMetricsTopic(String metricsTopic) {
        this.metricsTopic = metricsTopic;
    }

    public String getMetricsHttpAuthUsername() {
        return metricsHttpAuthUsername;
    }

    public void setMetricsHttpAuthUsername(String metricsHttpAuthUsername) {
        this.metricsHttpAuthUsername = metricsHttpAuthUsername;
    }

    public String getMetricsHttpAuthPassword() {
        return metricsHttpAuthPassword;
    }

    public void setMetricsHttpAuthPassword(String metricsHttpAuthPassword) {
        this.metricsHttpAuthPassword = metricsHttpAuthPassword;
    }

    public String getMonitorHost() {
        return monitorHost;
    }

    public void setMonitorHost(String monitorHost) {
        this.monitorHost = monitorHost;
    }

    public String getSizeTopic() {
        return sizeTopic;
    }

    public void setSizeTopic(String sizeTopic) {
        this.sizeTopic = sizeTopic;
    }
}
