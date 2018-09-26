package bd.com.universal.eparking.owner.DataModel;

public class Request {
    private String mRequestID;
    private String mSenderUID;
    private String mRequstSenderName;
    private String mRequstSenderPhone;
    private String mBookingTimeFrom;
    private String mBookingTimeTo;
    private String mVehicleType;
    private String mVehicleNumber;


    public Request() {
    }

    public Request(String mRequestID, String mSenderUID, String mRequstSenderName, String mRequstSenderPhone, String mBookingTimeFrom, String mBookingTimeTo, String mVehicleType, String mVehicleNumber) {
        this.mRequestID = mRequestID;
        this.mSenderUID = mSenderUID;
        this.mRequstSenderName = mRequstSenderName;
        this.mRequstSenderPhone = mRequstSenderPhone;
        this.mBookingTimeFrom = mBookingTimeFrom;
        this.mBookingTimeTo = mBookingTimeTo;
        this.mVehicleType = mVehicleType;
        this.mVehicleNumber = mVehicleNumber;
    }

    public String getmRequestID() {
        return mRequestID;
    }

    public void setmRequestID(String mRequestID) {
        this.mRequestID = mRequestID;
    }

    public String getmSenderUID() {
        return mSenderUID;
    }

    public void setmSenderUID(String mSenderUID) {
        this.mSenderUID = mSenderUID;
    }

    public String getmRequstSenderName() {
        return mRequstSenderName;
    }

    public void setmRequstSenderName(String mRequstSenderName) {
        this.mRequstSenderName = mRequstSenderName;
    }

    public String getmRequstSenderPhone() {
        return mRequstSenderPhone;
    }

    public void setmRequstSenderPhone(String mRequstSenderPhone) {
        this.mRequstSenderPhone = mRequstSenderPhone;
    }

    public String getmBookingTimeFrom() {
        return mBookingTimeFrom;
    }

    public void setmBookingTimeFrom(String mBookingTimeFrom) {
        this.mBookingTimeFrom = mBookingTimeFrom;
    }

    public String getmBookingTimeTo() {
        return mBookingTimeTo;
    }

    public void setmBookingTimeTo(String mBookingTimeTo) {
        this.mBookingTimeTo = mBookingTimeTo;
    }

    public String getmVehicleType() {
        return mVehicleType;
    }

    public void setmVehicleType(String mVehicleType) {
        this.mVehicleType = mVehicleType;
    }

    public String getmVehicleNumber() {
        return mVehicleNumber;
    }

    public void setmVehicleNumber(String mVehicleNumber) {
        this.mVehicleNumber = mVehicleNumber;
    }
}
