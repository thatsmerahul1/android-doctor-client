package com.ecarezone.android.doctor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

//import com.crittercism.app.Crittercism;
import com.ecarezone.android.doctor.fragment.SplashScreenFragment;
import com.ecarezone.android.doctor.utils.SinchUtil;


public class SplashScreenActivty extends EcareZoneBaseActivity {

    Activity activity;
    SplashScreenFragment splashScreenFragment;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        float density = getResources().getDisplayMetrics().density;
//        Crittercism.initialize(getApplicationContext(),
//                "56b49f2fb35f950b00e1ad37");

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            activity = this;
            Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.ecarezone_green_dark));
        }
        splashScreenFragment = SplashScreenFragment.newInstance();
                onNavigationChanged(R.layout.frag_splashscreen, null);
        addSupportOnBackStackChangedListener(this);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId == R.layout.frag_splashscreen) {
            changeFragment(R.id.screen_container, splashScreenFragment,
                    SplashScreenFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected String getCallerName() {
        return SplashScreenActivty.class.getSimpleName();
    }

    @Override
    public void onServiceConnected() {
        SinchUtil.getSinchServiceInterface().setStartListener(splashScreenFragment);
    }

    @Override
    public void onServiceDisconnected() {

    }

}
