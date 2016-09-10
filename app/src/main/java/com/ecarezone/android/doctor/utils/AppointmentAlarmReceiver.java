package com.ecarezone.android.doctor.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.ecarezone.android.doctor.MyPatientActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.model.database.PatientProfileDbApi;
import com.ecarezone.android.doctor.model.rest.Patient;


public class AppointmentAlarmReceiver extends BroadcastReceiver {
    public AppointmentAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//       String doctorName = intent.getStringExtra("doctor_name");


        // Log.e("Appointment alarm", "Heartbeat method called. ");
      /*  if(intent.getAction().equalsIgnoreCase("START_ALARM")) {
            String doctorName = intent.getStringExtra("doctor_name");
            String appointment_type = intent.getStringExtra("appointment_type");
            int docId = intent.getIntExtra("docId", 0);
*/
        //
        if(intent.getAction().equalsIgnoreCase("START_ALARM")) {
            String appointment_type = intent.getStringExtra("appointment_type");
            int docId = intent.getIntExtra("patId", 0);

            PatientProfileDbApi patientProfileDbApi = PatientProfileDbApi.getInstance(context);
            Patient patient = patientProfileDbApi.getProfile(String.valueOf(intent.getIntExtra("patId", 0)));
            PendingIntent contentIntent =
                    PendingIntent.getActivity(context, 0, new Intent(context, MyPatientActivity.class), 0);
            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle("Appointment with " + patient.name)
                    .setContentText("You have an "+appointment_type+" appointment with "+patient.name)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setColor(Color.BLUE)
                    .setAutoCancel(true);

            mNotifyBuilder.setContentIntent(contentIntent);
            Notification notification = mNotifyBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(docId, notification);
        }
    }
}
