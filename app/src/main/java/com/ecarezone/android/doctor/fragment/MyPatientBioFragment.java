package com.ecarezone.android.doctor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.PatientProfileAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.model.PatientProfile;
import com.ecarezone.android.doctor.model.database.PatientUserProfileDbiApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by L&T Technology Services on 22-02-2016.
 */
public class MyPatientBioFragment extends EcareZoneBaseFragment {

    private static final String TAG = MyPatientBioFragment.class.getSimpleName();
    private Bundle patientBioData;
    private TextView doctorBioNameView;
    private ImageView doctorBioImage;
    ListView profileList;
    PatientProfileAdapter patientProfileAdapter;
    private ArrayList<PatientProfile> patientLists = new ArrayList<PatientProfile>();

    @Override
    protected String getCallerName() {
        return MyPatientBioFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.frag_doctor_bio, container, false);
        profileList = (ListView)view.findViewById(R.id.patient_profile);

        patientBioData = getArguments();
        Patient patient  = patientBioData.getParcelable(Constants.DOCTOR_DETAIL);
        Log.i(TAG, "doctor in BIO = " + patient);
//        doctorDescriptionView = (TextView) view.findViewById(R.id.doctor_description);
        doctorBioNameView = (TextView) view.findViewById(R.id.doctor_bio_name_id);
        doctorBioImage = (ImageView)view.findViewById(R.id.doctor_bio_profile_pic_id);
        doctorBioNameView.setText(patient.name);

        patientLists = (ArrayList<PatientProfile>) patient.userProfiles;
        patientProfileAdapter = new PatientProfileAdapter(getActivity(), patientLists);
        profileList.setAdapter(patientProfileAdapter);

//        PatientUserProfileDbiApi userProfileDbApi = PatientUserProfileDbiApi.getInstance(getApplicationContext());
//
//        userProfileDbApi.saveProfile(patientLists);
        String imageUrl = patient.avatarUrl;

        if (imageUrl != null && imageUrl.trim().length() > 8) {
            int dp = getActivity().getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);;
            Picasso.with(getContext())
                    .load(imageUrl).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(doctorBioImage);
        }
//        doctorBioCategoryView.setText(patient.doctorCategory);
//        doctorDescriptionView.setText(patient.doctorDescription);
//        doctorBioData = getArguments();
//        Doctor doctor = doctorBioData.getParcelable(Constants.DOCTOR_DETAIL);
//        Log.i(TAG, "doctor in BIO = " + doctor);
//        doctorDescriptionView = (TextView) view.findViewById(R.id.doctor_description);
//        doctorBioNameView = (TextView) view.findViewById(R.id.doctor_bio_name_id);
//        doctorBioCategoryView = (TextView) view.findViewById(R.id.doctor_bio_specialist_id);
//        doctorBioNameView.setText(doctor.name);

        return view;
    }
}
