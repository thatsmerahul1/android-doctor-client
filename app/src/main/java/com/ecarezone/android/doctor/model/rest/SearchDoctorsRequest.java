package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class SearchDoctorsRequest extends RetrofitSpiceRequest<SearchDoctorsResponse, EcareZoneApi> implements Serializable {
    @Expose
    String keyword;
    @Expose
    String email;
    @Expose
    String password;
    @Expose
    String apiKey;
    @Expose
    String deviceUnique;
    @Expose
    long userId;
    boolean mycare;

    public SearchDoctorsRequest(long userId, String email, String password, String apiKey, String deviceUnique, String keyword,boolean mycare) {
        super(SearchDoctorsResponse.class, EcareZoneApi.class);
        this.keyword = keyword;
        this.userId = userId;
        this.email = email;
        this.apiKey = apiKey;
        this.deviceUnique = deviceUnique;
        this.password = password;
        this.mycare = mycare;
    }

    @Override
    public SearchDoctorsResponse loadDataFromNetwork() throws Exception {

        //This file is common for searchDoctor , recommendedDoctot and mycareteam doctors

        if(mycare) {
            return getService().getPatients(userId);
        } else
            return getService().getPendingRequest(userId);

    }

}
