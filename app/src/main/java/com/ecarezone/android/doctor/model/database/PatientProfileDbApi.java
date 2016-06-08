package com.ecarezone.android.doctor.model.database;

import android.content.Context;

import com.ecarezone.android.doctor.model.UserProfile;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by 20109804 on 5/17/2016.
 */
public class PatientProfileDbApi {

    private static DbHelper mDbHelper;
    private static Context mContext;
    private static PatientProfileDbApi mPatientProfileDbApi;

    private PatientProfileDbApi() {

    }

    public static PatientProfileDbApi getInstance(Context context) {
        mContext = context;
        if (mDbHelper == null || mPatientProfileDbApi == null) {
            mDbHelper = new DbHelper(context);
            mPatientProfileDbApi = new PatientProfileDbApi();
        }
        return mPatientProfileDbApi;

    }


    /* Saves a user profile in local database. Returns success failure response. */
    public boolean saveProfile(long userId, Patient userProfile, String profileId) {
        try {
            Dao<Patient, Integer> userProfileDao = mDbHelper.getPatientProfileDao();
            userProfile.userId = userId;
            int status = userProfileDao.create(userProfile);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean saveProfile(Long userId, Patient userProfile) {
        try {
            Dao<Patient, Integer> userProfileDao = mDbHelper.getPatientProfileDao();
            userProfile.userId = userId;
            int status = userProfileDao.create(userProfile);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /* Saves a user profile in local database. Returns success failure response. */
    public boolean updateProfile(String userId, Patient userProfile) {
        try {
            Dao<Patient, Integer> userProfileDao = mDbHelper.getPatientProfileDao();
            UpdateBuilder<Patient, Integer> updateBuilder = userProfileDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.PatientProfiles.COLUMN_NAME_USER_ID, userId);

            updateBuilder.updateColumnValue(DbContract.PatientProfiles.COLUMN_NAME_NAME, userProfile.name);
            updateBuilder.updateColumnValue(DbContract.PatientProfiles.COLUMN_NAME_EMAIL, userProfile.email);
            updateBuilder.updateColumnValue(DbContract.PatientProfiles.COLUMN_NAME_STATUS, userProfile.status);
            updateBuilder.updateColumnValue(DbContract.PatientProfiles.COLUMN_NAME_AVATAR_URL, userProfile.avatarUrl);
            updateBuilder.updateColumnValue(DbContract.PatientProfiles.COLUMN_NAME_AVATAR_URL, userProfile.avatarUrl);

            int updatedRowCount = updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the details of a particular profile */
    public Patient getProfile(String userId /*String profileId*/) {
        if(userId != null) {
            try {
                Dao<Patient, Integer> userProfileDao = mDbHelper.getPatientProfileDao();
                QueryBuilder<Patient, Integer> queryBuilder = userProfileDao.queryBuilder();
                return queryBuilder.where()
                        .eq(DbContract.PatientProfiles.COLUMN_NAME_USER_ID, userId)
                        .queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /* retrieve the details of a particular profile */
    public Patient getProfileByEmail(String email) {
        try {
            Dao<Patient, Integer> userProfileDao = mDbHelper.getPatientProfileDao();
            QueryBuilder<Patient, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.PatientProfiles.COLUMN_NAME_EMAIL, email)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* retrieve the details of a particular profile */
    public Patient getProfileByProfileId(String profileId) {
        try {
            Dao<Patient, Integer> userProfileDao = mDbHelper.getPatientProfileDao();
            QueryBuilder<Patient, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.PatientProfiles.COLUMN_NAME_USER_ID, profileId)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean areAllFieldsFilled(Patient userProfile) {
        try {
            if (userProfile.email.length() < 2) {
                return false;
            }  else if (userProfile.name.length() < 2) {
                return false;
            }  /*else if (userProfile.gender.length() < 2) {
                return false;
            }*/else {
                // all fields have some data
                return true;
            }
        } catch (NullPointerException e) {
            // if any of the field is not set, null pointer exception is caught. it means profile is not Finished.
            return false;
        }
    }
}
