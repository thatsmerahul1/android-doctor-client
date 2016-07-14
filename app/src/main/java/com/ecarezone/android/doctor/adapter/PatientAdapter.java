package com.ecarezone.android.doctor.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.fragment.MyPatientListFragment;
import com.ecarezone.android.doctor.model.database.ChatDbApi;
import com.ecarezone.android.doctor.model.pojo.PatientListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 20109804 on 4/21/2016.
 */
public class PatientAdapter extends BaseAdapter {
    private final static String TAG = PatientAdapter.class.getSimpleName();
    private Activity activity;
    private ArrayList<PatientListItem> patientList;
    private static LayoutInflater inflater;
    private MyPatientListFragment.OnButtonClicked mOnButtonClickedListener;
    private boolean fromMessage;

    private static final int TYPES_COUNT = 2;
    private static final int TYPE_PENDING = 0;
    private static final int TYPE_MY_CARE = 1;


    public PatientAdapter(Activity activity, ArrayList<PatientListItem> patientList, MyPatientListFragment.OnButtonClicked mOnButtonClickedListener) {
        this.activity = activity;
        this.patientList = patientList;
        this.mOnButtonClickedListener = mOnButtonClickedListener;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int dp = activity.getResources().getDimensionPixelSize(R.dimen.profile_thumbnail_edge_size);
        PatientListItem patient = patientList.get(position);
        if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_PENDING) {
            //pending patient list
            view = inflater.inflate(R.layout.patient_pending_list_item, null, false);
        } else {
            // accepted patient list
            view = inflater.inflate(R.layout.doctor_list_item_layout, null, false);
        }
         if (patient.listItemType == PatientListItem.LIST_ITEM_TYPE_PENDING) {
                ImageView pendingPatientAvatar = (ImageView) view.findViewById(R.id.patient_avatar_of_request);
                TextView pendingPatientName = (TextView) view.findViewById(R.id.myPatient_name);
                Button accept = (Button) view.findViewById(R.id.patient_request_accept);
                Button reject = (Button) view.findViewById(R.id.patient_request_reject);

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnButtonClickedListener.onButtonClickedListener(position, true);
                    }
                });

                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnButtonClickedListener.onButtonClickedListener(position, false);
                    }
                });

                pendingPatientName.setText(patient.name);
                String imageUrl = patient.avatarUrl;

                if (imageUrl != null && imageUrl.trim().length() > 8) {
                    Picasso.with(activity)
                            .load(imageUrl).resize(dp, dp)
                            .centerCrop().placeholder(R.drawable.news_other)
                            .error(R.drawable.news_other)
                            .into(pendingPatientAvatar);
                }

        } else {
            ImageView patientAvatar = (ImageView) view.findViewById(R.id.patient_avatar);
            TextView patientStatus = (TextView) view.findViewById(R.id.patient_status);
            TextView patientName = (TextView) view.findViewById(R.id.patient_name);
            TextView chatCount = (TextView) view.findViewById(R.id.chat_count);
            View patientPresence = view.findViewById(R.id.patient_presence);

            if (patient.status.equalsIgnoreCase("0") ) {
                patientStatus.setText(R.string.doctor_idle);
                patientPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_amber));
            } else if(patient.status.equalsIgnoreCase("1")){
                patientStatus.setText(R.string.doctor_available);
                patientPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
            } else {
                patientStatus.setText(R.string.doctor_busy);
                patientPresence.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
            }
            patientName.setText(patient.name);
            int count = ChatDbApi.getInstance(activity).getUnReadChatCountByUserId(patient.email);
            if(count > 0){
                chatCount.setText(String.valueOf(count));
                chatCount.setVisibility(View.VISIBLE);
            } else {
                chatCount.setText(String.valueOf(0));
                chatCount.setVisibility(View.GONE);
             }
            String imageUrl = patient.userProfile.get(0).avatarUrl;

            if (imageUrl != null && imageUrl.trim().length() > 8) {
                Picasso.with(activity)
                        .load(imageUrl).resize(dp, dp)
                        .centerCrop().placeholder(R.drawable.news_other)
                        .error(R.drawable.news_other)
                        .into(patientAvatar);
            }
        }
        return view;
    }
}

