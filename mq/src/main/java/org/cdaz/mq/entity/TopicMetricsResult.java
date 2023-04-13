package org.cdaz.mq.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TopicMetricsResult {
    @SerializedName("topic")
    private String topic;
    @SerializedName("create_time")
    private Date createTime;
    @SerializedName("reset_time")
    private Date resetTime;
    @SerializedName("metrics")
    private TopicMetrics metrics;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getResetTime() {
        return resetTime;
    }

    public void setResetTime(Date resetTime) {
        this.resetTime = resetTime;
    }

    public TopicMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(TopicMetrics metrics) {
        this.metrics = metrics;
    }
}
