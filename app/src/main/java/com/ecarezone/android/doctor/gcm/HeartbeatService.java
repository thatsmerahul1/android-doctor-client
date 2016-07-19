package com.ecarezone.android.doctor.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ecarezone.android.doctor.DoctorApplication;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.database.ProfileDbApi;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.ecarezone.android.doctor.model.rest.base.ChangeStatusRequest;
import com.ecarezone.android.doctor.service.RoboEcareSpiceServices;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by Umesh on 27-06-2016.
 */
public class HeartbeatService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public HeartbeatService() {
        super("HeartbeatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * sent a heart beat to the GCM to keep the TCP connection alive
         */
        if (intent.getBooleanExtra(Constants.SEND_HEART_BEAT, false)) {
            sendBroadcast(new Intent(
                    "com.google.android.intent.action.GTALK_HEARTBEAT"));
            sendBroadcast(new Intent(
                    "com.google.android.intent.action.MCS_HEARTBEAT"));
            Log.i("HeartbeatService", "Heartbeat sent to GCM");
        }
        if (intent.getBooleanExtra(Constants.UPDATE_STATUS, false)) {
            DoctorApplication doctorApplication = (DoctorApplication) getApplicationContext();
            int status;
            if (doctorApplication.getNameValuePair().containsKey(Constants.STATUS_CHANGE)) {
                if (!doctorApplication.getNameValuePair().get(Constants.STATUS_CHANGE)) {
                    status = Constants.IDLE;
                } else {
                    status = Constants.ONLINE;
                }
                ProfileDbApi profileDbApi = ProfileDbApi.getInstance(getApplicationContext());
                String name = profileDbApi.getMyProfile().name;
                if (doctorApplication.getLastAvailabilityStaus() != status) {

                    ChangeStatusRequest request = new ChangeStatusRequest(
                            LoginInfo.userName, LoginInfo.hashedPassword,
                            name, Constants.USER_ROLE, status, Constants.deviceUnique);
                    getSpiceManager().execute(request, new ChangeStatusRequestListener());
                }
            } else {
                doctorApplication.getNameValuePair().put(Constants.STATUS_CHANGE, false);
                status = Constants.ONLINE;
            }
            doctorApplication.setLastAvailabilityStaus(status);
            Log.i("HeartbeatService", "status updated");
        }
    }

    public final class ChangeStatusRequestListener implements RequestListener<String> {

        private String TAG = "ChangeStatusRequestListener";

        @Override
        public void onRequestFailure(SpiceException spiceException) {
//            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(final String baseResponse) {
            Log.d(TAG, "statuschange " + "changed");

//            DoctorApplication.lastAvailablityStaus = status ;
        }
    }

    private SpiceManager spiceManager = new SpiceManager(RoboEcareSpiceServices.class);

    public SpiceManager getSpiceManager() {
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
        return spiceManager;
    }

}
