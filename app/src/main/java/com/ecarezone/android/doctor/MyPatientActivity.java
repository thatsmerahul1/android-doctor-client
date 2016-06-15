package com.ecarezone.android.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.fragment.DoctorFragment;
import com.ecarezone.android.doctor.utils.Util;

/**
 * Created by CHAO WEI on 6/1/2015.
 */
public class MyPatientActivity extends EcareZoneBaseActivity {

    private static final String TAG = MyPatientActivity.class.getSimpleName();
    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private Bundle data;

    @Override
    protected String getCallerName() {
        return MyPatientActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_doctor);
        Log.i(TAG, Constants.DOCTOR_DETAIL + " = " + getIntent().getBundleExtra(Constants.DOCTOR_DETAIL));
        data = getIntent().getBundleExtra(Constants.DOCTOR_DETAIL);
        onNavigationChanged(R.layout.frag_doctor, data);
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
            mToolBar.setOnMenuItemClickListener(
                    new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Handle menu item click event
                            if (item.getItemId() == R.id.action_info) {
                                Log.i(TAG, "Menu = " + item.getTitle() + ", " + item.getItemId());
                                Intent showDoctorBioIntent = new Intent(MyPatientActivity.this, MyPatientBioActivity.class);
                                showDoctorBioIntent.putExtra(Constants.DOCTOR_BIO_DETAIL, data);
                                startActivity(showDoctorBioIntent);
                            }
                            return true;
                        }
                    });
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(getResources().getString(R.string.main_side_menu_my_patients));
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_doctor) {
            changeFragment(R.id.screen_container, new DoctorFragment(),
                    DoctorFragment.class.getSimpleName(), args);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false,this);
    }
}


