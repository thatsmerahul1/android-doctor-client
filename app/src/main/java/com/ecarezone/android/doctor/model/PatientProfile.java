package com.ecarezone.android.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 20109804 on 6/23/2016.
 */
public class PatientProfile implements Serializable {
    @Expose
    @DatabaseField(canBeNull = false)
    public long userId;
    @Expose
    @DatabaseField
    public String profileName;
    @Expose
    @DatabaseField
    public String email;
    @Expose
    @DatabaseField
    public String height;
    @Expose
    @DatabaseField
    public String name;
    @Expose
    @DatabaseField
    public String address;
    @Expose
    @DatabaseField
    public String weight;
    @Expose
    @DatabaseField
    public String avatarUrl;
    @Expose
    @DatabaseField(canBeNull = false)
    public String profileId;
    @Expose
    @DatabaseField
    public String ethnicity;
    @Expose
    @DatabaseField
    public String gender;
    @Expose
    @DatabaseField
    public String birthdate;

    public PatientProfile() {
    }

    public String toString() {
        return profileName + " " + email + " " + height + " " + name + " " + address + " " + weight + " " + avatarUrl + " " + profileId + " " + ethnicity + " " + gender + " " + birthdate ;
    }

}
