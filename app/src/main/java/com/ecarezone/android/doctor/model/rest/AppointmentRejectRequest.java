package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.ecarezone.android.doctor.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by 10603675 on 15-06-2016.
 */
public class AppointmentRejectRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> implements Serializable {

    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String apiKey;
    @Expose
    private String deviceUnique;
    @Expose
    private String dateTime;
    @Expose
    private String callType;

    private long appointmentId;

    public AppointmentRejectRequest(long appointmentId,
                                    String dateTime, String callType) {
        super(BaseResponse.class, EcareZoneApi.class);
        this.appointmentId = appointmentId;

        this.email = LoginInfo.userName;
        this.password = LoginInfo.hashedPassword;
        this.apiKey = Constants.API_KEY;
        this.deviceUnique = Constants.deviceUnique;
        this.dateTime = dateTime;
        this.callType = callType;

    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {

        return getService().rejectAppointmentRequest(appointmentId, this);

    }
}