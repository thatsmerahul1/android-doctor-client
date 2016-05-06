package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ecarezone.android.doctor.MyPatientActivity;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.PatientAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.model.rest.PatientAcceptRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by CHAO WEI on 5/25/2015.
 */
public class MyPatientListFragment extends EcareZoneBaseFragment {

    public static final String ADD_DOCTOR_DISABLE_CHECK = "addDocotrDisablecheck";

    private static final String TAG = MyPatientListFragment.class.getSimpleName();
    private static final int HTTP_STATUS_OK = 200;
    private ArrayList<PatientListItem> patientLists = new ArrayList<PatientListItem>();
    private PatientAdapter mycareDoctorAdapter;
    View patientList;
    private ProgressDialog progressDialog;
    private boolean checkProgress;
    private ListView myPatientListView = null;
    private ListView myPatientPendingListView = null;

    @Override
    protected String getCallerName() {
        return MyPatientListFragment.class.getSimpleName();
    }

    public interface OnButtonClicked{
        public void onButtonClickedListener(int position, boolean isAccept);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setHasOptionsMenu(true);
        } catch (Exception e) {
        }
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.main_side_menu_my_patients));
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_loading).toString());
        checkProgress = true;

        patientLists.clear();

        populatePendingPatientList();
        populateMyCarePatientList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null) return false;

        final int itemId = item.getItemId();
        if (itemId == R.id.menu_action_search) {
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_doctor_list_2, container, false);

//        patientAvatar = (ImageView)view.findViewById(R.id.patient_avatar_of_request);
//        patientName = (TextView)view.findViewById(R.id.myPatient_name);
//        accept = (Button)view.findViewById(R.id.patient_request_accept);
//        reject = (Button)view.findViewById(R.id.patient_request_reject);

//        recommendedDoctorListView = (ListView) view.findViewById(R.id.recommended_doctors_list);
        patientList = view.findViewById(R.id.patient_list);
        myPatientListView = (ListView)view.findViewById(R.id.added_patient_list);
//        myPatientPendingListView = (ListView)view.findViewById(R.id.pending_patient_list);
//        pendingRequest = (LinearLayout)view.findViewById(R.id.patient_layout_);

//        accept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                acceptedPatientRequest();
////                pendingRequest.setVisibility(View.GONE);
//            }
//        });
//        view.setVisibility(View.GONE);
        //TODO: if request is not present then make it invisible
//        pendingRequest = (LinearLayout)view.findViewById(R.id.doctor_layout);
//        recommendedDoctorContainer = view.findViewById(R.id.recommended_doctors_container);
        return view;
    }

    private void populateMyCarePatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null,true);
        getSpiceManager().execute(request, new PopulateMyCareDoctorListRequestListener());
    }

    private void populatePendingPatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null, false);
        getSpiceManager().execute(request, new PopulatePendingPatientListRequestListener());
    }
    private void acceptedPatientRequest(long patientId) {
        PatientAcceptRequest request = new PatientAcceptRequest(/*2,26*/LoginInfo.userId, patientId  );
        getSpiceManager().execute(request, new RequestFromPatients());
    }

    public final class RequestFromPatients implements  RequestListener<SearchDoctorsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse status) {
            if(status.status.code == HTTP_STATUS_OK) {

            }
            Toast.makeText(getActivity(), status.status.message, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
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
                    mycareDoctorAdapter.notifyDataSetChanged();

                    myPatientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.i(TAG, "position = " + position);
                            Bundle data = new Bundle();

                            PatientListItem patientListItem = patientLists.get(position);
                            Patient patient = new Patient(patientListItem.userId, patientListItem.email,
                                    patientListItem.name, patientListItem.recommandedDoctorId, patientListItem.status,
                                    patientListItem.isCallAllowed, patientListItem.userDevicesCount,
                                    patientListItem.userSettings, patientListItem.userProfile);

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
                    patientLists.add(patientItem);
                }
                mycareDoctorAdapter = new PatientAdapter(getActivity(), patientLists, mOnButtonClickedListener);
                myPatientListView.setAdapter(mycareDoctorAdapter);
                if (patientLists.size() == 0) {
                    progressDialog.dismiss();

                } else if (patientLists.size() > 0) {
//                    mycareDoctorAdapter = new PatientAdapter(getActivity(), patientLists, mOnButtonClickedListener);
//                    myPatientListView.setAdapter(mycareDoctorAdapter);

                    myPatientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.i(TAG, "position = " + position);
                            Bundle data = new Bundle();


                            PatientListItem patientListItem = patientLists.get(position);
                            Patient patient = new Patient(patientListItem.userId, patientListItem.email,
                                    patientListItem.name, patientListItem.recommandedDoctorId, patientListItem.status,
                                    patientListItem.isCallAllowed, patientListItem.userDevicesCount,
                                    patientListItem.userSettings, patientListItem.userProfile);

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

    private OnButtonClicked mOnButtonClickedListener = new OnButtonClicked() {
        @Override
        public void onButtonClickedListener(int position, boolean isAccept) {

            PatientListItem patientItem = patientLists.get(position);

            if(isAccept){
                acceptedPatientRequest(patientItem.userId);
            }
            else{

            }

        }
    };
}
