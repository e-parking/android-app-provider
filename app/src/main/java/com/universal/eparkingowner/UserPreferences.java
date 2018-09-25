package com.universal.eparkingowner;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mobile App Develop on 4/8/2018.
 */

public class UserPreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String USER_ID = "userid";
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "useremail";

    private static final String LAST_LATITUDE = "latitude";
    private static final String LAST_LONGITUDE= "longitude";



    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }




    public void setUserID (String userid)
    {
        editor.putString(USER_ID,userid);
        editor.commit();
        return;
    }

    public String getUserID ()
    {
        return sharedPreferences.getString(USER_ID,"null");
    }


    public void setUserName (String userName)
    {
        editor.putString(USER_NAME,userName);
        editor.commit();
        return;
    }

    public String getUserEmail ()
    {
        return sharedPreferences.getString(USER_EMAIL,"Unknown Email");
    }

    public void setUserEmail (String userEmail)
    {
        editor.putString(USER_EMAIL,userEmail);
        editor.commit();
        return;
    }

    public String getUserName ()
    {
        return sharedPreferences.getString(USER_NAME,"Unknown Name");
    }


    public void setLastLocation(float latitude,float longitude) {
        editor.putFloat(LAST_LATITUDE,latitude);
        editor.putFloat(LAST_LONGITUDE,longitude);
        editor.commit();
        return;
    }

    public float getLatitude() {
        return sharedPreferences.getFloat(LAST_LATITUDE,23);
    }

    public float getLongitude() {
        return sharedPreferences.getFloat(LAST_LONGITUDE,90);
    }



}
