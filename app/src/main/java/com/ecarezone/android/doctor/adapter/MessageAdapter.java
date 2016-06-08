package com.ecarezone.android.doctor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.Chat;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 20109804 on 4/15/2016.
 */
public class MessageAdapter extends BaseAdapter {

    private final static String TAG = MessageAdapter.class.getSimpleName();
    private Activity activity;
    private static LayoutInflater inflater;
    private ArrayList<PatientListItem> patientList;
    private List<Chat> mMessages;
    private SimpleDateFormat mTimeFormat;
    private SimpleDateFormat mDateFormat;
    private int todayDate = Calendar.getInstance().get(Calendar.DATE);


    public MessageAdapter(Activity activity, ArrayList<PatientListItem> patientList) {
        this.activity = activity;
        this.patientList = patientList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTimeFormat = new SimpleDateFormat("HH:mm");
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public int getCount() {
        return patientList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PatientListItem patient = patientList.get(position);

        if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_PENDING) {
            view = inflater.inflate(R.layout.message_list_item_layout, null);

        } else {
            view = inflater.inflate(R.layout.message_chat_list_item, null);
        }
        if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_PENDING) {
            TextView messageSenderName = (TextView) view.findViewById(R.id.messagesenderName);
            TextView timeStamp = (TextView) view.findViewById(R.id.updateTime);
            messageSenderName.setText(patient.name);

        } else if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_MESSAGE) {
            String count = String.valueOf(ChatDbApi.getInstance(activity).getUnReadChatCountByUserId(patient.email));
            if (!count.equalsIgnoreCase("0")) {
                List<Chat> mMessages = ChatDbApi.getInstance(activity).getChatHistory(patient.email);

                Chat lastMsg = mMessages.get(mMessages.size() - 1);
                TextView messageSenderName_chat = (TextView) view.findViewById(R.id.messageSenderName_chat);
                TextView message_detail = (TextView) view.findViewById(R.id.messageDetail);
                TextView messageTimestamp = (TextView) view.findViewById(R.id.timeStamp);

                messageSenderName_chat.setText(patient.name);
                message_detail.setText(lastMsg.getMessageText());

                String timeStamp;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastMsg.getTimeStamp());
                if (todayDate == calendar.get(Calendar.DATE)) {
                    timeStamp = mTimeFormat.format(lastMsg.getTimeStamp());
                } else {
                    timeStamp = mDateFormat.format(lastMsg.getTimeStamp());
                }
                messageTimestamp.setText(timeStamp);

            } else {
                view.setVisibility(View.GONE);
            }
        } else if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_APPOINTMENT) {

            TextView messageSenderName_chat = (TextView) view.findViewById(R.id.messageSenderName_chat);
            TextView message_detail = (TextView) view.findViewById(R.id.messageDetail);
            TextView messageTimestamp = (TextView) view.findViewById(R.id.timeStamp);

            Appointment appointment = AppointmentDbApi.getInstance(activity).getAppointment(patient.appointmentId);

            messageSenderName_chat.setText("Appointment Request");
            message_detail.setText("From:" + patient.name);
            messageTimestamp.setText(mTimeFormat.format(appointment.getTimeStamp()));

            view.setTag(R.string.message_adapter_key, appointment);
        }
        return view;
    }

}
