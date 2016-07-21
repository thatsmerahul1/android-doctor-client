package com.ecarezone.android.doctor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.fragment.AppointmentFragment;
import com.ecarezone.android.doctor.fragment.MessagesListFragment;
import com.ecarezone.android.doctor.fragment.MyPatientListFragment;
import com.ecarezone.android.doctor.fragment.WelcomeFragment;
import com.ecarezone.android.doctor.gcm.HeartBeatReceiver;
import com.ecarezone.android.doctor.gcm.HeartbeatService;
import com.ecarezone.android.doctor.model.database.ProfileDbApi;
import com.ecarezone.android.doctor.service.FetchAppointmentService;
import com.ecarezone.android.doctor.utils.Util;
import com.urbanairship.UAirship;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by CHAO WEI on 5/3/2015.
 */
public class MainActivity extends EcareZoneBaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle mDrawerToggle = null;
    private Toolbar mToolBar = null;
    private ActionBar mActionBar = null;
    private boolean isBackStackRequired;
    private boolean isWelcomeMainRequired;
    public static int VIEW_PROFILE_REQUEST_CODE = 1001;
    int status = 1;

    public static final long DISCONNECT_TIMEOUT = 15000; // 1 min = 1 * 60 * 1000 ms

    //    HashMap<String,Boolean> nameValuePair;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.side_drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                    syncState();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                    syncState();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setFitsSystemWindows(true);
            mDrawerToggle.syncState();
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        }

        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(Constants.ECARE_ZONE);

        if(getIntent().getBooleanExtra("from_login_screen", false)){
            // when a user logs into a different device.
            // needed to fetch all his/her existing appointments and populate the DB
            FetchAppointmentService.startActionFetchAppointment(getApplicationContext());
        }

         /* queries the db and checks whether to show welcome screen or to show home screen.
           Check is based on whether the user has created a profile or not. */
        if (!isNavigationChanged) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                    boolean hasProfiles = profileDbApi.hasProfile(LoginInfo.userId.toString());
                    return hasProfiles;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    onNavigationChanged(R.layout.frag_welcome, null);
                    isWelcomeMainRequired = aBoolean;
                    super.onPostExecute(aBoolean);
                }
            }.execute();
        } else {
            isNavigationChanged = false;
        }
//        disconnectHandler.post(disconnectCallback);
        initStatus();
        setStatusAlarm();
        Util.setAppointmentAlarm(this);
    }

    private void initStatus() {
        DoctorApplication doctorApplication = (DoctorApplication) getApplicationContext();
        doctorApplication.setLastAvailabilityStaus(Constants.ONLINE);
        HashMap<String, Boolean> statusMap = doctorApplication.getNameValuePair();
        statusMap.put(Constants.STATUS_CHANGE, true);
        doctorApplication.setStatusNameValuePair(statusMap);

        Intent intent = new Intent(this, HeartbeatService.class);
        intent.putExtra(Constants.UPDATE_STATUS, true);
        startService(intent);
    }

    private void setStatusAlarm() {

        try {
            Calendar updateTime = Calendar.getInstance();
            Intent intent = new Intent(this, HeartBeatReceiver.class);
            intent.putExtra(Constants.SEND_HEART_BEAT, true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setInexactRepeating(AlarmManager.RTC,
                    updateTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES / 3, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    public void toggleDrawer(boolean open) {
        if (mDrawerLayout != null) {
            if (open) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.closeDrawers();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((mDrawerToggle != null) && (mDrawerToggle.onOptionsItemSelected(item))) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
                return;
            }
        }
        if (isBackStackRequired) {
            onNavigationChanged(isWelcomeMainRequired ? R.layout.frag_doctor_main : R.layout.frag_welcome, null);
            isBackStackRequired = false;
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected String getCallerName() {
        return MainActivity.class.getSimpleName();
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        Log.d(TAG, "onNavigationChanged " + fragmentLayoutResId);
        if (fragmentLayoutResId < 0) return;
        if (fragmentLayoutResId == R.layout.frag_message_list) {
            changeFragment(R.id.screen_container, new MessagesListFragment(),
                    getString(R.string.main_side_menu_messages), args, false);
            isBackStackRequired = false;
            isWelcomeMainRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_welcome) {
            changeFragment(R.id.screen_container, new WelcomeFragment(),
                    WelcomeFragment.class.getSimpleName(), args, false);
            isBackStackRequired = false;
        } else if (fragmentLayoutResId == R.layout.frag_appointment) {
            changeFragment(R.id.screen_container, new AppointmentFragment(),
                    getString(R.string.main_side_menu_appointments), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_doctor_list) {
            changeFragment(R.id.screen_container, new MyPatientListFragment(),
                    getString(R.string.main_side_menu_my_patients), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_settings) {
            changeFragment(R.id.screen_container, new SettingsFragment(),
                    getString(R.string.main_side_menu_settings), args, false);
            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.list_view) {
            Long profileId = (LoginInfo.userId);
            startActivityForResult(new Intent(getApplicationContext(), ProfileDetailsActivity.class)
                    .putExtra(ProfileDetailsActivity.IS_NEW_PROFILE, false)
                    .putExtra(ProfileDetailsActivity.PROFILE_ID, profileId), VIEW_PROFILE_REQUEST_CODE);
//            changeFragment(R.id.screen_container, new UserProfileDetailsFragment(),
//                    UserProfileDetailsFragment.class.getSimpleName(), args, false);
//            isBackStackRequired = true;
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    private boolean isNavigationChanged = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {// coming back from patient screen
            onNavigationChanged(R.layout.frag_doctor_list, null);
            isNavigationChanged = true;
        }
    }

    public void changeFragmentToAppointment(){
        changeFragment(R.id.screen_container, new MyPatientListFragment(),
                getString(R.string.main_side_menu_my_patients), null, false);
        isBackStackRequired = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true,this);
    }
}
