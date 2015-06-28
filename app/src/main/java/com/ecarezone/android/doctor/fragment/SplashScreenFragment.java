package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.LoginActivity;

public class SplashScreenFragment extends EcareZoneBaseFragment {

    public static SplashScreenFragment newInstance() {
        return  new SplashScreenFragment();
    }

    @Override
    protected String getCallerName() {
        return SplashScreenFragment.class.getSimpleName();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_splashscreen, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        performSplashTask();
    }


    private void performSplashTask () {
        final Activity activity = getActivity();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(activity != null) {
                    //activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                    activity.startActivity(new Intent(activity.getApplicationContext(), LoginActivity.class));
                    activity.finish();
                }
            }
        }, 1500L);

    }
}
