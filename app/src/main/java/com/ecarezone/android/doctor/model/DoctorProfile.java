package com.ecarezone.android.doctor.model;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by Namitha on 4/28/2016.
 */
public class DoctorProfile implements Serializable {

        @Expose
        @DatabaseField(canBeNull = false)
        public String name;
        @Expose
        @DatabaseField
        public String doctorDescription;
        @Expose
        @DatabaseField
        public String avatarUrl;
        @Expose
        @DatabaseField
        public String registrationId;
        @Expose
        @DatabaseField
        public String birthDate;
        @Expose
        @DatabaseField
        public String gender;
        @Expose
        @DatabaseField
        public String category;

        public String toString() {
            return   name + " "  + doctorDescription + " " + avatarUrl + " " +  registrationId + " " + birthDate + " " +  gender+ " " + category;
        }

}
