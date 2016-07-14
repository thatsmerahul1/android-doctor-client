package com.ecarezone.android.doctor.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ecarezone.android.doctor.config.Constants;


/**
 * Created by 10603675 on 27-06-2016.
 */
public class HeartBeatReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent heartBeatService = new Intent(context, HeartbeatService.class);
        heartBeatService.putExtra(Constants.SEND_HEART_BEAT, true);
        heartBeatService.putExtra(Constants.UPDATE_STATUS, true);
        context.startService(heartBeatService);
    }
}
