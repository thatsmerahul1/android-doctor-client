package com.ecarezone.android.doctor.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;

import java.util.ArrayList;

/**
 * Created by 20109804 on 5/9/2016.
 */
public class AppointmentAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<PatientListItem> patientList;
    private static LayoutInflater inflater;


    public AppointmentAdapter(){

    }
    @Override
    public int getCount() {
        return patientList.size();
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
        PatientListItem patient = patientList.get(position);
//        if (view == null) {
//            holder = new ViewHolder();
        if (patient.isPending) {

            view = inflater.inflate(R.layout.appointment_pending_list_item, null, false);
//              holder.pendingPatientAvatar.setImageURI(patient.a);
        } else {
            view = inflater.inflate(R.layout.appointment_accepted_list_item, null, false);
        }
//            view.setTag(holder);
//        } else {
//            holder = (ViewHolder) view.getTag();
//        }

        if (patient.isPending) {
            ImageView pendingPatientAvatar = (ImageView) view.findViewById(R.id.appointment_patient_avatar_of_request);
            TextView appointmentPendingTime = (TextView) view.findViewById(R.id.appointment_time);
            TextView appointmentTypeOfCall = (TextView) view.findViewById(R.id.type_of_call);
            TextView appointmentPatientName = (TextView) view.findViewById(R.id.patient_name);

            Button accept = (Button) view.findViewById(R.id.patient_appointment_request_accept);
            Button reject = (Button) view.findViewById(R.id.patient_appointment_request_reject);
            //TODO:
//            accept.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnButtonClickedListener.onButtonClickedListener(position, true);
//                }
//            });
//
//            reject.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnButtonClickedListener.onButtonClickedListener(position, false);
//                }
//            });

//            appointmentPatientName.setText(patient.name);
//            appointmentPendingTime.setText(patient.);
//            appointmentTypeOfCall.setText(patient.name);
//            pendingPatientAvatar.setImageResource(R.drawable.request_icon);

        } else {
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
