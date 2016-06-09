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
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ecarezone.android.doctor.AppointmentActivity;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.MyPatientActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.AppointmentAdapter;
import com.ecarezone.android.doctor.adapter.MessageAdapter;
import com.ecarezone.android.doctor.adapter.OnButtonClickedListener;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.database.PatientProfileDbApi;
import com.ecarezone.android.doctor.model.pojo.AppointmentListItem;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.AppointmentRequest;
import com.ecarezone.android.doctor.model.rest.AppointmentResponse;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by L&T Technology Services.
 */
public class AppointmentFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    private Activity mActivity;
    private RadioButton radioVideo, radioVoip;
    private Button btnAppointment;
    private boolean checkProgress;
    private ProgressDialog progressDialog;
    private ArrayList<AppointmentListItem> mAppointmentList;
    private CalendarView mCalendarView;
    private ListView appointmentList;
    private AppointmentAdapter adapter;

    private static final int HTTP_STATUS_OK = 200;

    @Override
    protected String getCallerName() {
        return AppointmentFragment.class.getName().toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_appointment, container, false);


        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.doctor_appointment_header));

        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setDate(System.currentTimeMillis());

        mAppointmentList = new ArrayList<AppointmentListItem>();
        adapter = new AppointmentAdapter(getActivity(), mAppointmentList, new OnButtonClickedListener() {
            @Override
            public void onButtonClickedListener(int position, boolean isPositiveButtonPressed) {

            }
        });

        appointmentList = (ListView) view.findViewById(R.id.appointment_list);
        appointmentList.setAdapter(adapter);

        populateMyAppointmentList();
        return view;
    }

    private void getAllComponent(View view) {

    }

    private void populateMyAppointmentList() {
        progressDialog = new ProgressDialog(getActivity());
        AppointmentRequest request =
                new AppointmentRequest(LoginInfo.userId);
        getSpiceManager().execute(request, new PopulateAppointmentListRequestListener());
    }

    //    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.radioVideo:
//                if (radioVideo.isChecked())
//                    break;
//            case R.id.radioVoip:
//                if (radioVoip.isChecked())
//                    break;
//            case R.id.button_appointment:
//                Toast.makeText(getActivity(), "button appointment", Toast.LENGTH_SHORT).show();
//                break;
//        }
    }

    public final class PopulateAppointmentListRequestListener implements RequestListener<AppointmentResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(AppointmentResponse appointmentResponse) {
//            if (appointmentResponse.status.code == HTTP_STATUS_OK) {
            if (appointmentResponse != null) {
                AppointmentDbApi profileDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                ArrayList<Appointment> appointments = (ArrayList<Appointment>) appointmentResponse.data;
                if (appointments != null) {
                    ListIterator<Appointment> iter = appointments.listIterator();
                    Appointment appointment = null;
                    while (iter.hasNext()) {
                        appointment = iter.next();

                        AppointmentListItem appointmentItem = new AppointmentListItem();
                        appointmentItem.appointmentId = appointment.getAppointmentId();
                        appointmentItem.callType = appointment.getCallType();
                        appointmentItem.dateTime = appointment.getTimeStamp();
                        appointmentItem.patientId = appointment.getPatientId();

                        if (profileDbApi.isAppointmentPresent(appointmentItem.appointmentId)) {
                            profileDbApi.updateAppointment(appointmentItem.appointmentId, appointment);
                        } else {
                            profileDbApi.saveAppointment(appointment);
                        }

                        Appointment app = profileDbApi.getAppointment(appointmentItem.appointmentId);
                        if (app.isConfirmed()) {
                            appointmentItem.listItemType = PatientListItem.LIST_ITEM_TYPE_APPROVED;
                        } else {
                            appointmentItem.listItemType = PatientListItem.LIST_ITEM_TYPE_PENDING;
                        }

                        mAppointmentList.add(appointmentItem);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get appointments: " + appointmentResponse.data, Toast.LENGTH_LONG).show();
            }
            if (checkProgress) {
                checkProgress = false;
            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            adapter.notifyDataSetChanged();
        }
    }
}
