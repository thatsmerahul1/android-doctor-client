package com.ecarezone.android.doctor;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.fragment.MyPatientBioFragment;
import com.ecarezone.android.doctor.fragment.dialog.AddDoctorRequestDialog;
import com.ecarezone.android.doctor.model.rest.AddDoctorRequest;
import com.ecarezone.android.doctor.model.rest.AddDoctorResponse;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by L&T Technology Services on 22-02-2016.
 */
public class MyPatientBioActivity extends EcareZoneBaseActivity {

    private static final String TAG = MyPatientBioActivity.class.getSimpleName();
    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private static final int HTTP_STATUS_OK = 200;
    private Long doctorId;
    private String doctorName;
    private ProgressDialog progressDialog;

    @Override
    protected String getCallerName() {
        return MyPatientBioActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_doctor);
//        Bundle bundle = getIntent().getBundleExtra(Constants.PATIENT_BIO_DETAIL);

        onNavigationChanged(R.layout.frag_doctor_bio, getIntent().getBundleExtra("doctorBioDetail"));
        Log.i(TAG, "bio data = " + getIntent().getBundleExtra("doctorBioDetail"));
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
            mToolBar.setOnMenuItemClickListener(
                    new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.action_add) {
                                Log.i(TAG, "Menu = " + item.getTitle() + ", " + item.getItemId());
//                                sendAddDoctorRequest();
                            }
                            return true;
                        }
                    });
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(getResources().getString(R.string.doctor_bio));
        doctorName = ((Patient) getIntent().getBundleExtra(Constants.DOCTOR_BIO_DETAIL).getParcelable(Constants.DOCTOR_DETAIL)).name;
        doctorId = ((Patient) getIntent().getBundleExtra(Constants.DOCTOR_BIO_DETAIL).getParcelable(Constants.DOCTOR_DETAIL)).userId;
        addSupportOnBackStackChangedListener(this);
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

        if (fragmentLayoutResId == R.layout.frag_doctor_bio) {
            changeFragment(R.id.screen_container, new MyPatientBioFragment(),
                    MyPatientBioFragment.class.getSimpleName(), args);
        }
    }

    private void sendAddDoctorRequest() {
        Log.d(TAG, "SendAddDoctorRequest");
        progressDialog = ProgressDialogUtil.getProgressDialog(this, "Adding Doctor......");
        AddDoctorRequest request =
                new AddDoctorRequest(doctorId, doctorName, LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique);
        getSpiceManager().execute(request, new AddDoctorTaskRequestListener());
    }

    public final class AddDoctorTaskRequestListener implements RequestListener<AddDoctorResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(AddDoctorResponse addDoctorResponse) {
            progressDialog.dismiss();
            Log.d(TAG, "ResponseCode " + addDoctorResponse.status.code);

            if (addDoctorResponse.status.code == HTTP_STATUS_OK) {
                AddDoctorRequestDialog addDoctorRequestDialog = new AddDoctorRequestDialog(doctorName);
                FragmentManager fragmentManager = getFragmentManager();
                addDoctorRequestDialog.show(fragmentManager, "AddDoctorRequestSuccessFragment");
            } else {
                Toast.makeText(MyPatientBioActivity.this, addDoctorResponse.status.message, Toast.LENGTH_LONG).show();
            }


        }
    }
}
