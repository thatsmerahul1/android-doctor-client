package com.ecarezone.android.doctor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class UserProfileFragment extends EcareZoneBaseFragment {

    @Override
    protected String getCallerName() {
        return UserProfileFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container,savedInstanceState);
    }

}
