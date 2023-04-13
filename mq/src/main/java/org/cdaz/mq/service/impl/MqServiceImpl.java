package org.cdaz.mq.service.impl;

import org.cdaz.mq.config.MqConfig;
import org.cdaz.mq.entity.TopicMetricsResult;
import org.cdaz.mq.service.MqService;
import org.cdaz.mq.service.MqttClientService;
import org.cdaz.mq.service.TopicMetricsService;
import org.cdaz.mq.util.JsonUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class MqServiceImpl implements MqService, ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private MqttClientService mqttClientService;

    @Autowired
    private TopicMetricsService topicMetricsService;

    private ScheduledExecutorService scheduledExecutorService;

    private final Logger LOG = LoggerFactory.getLogger(MqServiceImpl.class);

    private List<String> topics;


    // @PostConstruct
    @Override
    public void start() {
        topics = new ArrayList<>();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.submit(() -> {
            publishMqSize();
            for (int i = 0; i < mqConfig.getSize(); i++) {
                String topic = mqConfig.getTopicPrefix() + i;
                boolean result = topicMetricsService.createMetrics(topic);
                if (!result) {
                    LOG.warn("Create metrics fail, topic: {}", topic);
                }
                topics.add(topic);
            }
            LOG.info("创建 metrics topic 成功");
        });
        scheduledExecutorService.scheduleAtFixedRate(this::publishMqMetrics, 1L, 2L, TimeUnit.SECONDS);
    }

    private void publishMqSize() {
        int size = mqConfig.getSize();
        Map<String, Integer> map = new HashMap<>();
        map.put("size", size);

        MqttMessage message = new MqttMessage(JsonUtils.toJson(map).getBytes(StandardCharsets.UTF_8));
        message.setRetained(true);
        mqttClientService.publish(mqConfig.getSizeTopic(), message);
    }

    private void publishMqMetrics() {
        List<TopicMetricsResult> results = new ArrayList<>();
        for (String topic : topics) {
            TopicMetricsResult result = topicMetricsService.getMetrics(topic);
            results.add(result);
        }
        MqttMessage message = new MqttMessage(JsonUtils.toJson(results).getBytes(StandardCharsets.UTF_8));
        message.setQos(2);
        mqttClientService.publish(mqConfig.getMetricsTopic(), message);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        start();
    }
}
