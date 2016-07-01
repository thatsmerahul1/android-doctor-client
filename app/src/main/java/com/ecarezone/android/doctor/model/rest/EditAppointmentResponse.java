package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.model.Appointment;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Umesh on 28-06-2016.
 */
public class EditAppointmentResponse implements Serializable {

    @Expose
    public Status status;
    @Expose
    public Appointment data;
}