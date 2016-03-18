package com.ecarezone.android.doctor.model.rest;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class Status implements Serializable {
    @Expose
    public Integer code;
    @Expose
    public String message;
}
