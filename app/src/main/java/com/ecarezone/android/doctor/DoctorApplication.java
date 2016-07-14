package com.ecarezone.android.doctor;

import android.app.Application;

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
