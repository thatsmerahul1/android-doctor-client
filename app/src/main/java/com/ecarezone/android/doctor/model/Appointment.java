package com.ecarezone.android.doctor.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 10603675 on 30-05-2016.
 */
public class Appointment implements Serializable {

    @Expose
    @DatabaseField(canBeNull = false)
    private int id;

    @Expose
    @DatabaseField(canBeNull = false)
    private String dateTime;

    @Expose
    @DatabaseField(canBeNull = false)
    private String callType;

    @Expose
    @DatabaseField(canBeNull = false)
    private int patientId;

    @Expose
    private int doctorId;

    @Expose
    private String message;

    @DatabaseField(canBeNull = true)
    private boolean isConfirmed;

    private boolean isAppointmentPresent;

    public Appointment() {

    }

    public boolean isAppointmentPresent() {
        return isAppointmentPresent;
    }

    public void setAppointmentPresent(boolean isAppointmentPresent) {
        this.isAppointmentPresent = isAppointmentPresent;
    }

    public String getTimeStamp() {
        return dateTime;
    }

    public void setTimeStamp(String timeStamp) {
        this.dateTime = timeStamp;
    }

    public int getAppointmentId() {
        return id;
    }

    public void setAppointmentId(int appointmentId) {
        this.id = appointmentId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int doctorId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return this.patientId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public boolean isConfirmed() {
        return this.isConfirmed;
    }

    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

}
