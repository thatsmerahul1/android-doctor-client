package com.ecarezone.android.doctor;

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
import com.ecarezone.android.doctor.fragment.MyPatientListFragment;
import com.ecarezone.android.doctor.fragment.FirstTimeUserProfileFragment;
import com.ecarezone.android.doctor.fragment.MessagesListFragment;
import com.ecarezone.android.doctor.fragment.WelcomeFragment;
import com.ecarezone.android.doctor.model.database.ProfileDbApi;

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

         /* queries the db and checks whether to show welcome screen or to show home screen.
           Check is based on whether the user has created a profile or not. */
        if(!isNavigationChanged) {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected Boolean doInBackground(Void... params) {
                    ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
                    boolean hasProfiles = profileDbApi.hasProfile(LoginInfo.userId.toString());
                    return hasProfiles;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
//                if (aBoolean) {
                    onNavigationChanged(R.layout.frag_welcome, null);
//                } else {
//                    onNavigationChanged(R.layout.frag_welcome, null);
//                }
                    isWelcomeMainRequired = aBoolean;
                    super.onPostExecute(aBoolean);
                }
            }.execute();
        }
        else{
            isNavigationChanged = false;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
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
            Long profileId =  (LoginInfo.userId);
            startActivityForResult(new Intent(getApplicationContext(), ProfileDetailsActivity.class)
                    .putExtra(ProfileDetailsActivity.IS_NEW_PROFILE, false)
                    .putExtra(ProfileDetailsActivity.PROFILE_ID, profileId), VIEW_PROFILE_REQUEST_CODE);
//            changeFragment(R.id.screen_container, new UserProfileDetailsFragment(),
//                    UserProfileDetailsFragment.class.getSimpleName(), args, false);
//            isBackStackRequired = true;
        } else if (fragmentLayoutResId == R.layout.frag_first_time_profile) {
            changeFragment(R.id.screen_container, new FirstTimeUserProfileFragment(),
                    FirstTimeUserProfileFragment.class.getSimpleName(), args, false);
            isBackStackRequired = true;
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

        if(requestCode == 100 && resultCode == RESULT_OK){// coming back from patient screen
            onNavigationChanged( R.layout.frag_doctor_list, null);
            isNavigationChanged = true;
        }
    }

}
