package com.ecarezone.android.doctor;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ecarezone.android.doctor.fragment.EditAppointmentFragment;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.pojo.AppointmentListItem;
import com.ecarezone.android.doctor.utils.Util;

import java.util.Objects;

/**
 * Created by L&T Technology Services.
 */
public class  EditAppointmentActivity extends EcareZoneBaseActivity {

    private Toolbar mToolBar = null;
    private ActionBar mActionBar;
    private long doctorId;
    private AppointmentListItem currentAppointment;

    @Override
    protected String getCallerName() {
        return null;
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_edit_appointment) {
            args.putLong("doctorId", doctorId);
            args.putSerializable("currentAppointment", currentAppointment);
            changeFragment(R.id.screen_container, new EditAppointmentFragment(),
                    EditAppointmentFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_appointment);
        Bundle data = getIntent().getExtras();
        doctorId = getIntent().getLongExtra("doctorId", -1);

        Object obj = getIntent().getSerializableExtra("currentAppointment");
        if(obj != null) {
            this.currentAppointment = (AppointmentListItem)obj;
        }

        onNavigationChanged(R.layout.frag_edit_appointment, ((data == null) ? null : data));
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.back_);
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false, this);
    }
}
