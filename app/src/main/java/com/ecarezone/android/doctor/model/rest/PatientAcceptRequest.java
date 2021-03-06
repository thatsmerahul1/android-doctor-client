package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by Namitha on 4/22/2016.
 */
public class PatientAcceptRequest extends RetrofitSpiceRequest<SearchDoctorsResponse, EcareZoneApi> implements Serializable {
    @Expose
    String email;
    @Expose
    String password;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;
    @Expose
    String status;
    long profileId;
    long doctorId;

    public PatientAcceptRequest(long profileId , long doctorId, String status) {
        super(SearchDoctorsResponse.class, EcareZoneApi.class);
        this.email = LoginInfo.userName;
        this.password = LoginInfo.hashedPassword;
        this.apiKey = Constants.API_KEY;
        this.deviceUnique = Constants.deviceUnique;
        this.profileId = profileId;
        this.doctorId = doctorId;
        this.status = status;
    }

    @Override
    public SearchDoctorsResponse loadDataFromNetwork() throws Exception {
        return getService().requestAccept(profileId, doctorId,this);
    }
}

