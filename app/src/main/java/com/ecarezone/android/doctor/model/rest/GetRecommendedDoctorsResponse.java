package com.ecarezone.android.doctor.model.rest;

import com.ecarezone.android.doctor.model.Doctor;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;

import java.util.List;

/**
 * Created by L&T Technology Services on 01-03-2016.
 */
public class GetRecommendedDoctorsResponse extends BaseResponse {

    public List<Doctor> data;
}
