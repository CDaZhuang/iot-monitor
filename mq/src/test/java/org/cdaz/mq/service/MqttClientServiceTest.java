package org.cdaz.mq.service;

import org.cdaz.mq.MqApplication;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MqApplication.class)
public class MqttClientServiceTest {
    @Autowired
    private MqttClientService mqttClientService;

    @Test
    public void test() {
        mqttClientService.publish("topic", new MqttMessage("topic".getBytes(StandardCharsets.UTF_8)));
        mqttClientService.subscribe("topic", 0, new IMqttMessageListener() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

            }
        });
    }

    public MqttClientService getMqttClientService() {
        return mqttClientService;
    }

    public void setMqttClientService(MqttClientService mqttClientService) {
        this.mqttClientService = mqttClientService;
    }
}
