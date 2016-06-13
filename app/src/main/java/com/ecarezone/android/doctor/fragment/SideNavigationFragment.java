package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ecarezone.android.doctor.DoctorApplication;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.ProfileDetailsActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.RegistrationActivity;
import com.ecarezone.android.doctor.app.widget.NavigationItem;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.rest.LoginRequest;
import com.ecarezone.android.doctor.model.rest.LoginResponse;
import com.ecarezone.android.doctor.service.SinchService;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.ecarezone.android.doctor.utils.SinchUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by CHAO WEI on 5/3/2015.
 */
public class SideNavigationFragment extends EcareZoneBaseFragment implements NavigationItem.OnNavigationItemClickListener,
        View.OnClickListener,
        FragmentManager.OnBackStackChangedListener {
    public static final String FRAGMENT_NAME = "fragmentName";
    private ProgressDialog progressDialog;

    @Override
    protected String getCallerName() {
        return SideNavigationFragment.class.getSimpleName();
    }

    private NavigationItem mHome = null;
    private NavigationItem mAppointments = null;
    private NavigationItem mDoctors = null;
    private NavigationItem mSettings = null;
    private NavigationItem mLogout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_side_navigation, container, false);

        view.findViewById(R.id.navigation_user_profile).setOnClickListener(this);

        mHome = (NavigationItem) view.findViewById(R.id.navigation_messages);
        mHome.setOnNavigationItemClickListener(this);

        mAppointments = (NavigationItem) view.findViewById(R.id.navigation_appointments);
        mAppointments.setOnNavigationItemClickListener(this);
//        mAppointments.setEnabled(false);

        mDoctors = (NavigationItem) view.findViewById(R.id.navigation_my_patients);
        mDoctors.setOnNavigationItemClickListener(this);

        mSettings = (NavigationItem) view.findViewById(R.id.navigation_settings);
        mSettings.setOnNavigationItemClickListener(this);

        mLogout = (NavigationItem) view.findViewById(R.id.navigation_logout);
        mLogout.setOnNavigationItemClickListener(this);

        mHome.highlightItem(true);
        highlightNavigationItem(null);

        return view;
    }

    @Override
    public void onItemClick(View v) {
        final String tag = String.valueOf(v.getTag());
        int layoutResId = 0;
        if (!TextUtils.isEmpty(tag)) {
            Bundle b = null;
            if (getString(R.string.main_side_menu_messages).equals(tag)) {
                layoutResId = R.layout.frag_message_list;
            } else if (getString(R.string.main_side_menu_news).equals(tag)) {
                layoutResId = R.layout.frag_news_categories;
            } else if (getString(R.string.main_side_menu_my_patients).equals(tag)) {
                layoutResId = R.layout.frag_doctor_list;
            } else if (getString(R.string.main_side_menu_appointments).equals(tag)) {
                layoutResId = R.layout.frag_appointment;
            } else if (getString(R.string.main_side_menu_logout).equals(tag)) {
                doLogout();

            } else if (getString(R.string.main_side_menu_settings).equals(tag)) {
                layoutResId = R.layout.frag_settings;
            }

            if (layoutResId > 0) {
                invokeNavigationChanged(layoutResId, b);
            }
        }
    }

    //Logout request
    private void doLogout() {
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), getText(R.string.progress_dialog_logout).toString());
        LoginRequest request =
                new LoginRequest(LoginInfo.userName, null, 0/*Integer.parseInt(LoginInfo.role)*/, null, null, null, null);
        getSpiceManager().execute(request, new LogoutRequestListener());
    }

    //Logout response
    public final class LogoutRequestListener implements RequestListener<LoginResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(final LoginResponse loginResponse) {
            if (loginResponse.status.code == 200) {
                Toast.makeText(getApplicationContext(), loginResponse.status.message, Toast.LENGTH_LONG).show();
                if (loginResponse.status.code == 200) {
                    final Activity activity = getActivity();
                    if (activity != null) {
                        SharedPreferences perPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = perPreferences.edit();
                        //Making the Login status false
                        editor.putBoolean(Constants.IS_LOGIN, false);
                        editor.commit();

                        Intent intent = new Intent(activity.getApplicationContext(), RegistrationActivity.class);
                        intent.putExtra("stop_sinch", true);
                        activity.startActivity(intent);
                        DoctorApplication.nameValuePair = null;
                        DoctorApplication.lastAvailablityStaus = -1;
                        activity.finish();
                    }

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Failed to login: " + loginResponse.status.message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            progressDialog.dismiss();
        }
    }

    private void highlightNavigationItem(NavigationItem navigationItem) {
        mHome.highlightItem(false);
        mDoctors.highlightItem(false);
        mAppointments.highlightItem(false);
        mSettings.highlightItem(false);
        mLogout.highlightItem(false);
        if (navigationItem != null) {
            navigationItem.highlightItem(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        final int viewId = v.getId();
        if (viewId == R.id.navigation_user_profile) {
            invokeNavigationChanged(R.layout.list_view, null);
        }
    }

    @Override
    public void onBackStackChanged() {
        final Fragment fragment = getFragmentById(R.id.screen_container);
        if (fragment != null) {
            final String tag = fragment.getTag();
            if (getString(R.string.main_side_menu_my_patients).equals(tag)) {
                highlightNavigationItem(mDoctors);
            } else if (getString(R.string.main_side_menu_messages).equals(tag)) {
                highlightNavigationItem(mHome);
            } else if (getString(R.string.main_side_menu_logout).equals(tag)) {
            } else if (getString(R.string.main_side_menu_settings).equals(tag)) {
                highlightNavigationItem(mSettings);
            }
        }
    }
}
