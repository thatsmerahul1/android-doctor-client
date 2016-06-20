package com.ecarezone.android.doctor.model.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by 20109804 on 4/21/2016.
 */
public class Patient implements Parcelable, Serializable {
    @Expose
    @DatabaseField
    public String userProfile;
    @Expose
    @DatabaseField
    public String isCallAllowed;
    @Expose
    @DatabaseField
    public String status;
    @Expose
    @DatabaseField
    public String email;
    @Expose
    public String userDevicesCount;
    @Expose
    @DatabaseField
    public Long userId;
    @Expose
    @DatabaseField
    public String name;
    @Expose
    public String recommandedDoctorId;
    @Expose
    public String userSettings;
    @Expose
    @DatabaseField
    public String avatarUrl;
    @DatabaseField
    public long dateTime;
    public Patient(){

    }

    public Patient(Long userId, String email, String name, String recommandedDoctorId, String status,
                   String isCallAllowed, String userDevicesCount, String userSettings, String userProfile,
                   String avatarUrl) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.recommandedDoctorId = recommandedDoctorId;
        this.status = status;
        this.isCallAllowed = isCallAllowed;
        this.userDevicesCount = userDevicesCount;
        this.userSettings = userSettings;
        this.userProfile = userProfile;
        this.avatarUrl = avatarUrl;
    }
    public Patient(Parcel in) {
        this.userId = in.readLong();
        this.email = in.readString();
        this.name = in.readString();
        this.userProfile = in.readString();
        this.status = in.readString();
        this.isCallAllowed = in.readString();
        this.userDevicesCount = in.readString();
        this.recommandedDoctorId = in.readString();
        this.userSettings = in.readString();
        this.avatarUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(userProfile);
        dest.writeString(status);
        dest.writeString(isCallAllowed);
        dest.writeString(userDevicesCount);
        dest.writeString(recommandedDoctorId);
        dest.writeString(userSettings);
        dest.writeString(avatarUrl);
    }

    public static Parcelable.Creator<Patient> CREATOR = new Parcelable.Creator<Patient>() {

        @Override
        public Patient createFromParcel(Parcel source) {
            return new Patient(source);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };
}

