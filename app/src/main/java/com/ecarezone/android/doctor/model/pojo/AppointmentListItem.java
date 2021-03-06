package com.ecarezone.android.doctor.model.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 10603675 on 30-05-2016.
 */
public class AppointmentListItem implements Serializable{

    public int appointmentId;
    public String dateTime;
    public String callType;
    public int patientId;
    public int listItemType;
    public boolean isConfirmed;
    public String patientName;
    public String profilePicUrl;

    public static final int LIST_ITEM_TYPE_PENDING = 1;
    public static final int LIST_ITEM_TYPE_APPROVED = 2;

}
