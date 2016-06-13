package com.ecarezone.android.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.fragment.UserProfileDetailsFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by L&T Technology Services on 3/18/2016.
 */
public class ProfileDetailsActivity extends EcareZoneBaseActivity {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private UserProfileDetailsFragment userProfileDetailsFragment = new UserProfileDetailsFragment();

    public static String IS_NEW_PROFILE = "is_new_profile";
    public static String PROFILE_ID = "profile_id";

    @Override
    protected String getCallerName() {
        return ProfileDetailsActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile);
        onNavigationChanged(R.layout.frag_user_profle_details, null);

        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolBar);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_user_profle_details) {
            changeFragment(R.id.screen_container, userProfileDetailsFragment,
                    UserProfileDetailsFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        userProfileDetailsFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        userProfileDetailsFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DoctorApplication.nameValuePair.put(Constants.STATUS_CHANGE, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DoctorApplication.nameValuePair.put(Constants.STATUS_CHANGE, false);
    }
}
