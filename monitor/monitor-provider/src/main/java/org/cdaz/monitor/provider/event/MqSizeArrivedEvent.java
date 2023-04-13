package org.cdaz.monitor.provider.event;

import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

public class MqSizeArrivedEvent extends ApplicationEvent {
    private int size;
    private List<String> mqTopics;

    public MqSizeArrivedEvent(Object source, int size, String mqTopicPrefix) {
        super(source);
        this.size = size;
        mqTopics = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            mqTopics.add(mqTopicPrefix + i);
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<String> getMqTopics() {
        return mqTopics;
    }
}
