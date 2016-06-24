package com.ecarezone.android.doctor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.fragment.MyPatientListFragment;
import com.ecarezone.android.doctor.model.PatientProfile;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.pojo.PatientUserProfileListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 20109804 on 6/23/2016.
 */
public class PatientProfileAdapter extends BaseAdapter {
    private final static String TAG = PatientProfileAdapter.class.getSimpleName();
    private Activity activity;
    private ArrayList<PatientUserProfileListItem> patientList;
    private static LayoutInflater inflater;
    private boolean fromMessage;



    public PatientProfileAdapter(Activity activity, ArrayList<PatientUserProfileListItem> patientList) {
        this.activity = activity;
        this.patientList = patientList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PatientUserProfileListItem patient = patientList.get(position);
        view = inflater.inflate(R.layout.patient_bio_item, null, false);

        TextView patientProfileName = (TextView)view.findViewById(R.id.editText_profile_name);
        TextView patientHightName = (TextView)view.findViewById(R.id.editText_hight);
        TextView patientWeightName = (TextView)view.findViewById(R.id.editText_weight);
        TextView patientAddressName = (TextView)view.findViewById(R.id.editText_address);
        TextView patientEthnicityName = (TextView)view.findViewById(R.id.editText_ethnicity);
        TextView patientGenderName = (TextView)view.findViewById(R.id.editText_gender);
        TextView patientBirthDateName = (TextView)view.findViewById(R.id.editText_birthdate);

        patientProfileName.setText(patient.profileName);
        patientHightName.setText(patient.height);
        patientWeightName.setText(patient.weight);
        patientAddressName.setText(patient.address);
        patientEthnicityName.setText(patient.ethnicity);
        patientGenderName.setText(patient.gender);
        patientBirthDateName.setText(patient.birthdate);
        return view;
    }
}

