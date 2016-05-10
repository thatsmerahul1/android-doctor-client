package com.ecarezone.android.doctor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ecarezone.android.doctor.adapter.ChatAdapter;
import com.ecarezone.android.doctor.fragment.ChatFragment;
import com.ecarezone.android.doctor.utils.SinchUtil;

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

    /*@Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        finish();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SinchUtil.getSinchServiceInterface().removeMessageClientListener(chatFragment);
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
    public void onResume() {
        super.onResume();
        getBaseContext().registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        getBaseContext().unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ChatAdapter chatAdapter = new ChatAdapter(context);


            // Extract data included in the Intent
            String message = intent.getStringExtra("recipient");
            chatAdapter.getChatHistory(message);
            //do other stuff here
        }
    };

}
