package com.ecarezone.android.doctor.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.Chat;
import com.ecarezone.android.doctor.model.User;
import com.ecarezone.android.doctor.model.UserProfile;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Created by L&T Technology Services on 2/16/2016.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ecarezone.db";

    private Context mContext;
    private Dao<UserProfile, Integer> mProfileDao = null;
    private Dao<Patient, Integer> mPatientProfileDao = null;
    private Dao<User, Integer> mUserDao = null;
    private Dao<Chat, Integer> mChatDao = null;
    private Dao<Appointment, Integer> mAppointmentDao = null;

    private static final String SQL_DELETE_PROFILES =
            "DELETE FROM " + DbContract.Profiles.TABLE_NAME;

    private static final String SQL_DELETE_PATIENT_PROFILES =
            "DELETE FROM " + DbContract.PatientProfiles.TABLE_NAME;

    /* Constructor */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserProfile.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Chat.class);
            TableUtils.createTable(connectionSource, Patient.class);
            TableUtils.createTable(connectionSource, Appointment.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        // onUpgrade is not necessary in the first release, so simply dropping and recreating the table.
        try {
            TableUtils.dropTable(connectionSource, UserProfile.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Chat.class, true);
            TableUtils.dropTable(connectionSource, Patient.class, true);
            TableUtils.dropTable(connectionSource, Appointment.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(sqLiteDatabase, connectionSource);
    }

    // profile data-access-object(Dao).
    public Dao<UserProfile, Integer> getProfileDao() throws SQLException {
        if (mProfileDao == null) {
            mProfileDao = getDao(UserProfile.class);
        }
        return mProfileDao;
    }

    public Dao<Patient, Integer> getPatientProfileDao() throws SQLException {
        if (mPatientProfileDao == null) {
            mPatientProfileDao = getDao(Patient.class);
        }
        return mPatientProfileDao;
    }
    // user data-access-object(Dao).
    public Dao<User, Integer> getUserDao() throws SQLException {
        if (mUserDao == null) {
            mUserDao = getDao(User.class);
        }
        return mUserDao;
    }

    // Chat data-access-object(Dao).
    public Dao<Chat, Integer> getChatDao() throws SQLException {
        if (mChatDao == null) {
            mChatDao = getDao(Chat.class);
        }
        return mChatDao;
    }

    public Dao<Appointment, Integer> getAppointmentDao() throws SQLException {
        if (mAppointmentDao == null) {
            mAppointmentDao = getDao(Appointment.class);
        }
        return mAppointmentDao;
    }

    @Override
    public void close() {
        super.close();
        mProfileDao = null;
    }
}