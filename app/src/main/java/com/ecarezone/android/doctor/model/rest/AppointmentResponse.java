package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.model.Appointment;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by umesh on 30-05-2016.
 */
public class AppointmentResponse implements Serializable{

    @Expose
    public Status status;
    @Expose
    public List<Appointment> data;
}
