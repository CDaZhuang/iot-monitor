package org.cdaz.monitor.api.entity;

import java.io.Serializable;
import java.util.Objects;

public class MonitorResult implements Serializable {
    private String clientId;
    private String prop;
    private Result result;
    private Long timestamp;

    public MonitorResult(String clientId, String prop, Long timestamp, Result result) {
        this.clientId = clientId;
        this.prop = prop;
        this.timestamp = timestamp;
        this.result = result;
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonitorResult)) return false;
        MonitorResult that = (MonitorResult) o;
        return Objects.equals(getClientId(), that.getClientId()) && Objects.equals(getProp(), that.getProp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClientId(), getProp());
    }
}
