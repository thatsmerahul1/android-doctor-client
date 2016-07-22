package com.ecarezone.android.doctor;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ecarezone.android.doctor.config.Constants;
import com.urbanairship.UAirship;

import java.util.HashMap;

public class DoctorApplication extends Application {

    private HashMap<String, Integer> nameValuePair = new HashMap<String, Integer>();
    private int lastAvailabilityStatus;

    final String getCallerName() {
        return DoctorApplication.class.getSimpleName();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        if(sharedPreferences.getString(Constants.UA_CHANNEL_NUMBER, null) == null) {

        }
        else{
            Constants.deviceUnique = sharedPreferences.getString(Constants.UA_CHANNEL_NUMBER, Constants.deviceUnique);
        }
    }

    public void setStatusNameValuePair(HashMap<String, Integer> nameValuePair){
        this.nameValuePair = nameValuePair;
    }

    public HashMap<String, Integer> getNameValuePair() {
        return nameValuePair;
    }

    public void setLastAvailabilityStaus(int lastAvailabilityStaus) {
        this.lastAvailabilityStatus = lastAvailabilityStaus;
    }

    public int getLastAvailabilityStaus() {
        return this.lastAvailabilityStatus;
    }

}
