package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.ProfileDetailsActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.database.ProfileDbApi;

/**
 * Created by CHAO WEI on 5/1/2015.
 */
public class WelcomeFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    @Override
    protected String getCallerName() {
        return WelcomeFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_welcome, container, false);
        view.findViewById(R.id.button_welcome_not_now).setOnClickListener(this);
        view.findViewById(R.id.button_welcome_ok).setOnClickListener(this);
        LinearLayout welcom_layout = (LinearLayout) view.findViewById(R.id.welcome_page);
        TextView noMessage = (TextView)view.findViewById(R.id.nomessage);

        ProfileDbApi profileDbApi = new ProfileDbApi(getApplicationContext());
        boolean hasProfiles = profileDbApi.hasProfile(LoginInfo.userId.toString());
        if(hasProfiles){
            noMessage.setVisibility(View.VISIBLE);
            welcom_layout.setVisibility(View.GONE);
        } else {
            noMessage.setVisibility(View.GONE);
            welcom_layout.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        final int viewId = v.getId();
        if (viewId == R.id.button_welcome_not_now) {
            // open side menu
            Activity act = getActivity();
            if ((act != null) && (act instanceof MainActivity)) {
                ((MainActivity) act).toggleDrawer(true);
            }
        } else if (viewId == R.id.button_welcome_ok) {
            // open profile
            Intent intent = new Intent(getContext(), ProfileDetailsActivity.class);
            startActivity(intent);
        }
    }
}