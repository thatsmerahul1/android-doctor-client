package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.service.EcareZoneApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by 10603675 on 30-05-2016.
 */
public class AppointmentRequest extends RetrofitSpiceRequest<AppointmentResponse, EcareZoneApi> implements Serializable {

    private long doctorId;

    public AppointmentRequest(long doctorId) {
        super(AppointmentResponse.class, EcareZoneApi.class);
        this.doctorId = doctorId;
    }

    @Override
    public AppointmentResponse loadDataFromNetwork() throws Exception {
        return getService().getAppointmentDate(doctorId);
    }
}
