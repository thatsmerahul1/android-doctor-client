package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.ecarezone.android.doctor.EditAppointmentActivity;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.AppointmentAdapter;
import com.ecarezone.android.doctor.adapter.OnButtonClickedListener;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.fragment.dialog.EditAppointmentDialog;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.database.PatientProfileDbApi;
import com.ecarezone.android.doctor.model.pojo.AppointmentListItem;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.AppointmentAcceptRequest;
import com.ecarezone.android.doctor.model.rest.AppointmentRejectRequest;
import com.ecarezone.android.doctor.model.rest.AppointmentRequest;
import com.ecarezone.android.doctor.model.rest.AppointmentResponse;
import com.ecarezone.android.doctor.model.rest.EditAppointmentResponse;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.model.rest.base.BaseResponse;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.ecarezone.android.doctor.utils.Util;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.entity.InputStreamEntity;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

/**
 * Created by L&T Technology Services.
 */
public class AppointmentFragment extends EcareZoneBaseFragment implements AdapterView.OnItemClickListener {
    private Activity mActivity;
    private RadioButton radioVideo, radioVoip;
    private Button btnAppointment;
    private boolean checkProgress;
    private ProgressDialog progressDialog;
    private ArrayList<AppointmentListItem> mAppointmentList;
    private CalendarView mCalendarView;
    private ListView appointmentList;
    private AppointmentAdapter adapter;
    private AppointmentListItem currentAppointment;
    private DateFormat dateFormat;
    private int appointmentIdRespondedTo;
    private EditAppointmentDialog editAppointmentDialog;
    private AppointmentDbApi mAppointmentDbApi;

    private static final int HTTP_STATUS_OK = 200;

    public interface OnAppointmentOptionButtonClickListener {
        public static int BTN_TIME_TO_CALL = 0;
        public static int BTN_CHANGE_TIME = 1;
        public static int BTN_CANCEL = 2;
        public static int BTN_MAKE_AN_APPOINTMENT = 3;

        public void onButtonClicked(int whichButtonClicked, AppointmentListItem mSelectdAppointment);
    }

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


        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar()
                    .setTitle(getResources().getText(R.string.doctor_appointment_header));
        } else if (getActivity() instanceof AppointmentActivity) {
            ((AppointmentActivity) getActivity()).getSupportActionBar()
                    .setTitle(getResources().getText(R.string.doctor_appointment_header));
        }

        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setDate(System.currentTimeMillis());
        mCalendarView.setOnDateChangeListener(mDateChangeListener);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        mAppointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());

        mAppointmentList = new ArrayList<AppointmentListItem>();
        adapter = new AppointmentAdapter(getActivity(), mAppointmentList, new OnButtonClickedListener() {
            @Override
            public void onButtonClickedListener(int position, boolean isPositiveButtonPressed) {

                currentAppointment = mAppointmentList.get(position);
                appointmentIdRespondedTo = currentAppointment.appointmentId;
                if (!isPositiveButtonPressed) {
                    String callTime = dateFormat.format(new Date(Long.parseLong(currentAppointment.dateTime)));
                    AppointmentRejectRequest request = new AppointmentRejectRequest(currentAppointment.appointmentId,
                            callTime, currentAppointment.callType);
                    getSpiceManager().execute(request, new RespondRequestListener());
//                    DeleteAppointment deleteAppointment = new DeleteAppointment(currentAppointment.appointmentId,
//                            mButtonClickListener);
//                    deleteAppointment.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    AppointmentAcceptRequest request = new AppointmentAcceptRequest(currentAppointment.appointmentId);
                    getSpiceManager().execute(request, new RespondRequestListener());
                }
            }
        });

        appointmentList = (ListView) view.findViewById(R.id.appointment_list);
        appointmentList.setAdapter(adapter);
        appointmentList.setOnItemClickListener(this);

        populateMyAppointmentListFromServer();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        populateAppointmentList();
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

//      List<Appointment> appoList1 = appointmentDbApi.getAppointmentHistory(253);
        PatientProfileDbApi patientProfileDbApi = PatientProfileDbApi.getInstance(getApplicationContext());

        mAppointmentList.clear();

        List<Appointment> appoConfirmedList = appointmentDbApi.getAllAppointments(true, startDate, endDate);
