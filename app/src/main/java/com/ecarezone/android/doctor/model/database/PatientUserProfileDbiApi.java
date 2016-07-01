package com.ecarezone.android.doctor.model.database;

import android.content.Context;

import com.ecarezone.android.doctor.model.PatientProfile;
import com.ecarezone.android.doctor.model.pojo.PatientUserProfileListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by 20109804 on 6/23/2016.
 */
public class PatientUserProfileDbiApi {
private static DbHelper mDbHelper;
private static Context mContext;
private static PatientUserProfileDbiApi mPatientProfileDbApi;

        private PatientUserProfileDbiApi() {

        }

        public static PatientUserProfileDbiApi getInstance(Context context) {
            mContext = context;
            if (mDbHelper == null || mPatientProfileDbApi == null) {
                mDbHelper = new DbHelper(context);
                mPatientProfileDbApi = new PatientUserProfileDbiApi();
            }
            return mPatientProfileDbApi;

        }

        public static boolean saveProfile(PatientProfile userProfile) {
            try {
                Dao<PatientProfile, Integer> userProfileDao = mDbHelper.getPatientUserProfileDao();
                int status = userProfileDao.create(userProfile);
                return status != 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        /* Saves a user profile in local database. Returns success failure response. */
        public boolean updateProfile(String profileId, PatientProfile userProfile) {
            try {
                Dao<PatientProfile, Integer> userProfileDao = mDbHelper.getPatientUserProfileDao();
                UpdateBuilder<PatientProfile, Integer> updateBuilder = userProfileDao.updateBuilder();
                updateBuilder.where()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_PROFILE_ID, profileId);

                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_NAME, userProfile.name);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_EMAIL, userProfile.email);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_PROFILE_NAME, userProfile.profileName);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_AVATAR_URL, userProfile.avatarUrl);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_BIRTH_DATE, userProfile.birthdate);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_ETHNICITY, userProfile.ethnicity);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_GENDER, userProfile.gender);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_HIGHT, userProfile.height);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_WEIGHT, userProfile.weight);
                updateBuilder.updateColumnValue(DbContract.PatientUerProfile.COLUMN_NAME_USER_ID, userProfile.userId);

                int updatedRowCount = updateBuilder.update();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * Deletes the profile of the user bearing the user id
         * @param userId
         * @return
         */
        public boolean deleteProfile(String userId){
            try {
                Dao<PatientProfile, Integer> userProfileDao = mDbHelper.getPatientUserProfileDao();
                DeleteBuilder<PatientProfile, Integer> deleteBuilder = userProfileDao.deleteBuilder();
                deleteBuilder.where()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_USER_ID, userId);

                int numOfRowsDeleted = deleteBuilder.delete();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        /* retrieve the details of a particular profile */
        public PatientProfile getProfile(String userId, String profileId) {
            try {
                Dao<PatientProfile, Integer> userProfileDao = mDbHelper.getPatientUserProfileDao();
                QueryBuilder<PatientProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
                return queryBuilder.where()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_USER_ID, userId)
                        .and()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_PROFILE_ID, profileId)
                        .queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    
        /* retrieve the details of a particular profile */
        public PatientProfile getProfileByEmail(String email) {
            try {
                Dao<PatientProfile, Integer> userProfileDao = mDbHelper.getPatientUserProfileDao();
                QueryBuilder<PatientProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
                return queryBuilder.where()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_EMAIL, email)
                        .queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public int getProfileIdUsingEmail(String emailId) {
            Patient[] profiles = new Patient[0];
            try {
                Dao<PatientProfile, Integer> userProfileDao = mDbHelper.getPatientUserProfileDao();
                QueryBuilder<PatientProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
                List<PatientProfile> userProfilesList = queryBuilder.where()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_EMAIL, emailId)
                        .query();
                if(userProfilesList!=null && userProfilesList.size() > 0){
                    return Integer.parseInt(String.valueOf(userProfilesList.get(0).userId));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }
        /* retrieve the details of a particular profile */
        public PatientProfile getProfileByProfileId(String profileId, String userId) {
            try {
                Dao<PatientProfile, Integer> userProfileDao = mDbHelper.getPatientUserProfileDao();
                QueryBuilder<PatientProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
                return queryBuilder.where()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_USER_ID, userId)
                        .and()
                        .eq(DbContract.PatientUerProfile.COLUMN_NAME_PROFILE_ID, profileId)
                        .queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        private boolean areAllFieldsFilled(PatientProfile userProfile) {
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
