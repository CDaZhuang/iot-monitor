package org.cdaz.monitor.provider.service;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttClientService {
    void publish(String topic, MqttMessage message);
    void subscribe(String topicFilter, int qos, IMqttMessageListener listener);
    void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] listeners);
    void connect();
    void disconnect();
    boolean isConnected();
}
