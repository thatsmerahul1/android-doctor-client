package com.ecarezone.android.doctor.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * Created by 20109804 on 4/15/2016.
 */
public class MessagesListFragment extends EcareZoneBaseFragment {

    private ProgressDialog progressDialog;
    private boolean checkProgress;
    private ListView messageListView = null;
    View messageContainer;

    @Override
    protected String getCallerName() {
        return MessagesListFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setHasOptionsMenu(true);
        } catch (Exception e) {
        }
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.main_side_menu_messages));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_message_list, container, false);

        messageListView = (ListView) view.findViewById(R.id.message_list);
        messageContainer = view.findViewById(R.id.message_container);

        return view;
    }

    private void populateMessageList() {
//        SearchDoctorsRequest request =
//                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null);
//        getSpiceManager().execute(request, new PopulateMyCareDoctorListRequestListener());
    }

    //TODO:
    public final class PopulateMessageListRequestListener implements RequestListener<SearchDoctorsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse getDoctorsResponse) {

        }
    }
}
