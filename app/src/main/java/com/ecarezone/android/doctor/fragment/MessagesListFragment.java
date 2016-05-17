package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.MyPatientActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.MessageAdapter;
import com.ecarezone.android.doctor.adapter.PatientAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by 20109804 on 4/15/2016.
 */
public class MessagesListFragment extends EcareZoneBaseFragment {

    private ProgressDialog progressDialog;
    private boolean checkProgress;
    private ListView messageListView = null;
    View messageContainer;
    private static final int HTTP_STATUS_OK = 200;
    private ArrayList<PatientListItem> patientLists = new ArrayList<PatientListItem>();
    private MessageAdapter mycareDoctorAdapter;
    private ListView myPatientListView = null;
    private static final String TAG = MyPatientListFragment.class.getSimpleName();
    public static final String ADD_DOCTOR_DISABLE_CHECK = "addDocotrDisablecheck";


    @Override
    protected String getCallerName() {
        return MessagesListFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setHasOptionsMenu(true);
        } catch (Exception e) {
        }
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.main_side_menu_messages));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_message_list, container, false);

        messageContainer = view.findViewById(R.id.message_container);
        myPatientListView = (ListView)view.findViewById(R.id.message_list);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_loading).toString());
        populatePendingPatientList();
        populateMyCarePatientList();
    }

    private void populatePendingPatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null, false);
        getSpiceManager().execute(request, new PopulatePendingPatientListRequestListener());
    }

    private void populateMyCarePatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null,true);
        getSpiceManager().execute(request, new PopulateMyCareDoctorListRequestListener());
    }

    public final class PopulatePendingPatientListRequestListener implements RequestListener<SearchDoctorsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse getDoctorsResponse) {
            if (getDoctorsResponse.status.code == HTTP_STATUS_OK) {
                ArrayList<Patient> tempPatient = (ArrayList<Patient>) getDoctorsResponse.data;
                ListIterator<Patient> iter = tempPatient.listIterator();

                while(iter.hasNext()){
                    Patient patient = iter.next();
                    PatientListItem patientItem = new PatientListItem();
                    patientItem.isPending = true;
                    patientItem.email = patient.email;
                    patientItem.name = patient.name;
                    patientItem.recommandedDoctorId = patient.recommandedDoctorId;
                    patientItem.status = patient.status;
                    patientItem.userDevicesCount = patient.userDevicesCount;
                    patientItem.userId = patient.userId;
                    patientItem.avatarUrl = patient.avatarUrl;
                    patientLists.add(patientItem);
                }
                mycareDoctorAdapter = new MessageAdapter(getActivity(), patientLists);
                myPatientListView.setAdapter(mycareDoctorAdapter);
                if (patientLists.size() == 0) {
                    progressDialog.dismiss();
                } else if (patientLists.size() > 0) {

                    myPatientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.i(TAG, "position = " + position);
                            Bundle data = new Bundle();

                            PatientListItem patientListItem = patientLists.get(position);
                            Patient patient = new Patient(patientListItem.userId, patientListItem.email,
                                    patientListItem.name, patientListItem.recommandedDoctorId, patientListItem.status,
                                    patientListItem.isCallAllowed, patientListItem.userDevicesCount,
                                    patientListItem.userSettings, patientListItem.userProfile, patientListItem.avatarUrl);

                            data.putParcelable(Constants.DOCTOR_DETAIL, patient);
                            data.putBoolean(Constants.PATIENT_ALREADY_ADDED, true);
                            final Activity activity = getActivity();
                            if (activity != null) {
                                Intent showDoctorIntent = new Intent(activity.getApplicationContext(), MyPatientActivity.class);
                                showDoctorIntent.putExtra(Constants.DOCTOR_DETAIL, data);
                                showDoctorIntent.putExtra(ADD_DOCTOR_DISABLE_CHECK, true);
                                activity.startActivity(showDoctorIntent);
                                activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get doctors: " + getDoctorsResponse.status.message, Toast.LENGTH_LONG).show();
            }
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
         }
    }

    public final class PopulateMyCareDoctorListRequestListener implements RequestListener<SearchDoctorsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse getDoctorsResponse) {
            if (getDoctorsResponse.status.code == HTTP_STATUS_OK) {
                ArrayList<Patient> tempPatient = (ArrayList<Patient>) getDoctorsResponse.data;
                ListIterator<Patient> iter = tempPatient.listIterator();

                while(iter.hasNext()){
                    Patient patient = iter.next();
                    PatientListItem patientItem = new PatientListItem();
                    patientItem.isPending = false;
                    patientItem.email = patient.email;
                    patientItem.name = patient.name;
                    patientItem.recommandedDoctorId = patient.recommandedDoctorId;
                    patientItem.status = patient.status;
                    patientItem.userDevicesCount = patient.userDevicesCount;
                    patientItem.userId = patient.userId;
                    patientLists.add(patientItem);
                }

                if (patientLists.size() == 0) {
                    myPatientListView.setVisibility(View.GONE);
                    progressDialog.dismiss();

                } else if (patientLists.size() > 0) {
                    if(mycareDoctorAdapter != null) {
                        mycareDoctorAdapter.notifyDataSetChanged();
                    }
                    myPatientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.i(TAG, "position = " + position);
                            Bundle data = new Bundle();

                            PatientListItem patientListItem = patientLists.get(position);
                            Patient patient = new Patient(patientListItem.userId, patientListItem.email,
                                    patientListItem.name, patientListItem.recommandedDoctorId, patientListItem.status,
                                    patientListItem.isCallAllowed, patientListItem.userDevicesCount,
                                    patientListItem.userSettings, patientListItem.userProfile, patientListItem.avatarUrl);

                            data.putParcelable(Constants.DOCTOR_DETAIL, patient);
                            data.putBoolean(Constants.PATIENT_ALREADY_ADDED, true);
                            final Activity activity = getActivity();
                            if (activity != null) {
                                Intent showDoctorIntent = new Intent(activity.getApplicationContext(), MyPatientActivity.class);
                                showDoctorIntent.putExtra(Constants.DOCTOR_DETAIL, data);
                                showDoctorIntent.putExtra(ADD_DOCTOR_DISABLE_CHECK, true);
                                activity.startActivity(showDoctorIntent);
                                activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            }
                        }
                    });

                }

            } else {
                Toast.makeText(getApplicationContext(), "Failed to get doctors: " + getDoctorsResponse.status.message, Toast.LENGTH_LONG).show();
            }
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
            progressDialog.dismiss();
        }
    }

}
