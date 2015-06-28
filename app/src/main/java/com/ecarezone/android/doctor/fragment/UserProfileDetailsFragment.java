package com.ecarezone.android.doctor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecarezone.android.doctor.R;

public class UserProfileDetailsFragment extends EcareZoneBaseFragment {

    @Override
    protected String getCallerName() {
        return UserProfileDetailsFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_user_profle_details, container, false);
        return view;
    }
}
