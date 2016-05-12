package com.ecarezone.android.doctor.model.pojo;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by 20109804 on 5/5/2016.
 */
public class PatientListItem {

    @Expose
    public String userProfile;
    @Expose
    public String isCallAllowed;
    @Expose
    public String status;
    @Expose
    public String email;
    @Expose
    public String userDevicesCount;
    @Expose
    public Long userId;
    @Expose
    public String name;
    @Expose
    public String recommandedDoctorId;
    @Expose
    public String userSettings;
    public boolean isPending;
    @Expose
    public String avatarUrl;
}
