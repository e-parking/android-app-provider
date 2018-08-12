package com.nerdcastle.eparkingprovider.DataModel;

public class Payment {

    private String mTransactionDate;
    private String mPlaceType;
    private String mEnteringTime;
    private String mLeavingTime;
    private String mTransactionAmount;
    private String mAddress;
    private String mVehicleNumber;


    public Payment() {
    }

    public Payment(String mTransactionDate, String mPlaceType, String mEnteringTime, String mLeavingTime, String mTransactionAmount, String mAddress, String mVehicleNumber) {
        this.mTransactionDate = mTransactionDate;
        this.mPlaceType = mPlaceType;
        this.mEnteringTime = mEnteringTime;
        this.mLeavingTime = mLeavingTime;
        this.mTransactionAmount = mTransactionAmount;
        this.mAddress = mAddress;
        this.mVehicleNumber = mVehicleNumber;
    }


    public String getmTransactionDate() {
        return mTransactionDate;
    }

    public void setmTransactionDate(String mTransactionDate) {
        this.mTransactionDate = mTransactionDate;
    }

    public String getmPlaceType() {
        return mPlaceType;
    }

    public void setmPlaceType(String mPlaceType) {
        this.mPlaceType = mPlaceType;
    }

    public String getmEnteringTime() {
        return mEnteringTime;
    }

    public void setmEnteringTime(String mEnteringTime) {
        this.mEnteringTime = mEnteringTime;
    }

    public String getmLeavingTime() {
        return mLeavingTime;
    }

    public void setmLeavingTime(String mLeavingTime) {
        this.mLeavingTime = mLeavingTime;
    }

    public String getmTransactionAmount() {
        return mTransactionAmount;
    }

    public void setmTransactionAmount(String mTransactionAmount) {
        this.mTransactionAmount = mTransactionAmount;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmVehicleNumber() {
        return mVehicleNumber;
    }

    public void setmVehicleNumber(String mVehicleNumber) {
        this.mVehicleNumber = mVehicleNumber;
    }
}