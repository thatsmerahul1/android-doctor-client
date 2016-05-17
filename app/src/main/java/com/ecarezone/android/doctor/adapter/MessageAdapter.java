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
import com.ecarezone.android.doctor.model.Chat;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.ecarezone.android.doctor.model.rest.Patient;

import java.util.ArrayList;
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


    public MessageAdapter(Activity activity, ArrayList<PatientListItem> patientList) {
        this.activity = activity;
        this.patientList = patientList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         mMessages = new ArrayList<Chat>();
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

        if(patient.isPending) {
            view = inflater.inflate(R.layout.message_list_item_layout, null);

        } else {
            view = inflater.inflate(R.layout.message_chat_list_item, null);
        }
        if(patient.isPending) {
            TextView messageSenderName = (TextView) view.findViewById(R.id.messagesenderName);
            messageSenderName.setText(patient.name);
        } else {
            String count = String.valueOf(ChatDbApi.getInstance(activity).getUnReadChatCountByUserId(patient.email));
            if(!count.equalsIgnoreCase("0")) {
                mMessages = ChatDbApi.getInstance(activity).getChatHistory(patient.email);

                TextView messageSenderName_chat = (TextView) view.findViewById(R.id.messageSenderName_chat);
                TextView message_detail = (TextView) view.findViewById(R.id.messageDetail);
                messageSenderName_chat.setText(patient.name);
                message_detail.setText(mMessages.get(mMessages.size()-1).getMessageText());
            } else {
                view.setVisibility(View.GONE);
            }
        }
        return view;
    }

}
