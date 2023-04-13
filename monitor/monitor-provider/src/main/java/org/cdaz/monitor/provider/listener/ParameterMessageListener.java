package org.cdaz.monitor.provider.listener;

import com.google.gson.reflect.TypeToken;
import org.cdaz.monitor.provider.entity.PendingData;
import org.cdaz.monitor.provider.util.JsonUtils;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ParameterMessageListener implements IMqttMessageListener {
    private Thread thread;
    private ParameterMessageTask task;

    public ParameterMessageListener(CyclicBarrier cb, BlockingDeque<List<PendingData>> batchQueue,
                                    AtomicStampedReference<List<PendingData>> globalBatchWrap) {
        task = new ParameterMessageTask(cb, batchQueue, globalBatchWrap);
        thread = new Thread(task);
        thread.start();
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {
        };
        Map<String, Object> parameters = (Map<String, Object>) JsonUtils.fromJson(new String(mqttMessage.getPayload()), typeToken.getType());
        task.submit(parameters);
    }
}
