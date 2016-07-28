package com.ecarezone.android.doctor.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.database.PatientProfileDbApi;
import com.ecarezone.android.doctor.model.pojo.AppointmentListItem;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.utils.Util;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 20109804 on 5/9/2016.
 */
public class AppointmentAdapter extends BaseAdapter {
    private Activity activity;
    private static LayoutInflater inflater;
    private Context mContext;
    private List<AppointmentListItem> mAppointmentList;
    private OnButtonClickedListener mOnButtonClickedListener;
    private SimpleDateFormat startTime;
    PatientProfileDbApi parentPatientProfileDbApi;


    public AppointmentAdapter(Context context, List<AppointmentListItem> appointmentList,
                              OnButtonClickedListener onButtonClickedListener){
        mContext = context;
        this.mAppointmentList = appointmentList;
        mOnButtonClickedListener = onButtonClickedListener;
        startTime = new SimpleDateFormat("hh:mm ");
        parentPatientProfileDbApi = PatientProfileDbApi.getInstance(context);
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mAppointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        AppointmentListItem appointment = mAppointmentList.get(position);
//        if (view == null) {
//            holder = new ViewHolder();
        if (! appointment.isConfirmed) {

            view = inflater.inflate(R.layout.appointment_pending_list_item, null, false);
//              holder.pendingPatientAvatar.setImageURI(patient.a);
        } else {
            view = inflater.inflate(R.layout.appointment_accepted_list_item, null, false);
        }

        ImageView pendingPatientAvatar = (ImageView) view.findViewById(R.id.appointment_patient_avatar_of_request);
        Patient patient = parentPatientProfileDbApi.getProfile(String.valueOf(appointment.patientId));
        if(patient != null && patient.avatarUrl != null){
            Picasso.with(mContext)
                    .load(patient.avatarUrl)
                    .config(Bitmap.Config.RGB_565).fit()
                    .centerCrop()
                    .into(pendingPatientAvatar);
        }

        if (! appointment.isConfirmed) {
            TextView appointmentPendingTime = (TextView) view.findViewById(R.id.appointment_time);
            TextView appointmentTypeOfCall = (TextView) view.findViewById(R.id.type_of_call);
            TextView appointmentPatientName = (TextView) view.findViewById(R.id.patient_name);

            Button accept = (Button) view.findViewById(R.id.patient_appointment_request_accept);
            accept.setTag(position);
            Button reject = (Button) view.findViewById(R.id.patient_appointment_request_reject);
            reject.setTag(position);

            //TODO:
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnButtonClickedListener.onButtonClickedListener((Integer)v.getTag(), true);
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnButtonClickedListener.onButtonClickedListener((Integer)v.getTag(), false);
                }
            });

            if(patient != null) {
                appointmentPatientName.setText("Patient: "+WordUtils.capitalize(patient.name));
            }

            long forEndTime = Long.parseLong(appointment.dateTime) + ( 30 * 60 * 1000);
            appointmentPendingTime.setText(Util.getTimeInStringFormat(Long.parseLong(appointment.dateTime), startTime) +
                    " - " + Util.getTimeInStringFormat((forEndTime), startTime));

            appointmentTypeOfCall.setText(WordUtils.capitalize(appointment.callType) + " call");
            if(patient != null && patient.avatarUrl != null){
                Picasso.with(mContext)
                        .load(patient.avatarUrl)
                        .config(Bitmap.Config.RGB_565).fit()
                        .centerCrop()
                        .into(pendingPatientAvatar);
            }
        }
        else {
            TextView appointmentTime = (TextView) view.findViewById(R.id.appointment_accepted_time);
            TextView appointedPatientName = (TextView) view.findViewById(R.id.accepted_patient_name);
            TextView modeOfAppointment = (TextView) view.findViewById(R.id.accepted_type_of_call);
            View patientPresence = view.findViewById(R.id.patient_presence);

            if(patient != null) {
                appointedPatientName.setText("Patient: "+ WordUtils.capitalize(patient.name));
            }
            modeOfAppointment.setText(WordUtils.capitalize(appointment.callType) + " call");
            long forEndTime = Long.parseLong(appointment.dateTime) + ( 30 * 60 * 1000);
            appointmentTime.setText(Util.getTimeInStringFormat(Long.parseLong(appointment.dateTime), startTime) +
                    " - " + Util.getTimeInStringFormat((forEndTime), startTime ));


           //TODO:
        }
        return view;
    }
}
