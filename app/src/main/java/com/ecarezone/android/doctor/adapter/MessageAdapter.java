package com.ecarezone.android.doctor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.doctor.AppointmentActivity;
import com.ecarezone.android.doctor.ChatActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.model.Appointment;
import com.ecarezone.android.doctor.model.Chat;
import com.ecarezone.android.doctor.model.database.AppointmentDbApi;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;
import com.ecarezone.android.doctor.view.CircleImageView;
import com.squareup.picasso.Picasso;

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
    private int iconSizeInDp;
    private int todayDate = Calendar.getInstance().get(Calendar.DATE);


    public MessageAdapter(Activity activity, ArrayList<PatientListItem> patientList) {
        this.activity = activity;
        this.patientList = patientList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        iconSizeInDp = activity.getResources().getDimensionPixelSize(R.dimen.unread_profile_thumbnail_size);;
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
            CircleImageView imgView = (CircleImageView) view.findViewById(R.id.messageIcon);

            messageSenderName.setText("From:" + patient.name);
            timeStamp.setText(patient.requestedDate);

//            Picasso.with(activity)
//                    .load(patient.avatarUrl).resize(iconSizeInDp, iconSizeInDp)
//                    .centerCrop().placeholder(R.drawable.request_icon)
//                    .error(R.drawable.news_other)
//                    .into(imgView);


        } else if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_MESSAGE) {

            if (patient.unreadMsgCount > 0) {

                TextView messageSenderName_chat = (TextView) view.findViewById(R.id.messageSenderName_chat);
                TextView message_detail = (TextView) view.findViewById(R.id.messageDetail);
                TextView messageTimestamp = (TextView) view.findViewById(R.id.timeStamp);

                messageSenderName_chat.setText(patient.name);
                message_detail.setText(patient.msgText);
                messageTimestamp.setText(patient.dateTime);
                view.setTag(patient);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag() != null) {
                            PatientListItem patient = (PatientListItem) v.getTag();
                            Intent chatIntent = new Intent(activity, ChatActivity.class);
                            chatIntent.putExtra(Constants.EXTRA_NAME, patient.name);
                            chatIntent.putExtra(Constants.EXTRA_EMAIL, patient.email);
                            activity.startActivity(chatIntent);
                            activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    }
                });

            }
        } else if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_APPOINTMENT) {

            TextView messageSenderName_chat = (TextView) view.findViewById(R.id.messageSenderName_chat);
            TextView message_detail = (TextView) view.findViewById(R.id.messageDetail);
            TextView messageTimestamp = (TextView) view.findViewById(R.id.timeStamp);
            ImageView imgView = (ImageView) view.findViewById(R.id.messageIcon);

            imgView.setImageResource(R.drawable.notification_appointment);

            messageSenderName_chat.setText("Appointment Request");
            message_detail.setText("From:" + patient.name);
            try {
                messageTimestamp.setText(patient.dateTime);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

//            view.setTag(R.string.message_adapter_key, appointment);
//            view.setTag(patient);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                        Appointment appointment = (Appointment) v.getTag(R.string.message_adapter_key);
                    Intent chatIntent = new Intent(activity, AppointmentActivity.class);
                    activity.startActivity(chatIntent);
                    activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                }
            });
        }
        return view;
    }

}
