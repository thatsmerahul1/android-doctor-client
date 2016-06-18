package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.doctor.MyPatientActivity;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.NetworkCheck;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.PatientAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.database.PatientProfileDbApi;
import com.ecarezone.android.doctor.model.database.ProfileDbApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.model.rest.PatientAcceptRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
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
    private TextView noPatient;

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
        pullDBFromdevice();

    }
    BroadcastReceiver message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mycareDoctorAdapter.notifyDataSetChanged();
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_loading).toString());
        checkProgress = true;

        initListWithData();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(message,
                new IntentFilter("send"));
        pullDBFromdevice();
    }

    private void initListWithData(){
        patientLists.clear();
        if(NetworkCheck.isNetworkAvailable(getActivity())) {
            populatePendingPatientList();
            populateMyCarePatientList();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(message);
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

        patientList = view.findViewById(R.id.patient_list);
        myPatientListView = (ListView)view.findViewById(R.id.added_patient_list);
        noPatient = (TextView)view.findViewById(R.id.nomessage);
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
    private void acceptedPatientRequest(long patientId, String status) {
        PatientAcceptRequest request = new PatientAcceptRequest(LoginInfo.userId, patientId, status );
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
            initListWithData();
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
                Patient patient = null;
                while(iter.hasNext()){
                    patient = iter.next();
                    PatientListItem patientItem = new PatientListItem();
                    patientItem.listItemType = PatientListItem.LIST_ITEM_TYPE_APPROVED;
                    patientItem.email = patient.email;
                    patientItem.name = patient.name;
                    patientItem.recommandedDoctorId = patient.recommandedDoctorId;
                    patientItem.status = patient.status;
                    patientItem.userDevicesCount = patient.userDevicesCount;
                    patientItem.userId = patient.userId;
                    patientItem.avatarUrl = patient.avatarUrl;
                    patientLists.add(patientItem);
                    PatientProfileDbApi profileDbApi = PatientProfileDbApi.getInstance(getApplicationContext());

                    profileDbApi.deleteProfile(String.valueOf(patient.userId));
//                    if(profileDbApi.getProfileByEmail(patient.email) == null) {
                        profileDbApi.saveProfile(patient);
//                    }
//                   else {
//                        profileDbApi.updateProfile(String.valueOf(patient.userId)/*String.valueOf(LoginInfo.userId)*/, patient);
//                    }
                }
//                if(patient != null) {
//                    PatientProfileDbApi profileDbApi = new PatientProfileDbApi(getApplicationContext());
////                    profileDbApi.saveProfile(LoginInfo.userId, patient);
//                    profileDbApi.updateProfile(String.valueOf(LoginInfo.userId), patient);
//                }

                if (patientLists.size() == 0) {
                    myPatientListView.setVisibility(View.GONE);
                    noPatient.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();

                } else if (patientLists.size() > 0) {
                    if(mycareDoctorAdapter != null) {
                        mycareDoctorAdapter.notifyDataSetChanged();
                    }
                    noPatient.setVisibility(View.GONE);

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
                Patient patient = null;
                while(iter.hasNext()){
                    patient = iter.next();
                    PatientListItem patientItem = new PatientListItem();
                    patientItem.listItemType = PatientListItem.LIST_ITEM_TYPE_PENDING;
                    patientItem.email = patient.email;
                    patientItem.name = patient.name;
                    patientItem.recommandedDoctorId = patient.recommandedDoctorId;
                    patientItem.status = patient.status;
                    patientItem.userDevicesCount = patient.userDevicesCount;
                    patientItem.userId = patient.userId;
                    patientItem.avatarUrl = patient.avatarUrl;
                    patientLists.add(patientItem);

                    PatientProfileDbApi profileDbApi = PatientProfileDbApi.getInstance(getActivity());
                    Patient id = profileDbApi.getProfileByEmail(patient.email);
                    if(id == null || patient.userId != id.userId ) {
                        profileDbApi.saveProfile(patient);
                    }
                    else {
                        profileDbApi.updateProfile(String.valueOf(patient.userId), patient);
                    }
                }

                mycareDoctorAdapter = new PatientAdapter(getActivity(), patientLists, mOnButtonClickedListener);
                myPatientListView.setAdapter(mycareDoctorAdapter);
                if (patientLists.size() == 0) {
                    progressDialog.dismiss();
                    noPatient.setVisibility(View.VISIBLE);
                } else if (patientLists.size() > 0) {
                    noPatient.setVisibility(View.GONE);

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

    private OnButtonClicked mOnButtonClickedListener = new OnButtonClicked() {
        @Override
        public void onButtonClickedListener(int position, boolean isAccept) {

            PatientListItem patientItem = patientLists.get(position);

            if(isAccept){
                if(NetworkCheck.isNetworkAvailable(getActivity())) {
                    acceptedPatientRequest(patientItem.userId, "approved");
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                mycareDoctorAdapter.notifyDataSetChanged();
            }
            else{
                if(NetworkCheck.isNetworkAvailable(getActivity())) {
                    acceptedPatientRequest(patientItem.userId, "rejected");                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }

            }

        }
    };

    @SuppressWarnings("resource")
    private void pullDBFromdevice() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {

                String currentDBPath = getApplicationContext().getDatabasePath("ecarezone.db").toString();/*"/data/" + getApplicationContext().getPackageName() + "/databases/ecarezone"*/

                File currentDB = new File(currentDBPath);

                String backupDBPath = "ecarezone.db";
                File backupDB = new File(sd, "/Download/" + backupDBPath);
                if(!backupDB.exists()){
                    backupDB.createNewFile();
                }

                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
            }
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }


}
