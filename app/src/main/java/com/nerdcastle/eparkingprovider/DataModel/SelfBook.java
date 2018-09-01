package com.nerdcastle.eparkingprovider.DataModel;

public class SelfBook {

    private long date;
    private long startTime;
    private long endTime;
    private String selfBookId;

    public SelfBook(String selfBookId, long date, long startTime, long endTime) {
        this.selfBookId = selfBookId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getSelfBookId() {
        return selfBookId;
    }

    public void setSelfBookId(String selfBookId) {
        this.selfBookId = selfBookId;
    }
}
