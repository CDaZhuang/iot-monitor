package org.cdaz.monitor.api.entity;

import java.io.Serializable;

public class CountingResult extends Result implements Serializable {
    private Long cnt;
    private Integer limitErrorCnt;

    public CountingResult(Integer limitErrorCnt) {
        super(true);
        this.limitErrorCnt = limitErrorCnt;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }

    public Integer getLimitErrorCnt() {
        return limitErrorCnt;
    }

    public void setLimitErrorCnt(Integer limitErrorCnt) {
        this.limitErrorCnt = limitErrorCnt;
    }

    public void error() {
        if (cnt >= limitErrorCnt) {
            return;
        }

        ++cnt;
    }
}
