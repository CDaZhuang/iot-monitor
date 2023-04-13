package org.cdaz.monitor.provider.service.impl;

import org.cdaz.monitor.provider.config.MqConfig;
import org.cdaz.monitor.provider.event.MqSizeArrivedEvent;
import org.cdaz.monitor.provider.service.MqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.PostConstruct;

// @Service
public class MqServiceImpl implements MqService {
    private final Logger LOG = LoggerFactory.getLogger(MqServiceImpl.class);

    /*
    @Autowired
    private MqttClientService mqttClientService;
     */

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private ApplicationEventPublisher publisher;

    private int mqSize;

    @PostConstruct
    public void init() {
        /*
        mqttClientService.subscribe(mqConfig.getSizeTopic(), 0, new IMqttMessageListener() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                TypeToken<Map<String, Integer>> typeToken = new TypeToken<Map<String, Integer>>() {};
                Map<String, Integer> map = (Map<String, Integer>) JsonUtils.fromJson(new String(mqttMessage.getPayload()), typeToken.getType());
                if (map.getOrDefault("size", 0) > 0) {
                    LOG.info("发布MqSizeArrivedEvent事件，size: {}", map.getOrDefault("size", 0));
                    MqSizeArrivedEvent event = new MqSizeArrivedEvent(this, map.get("size"), mqConfig.getTopicPrefix());
                    publisher.publishEvent(event);
                }
            }
        });*/

        MqSizeArrivedEvent event = new MqSizeArrivedEvent(this, 1, mqConfig.getTopicPrefix());
        publisher.publishEvent(event);
    }
}
