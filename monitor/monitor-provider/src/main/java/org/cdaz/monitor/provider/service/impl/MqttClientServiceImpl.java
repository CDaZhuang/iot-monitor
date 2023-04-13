package org.cdaz.monitor.provider.service.impl;

import org.cdaz.monitor.provider.config.MqttConfig;
import org.cdaz.monitor.provider.service.MqttClientService;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MqttClientServiceImpl implements MqttClientService {
    @Autowired(required = true)
    public MqttConfig mqttConfig;

    private MqttClient client;
    private MqttConnectOptions connectOpts;
    private final org.slf4j.Logger Logger = LoggerFactory.getLogger(getClass());

    @Override
    public void publish(String topic, MqttMessage message) {
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(String topicFilter, int qos, IMqttMessageListener listener) {
        try {
            client.subscribe(topicFilter, qos, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] listener) {
        try {
            client.subscribe(topicFilters, qos, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    @Override
    public void connect() {


        if (client != null && client.isConnected()) {
            Logger.warn("Mqtt客户端 {} 已经连接，无需重连", client.getClientId());
            return;
        }

        try {
            client = new MqttClient(mqttConfig.getAddress(), mqttConfig.getClientId(), new MemoryPersistence());

            connectOpts = new MqttConnectOptions();
            connectOpts.setUserName(mqttConfig.getUsername());
            connectOpts.setPassword(mqttConfig.getPassword().toCharArray());
            connectOpts.setKeepAliveInterval(mqttConfig.getKeepAlive());
            connectOpts.setCleanSession(mqttConfig.isCleanSession());
            connectOpts.setConnectionTimeout(mqttConfig.getConnectTimeout());

            client.connect(connectOpts);
            Logger.info("Mqtt客户端 {} 连接成功", client.getClientId());
        } catch (MqttException e) {
            Logger.error("Mqtt客户端 {} 连接失败", mqttConfig.getClientId());

            if (client != null) {
                while (!client.isConnected()) {
                    try {
                        client.disconnect();
                    } catch (MqttException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        Logger.error("Mqtt客户端 {} 正在断开连接", mqttConfig.getClientId());

        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        Logger.error("Mqtt客户端 {} 已经断开连接", mqttConfig.getClientId());
    }

    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

}
