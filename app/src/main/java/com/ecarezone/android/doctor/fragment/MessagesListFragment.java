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
import android.widget.TextView;
import android.widget.Toast;

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.MyPatientActivity;
import com.ecarezone.android.doctor.NetworkCheck;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.MessageAdapter;
import com.ecarezone.android.doctor.adapter.PatientAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.Chat;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private TextView noMessage;
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
            ((MainActivity) getActivity()).getSupportActionBar()
                    .setTitle(getResources().getString(R.string.main_side_menu_messages));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_message_list, container, false);

        messageContainer = view.findViewById(R.id.message_container);
        myPatientListView = (ListView) view.findViewById(R.id.message_list);
        noMessage = (TextView) view.findViewById(R.id.emptyMessages);

//        myPatientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i(TAG, "position = " + position);
//                Bundle data = new Bundle();
//
//                PatientListItem patientListItem = patientLists.get(position);
//                Patient patient = new Patient(patientListItem.userId, patientListItem.email,
//                        patientListItem.name, patientListItem.recommandedDoctorId, patientListItem.status,
//                        patientListItem.isCallAllowed, patientListItem.userDevicesCount,
//                        patientListItem.userSettings, patientListItem.userProfile, patientListItem.avatarUrl);
//
//                data.putParcelable(Constants.DOCTOR_DETAIL, patient);
//                data.putBoolean(Constants.PATIENT_ALREADY_ADDED, true);
//                final Activity activity = getActivity();
//                if (activity != null) {
//                    Intent showDoctorIntent = new Intent(activity.getApplicationContext(), MyPatientActivity.class);
//                    showDoctorIntent.putExtra(Constants.DOCTOR_DETAIL, data);
//                    showDoctorIntent.putExtra(ADD_DOCTOR_DISABLE_CHECK, true);
//                    activity.startActivity(showDoctorIntent);
//                    activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
//                }
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_loading).toString());
        patientLists.clear();
        if (NetworkCheck.isNetworkAvailable(getActivity())) {
            populatePendingPatientList();
            populateMyCarePatientList();
            progressDialog.dismiss();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void populatePendingPatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null, false);
        getSpiceManager().execute(request, new PopulatePendingPatientListRequestListener());
    }

    private void populateMyCarePatientList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null, true);
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

                while (iter.hasNext()) {
                    Patient patient = iter.next();
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
                }
                mycareDoctorAdapter = new MessageAdapter(getActivity(), patientLists);
                myPatientListView.setAdapter(mycareDoctorAdapter);
                if (patientLists.size() == 0) {
                    progressDialog.dismiss();
//                    noMessage.setVisibility(View.VISIBLE);

                } else if (patientLists.size() > 0) {
//                    noMessage.setVisibility(View.GONE);
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
                int todayDate = Calendar.getInstance().get(Calendar.DATE);
                SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                ChatDbApi chatDbi = ChatDbApi.getInstance(getActivity());
                while (iter.hasNext()) {
                    Patient patient = iter.next();
                    int count = chatDbi.getUnReadChatCountByUserId(patient.email);
                    if (count > 0) {
                        PatientListItem patientItem = new PatientListItem();
                        patientItem.listItemType = PatientListItem.LIST_ITEM_TYPE_MESSAGE;
                        patientItem.email = patient.email;
                        patientItem.name = patient.name;
                        patientItem.recommandedDoctorId = patient.recommandedDoctorId;
                        patientItem.status = patient.status;
                        patientItem.userDevicesCount = patient.userDevicesCount;
                        patientItem.userId = patient.userId;


                        List<Chat> mMessages = chatDbi.getChatHistory(patient.email);

                        Chat lastMsg = mMessages.get(mMessages.size() - 1);
                        patientItem.unreadMsgCount = count;
                        patientItem.name = patient.name;
                        patientItem.msgText = lastMsg.getMessageText();

                        String timeStamp;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(lastMsg.getTimeStamp());
                        if (todayDate == calendar.get(Calendar.DATE)) {
                            timeStamp = mTimeFormat.format(lastMsg.getTimeStamp());
                        } else {
                            timeStamp = mDateFormat.format(lastMsg.getTimeStamp());
                        }
                        patientItem.dateTime = timeStamp;
                        patientLists.add(patientItem);
                    }
                }

                for (Patient patient : tempPatient) {
                    List<Appointment> appointmentList = AppointmentDbApi.getInstance(getActivity()).
                            getAppointmentHistory(patient.userId, false);
                    int size = appointmentList.size();
                    for (int i = 0; i < size; i++) {

                        Appointment appointment = appointmentList.get(i);
                        PatientListItem patientItem = new PatientListItem();
                        patientItem.listItemType = PatientListItem.LIST_ITEM_TYPE_APPOINTMENT;
                        patientItem.userId = patient.userId;
                        patientItem.patientId = appointment.getPatientId();
                        patientItem.appointmentId = appointment.getAppointmentId();
                        patientItem.callType = appointment.getCallType();
                        patientItem.name = patient.name;

                        String timeStamp;
                        Calendar calendar = Calendar.getInstance();
                        Date currDate = new Date(Long.parseLong(appointment.getTimeStamp()));
                        calendar.setTime(currDate);
                        if (todayDate == calendar.get(Calendar.DATE)) {
                            timeStamp = mTimeFormat.format(currDate);
                        } else {
                            timeStamp = mDateFormat.format(currDate);
                        }

                        patientItem.dateTime = timeStamp;
                        patientLists.add(patientItem);
                    }
                }

                if (patientLists.size() == 0) {
                    myPatientListView.setVisibility(View.GONE);
                    noMessage.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();

                } else if (patientLists.size() > 0) {
                    ChatDbApi chatDbApi = ChatDbApi.getInstance(getApplicationContext());

//                    if (chatDbApi.getUnReadChatCount() == 0) {
//                        noMessage.setVisibility(View.VISIBLE);
//                    } else {
//                        noMessage.setVisibility(View.GONE);
//                    }
                    if (mycareDoctorAdapter != null) {
                        mycareDoctorAdapter.notifyDataSetChanged();
                    }
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
