package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.RegistrationActivity;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.User;
import com.ecarezone.android.doctor.model.database.UserTable;
import com.ecarezone.android.doctor.service.SinchService;
import com.ecarezone.android.doctor.utils.SinchUtil;
import com.sinch.android.rtc.SinchError;


public class SplashScreenFragment extends EcareZoneBaseFragment implements SinchService.StartFailedListener {

    private static String TAG = SplashScreenFragment.class.getSimpleName();
    private UserTable userTable;

    public static SplashScreenFragment newInstance() {
        return new SplashScreenFragment();
    }

    @Override
    protected String getCallerName() {
        return SplashScreenFragment.class.getSimpleName();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

    private void performSplashTask() {
        final Activity activity = getActivity();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity != null) {
                    // we use SharedPreferences to show a indicator for checking
                    // if the user has logged in
                    SharedPreferences perPreferences =
                            activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                    boolean is_login = perPreferences.getBoolean(Constants.IS_LOGIN, false);
                    String userId = perPreferences.getString(Constants.USER_ID, null);
                    userTable = new UserTable(getApplicationContext());
                    User user = userTable.getUserData(userId);
                    if (is_login) { // the current user is still in login status
                        LoginInfo.userId = Long.parseLong(userId);
                        LoginInfo.userName = user.email;
                        LoginInfo.hashedPassword = user.password;
                        LoginInfo.role = user.role;
                        if (!SinchUtil.getSinchServiceInterface().isStarted()) {
                            Log.i(TAG, "userId::" + userId);
                            SinchUtil.getSinchServiceInterface().startClient(LoginInfo.userName);
//commented to run this on android M emulator
                        } else {
                            Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            activity.finish();
                        }
                    } else {
                        if (user != null) {
                            LoginInfo.userName = user.email;
                        }
                        activity.startActivity(new Intent(activity.getApplicationContext(), RegistrationActivity.class));
                        activity.finish();
                    }
                }
            }
        }, 1500L);

    }

    @Override
    public void onStartFailed(SinchError error) {
        //Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Sinch onStartFailed");
    }

    @Override
    public void onStarted() {
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}