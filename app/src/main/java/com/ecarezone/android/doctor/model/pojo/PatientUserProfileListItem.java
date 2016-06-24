package com.ecarezone.android.doctor.model.pojo;

import com.ecarezone.android.doctor.model.PatientProfile;
import com.ecarezone.android.doctor.model.database.DbContract;
import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import java.util.List;

/**
 * Created by 20109804 on 6/24/2016.
 */
public class PatientUserProfileListItem {
    @Expose
    public long userId;
    @Expose
    public String profileName;
    @Expose
    public String email;
    @Expose
    public String height;
    @Expose
    public String name;
    @Expose
    public String address;
    @Expose
    public String weight;
    @Expose
    public String avatarUrl;
    @Expose
    public String profileId;
    @Expose
    public String ethnicity;
    @Expose
    public String gender;
    @Expose
    public String birthdate;
    @Expose
    public List<DbContract.PatientUerProfile> userProfile;

    public int listItemType;
    public int patientId;
    public int appointmentId;
    public String callType;
    public String dateTime;

    //message data
    public String msgText;
    public int unreadMsgCount = 0;

    public static final int LIST_ITEM_TYPE_PENDING = 1;
    public static final int LIST_ITEM_TYPE_APPROVED = 2;
    public static final int LIST_ITEM_TYPE_MESSAGE = 3;
    public static final int LIST_ITEM_TYPE_APPOINTMENT = 4;


}

