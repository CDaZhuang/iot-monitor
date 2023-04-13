package org.cdaz.monitor.api.entity;

import java.io.Serializable;

public class Result implements Serializable {
    private boolean passing;

    public Result() {

    }

    public Result(boolean passing) {
        this.passing = passing;
    }

    public boolean isPassing() {
        return passing;
    }

    public void setPassing(boolean passing) {
        this.passing = passing;
    }


}
