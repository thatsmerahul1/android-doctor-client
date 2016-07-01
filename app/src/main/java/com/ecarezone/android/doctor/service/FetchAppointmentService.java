package com.ecarezone.android.doctor.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.rest.AppointmentRequest;
import com.ecarezone.android.doctor.model.rest.AppointmentResponse;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * class helps in getting all the approved appointments and populate the database.
 * Specially needed when the user may login using multiple devices.
 */
public class FetchAppointmentService extends IntentService{

    private static final String ACTION_FETCH_APPOINTMENTS = "fetchAppointments";

    private static final String EXTRA_PARAM_DOC_ID = "docId";

    private SpiceManager spiceManager = new SpiceManager(RoboEcareSpiceServices.class);

    public SpiceManager getSpiceManager() {
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
        return spiceManager;
    }

    public FetchAppointmentService() {
        super("FetchAppointmentService");
    }

    /**
     * Starts this service to fetch all appointments with the given doctor id. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchAppointment(Context context) {
        Intent intent = new Intent(context, FetchAppointmentService.class);
        intent.setAction(ACTION_FETCH_APPOINTMENTS);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_APPOINTMENTS.equals(action)) {
                handleActionFetchAppointments();
            }
        }
    }

    /**
     * Handle action Fetch Appointments in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchAppointments() {

        AppointmentRequest request =
                new AppointmentRequest(LoginInfo.userId);
        getSpiceManager().execute(request, new FetchAppointmentListRequestListener());

    }


    private class FetchAppointmentListRequestListener implements RequestListener<AppointmentResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
//          fail silently..
        }

        @Override
        public void onRequestSuccess(AppointmentResponse appointmentResponse) {

            if (appointmentResponse != null) {
               DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
                ArrayList<Appointment> appointments = (ArrayList<Appointment>) appointmentResponse.data;

                if (appointments != null) {
                    AppointmentDbApi appointmentDbApi = AppointmentDbApi.getInstance(getApplicationContext());
                    ListIterator<Appointment> iter = appointments.listIterator();
                    Appointment appointment = null;
                    while (iter.hasNext()) {
                        appointment = iter.next();
                        try {
                            appointment.setTimeStamp(String.valueOf(dateFormat.parse(appointment.getTimeStamp()).getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(appointmentDbApi.isAppointmentPresent(appointment.getAppointmentId())){
                            appointmentDbApi.updateAppointment(appointment.getAppointmentId(), appointment);
                        }
                        else {
                            appointmentDbApi.saveAppointment(appointment);
                        }
                    }
                }
            }
        }
    }
}
