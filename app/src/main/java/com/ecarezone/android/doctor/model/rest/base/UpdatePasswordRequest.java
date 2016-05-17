package com.ecarezone.android.doctor.model.rest.base;

import com.ecarezone.android.doctor.service.EcareZoneApi;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by 20109804 on 5/16/2016.
 */
public class UpdatePasswordRequest extends RetrofitSpiceRequest<BaseResponse, EcareZoneApi> {
    @Expose
    public String currentPassword;
    @Expose
    public String newPassword;
    @Expose
    public String email;
    @Expose
    public String role;
    public UpdatePasswordRequest(String currentPassword, String newPassword,String email,String role) {
        super(BaseResponse.class,EcareZoneApi.class);
        this.currentPassword=currentPassword;
        this.newPassword=newPassword;
        this.email=email;
        this.role=role;
    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {
        return getService().updatePassword(this);
    }
}

