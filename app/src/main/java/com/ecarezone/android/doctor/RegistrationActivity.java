package com.ecarezone.android.doctor;

import android.os.Bundle;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.fragment.ForgetPasswordFragment;
import com.ecarezone.android.doctor.fragment.LoginFragment;
import com.ecarezone.android.doctor.fragment.RegistrationFragment;
import com.ecarezone.android.doctor.utils.SinchUtil;
import com.ecarezone.android.doctor.utils.Util;

/**
 * Created by CHAO WEI on 5/10/2015.
 */
public class RegistrationActivity extends EcareZoneBaseActivity {

    LoginFragment loginFragment;
    RegistrationFragment registrationFragment;
    ForgetPasswordFragment forgetPasswordFragment;

    @Override
    protected String getCallerName() {
        return RegistrationActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginFragment = LoginFragment.newInstance();
        registrationFragment = RegistrationFragment.newInstance();
        forgetPasswordFragment = ForgetPasswordFragment.newInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_registraion);
        onNavigationChanged(R.layout.frag_login, null);
        addSupportOnBackStackChangedListener(this);

        if(getIntent().getBooleanExtra("stop_sinch", false)){
            stopSinchService();
        }
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_login) {

            changeFragment(R.id.screen_container, loginFragment,
                    LoginFragment.class.getSimpleName(), args);

        } else if (fragmentLayoutResId == R.layout.frag_registration) {

            changeFragment(R.id.screen_container, registrationFragment,
                    RegistrationFragment.class.getSimpleName(), args);
        } else if (fragmentLayoutResId == R.layout.act_forgotpassword) {
            changeFragment(R.id.screen_container, forgetPasswordFragment,
                    ForgetPasswordFragment.class.getSimpleName(), args);
        }
    }

    @Override
    public void onServiceConnected() {
        if (loginFragment != null) {
            SinchUtil.getSinchServiceInterface().setStartListener(loginFragment);
        } else if (registrationFragment != null) {
            SinchUtil.getSinchServiceInterface().setStartListener(registrationFragment);
        }
    }

    @Override
    public void onServiceDisconnected() {

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Util.changeStatus(true,this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Util.changeStatus(false,this);
//    }

}
