package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.ecarezone.android.doctor.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by 10603675 on 28-06-2016.
 */
public class EditAppointmentRequest extends RetrofitSpiceRequest<EditAppointmentResponse, EcareZoneApi> implements Serializable {

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
    private String newDateTime;
    @Expose
    private String callType;

    private long doctorId;
    private long userId;

    /**
     *
     * @param doctorId
     * @param userId
     * @param dateTime
     * @param newDateTime
     * @param callType
     */
    public EditAppointmentRequest(long doctorId, long userId,
                                    String dateTime, String newDateTime, String callType) {
        super(EditAppointmentResponse.class, EcareZoneApi.class);

        this.doctorId = doctorId;
        this.userId = userId;
        this.email = LoginInfo.userName;
        this.password = LoginInfo.hashedPassword;
        this.apiKey = Constants.API_KEY;
        this.deviceUnique = Constants.deviceUnique;
        this.dateTime = dateTime;
        this.newDateTime = newDateTime;
        this.callType = callType;

    }

    @Override
    public EditAppointmentResponse loadDataFromNetwork() throws Exception {

        return getService().rescheduleAppointment(doctorId, userId, this);

    }
}
