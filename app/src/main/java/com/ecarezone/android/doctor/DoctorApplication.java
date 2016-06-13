package com.ecarezone.android.doctor;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.ecarezone.android.doctor.model.rest.base.ChangeStatusRequest;
import com.ecarezone.android.doctor.model.rest.base.UpdatePasswordRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

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
