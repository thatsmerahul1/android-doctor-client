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

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.MyPatientActivity;
import com.ecarezone.android.doctor.NetworkCheck;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.PatientAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.PatientProfile;
import com.ecarezone.android.doctor.model.database.PatientProfileDbApi;
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

    public interface OnButtonClicked {
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

    @Override
    public void onResume() {
        super.onResume();
        checkProgress = true;

        initListWithData();
        IntentFilter intentFilter = new IntentFilter("send");
        intentFilter.addAction(Constants.BROADCAST_STATUS_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                intentFilter);

        pullDBFromdevice();
    }

    private void initListWithData() {
        if (NetworkCheck.isNetworkAvailable(getActivity())) {
            patientLists.clear();
            progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                    getText(R.string.progress_dialog_loading).toString());
            progressDialog.show();
            populatePendingPatientList();
            populateMyCarePatientList();
            progressDialog.dismiss();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        }
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
        myPatientListView = (ListView) view.findViewById(R.id.added_patient_list);
        noPatient = (TextView) view.findViewById(R.id.nomessage);
        return view;
    }

    private void populateMyCarePatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null, true);
        getSpiceManager().execute(request, new PopulatePatientListRequestListener());
    }

    private void populatePendingPatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null, false);
        getSpiceManager().execute(request, new PopulatePendingPatientListRequestListener());
    }

    private void acceptedPatientRequest(long patientId, String status) {
        PatientAcceptRequest request = new PatientAcceptRequest(LoginInfo.userId, patientId, status);
        getSpiceManager().execute(request, new RequestFromPatients());
    }

    public final class RequestFromPatients implements RequestListener<SearchDoctorsResponse> {

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

    public final class PopulatePatientListRequestListener implements RequestListener<SearchDoctorsResponse> {

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
                while (iter.hasNext()) {
                    patient = iter.next();
                    PatientListItem patientItem = new PatientListItem(); //PatientListItem this class is because there is list of data but in db only one patient details can be saved at a time
                    patientItem.listItemType = PatientListItem.LIST_ITEM_TYPE_APPROVED;
                    patientItem.email = patient.email;
                    patientItem.name = patient.name;
                    patientItem.recommandedDoctorId = patient.recommandedDoctorId;
                    patientItem.status = patient.status;
                    patientItem.userDevicesCount = patient.userDevicesCount;
                    patientItem.userId = patient.userId;
                    patientItem.avatarUrl = patient.avatarUrl;
                    patientItem.userProfile = patient.userProfiles;

                    for(PatientProfile patientProfile : patient.userProfiles){
                        patientProfile.userId = patient.userId;
                    }

                    patientLists.add(patientItem);
                    PatientProfileDbApi profileDbApi = PatientProfileDbApi.getInstance(getApplicationContext());
                    profileDbApi.saveProfile(patient);

                }

                if (patientLists.size() == 0) {
                    myPatientListView.setVisibility(View.GONE);
                    noPatient.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();

                } else if (patientLists.size() > 0) {
                    if (mycareDoctorAdapter != null) {
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
                while (iter.hasNext()) {
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
                    patientItem.userProfile = patient.userProfiles;

                    patientLists.add(patientItem);

                    PatientProfileDbApi profileDbApi = PatientProfileDbApi.getInstance(getActivity());
                    Patient id = profileDbApi.getProfileByEmail(patient.email);
                    if (id == null || patient.userId != id.userId) {
                        profileDbApi.saveProfile(patient);
                    } else {
                        profileDbApi.updateProfile(patient.userId, patient);
                    }
                }

                mycareDoctorAdapter = new PatientAdapter(getActivity(), patientLists, mOnButtonClickedListener);
                myPatientListView.setAdapter(mycareDoctorAdapter);
                if (patientLists.size() == 0) {
                    progressDialog.dismiss();
                    noPatient.setVisibility(View.VISIBLE);
                } else if (patientLists.size() > 0) {
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

    private OnButtonClicked mOnButtonClickedListener = new OnButtonClicked() {
        @Override
        public void onButtonClickedListener(int position, boolean isAccept) {

            PatientListItem patientItem = patientLists.get(position);

            if (isAccept) {
                if (NetworkCheck.isNetworkAvailable(getActivity())) {
                    acceptedPatientRequest(patientItem.userId, "approved");
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
                mycareDoctorAdapter.notifyDataSetChanged();
            } else {
                if (NetworkCheck.isNetworkAvailable(getActivity())) {
                    acceptedPatientRequest(patientItem.userId, "rejected");
                } else {
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
                if (!backupDB.exists()) {
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

    /* BroadcastReceiver receiver that updates the chat count or
        * changes the availability status */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("send")) {
                mycareDoctorAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_STATUS_CHANGED)) {
                String statusTxt = intent.getStringExtra(Constants.SET_STATUS);
                if (statusTxt != null) {
                    String[] statusArr = statusTxt.split(",");
                    if (statusArr.length > 2) {
                        int patId = -1;
                        try {
                            patId = Integer.parseInt(statusArr[1].trim());
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                        }
                        if (patId > -1) {
                            for (PatientListItem patientListItem : patientLists) {
                                if (patientListItem.userId == patId) {
                                     patientListItem.status = statusArr[2].trim() ;

                                     break;
                                }
                            }
                        }
                    }
                    mycareDoctorAdapter.notifyDataSetChanged();
                }

            }
        }
    };


}
