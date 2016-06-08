package com.ecarezone.android.doctor.adapter;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;
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


    public AppointmentAdapter(Context context, List<AppointmentListItem> appointmentList,
                              OnButtonClickedListener onButtonClickedListener){
        mContext = context;
        this.mAppointmentList = appointmentList;
        mOnButtonClickedListener = onButtonClickedListener;
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


        if (! appointment.isConfirmed) {
            ImageView pendingPatientAvatar = (ImageView) view.findViewById(R.id.appointment_patient_avatar_of_request);
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
            PatientProfileDbApi db = PatientProfileDbApi.getInstance(mContext);
            Patient patient = db.getProfileByProfileId(String.valueOf(appointment.patientId));
            if(patient != null) {
                appointmentPatientName.setText("Patient: "+patient.name);
            }
            appointmentPendingTime.setText(appointment.dateTime);
            appointmentTypeOfCall.setText(appointment.callType + " call");
            pendingPatientAvatar.setImageResource(R.drawable.request_icon);

        }
        else {
            ImageView patientAvatar = (ImageView) view.findViewById(R.id.appointment_patient_avatar_of_request);
            TextView appointmentTime = (TextView) view.findViewById(R.id.appointment_accepted_time);
            TextView appointedPatientName = (TextView) view.findViewById(R.id.accepted_patient_name);
            TextView modeOfAppointment = (TextView) view.findViewById(R.id.accepted_type_of_call);
            View patientPresence = view.findViewById(R.id.patient_presence);

           //TODO:
        }
        return view;
    }
}
