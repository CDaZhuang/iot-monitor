package org.cdaz.monitor.api.entity;

import java.io.Serializable;

public class DefaultResult extends Result implements Serializable {
    private Double prop;

    public DefaultResult() {
    }

    public DefaultResult(boolean passing, Double prop) {
        super(passing);
        this.prop = prop;
    }

    public Double getProp() {
        return prop;
    }

    public void setProp(Double prop) {
        this.prop = prop;
    }
}
