package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.model.Doctor;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jifeng.zhang on 20/06/15.
 */
public class SearchDoctorsResponse implements Serializable {
    @Expose
    public Status status;
    @Expose
    public List<Doctor> data;
}