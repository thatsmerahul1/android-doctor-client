package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ecarezone.android.doctor.EditAppointmentActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.fragment.dialog.EcareZoneAlertDialog;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.pojo.AppointmentListItem;
import com.ecarezone.android.doctor.model.rest.EditAppointmentRequest;
import com.ecarezone.android.doctor.model.rest.EditAppointmentResponse;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.ecarezone.android.doctor.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by L&T Technology Services.
 */
public class EditAppointmentFragment extends EcareZoneBaseFragment implements View.OnClickListener {
    private Activity mActivity;
    private RadioButton radioVideo, radioVoip;
    private Button btnAppointment;

    private TextView txtAppointmentTime;
    private TextView txtAppointmentDay;
    private TextView txtAppointmentMonth;
    private TextView txtAppointmentYear;

    private int selectedDate, selectedMonth, selectedYear, selectedTimeHr, selectedTimeMin;
    private long doctorId;

    private TextView txtErrorMsg;
    private ProgressDialog progressDialog;
    private EditAppointmentFragment appointmentFragment;

    private AppointmentListItem mExistingAppointment;

    public EditAppointmentFragment() {
    }

    public static EditAppointmentFragment getNewInstance() {

        EditAppointmentFragment fragment = new EditAppointmentFragment();
        return fragment;
    }

    @Override
    protected String getCallerName() {
        return EditAppointmentFragment.class.getName().toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        doctorId = getArguments().getLong("doctorId", -1);
        Object obj = getArguments().getSerializable("currentAppointment");
        if (obj != null) {
            this.mExistingAppointment = (AppointmentListItem) obj;
        }
        appointmentFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_edit_appointment, container, false);

        getAllComponent(view);

        ((EditAppointmentActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.reschedule_appointment));
        return view;
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    private void getAllComponent(View view) {
        radioVideo = (RadioButton) view.findViewById(R.id.radioVideo);
        radioVoip = (RadioButton) view.findViewById(R.id.radioVoip);
        btnAppointment = (Button) view.findViewById(R.id.button_appointment);

        radioVideo.setOnClickListener(this);
        radioVoip.setOnClickListener(this);
        btnAppointment.setOnClickListener(this);

        txtAppointmentDay = (TextView) view.findViewById(R.id.appointment_day);
        txtAppointmentDay.setOnClickListener(this);


        txtAppointmentYear = (TextView) view.findViewById(R.id.appointment_year);
        txtAppointmentYear.setOnClickListener(this);

        txtAppointmentMonth = (TextView) view.findViewById(R.id.appointment_month);
        txtAppointmentMonth.setOnClickListener(this);

        txtAppointmentTime = (TextView) view.findViewById(R.id.appointment_time);
        txtAppointmentTime.setOnClickListener(this);

        txtErrorMsg = (TextView) view.findViewById(R.id.txtErrorMsg);

        Calendar mcurrentDate = Calendar.getInstance();
        if (mExistingAppointment != null) {
            String timeStamp = mExistingAppointment.dateTime;
            long timeStampInLong = Long.parseLong(timeStamp);
            if (timeStampInLong > 0) {
                mcurrentDate.setTimeInMillis(timeStampInLong);
            }
        }

        selectedDate = mcurrentDate.get(Calendar.DATE);
        selectedMonth = mcurrentDate.get(Calendar.MONTH);
        selectedYear = mcurrentDate.get(Calendar.YEAR);

        String dayStr = String.valueOf(selectedDate);

        Calendar mTodayDate = Calendar.getInstance();
        final int day = mTodayDate.get(Calendar.DATE);
        final int month = mTodayDate.get(Calendar.MONTH);
        final int year = mTodayDate.get(Calendar.YEAR);

        if (selectedDate == day && month == selectedMonth && year == selectedYear) {
            dayStr += "(Today)";
        } else if (day + 1 == day && month == selectedMonth && year == selectedYear) {
            dayStr += "(Tomorrow)";
        }
        txtAppointmentDay.setText(dayStr);

        String monthString = new DateFormatSymbols().getMonths()[selectedMonth];
        txtAppointmentMonth.setText(monthString);


        selectedTimeHr = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        selectedTimeMin = mcurrentDate.get(Calendar.MINUTE);

        String timeStr = formattedTime(mcurrentDate);
        txtAppointmentTime.setText(timeStr);
    }

