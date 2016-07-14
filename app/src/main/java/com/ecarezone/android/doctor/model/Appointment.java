package com.ecarezone.android.doctor.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Umesh on 30-05-2016.
 */
public class Appointment implements Serializable {

    @Expose
    @DatabaseField(canBeNull = false)
    public int id;

    @Expose
    @DatabaseField(canBeNull = false)
    public String dateTime;

    @Expose
    @DatabaseField(canBeNull = false)
    public String callType;

    @Expose
    @DatabaseField(canBeNull = false)
    public int patientId;

    @Expose
    public int doctorId;

    @Expose
    public String reScheduledBy;

    @Expose
    public String message;

    @DatabaseField(canBeNull = true)
    public boolean isConfirmed;

    public boolean isAppointmentPresent;
}
