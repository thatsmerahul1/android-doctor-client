package com.ecarezone.android.doctor.model.pojo;

import android.os.Parcelable;

import com.ecarezone.android.doctor.model.PatientProfile;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by 20109804 on 5/5/2016.
 */
public class PatientListItem {

    @Expose
    public List<PatientProfile> userProfile;
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
    @Expose
    public String avatarUrl;

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
