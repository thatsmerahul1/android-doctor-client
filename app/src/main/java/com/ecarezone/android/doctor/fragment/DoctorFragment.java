package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
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
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.fragment.dialog.AddDoctorRequestDialog;
import com.ecarezone.android.doctor.model.rest.AddDoctorRequest;
import com.ecarezone.android.doctor.model.rest.AddDoctorResponse;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.utils.PermissionUtil;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
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
    private Button buttonAppointment;
    private Bundle doctorDetailData;
    private Long doctorId;
    private String doctorName;
    private Patient patient;

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
//        buttonAppointment = (Button) view.findViewById(R.id.button_appointment);

        doctorChat.setOnClickListener(this);
        doctorVideo.setOnClickListener(this);
        doctorVoice.setOnClickListener(this);
//        buttonAppointment.setOnClickListener(this);

        if (getActivity().getIntent().getBooleanExtra(MyPatientListFragment.ADD_DOCTOR_DISABLE_CHECK, false)) {
//            addDoctorButton.setVisibility(View.GONE);
        }

        if (patient != null) {
            setDoctorPresenceIcon(patient.status);
            if(patient.status.equalsIgnoreCase("0")) {
                doctorStatusText.setText(R.string.doctor_busy);
            } else if(patient.status.equalsIgnoreCase("1")) {
                doctorStatusText.setText(R.string.doctor_available);
            } else{
                doctorStatusText.setText(R.string.doctor_idle);
            }
            doctorNameView.setText(patient.name);
        }
        doctorId = patient.userId;
        doctorName = patient.name;

        String imageUrl = patient.avatarUrl;

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
        if (status.equalsIgnoreCase("1")) {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_green));
            doctorVideo.setEnabled(true);
            doctorVoice.setEnabled(true);
        } else if (status.equalsIgnoreCase("0")) {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_red));
            doctorVideo.setEnabled(false);
            doctorVoice.setEnabled(false);
        } else {
            doctorStatusIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.circle_amber));
            doctorVideo.setEnabled(true);
            doctorVoice.setEnabled(true);
        }
    }

    /*
        Sending Add docotor request
     */
    private void sendAddDoctorRequest() {
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), "Adding Doctor......");
        AddDoctorRequest request =
                new AddDoctorRequest(doctorId, doctorName, LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique);
        getSpiceManager().execute(request, new AddDoctorTaskRequestListener());
    }

    /*
            Add Doctor request response
     */
    public final class AddDoctorTaskRequestListener implements RequestListener<AddDoctorResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(AddDoctorResponse addDoctorResponse) {
            Log.d(TAG, "AddDoctorResponse Status " + addDoctorResponse.status.code);
            progressDialog.dismiss();
            if (addDoctorResponse.status.code.equals(HTTP_STATUS_OK)) {
                AddDoctorRequestDialog addDoctorRequestDialog = new AddDoctorRequestDialog(doctorName);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                addDoctorRequestDialog.show(fragmentManager, "AddDoctorRequestSuccessFragment");
            } else {
                Log.d(TAG, "AddDoctorResponse Status " + addDoctorResponse.status.message);
            }
        }
    }
}
