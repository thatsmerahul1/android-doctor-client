package com.ecarezone.android.doctor;

import android.os.Bundle;

import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.fragment.LoginFragment;
import com.ecarezone.android.doctor.utils.Util;


public class LoginActivity extends EcareZoneBaseActivity {

    @Override
    protected String getCallerName() {
        return LoginActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        onNavigationChanged(R.layout.frag_login, null);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId < 0) return;

        if(fragmentLayoutResId == R.layout.frag_login) {
            changeFragment(R.id.screen_container, LoginFragment.newInstance(),
                    LoginFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true,this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false,this);
    }
}
