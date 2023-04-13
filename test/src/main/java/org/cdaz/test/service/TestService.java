package org.cdaz.test.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class TestService {
    Map<String, Object> parameters = new HashMap<>();
    Random random = new Random();
    Gson gson = new Gson();

    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);


    @PostConstruct
    public void init() {
        String clientId = UUID.randomUUID().toString();
        String broker = "tcp://42.192.86.212:1883";

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            client.connect(opts);

            executorService.scheduleAtFixedRate(() -> {
                MqttMessage message = new MqttMessage();
                message.setPayload(randomParameters().getBytes(StandardCharsets.UTF_8));
                message.setQos(0);
                try {
                    client.publish("Mq0", message);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }, 0, 1, TimeUnit.SECONDS);


        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private String randomParameters() {
        parameters.put("client-id", "001");
        parameters.put("temperature", random.nextInt(100));
        parameters.put("voice", random.nextInt(80));
        return gson.toJson(parameters);
    }
}
