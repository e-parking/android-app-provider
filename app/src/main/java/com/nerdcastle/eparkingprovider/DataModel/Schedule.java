package com.nerdcastle.eparkingprovider.DataModel;

/**
 * Created by Nipon on 9/19/2018.
 */

public class Schedule {

    private String mDay;
    private long mFromTime;
    private long mToTime;
    private String mStatus;

    public Schedule() {
    }

    public Schedule(String mDay, long mFromTime, long mToTime, String mStatus) {
        this.mDay = mDay;
        this.mFromTime = mFromTime;
        this.mToTime = mToTime;
        this.mStatus = mStatus;
    }

    public String getmDay() {
        return mDay;
    }

    public void setmDay(String mDay) {
        this.mDay = mDay;
    }

    public long getmFromTime() {
        return mFromTime;
    }

    public void setmFromTime(long mFromTime) {
        this.mFromTime = mFromTime;
    }

    public long getmToTime() {
        return mToTime;
    }

    public void setmToTime(long mToTime) {
        this.mToTime = mToTime;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }
}