//      appoConfirmedList.addAll(appoPendingList);
        for (Appointment appointment : appoConfirmedList) {
            AppointmentListItem appointmentItem = new AppointmentListItem();
            appointmentItem.appointmentId = appointment.id;
            appointmentItem.callType = appointment.callType;
            appointmentItem.dateTime = appointment.dateTime;
            appointmentItem.patientId = appointment.patientId;
            appointmentItem.isConfirmed = true;
            Patient patient = patientProfileDbApi.getProfile(String.valueOf(appointment.patientId));
            if (patient != null) {
                appointmentItem.patientName = patient.name;
                appointmentItem.profilePicUrl = patient.avatarUrl;
            }

            appointmentItem.listItemType = PatientListItem.LIST_ITEM_TYPE_APPROVED;
            mAppointmentList.add(appointmentItem);
        }

        List<Appointment> appoPendingList = appointmentDbApi.getAllAppointments(false);
        for (Appointment appointment : appoPendingList) {

            AppointmentListItem appointmentItem = new AppointmentListItem();
            appointmentItem.appointmentId = appointment.id;
            appointmentItem.callType = appointment.callType;
            appointmentItem.dateTime = appointment.dateTime;
            appointmentItem.patientId = appointment.patientId;

            Patient patient = patientProfileDbApi.getProfile(String.valueOf(appointment.patientId));
            if (patient != null) {
                appointmentItem.patientName = patient.name;
                appointmentItem.profilePicUrl = patient.avatarUrl;
            }
            appointmentItem.listItemType = PatientListItem.LIST_ITEM_TYPE_PENDING;
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

    private CalendarView.OnDateChangeListener mDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            populateAppointmentList();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mAppointmentList != null && mAppointmentList.size() > position) {
            AppointmentListItem appointment = mAppointmentList.get(position);
            long dateTime = Util.getTimeInLongFormat(appointment.dateTime);
            Log.i("Appointment", System.currentTimeMillis() + " < " +(dateTime + Constants.THIRTY_MINUTES));
            if (System.currentTimeMillis() < (dateTime + Constants.THIRTY_MINUTES)) {
                if (appointment.isConfirmed) {
                    Bundle bundle;
                    editAppointmentDialog =
                            EditAppointmentDialog.newInstance(mOnAppointmentOptionClicked, appointment);
                    FragmentManager fragmentManager = getActivity().getFragmentManager();

                    editAppointmentDialog.show(fragmentManager, "EditAppointmentDialogFragment");
                }
            }
            else{
                Toast.makeText(getActivity(), "Appointment time over", Toast.LENGTH_LONG).show();
            }
        }

    }

    private OnAppointmentOptionButtonClickListener mOnAppointmentOptionClicked =
            new OnAppointmentOptionButtonClickListener() {
                @Override
                public void onButtonClicked(int whichButtonClicked, AppointmentListItem appointmentListItem) {

                    if (whichButtonClicked == OnAppointmentOptionButtonClickListener.BTN_CHANGE_TIME) {

//                        EditAppointmentRequest request = new EditAppointmentRequest(LoginInfo.userId, appointmentListItem.patientId,
//                                appointmentListItem.dateTime, appointmentListItem.dateTime, appointmentListItem.callType);
//                        getSpiceManager().execute(request, new RescheduleRequestListener());

                        Intent intent = new Intent(getActivity(), EditAppointmentActivity.class);
                        intent.putExtra("currentAppointment", appointmentListItem);
                        intent.putExtra("doctorId", LoginInfo.userId);
                        startActivity(intent);

                    } else if (whichButtonClicked == OnAppointmentOptionButtonClickListener.BTN_CANCEL) {
                        String callTime = dateFormat.format(new Date(Long.parseLong(appointmentListItem.dateTime)));
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(getString(R.string.cancelling_appointment));
                        progressDialog.show();

                        AppointmentRejectRequest request = new AppointmentRejectRequest(appointmentListItem.appointmentId,
                                callTime, appointmentListItem.callType);
                        getSpiceManager().execute(request, new RespondRequestListener());
                    }
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

                    while (iter.hasNext()) {
                        appointment = iter.next();
                        try {
                            appointment.dateTime = String.valueOf(dateFormat.parse(appointment.dateTime).getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        if (appointmentDbApi.isAppointmentPresent(appointment.id)) {
                            appointmentDbApi.updateAppointment(appointment.id, appointment);
                        } else {
                            appointmentDbApi.saveAppointment(appointment);
                        }
//                        }

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

            populateAppointmentList();
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
            if (baseResponse != null && baseResponse.status != null && baseResponse.status.message != null) {
                if ("Appointment accepted".equalsIgnoreCase(baseResponse.status.message)) {
                    AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                    boolean isConfirmed = appointmentDbApi.acceptAppointment(appointmentIdRespondedTo);
                    Toast.makeText(getActivity(), getString(R.string.appointment_accepted), Toast.LENGTH_LONG).show();
                    populateAppointmentList();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.appointment_rejected), Toast.LENGTH_LONG).show();
                    if (mAppointmentList != null) {
                        ListIterator<AppointmentListItem> iterator = mAppointmentList.listIterator();
                        while (iterator.hasNext()) {
                            AppointmentListItem appointmentListItem = iterator.next();
                            if (appointmentListItem.appointmentId == appointmentIdRespondedTo) {
                                iterator.remove();
                                AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                                appointmentDbApi.deleteAppointment(appointmentListItem.appointmentId);
                                break;
                            }
                        }
                        populateAppointmentList();
                    }
                }
            }
        }
    }

    /***********
     * RE SCHEDULE APPOINTMENT
     *********/

    private class RescheduleRequestListener implements RequestListener<EditAppointmentResponse> {
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
        public void onRequestSuccess(EditAppointmentResponse response) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if (response != null && response.status != null && response.status.message != null) {
                if (response.status.message.startsWith("Appointment re-scheduled successfully")) {
                    AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());

                    appointmentDbApi.updateAppointment(response.data.id, response.data);

                    Toast.makeText(getActivity(), getString(R.string.appointment_rescheduled) +
                            " " + response.data.dateTime, Toast.LENGTH_LONG).show();
                }
            }
            populateMyAppointmentListFromServer();
        }
    }


    /*********
     * REJECT APPOINTMENT NETWORK COMMUNICATION
     ***********/
     /*
        Reject Appointment
        Retrofit is unable to send body parameter with method type delete
         This is a workaround for this retrofit limitation
     */

    private class DeleteAppointment extends AsyncTask<String, String, String> {

        private int appointmentId;
        private String body;
        private OnButtonClickedListener mButtonClickListener;

        public DeleteAppointment(int appointmentId, OnButtonClickedListener mButtonClickListener) {
            this.appointmentId = appointmentId;
            this.mButtonClickListener = mButtonClickListener;

            progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(), getString(R.string.processing));
            progressDialog.show();

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("apiKey", Constants.API_KEY);
                jsonObj.put("password", LoginInfo.hashedPassword);
                jsonObj.put("deviceUnique", Constants.deviceUnique);
                jsonObj.put("email", LoginInfo.userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            body = jsonObj.toString();
        }

        @Override
        protected String doInBackground(String... param) {

            String response = null;
            String line = "";
            URL url;
            HttpURLConnection urlConnection = null;
            BufferedReader rd;

            try {
                url = new URL("http://188.166.55.204:9000/deleteappointment/" + appointmentId);
                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out);
                int code = urlConnection.getResponseCode();
                if (code == 200) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    response = Util.readDataFromInputStream(in);
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response;
        }

        private void writeStream(OutputStream stream)
                throws IOException {

            OutputStream out = new BufferedOutputStream(stream);

            if (body != null) {
                out.write(URLEncoder.encode(body, "UTF-8")
                        .getBytes());
            }
            out.flush();
        }


        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (mButtonClickListener != null) {
                if (s != null && s.equalsIgnoreCase("Appointment deleted successfully")) {
                    mButtonClickListener.onButtonClickedListener(-1, true);
                } else {
                    mButtonClickListener.onButtonClickedListener(-1, false);
                }
            }
        }
    }

    private OnButtonClickedListener mButtonClickListener = new OnButtonClickedListener() {
        @Override
        public void onButtonClickedListener(int position, boolean isSuccessful) {
            if (isSuccessful) {
                Toast.makeText(getActivity(), getString(R.string.appointment_deleted),
                        Toast.LENGTH_LONG).show();
            }
        }
    };

}
