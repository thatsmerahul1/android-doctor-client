package com.ecarezone.android.doctor;

import android.app.Application;

import java.util.HashMap;

public class DoctorApplication extends Application {

    public static HashMap<String,Boolean> nameValuePair = new HashMap<String, Boolean>();
    public static int lastAvailablityStaus;
    final String getCallerName() {
        return  DoctorApplication.class.getSimpleName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static HashMap<String, Boolean> getNameValuePair() {
        return nameValuePair;
    }

}
