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
import com.ecarezone.android.doctor.model.rest.Patient;

import java.util.ArrayList;

/**
 * Created by 20109804 on 4/15/2016.
 */
public class MessageAdapter extends BaseAdapter {

    private final static String TAG = MessageAdapter.class.getSimpleName();
    private Activity activity;
    //TODO : Message pojo class
    private ArrayList<Patient> messageList;
    private static LayoutInflater inflater;

    public MessageAdapter(Activity activity, ArrayList<Patient> messageList) {
        this.activity = activity;
        this.messageList = messageList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messageList.size();
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
        ViewHolder holder;

        if (convertView == null) {
            view = inflater.inflate(R.layout.message_list_item_layout, null);
            holder = new ViewHolder();
            holder.avatar = (ImageView) view.findViewById(R.id.message_avatar);
            holder.messageTitle = (TextView) view.findViewById(R.id.message_title);
            holder.messageDescription = (TextView) view.findViewById(R.id.message_description);
            holder.messageDate = (TextView) view.findViewById(R.id.message_date);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //TODO : fields in Message class and show avatar based on type of message
//        holder.messageTitle.setText(messageList.get(position).name);
//        holder.messageDescription.setText(messageList.get(position).doctorCategory);
//        holder.messageDate.setText(messageList.get(position).status);
        holder.messageTitle.setText("Joe");
        holder.messageDescription.setText("Hey I want to know..");
        holder.messageDate.setText("Monday");

        return view;
    }

    class ViewHolder {
        ImageView avatar;
        TextView messageTitle;
        TextView messageDescription;
        TextView messageDate;
    }
}
