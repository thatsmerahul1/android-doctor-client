package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.doctor.AppointmentActivity;
import com.ecarezone.android.doctor.CallActivity;
import com.ecarezone.android.doctor.ChatActivity;
import com.ecarezone.android.doctor.NetworkCheck;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.VideoActivity;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.utils.PermissionUtil;
import com.squareup.picasso.Picasso;


/**
 * Created by CHAO WEI on 6/1/2015.
 */
public class DoctorFragment extends EcareZoneBaseFragment implements View.OnClickListener {

    private static final String TAG = DoctorFragment.class.getSimpleName();
    private static final Integer HTTP_STATUS_OK = 200;
    private ImageView doctorStatusIcon;
    private TextView doctorStatusText;
    private TextView doctorNameView;
    private ImageView doctorProfileImg;
    private Button doctorChat;
    private Button doctorVideo;
    private Button doctorVoice;
    private Bundle doctorDetailData;
    private Long doctorId;
    private String doctorName;
    private Patient patient;
    private TextView unreadChatCount;

    private Activity mActivity;
    private int viewId;
    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    protected String getCallerName() {
        return DoctorFragment.class.getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_doctor, container, false);

        doctorDetailData = getArguments();
        patient = doctorDetailData.getParcelable(Constants.DOCTOR_DETAIL);
        IntentFilter intentFilter = new IntentFilter("send");
        intentFilter.addAction(Constants.BROADCAST_STATUS_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
        getAllComponent(view, patient);
        return view;
    }

    private void getAllComponent(View view, Patient patient) {
        doctorStatusIcon = (ImageView) view.findViewById(R.id.doctor_status_icon);
        doctorStatusText = (TextView) view.findViewById(R.id.doctor_status_text);
        doctorNameView = (TextView) view.findViewById(R.id.doctor_name_id);
        doctorProfileImg = (ImageView) view.findViewById(R.id.doctor_profile_pic_id);
        doctorChat = (Button) view.findViewById(R.id.btn_doctor_chat_id);
        doctorVideo = (Button) view.findViewById(R.id.btn_doctor_video_id);
        doctorVoice = (Button) view.findViewById(R.id.btn_doctor_voice_id);

        unreadChatCount = (TextView) view.findViewById(R.id.chat_count);

        doctorChat.setOnClickListener(this);
        doctorVideo.setOnClickListener(this);
        doctorVoice.setOnClickListener(this);

        if (getActivity().getIntent().getBooleanExtra(MyPatientListFragment.ADD_DOCTOR_DISABLE_CHECK, false)) {
//            addDoctorButton.setVisibility(View.GONE);
        }

        if (patient != null) {
            setDoctorPresenceIcon(patient.status);
            if (patient.status.equalsIgnoreCase(String.valueOf(Constants.OFFLINE))) {
                doctorStatusText.setText(R.string.doctor_busy);
            } else if (patient.status.equalsIgnoreCase(String.valueOf(Constants.ONLINE))) {
                doctorStatusText.setText(R.string.doctor_available);
            } else {
                doctorStatusText.setText(R.string.doctor_idle);
            }
            doctorNameView.setText(patient.name);
        }
        doctorId = patient.userId;
        doctorName = patient.name;
        //get(0) because in response patient profile pic is inside userprofile
        String imageUrl = patient.userProfiles.get(0).avatarUrl;

        if (imageUrl != null && imageUrl.trim().length() > 8) {
            int dp = mActivity.getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);;
            Picasso.with(mActivity)
                    .load(imageUrl).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(doctorProfileImg);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == null) return;

