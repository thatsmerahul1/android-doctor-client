package com.ecarezone.android.doctor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.app.widget.NavigationItem;

public class SideNavigationFragment extends EcareZoneBaseFragment implements NavigationItem.OnNavigationItemClickListener,
                                                                            View.OnClickListener,
                                                                            FragmentManager.OnBackStackChangedListener {

    @Override
    protected String getCallerName() {
        return SideNavigationFragment.class.getSimpleName();
    }

    private NavigationItem mHome = null;
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
        mHome =  (NavigationItem) view.findViewById(R.id.navigation_home);
        mHome.setOnNavigationItemClickListener(this);
        mSettings =  (NavigationItem) view.findViewById(R.id.navigation_settings);
        mSettings.setOnNavigationItemClickListener(this);
        mLogout =  (NavigationItem) view.findViewById(R.id.navigation_logout);
        mLogout.setOnNavigationItemClickListener(this);
        mHome.highlightItem(true);
        highlightNavigationItem(null);
        return view;
    }

    @Override
    public void onItemClick(View v) {
        final String tag = String.valueOf(v.getTag());
        int layoutResId = 0;
        if(!TextUtils.isEmpty(tag)) {
            Bundle b = null;
            if(getString(R.string.main_side_menu_home).equals(tag)) {
                // TODO
            } else if(getString(R.string.main_side_menu_logout).equals(tag)) {
                // TODO
            }  else if(getString(R.string.main_side_menu_settings).equals(tag)) {
                layoutResId = R.layout.frag_settings;
            }

            if(layoutResId > 0) {
                invokeNavigationChanged(layoutResId, b);
            }
        }
    }

    private void highlightNavigationItem(NavigationItem navigationItem) {
        mHome.highlightItem(false);
        mSettings.highlightItem(false);
        mLogout.highlightItem(false);
        if(navigationItem != null) {
            navigationItem.highlightItem(true);
        }
    }

    @Override
    public void onClick(View v) {
        if( v == null) return;

        final int viewId = v.getId();
        if(viewId == R.id.navigation_user_profile) {

        }
    }

    @Override
    public void onBackStackChanged() {
        final Fragment fragment = getFragmentById(R.id.screen_container);
        if(fragment != null) {
            final String tag = fragment.getTag();
            if(getString(R.string.main_side_menu_home).equals(tag)) {
                highlightNavigationItem(mHome);
            } else if(getString(R.string.main_side_menu_logout).equals(tag)) {
                //highlightNavigationItem(mLogout);
            } else if(getString(R.string.main_side_menu_settings).equals(tag)) {
                highlightNavigationItem(mSettings);
            }
        }
    }
}
