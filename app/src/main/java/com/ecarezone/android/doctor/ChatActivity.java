package com.ecarezone.android.doctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ecarezone.android.doctor.adapter.ChatAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.fragment.ChatFragment;
import com.ecarezone.android.doctor.utils.SinchUtil;
import com.ecarezone.android.doctor.utils.Util;

/**
 * Created by L&T Technology Services.
 */
public class ChatActivity extends EcareZoneBaseActivity {

    private ActionBar mActionBar = null;
    private Toolbar mToolBar = null;
    private ChatFragment chatFragment;

    @Override
    protected String getCallerName() {
        return ChatActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        chatFragment = new ChatFragment();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);
        Bundle data = getIntent().getExtras();
        onNavigationChanged(R.layout.frag_chat, ((data == null) ? null : data));
        mToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_menu);
        }
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        addSupportOnBackStackChangedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected() {
        SinchUtil.getSinchServiceInterface().addMessageClientListener(chatFragment);
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {
        if (fragmentLayoutResId < 0) return;

        if (fragmentLayoutResId == R.layout.frag_chat) {
            changeFragment(R.id.screen_container, chatFragment,
                    ChatFragment.class.getSimpleName(), args);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SinchUtil.getSinchServiceInterface().removeMessageClientListener(chatFragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getBundleExtra(Constants.EXTRA_EMAIL);
        onNavigationChanged(R.layout.frag_chat, ((extras == null) ? null : extras));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        chatFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        chatFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Util.changeStatus(true,this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.changeStatus(false, this);
    }
}