        viewId = v.getId();
        if(NetworkCheck.isNetworkAvailable(mActivity)) {
            switch (viewId) {
                case R.id.btn_doctor_chat_id:
                    chatButtonClicked();
                    break;
                case R.id.btn_doctor_video_id:
                    callVideoButtonClicked();
                    break;
                case R.id.btn_doctor_voice_id:
                    callButtonClicked();

                    break;
            /*case R.id.button_appointment:
                createAppointment();
                break;*/
            /*case R.id.add_doctor_button:
                sendAddDoctorRequest();
                break;*/
            }

        } else {
            Toast.makeText(mActivity, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void callVideoButtonClicked() {
        if (PermissionUtil.isPermissionRequired() && PermissionUtil.getAllpermissionRequired(mActivity, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
            PermissionUtil.setAllPermission(mActivity, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, PermissionUtil.SINCH_PERMISSIONS);
        } else {
            Intent videoScreen = new Intent(mActivity, VideoActivity.class);
            videoScreen.putExtra(Constants.EXTRA_NAME, patient.name);
            videoScreen.putExtra(Constants.EXTRA_EMAIL, patient.email);
            startActivity(videoScreen);
        }
    }

    private void callButtonClicked() {
        if (PermissionUtil.isPermissionRequired() && PermissionUtil.getAllpermissionRequired(mActivity, PermissionUtil.SINCH_PERMISSIONS).length > 0) {
            PermissionUtil.setAllPermission(mActivity, PermissionUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS, PermissionUtil.SINCH_PERMISSIONS);
        } else {
            Intent callScreen = new Intent(mActivity, CallActivity.class);
            callScreen.putExtra(Constants.EXTRA_NAME, patient.name);
            callScreen.putExtra(Constants.EXTRA_EMAIL, patient.email);
            startActivity(callScreen);
        }
    }

    private void chatButtonClicked() {
        Intent chatIntent = new Intent(mActivity.getApplicationContext(), ChatActivity.class);
        chatIntent.putExtra(Constants.EXTRA_NAME, patient.name);
        chatIntent.putExtra(Constants.EXTRA_EMAIL, patient.email);
        mActivity.startActivity(chatIntent);
        mActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private void createAppointment() {
        mActivity.startActivity(new Intent(mActivity.getApplicationContext(), AppointmentActivity.class));
        mActivity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    /*
        Doctor Status status updating
     */
    private void setDoctorPresenceIcon(String status) {
        if (status.equalsIgnoreCase(String.valueOf(Constants.ONLINE))) {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_green));
            doctorVideo.setEnabled(true);
            doctorVoice.setEnabled(true);
        } else if (status.equalsIgnoreCase(String.valueOf(Constants.OFFLINE))) {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_red));
            doctorVideo.setEnabled(false);
            doctorVoice.setEnabled(false);
        } else {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_amber));
            doctorVideo.setEnabled(true);
            doctorVoice.setEnabled(true);
        }
    }

    /**
     * updates the unread message count
     */
    public void updateChatCount() {

        if(unreadChatCount != null && patient != null){
            ChatDbApi chatDbApi = ChatDbApi.getInstance(getApplicationContext());
            int unreadCount = chatDbApi.getUnReadChatCountByUserId(patient.email);
            if(unreadCount > 0) {
                unreadChatCount.setText(String.valueOf(unreadCount));
                unreadChatCount.setVisibility(View.VISIBLE);
            }
            else{
                unreadChatCount.setText(String.valueOf(0));
                unreadChatCount.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        }
    }

    /* BroadcastReceiver receiver that updates the chat count or
    * changes the availability status */
     BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("send")) {
                updateChatCount();
            }
            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_STATUS_CHANGED)) {
                String statusTxt = intent.getStringExtra(Constants.SET_STATUS);
                if (statusTxt != null) {
                    String[] statusArr = statusTxt.split(",");
                    if (statusArr.length > 2) {
                        int patId = -1;
                        try {
                            patId = Integer.parseInt(statusArr[1].trim());
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                        }
                        if (patId > -1) {

                            if (patient.userId == patId) {
                                 patient.status = statusArr[2];
                                 setDoctorPresenceIcon(statusTxt);
                            }
                        }
                    }

                }
            }
        }
    };
}
