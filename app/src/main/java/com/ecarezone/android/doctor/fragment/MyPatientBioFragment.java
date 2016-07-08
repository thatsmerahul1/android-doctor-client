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
import com.ecarezone.android.doctor.model.pojo.PatientUserProfileListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.ListIterator;

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
    private ArrayList<PatientUserProfileListItem> patientProfileLists = new ArrayList<PatientUserProfileListItem>();

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
        doctorBioNameView = (TextView) view.findViewById(R.id.doctor_bio_name_id);
        doctorBioImage = (ImageView)view.findViewById(R.id.doctor_bio_profile_pic_id);
        doctorBioNameView.setText(patient.name);

        patientLists = (ArrayList<PatientProfile>) patient.userProfiles;


        ListIterator<PatientProfile> iter = patientLists.listIterator();
        PatientProfile patientProfile = null;
        PatientUserProfileDbiApi userProfileDbApi = PatientUserProfileDbiApi.getInstance(getApplicationContext());
        while(iter.hasNext()) {
            patientProfile = iter.next();
            PatientUserProfileListItem patientItem = new PatientUserProfileListItem();
            patientItem.listItemType = PatientListItem.LIST_ITEM_TYPE_PENDING;
            patientItem.email = patientProfile.email;
            patientItem.name = patientProfile.name;
            patientItem.address = patientProfile.address;
            patientItem.birthdate = patientProfile.birthdate;
            patientItem.ethnicity = patientProfile.ethnicity;
            patientItem.height = patientProfile.height;
            patientItem.weight = patientProfile.weight;
            patientItem.profileId = patientProfile.profileId;
            patientItem.userId = patientProfile.userId;
            patientItem.avatarUrl = patientProfile.avatarUrl;
            patientItem.gender = patientProfile.gender;
            patientItem.profileName =patientProfile.profileName;
            patientProfileLists.add(patientItem);

            PatientProfile id = userProfileDbApi.getProfileByProfileId(patientProfile.profileId, String.valueOf(patientProfile.userId));

            if(id == null ||  !patientProfile.profileId.equalsIgnoreCase(id.profileId) ) {
                userProfileDbApi.saveProfile(patientProfile);
            } else{
                userProfileDbApi.updateProfile(String.valueOf(patientProfile.profileId), patientProfile);
            }

            patientProfileAdapter = new PatientProfileAdapter(getActivity(), patientProfileLists);
            profileList.setAdapter(patientProfileAdapter);
        }

//        patientProfileAdapter.notifyDataSetChanged();

        //get(0) because in response patient profile pic is inside userprofile
        String imageUrl = patient.userProfiles.get(0).avatarUrl;

        if (imageUrl != null && imageUrl.trim().length() > 8) {
            int dp = getActivity().getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);;
            Picasso.with(getContext())
                    .load(imageUrl).resize(dp, dp)
                    .centerCrop().placeholder(R.drawable.news_other)
                    .error(R.drawable.news_other)
                    .into(doctorBioImage);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        patientProfileAdapter.notifyDataSetChanged();
    }
}
