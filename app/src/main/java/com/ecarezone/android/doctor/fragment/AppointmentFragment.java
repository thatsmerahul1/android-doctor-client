package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ecarezone.android.doctor.AppointmentActivity;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.AppointmentAdapter;
import com.ecarezone.android.doctor.adapter.OnButtonClickedListener;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.database.PatientProfileDbApi;
import com.ecarezone.android.doctor.model.pojo.AppointmentListItem;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.AppointmentAcceptRequest;
import com.ecarezone.android.doctor.model.rest.AppointmentRequest;
import com.ecarezone.android.doctor.model.rest.AppointmentResponse;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.model.rest.AppointmentRejectRequest;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

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
    private DateFormat dateFormat;
    private int appointmentIdRespondedTo;

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


        if(getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar()
                    .setTitle(getResources().getText(R.string.doctor_appointment_header));
        }
        else if(getActivity() instanceof AppointmentActivity){
            ((AppointmentActivity) getActivity()).getSupportActionBar()
                    .setTitle(getResources().getText(R.string.doctor_appointment_header));
        }

        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setDate(System.currentTimeMillis());
        mCalendarView.setOnDateChangeListener(mDateChangeListener);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        mAppointmentList = new ArrayList<AppointmentListItem>();
        adapter = new AppointmentAdapter(getActivity(), mAppointmentList, new OnButtonClickedListener() {
            @Override
            public void onButtonClickedListener(int position, boolean isPositiveButtonPressed) {

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.show();
                AppointmentListItem appointmentListItem = mAppointmentList.get(position);
                appointmentIdRespondedTo = appointmentListItem.appointmentId;
                if(! isPositiveButtonPressed) {
                    String callTime = dateFormat.format(new Date(Long.parseLong(appointmentListItem.dateTime)));
                    AppointmentRejectRequest request = new AppointmentRejectRequest(appointmentListItem.appointmentId,
                            callTime, appointmentListItem.callType);
                    getSpiceManager().execute(request, new RespondRequestListener());
                }
                else{
                    AppointmentAcceptRequest request = new AppointmentAcceptRequest(appointmentListItem.appointmentId);
                    getSpiceManager().execute(request, new RespondRequestListener());
                }

            }
        });

        appointmentList = (ListView) view.findViewById(R.id.appointment_list);
        appointmentList.setAdapter(adapter);
        populateAppointmentList();

        populateMyAppointmentListFromServer();
        return view;
    }

    private void populateAppointmentList() {

        AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date(mCalendarView.getDate()));
        startCalendar.set(Calendar.HOUR_OF_DAY, 23);
        startCalendar.set(Calendar.MINUTE, 59);
        startCalendar.set(Calendar.SECOND, 59);
        long endDate = startCalendar.getTimeInMillis();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(new Date(mCalendarView.getDate()));
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        long startDate = endCalendar.getTimeInMillis();

//        List<Appointment> appoList1 = appointmentDbApi.getAppointmentHistory(253);
        List<Appointment> appoPendingList = appointmentDbApi.getAllAppointments(false);
        List<Appointment> appoConfirmedList = appointmentDbApi.getAllAppointments(true, startDate, endDate);
        appoConfirmedList.addAll(appoPendingList);

        PatientProfileDbApi patientProfileDbApi = PatientProfileDbApi.getInstance(getApplicationContext());
        mAppointmentList.clear();
        for(Appointment appointment : appoConfirmedList){
            AppointmentListItem appointmentItem = new AppointmentListItem();
            appointmentItem.appointmentId = appointment.getAppointmentId();
            appointmentItem.callType = appointment.getCallType();
            appointmentItem.dateTime = appointment.getTimeStamp();
            appointmentItem.patientId = appointment.getPatientId();

            Patient patient = patientProfileDbApi.getProfile((long) appointment.getPatientId());
            if(patient != null) {
                appointmentItem.patientName = patient.name;
            }

            Appointment app = appointmentDbApi.getAppointment(appointmentItem.appointmentId);
            appointmentItem.isConfirmed = app.isConfirmed();
            if (app.isConfirmed()) {
                appointmentItem.listItemType = PatientListItem.LIST_ITEM_TYPE_APPROVED;
            } else {
                appointmentItem.listItemType = PatientListItem.LIST_ITEM_TYPE_PENDING;
            }

            mAppointmentList.add(appointmentItem);
        }
        adapter.notifyDataSetChanged();
    }

    private void getAllComponent(View view) {

    }

    private void populateMyAppointmentListFromServer() {
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

    private CalendarView.OnDateChangeListener mDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            populateAppointmentList();
        }
    };

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
                AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                ArrayList<Appointment> appointments = (ArrayList<Appointment>) appointmentResponse.data;
                if (appointments != null) {
                    ListIterator<Appointment> iter = appointments.listIterator();
                    Appointment appointment = null;
                    PatientProfileDbApi patientProfileDbApi = PatientProfileDbApi.getInstance(getApplicationContext());
                    while (iter.hasNext()) {
                        appointment = iter.next();

                        try {
                            appointment.setTimeStamp(String.valueOf(dateFormat.parse(appointment.getTimeStamp()).getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

//                        if (profileDbApi.isAppointmentPresent(appointment.getAppointmentId())) {
//                            profileDbApi.updateAppointment(appointment.getAppointmentId(), appointment);
//                        } else {
                        appointmentDbApi.deleteAppointment(appointment.getAppointmentId());
                        appointmentDbApi.saveAppointment(appointment);
//                        }
                        populateAppointmentList();
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

//            adapter.notifyDataSetChanged();
        }
    }

    private class RespondRequestListener implements RequestListener<com.ecarezone.android.doctor.model.rest.base.BaseResponse> {
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
        public void onRequestSuccess(BaseResponse baseResponse) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if(baseResponse != null && baseResponse.status != null && baseResponse.status.message != null) {
                if ("Appointment accepted by Doctor".equalsIgnoreCase(baseResponse.status.message)) {
                    AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                    boolean isConfirmed = appointmentDbApi.acceptAppointment(appointmentIdRespondedTo);
                    Toast.makeText(getActivity(), getString(R.string.appointment_accepted), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.appointment_rejected), Toast.LENGTH_LONG).show();
                }
            }
            populateMyAppointmentListFromServer();
        }
    }
}
