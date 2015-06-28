package com.ecarezone.android.doctor;

import android.content.res.Configuration;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;

import android.view.MenuItem;
import android.view.View;

import com.ecarezone.android.doctor.fragment.SettingsFragment;
import com.ecarezone.android.doctor.fragment.UserProfileFragment;

public class MainActivity extends EcareZoneBaseActivity {

    private DrawerLayout mDrawerLayout = null;
    private ActionBarDrawerToggle mDrawerToggle = null;
    private Toolbar mToolBar = null;
    private ActionBar mActionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        if(mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.side_drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                    syncState();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                    syncState();
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerLayout.setFitsSystemWindows(true);
            mDrawerToggle.syncState();
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        }

        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    public void toggleDrawer(boolean open) {
        if(mDrawerLayout != null) {
            if(open) {
                mDrawerLayout.openDrawer(Gravity.START | Gravity.LEFT);
            } else {
                mDrawerLayout.closeDrawers();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((mDrawerToggle != null) && (mDrawerToggle.onOptionsItemSelected(item))) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
                return;
            }
        }
         super.onBackPressed();
    }

    @Override
    protected String getCallerName() {
        return MainActivity.class.getSimpleName();
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if(fragmentLayoutResId < 0) return;

        if(fragmentLayoutResId == R.layout.frag_settings) {
            changeFragment(R.id.screen_container, new SettingsFragment(),
                    getString(R.string.main_side_menu_settings), args);
        }

        if(mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

}