    private String formattedTime(Calendar mcurrentDate) {
//        String ampmStr = mcurrentDate.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
        String minute = null;
        if (mcurrentDate.get(Calendar.MINUTE) < 10) {
            minute = "0" + mcurrentDate.get(Calendar.MINUTE);
        } else {
            minute = String.valueOf(mcurrentDate.get(Calendar.MINUTE));
        }

        String hour = null;
        if (mcurrentDate.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + mcurrentDate.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = String.valueOf(mcurrentDate.get(Calendar.HOUR_OF_DAY));
        }
        String timeStr = hour + ":" + minute + "(" + getString(R.string.thirty_mins) + ")";
        return timeStr;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioVideo:
                if (radioVideo.isChecked())
                    break;
            case R.id.radioVoip:
                if (radioVoip.isChecked())
                    break;
            case R.id.button_appointment:
//               Toast.makeText(getActivity(), "button appointment", Toast.LENGTH_SHORT).show();
//               date in YYYY-MM-DD format

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DATE, selectedDate);
                calendar.set(Calendar.HOUR_OF_DAY, selectedTimeHr);
                calendar.set(Calendar.MINUTE, selectedTimeMin);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    txtErrorMsg.setText(R.string.appointment_cannot_be_set);
                    return;
                }

//                BookAppointmentRequest request =
//                        new BookAppointmentRequest(LoginInfo.userName, LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique,
//                                selectedYear + "-" + (selectedMonth+1) + "-" + selectedDate + " " + selectedTimeHr + ":" + selectedTimeMin,
//                                radioVideo.isChecked() ? "video" : "voice", doctorId);
//                final BaseResponse response = new BaseResponse();
//
//                getSpiceManager().execute(request, new BookAppointmentRequestListener());

                String newDateTime =
                        selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDate
                                + " " + selectedTimeHr + ":" + selectedTimeMin;

                String oldDateTime = Util.getTimeInStringFormat(Long.parseLong(mExistingAppointment.dateTime));

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.rescheduling_appointment));
                progressDialog.show();

                EditAppointmentRequest request = new EditAppointmentRequest(
                        LoginInfo.userId, mExistingAppointment.patientId,
                        oldDateTime, newDateTime, mExistingAppointment.callType);
                getSpiceManager().execute(request, new RescheduleRequestListener());

                break;
            case R.id.appointment_time:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectedTimeHr = selectedHour;
                        selectedTimeMin = selectedMinute;

                        Calendar mcurrentDate = Calendar.getInstance();
                        mcurrentDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                        mcurrentDate.set(Calendar.MINUTE, selectedMinute);
                        String formattedDate = formattedTime(mcurrentDate);
                        txtAppointmentTime.setText(formattedDate);
                        txtErrorMsg.setText("");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

                break;
            case R.id.appointment_year:
                showDateTimePicker();
                break;
            case R.id.appointment_day:
                showDateTimePicker();
                break;
            case R.id.appointment_month:
                showDateTimePicker();
                break;

        }
    }

    private void showDateTimePicker() {
        Calendar mcurrentDate = Calendar.getInstance();
        final int day = mcurrentDate.get(Calendar.DATE);
        final int month = mcurrentDate.get(Calendar.MONTH);
        final int year = mcurrentDate.get(Calendar.YEAR);
        DatePickerDialog dPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth) {

                if (dayOfMonth < day) {
                    txtErrorMsg.setText(getString(R.string.invalid_date));
                    return;
                } else {
                    txtErrorMsg.setText("");
                }

                String dayStr = String.valueOf(dayOfMonth);
                if (day == dayOfMonth && monthOfYear == month && year == yearSel) {
                    dayStr += "(Today)";
                } else if (day + 1 == dayOfMonth && monthOfYear == month && year == yearSel) {
                    dayStr += "(Tomorrow)";
                }
                txtAppointmentDay.setText(dayStr);

                String monthString = new DateFormatSymbols().getMonths()[monthOfYear];
                txtAppointmentMonth.setText(monthString);

                txtAppointmentYear.setText(String.valueOf(yearSel));

                selectedDate = dayOfMonth;
                selectedMonth = monthOfYear;
                selectedYear = yearSel;
                txtErrorMsg.setText("");
            }
        }, year, month, day);
        dPicker.show();
    }

    /**
     *
     */
    private class RescheduleRequestListener implements RequestListener<EditAppointmentResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

        }

        @Override
        public void onRequestSuccess(EditAppointmentResponse response) {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (response != null && response.status != null && response.status.message != null) {
                if (response.status.message.startsWith("Appointment re-scheduled successfully")) {
                    AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());

                    appointmentDbApi.updateAppointment(response.data.id, response.data);

                    Toast.makeText(getActivity(), getString(R.string.appointment_rescheduled) +
                            " " + response.data.dateTime, Toast.LENGTH_LONG).show();
                }
            }
            getActivity().finish();
        }
    }
}
