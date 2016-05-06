package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jifeng on 23/06/15.
 */
public class CreateProfileResponse extends BaseResponse implements Serializable {
    @Expose
    public String id;
    @Expose
    public String name;
    @Expose
    public String doctorDescription;
    @Expose
    public String avatarUrl;
    @Expose
    public String registrationId;
    @Expose
    public String birthDate;
    @Expose
    public String gender;
    @Expose
    public String category;

    @Override
    public String toString() {
        return   id + ":" +  name + ":" +   doctorDescription + ":" + avatarUrl + ":" +  registrationId + ":" + birthDate + ":" + gender + ":" +category;
    }

}