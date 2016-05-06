package com.ecarezone.android.doctor.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;

/**
 * Created by L&T Technology Services on 22-02-2016.
 */
public class MyPatientBioFragment extends EcareZoneBaseFragment {

    private static final String TAG = MyPatientBioFragment.class.getSimpleName();
    private Bundle doctorBioData;
    private TextView doctorDescriptionView;
    private TextView doctorBioNameView;
    private TextView doctorBioCategoryView;

    @Override
    protected String getCallerName() {
        return MyPatientBioFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.frag_doctor_bio, container, false);

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
