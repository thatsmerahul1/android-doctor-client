package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.DoctorProfile;
import com.ecarezone.android.doctor.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by jifeng on 23/06/15.
 */
public class UpdateProfileRequest extends RetrofitSpiceRequest<CreateProfileResponse, EcareZoneApi> {

    @Expose
    public Long profileId;
    @Expose
    public DoctorProfile doctorProfile;
    @Expose
    public String email;
    @Expose
    public String password;
    @Expose
    public String apiKey;
    @Expose
    public String deviceUnique;


    public UpdateProfileRequest(Long profileId) {
        super(CreateProfileResponse.class, EcareZoneApi.class);
        this.email = LoginInfo.userName;
        this.password = LoginInfo.hashedPassword;
        this.apiKey = Constants.API_KEY;
        this.deviceUnique = Constants.deviceUnique;

        this.profileId = profileId;
    }

    @Override
    public CreateProfileResponse loadDataFromNetwork() throws Exception {
        return getService().updateProfile(LoginInfo.userId, profileId, this);
    }
}
