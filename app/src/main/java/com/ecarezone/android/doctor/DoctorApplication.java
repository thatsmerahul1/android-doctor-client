package com.ecarezone.android.doctor;

import android.app.Application;

public class DoctorApplication extends Application {


    final String getCallerName() {
        return  DoctorApplication.class.getSimpleName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
