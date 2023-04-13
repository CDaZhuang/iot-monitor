package org.cdaz.monitor.provider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    @Value("${mq.size-topic}")
    private String sizeTopic;
    @Value("${mq.mq-topic-prefix}")
    private String topicPrefix;

    public String getSizeTopic() {
        return sizeTopic;
    }

    public void setSizeTopic(String sizeTopic) {
        this.sizeTopic = sizeTopic;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
}
