package com.ecarezone.android.doctor;

import android.os.Bundle;
import android.view.Window;

import com.ecarezone.android.doctor.fragment.SplashScreenFragment;

public class SplashScreenActivty extends EcareZoneBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        onNavigationChanged(R.layout.frag_splashscreen, null);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId == R.layout.frag_splashscreen) {
            changeFragment(R.id.screen_container, SplashScreenFragment.newInstance(),
                                    SplashScreenFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected String getCallerName() {
        return SplashScreenActivty.class.getSimpleName();
    }

}
