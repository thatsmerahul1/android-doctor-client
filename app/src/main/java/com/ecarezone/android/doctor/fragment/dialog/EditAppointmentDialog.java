package com.ecarezone.android.doctor.fragment.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.fragment.AppointmentFragment;
import com.ecarezone.android.doctor.model.pojo.AppointmentListItem;
import com.ecarezone.android.doctor.view.CircleImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by 10603675 on 23-06-2016.
 */
public class EditAppointmentDialog extends DialogFragment implements View.OnClickListener {
    private String doctorName;
    private String callType;
    private String dateTime;
    private boolean isTimeToCall;

    private TextView textViewPatientName;
    private TextView textViewTypeOfCall;
    private TextView textViewTime;
    private CircleImageView imgViewProfilePic;

    private static AppointmentFragment.OnAppointmentOptionButtonClickListener mOptionButtonClickListener;

    private static AppointmentListItem mSelectdAppointment;

    public EditAppointmentDialog() {
    }

    /**
     * Create a new instance of EditAppointmentDialog, providing "num"
     * as an argument.
     */
    public static EditAppointmentDialog newInstance(
            AppointmentFragment.OnAppointmentOptionButtonClickListener optionButtonClickListener,
            AppointmentListItem selectdAppointment) {

        mOptionButtonClickListener = optionButtonClickListener;
        mSelectdAppointment = selectdAppointment;
        EditAppointmentDialog fragment = new EditAppointmentDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(350, 350);

        final View view = inflater.inflate(R.layout.edit_appointment_dialog_layout, container, false);

        textViewPatientName = (TextView) view.findViewById(R.id.textViewPatientName);
        textViewTypeOfCall = (TextView) view.findViewById(R.id.textViewTypeOfCall);
        textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        imgViewProfilePic = (CircleImageView) view.findViewById(R.id.profilePic);

//        Button btnTimeToCall = (Button) view.findViewById(R.id.buttonTimeToCall);
//        btnTimeToCall.setOnClickListener(this);

        Button btnChangeTime = (Button) view.findViewById(R.id.buttonChangeTime);
        btnChangeTime.setOnClickListener(this);

        Button btnCancel = (Button) view.findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(this);


        textViewPatientName.setText(mSelectdAppointment.patientName);
        textViewTypeOfCall.setText("(" + mSelectdAppointment.callType + ")");
        Picasso.with(getActivity())
                .load(mSelectdAppointment.profilePicUrl).resize(100, 100)
                .centerCrop().placeholder(R.drawable.news_other)
                .error(R.drawable.news_other)
                .into(imgViewProfilePic);
        textViewTime.setText(dateTime);

        if (isTimeToCall) {
//            btnTimeToCall.setEnabled(true);
            btnCancel.setEnabled(true);

            btnChangeTime.setEnabled(false);
//            btnChangeTime.setBackgroundResource(R.drawable.circle_gray_complete);

            btnCancel.setEnabled(false);
//            btnCancel.setBackgroundResource(R.drawable.circle_gray_complete);

//            btnTimeToCall.setOnClickListener(this);
        } else {

//            btnTimeToCall.setEnabled(false);
//            btnTimeToCall.setBackgroundResource(R.drawable.circle_gray_complete);

            btnCancel.setEnabled(true);

            btnChangeTime.setEnabled(true);
            btnCancel.setEnabled(true);
        }


        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

//            case R.id.buttonTimeToCall:
//                mOptionButtonClickListener.onButtonClicked(
//                        AppointmentFragment.OnAppointmentOptionButtonClickListener.BTN_TIME_TO_CALL, mSelectdAppointment);
//                break;

            case R.id.buttonChangeTime:
//                if (!isAppointmentAvailable) {
//                    mOptionButtonClickListener.onButtonClicked(
//                            AppointmentFragment.OnAppointmentOptionButtonClickListener.BTN_MAKE_AN_APPOINTMENT, typeOfCall);
//                } else {
                    mOptionButtonClickListener.onButtonClicked(
                            AppointmentFragment.OnAppointmentOptionButtonClickListener.BTN_CHANGE_TIME, mSelectdAppointment);
//                }
                break;

            case R.id.buttonCancel:
                mOptionButtonClickListener.onButtonClicked(
                        AppointmentFragment.OnAppointmentOptionButtonClickListener.BTN_CANCEL, mSelectdAppointment);
                break;

        }
        dismiss();

    }
}