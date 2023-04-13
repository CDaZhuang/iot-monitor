package org.cdaz.monitor.provider.entity;

public class PendingData {
    String clientId;
    String prop;
    Double data;
    Long timestamp;

    public PendingData() {
    }

    public PendingData(String clientId, String prop, Double data, Long timestamp) {
        this.clientId = clientId;
        this.prop = prop;
        this.data = data;
        this.timestamp = timestamp;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public Double getData() {
        return data;
    }

    public void setData(Double data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
