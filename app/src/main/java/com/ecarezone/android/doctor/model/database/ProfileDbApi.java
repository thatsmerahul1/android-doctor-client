package com.ecarezone.android.doctor.model.database;

import android.content.Context;

import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.UserProfile;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by L&T Technology Services on 3/2/2016.
 */

public class ProfileDbApi {

    private static DbHelper mDbHelper;
    private static Context mContext;
    private static ProfileDbApi mProfileDbApi;

    private ProfileDbApi() {

    }

    public static ProfileDbApi getInstance(Context context) {
        mContext = context;
        if (mDbHelper == null || mProfileDbApi == null) {
            mDbHelper = new DbHelper(context);
            mProfileDbApi = new ProfileDbApi();
        }
        return mProfileDbApi;

    }

    /* checks whether the user has any profiles. */
    public boolean hasProfile(String userId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            long noOfProfiles = queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .countOf();
            return noOfProfiles > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Saves multiple user profiles at a time in local database. Used during login */
    public boolean saveMultipleProfiles(String userId, UserProfile[] userProfiles) {
        for (UserProfile userProfile : userProfiles) {
            saveProfile(userId, userProfile, userProfile.id);
        }
        return true;
    }
    /* detele profile using userID */
    public int deleteProfiles(String userId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            DeleteBuilder<UserProfile, Integer> deleteBuilder = userProfileDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId);
            return deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* Saves a user profile in local database. Returns success failure response. */
    public boolean saveProfile(String userId, UserProfile userProfile, String profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            userProfile.userId = userId;
            userProfile.id = profileId;
//            userProfile.isComplete = areAllFieldsFilled(userProfile);
            int status = userProfileDao.create(userProfile);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean saveProfile(String userId, UserProfile userProfile) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            userProfile.id = userId;
 //            userProfile.isComplete = areAllFieldsFilled(userProfile);
            int status = userProfileDao.create(userProfile);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /* Saves a user profile in local database. Returns success failure response. */
    public boolean updateProfile(String userId, UserProfile userProfile, String profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            UpdateBuilder<UserProfile, Integer> updateBuilder = userProfileDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                   /* .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_ID, profileId)*/;

            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_NAME, userProfile.name);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_BIRTH_DATE, userProfile.birthDate);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_GENDER, userProfile.gender);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_AVATAR_URL, userProfile.avatarUrl);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_SPECIALIZED_AREAS, userProfile.category);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_REGISTRATION_ID, userProfile.registrationId);
            updateBuilder.updateColumnValue(DbContract.Profiles.COLUMN_NAME_MY_BIO, userProfile.doctorDescription);


            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the details of a particular profile */
    public UserProfile getProfile(String userId /*String profileId*/) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)/*
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_ID, profileId)*/
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /* Retrieve all the profiles associated with particular user */
    public UserProfile[] getProfiles(String userId) {
        UserProfile[] profiles = new UserProfile[0];
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            List<UserProfile> userProfilesList = queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .query();
            profiles = userProfilesList.toArray(profiles);
            return profiles;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

   /* *//* deletes a particular profile *//*
    public boolean deleteProfile(String userId, String profileId) {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            DeleteBuilder<UserProfile, Integer> deleteBuilder = userProfileDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, userId)
                    .and()
                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_ID, profileId);
            int count = deleteBuilder.delete();
            return count > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }*/

    public UserProfile getMyProfile() {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            return queryBuilder.where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, LoginInfo.userId)
//                    .and()
//                    .eq(DbContract.Profiles.COLUMN_NAME_PROFILE_NAME, mContext.getString(R.string.profile_mine))
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isMyProfileComplete() {
        try {
            Dao<UserProfile, Integer> userProfileDao = mDbHelper.getProfileDao();
            QueryBuilder<UserProfile, Integer> queryBuilder = userProfileDao.queryBuilder();
            UserProfile myProfile = queryBuilder
                    .where()
                    .eq(DbContract.Profiles.COLUMN_NAME_USER_ID, LoginInfo.userId)
//                    .and()
                    .queryForFirst();
            if(myProfile!=null){
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean areAllFieldsFilled(UserProfile userProfile) {
        try {
            if (userProfile.avatarUrl.length() < 2) {
                return false;
            } else if (userProfile.birthDate.length() < 2) {
                return false;
            }/* else if (userProfile.email.length() < 2) {
                return false;
            } */else if (userProfile.gender.length() < 2) {
                return false;
            } else if (userProfile.name.length() < 2) {
                return false;
            } else {
                // all fields have some data
                return true;
            }
        } catch (NullPointerException e) {
            // if any of the field is not set, null pointer exception is caught. it means profile is not Finished.
            return false;
        }
    }
}