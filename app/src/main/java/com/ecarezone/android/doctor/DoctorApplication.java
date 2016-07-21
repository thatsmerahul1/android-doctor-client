package com.ecarezone.android.doctor;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ecarezone.android.doctor.config.Constants;
import com.urbanairship.UAirship;

import java.util.HashMap;

public class DoctorApplication extends Application {

    private HashMap<String, Boolean> nameValuePair = new HashMap<String, Boolean>();
    private int lastAvailabilityStatus;

    final String getCallerName() {
        return DoctorApplication.class.getSimpleName();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        UAirship.takeOff(this, new UAirship.OnReadyCallback() {

            @Override
            public void onAirshipReady(UAirship uAirship) {
                //  do not show notification in the notification tray
                uAirship.getPushManager().setUserNotificationsEnabled(false);

            }

        });

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        if(sharedPreferences.getString(Constants.UA_CHANNEL_NUMBER, null) == null) {

        }
        else{
            Constants.deviceUnique = sharedPreferences.getString(Constants.UA_CHANNEL_NUMBER, Constants.deviceUnique);
        }
    }

    public void setStatusNameValuePair(HashMap<String, Boolean> nameValuePair){
        this.nameValuePair = nameValuePair;
    }

    public HashMap<String, Boolean> getNameValuePair() {
        return nameValuePair;
    }

    public void setLastAvailabilityStaus(int lastAvailabilityStaus) {
        this.lastAvailabilityStatus = lastAvailabilityStaus;
    }

    public int getLastAvailabilityStaus() {
        return this.lastAvailabilityStatus;
    }

}
