package org.cdaz.mq.entity;

import com.google.gson.annotations.SerializedName;

public class TopicMetrics {
    @SerializedName("messages.dropped.count")
    private int droppedCount;
    @SerializedName("messages.in.count")
    private int inCount;
    @SerializedName("messages.out.count")
    private int outCount;
    @SerializedName("messages.qos0.in.count")
    private int qos0InCount;
    @SerializedName("messages.qos0.out.count")
    private int qos0OutCount;
    @SerializedName("messages.qos1.in.count")
    private int qos1InCount;
    @SerializedName("messages.qos1.out.count")
    private int qos1OutCount;
    @SerializedName("messages.qos2.in.count")
    private int qos2InCount;
    @SerializedName("messages.qos2.out.count")
    private int qos2OutCount;
    @SerializedName("messages.dropped.rate")
    private double droppedRate;
    @SerializedName("messages.in.rate")
    private double inRate;
    @SerializedName("messages.out.rate")
    private double outRate;
    @SerializedName("messages.qos0.in.rate")
    private double qos0InRate;
    @SerializedName("messages.qos0.out.rate")
    private double qos0OutRate;
    @SerializedName("messages.qos1.in.rate")
    private double qos1InRate;
    @SerializedName("messages.qos1.out.rate")
    private double qos1OutRate;
    @SerializedName("messages.qos2.in.rate")
    private double qos2InRate;
    @SerializedName("messages.qos2.out.rate")
    private double Qos2OutRate;

    public int getDroppedCount() {
        return droppedCount;
    }

    public void setDroppedCount(int droppedCount) {
        this.droppedCount = droppedCount;
    }

    public int getInCount() {
        return inCount;
    }

    public void setInCount(int inCount) {
        this.inCount = inCount;
    }

    public int getOutCount() {
        return outCount;
    }

    public void setOutCount(int outCount) {
        this.outCount = outCount;
    }

    public int getQos0InCount() {
        return qos0InCount;
    }

    public void setQos0InCount(int qos0InCount) {
        this.qos0InCount = qos0InCount;
    }

    public int getQos0OutCount() {
        return qos0OutCount;
    }

    public void setQos0OutCount(int qos0OutCount) {
        this.qos0OutCount = qos0OutCount;
    }

    public int getQos1InCount() {
        return qos1InCount;
    }

    public void setQos1InCount(int qos1InCount) {
        this.qos1InCount = qos1InCount;
    }

    public int getQos1OutCount() {
        return qos1OutCount;
    }

    public void setQos1OutCount(int qos1OutCount) {
        this.qos1OutCount = qos1OutCount;
    }

    public int getQos2InCount() {
        return qos2InCount;
    }

    public void setQos2InCount(int qos2InCount) {
        this.qos2InCount = qos2InCount;
    }

    public int getQos2OutCount() {
        return qos2OutCount;
    }

    public void setQos2OutCount(int qos2OutCount) {
        this.qos2OutCount = qos2OutCount;
    }

    public double getDroppedRate() {
        return droppedRate;
    }

    public void setDroppedRate(double droppedRate) {
        this.droppedRate = droppedRate;
    }

    public double getInRate() {
        return inRate;
    }

    public void setInRate(double inRate) {
        this.inRate = inRate;
    }

    public double getOutRate() {
        return outRate;
    }

    public void setOutRate(double outRate) {
        this.outRate = outRate;
    }

    public double getQos0InRate() {
        return qos0InRate;
    }

    public void setQos0InRate(double qos0InRate) {
        this.qos0InRate = qos0InRate;
    }

    public double getQos0OutRate() {
        return qos0OutRate;
    }

    public void setQos0OutRate(double qos0OutRate) {
        this.qos0OutRate = qos0OutRate;
    }

    public double getQos1InRate() {
        return qos1InRate;
    }

    public void setQos1InRate(double qos1InRate) {
        this.qos1InRate = qos1InRate;
    }

    public double getQos1OutRate() {
        return qos1OutRate;
    }

    public void setQos1OutRate(double qos1OutRate) {
        this.qos1OutRate = qos1OutRate;
    }

    public double getQos2InRate() {
        return qos2InRate;
    }

    public void setQos2InRate(double qos2InRate) {
        this.qos2InRate = qos2InRate;
    }

    public double getQos2OutRate() {
        return Qos2OutRate;
    }

    public void setQos2OutRate(double qos2OutRate) {
        Qos2OutRate = qos2OutRate;
    }
}
