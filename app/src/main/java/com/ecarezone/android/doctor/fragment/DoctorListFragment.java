package com.ecarezone.android.doctor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ecarezone.android.doctor.DoctorActivity;
import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.SearchActivity;
import com.ecarezone.android.doctor.adapter.DoctorsAdapter;
import com.ecarezone.android.doctor.config.Constants;
import com.ecarezone.android.doctor.config.LoginInfo;
import com.ecarezone.android.doctor.model.Doctor;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.doctor.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

/**
 * Created by CHAO WEI on 5/25/2015.
 */
public class DoctorListFragment extends EcareZoneBaseFragment {

    public static final String ADD_DOCTOR_DISABLE_CHECK = "addDocotrDisablecheck";

    private static final String TAG = DoctorListFragment.class.getSimpleName();
    private static final int HTTP_STATUS_OK = 200;
    private ListView mycareDoctorListView = null;
    private ListView recommendedDoctorListView = null;
    private ArrayList<Doctor> doctorList;
    private ArrayList<Doctor> recommendedDoctorList;
    private SearchView searchView;
    private DoctorsAdapter mycareDoctorAdapter;
    private DoctorsAdapter recommendedDoctorAdapter;
    View mycareDoctorContainer;
    View recommendedDoctorContainer;
    View doctorsDivider;
    private ProgressDialog progressDialog;
    private boolean checkProgress;

    @Override
    protected String getCallerName() {
        return DoctorListFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setHasOptionsMenu(true);
        } catch (Exception e) {
        }
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.main_side_menu_my_patients));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_action_search);
        Log.i(TAG, "searchMenuItem = " + searchMenuItem);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setIconifiedByDefault(true);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getResources().getString(R.string.doctor_search_hint_text));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                performDoctorSearch(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        View searchEditFrame = searchView.findViewById(R.id.search_edit_frame);
        searchEditFrame.setBackgroundResource(R.drawable.search_edittext_border);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_loading).toString());
        checkProgress = true;
        populateMyCareDoctorList();
        populateRecommendedDoctorList();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null) return false;

        final int itemId = item.getItemId();
        if (itemId == R.id.menu_action_search) {
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_doctor_list_2, container, false);

        mycareDoctorListView = (ListView) view.findViewById(R.id.mycare_doctors_list);
        recommendedDoctorListView = (ListView) view.findViewById(R.id.recommended_doctors_list);
        mycareDoctorContainer = view.findViewById(R.id.mycare_doctors_container);
        recommendedDoctorContainer = view.findViewById(R.id.recommended_doctors_container);
        doctorsDivider = view.findViewById(R.id.doctors_divider);

        return view;
    }

    private void populateMyCareDoctorList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null);
        getSpiceManager().execute(request, new PopulateMyCareDoctorListRequestListener());
    }

    private void populateRecommendedDoctorList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(null, null, null, null, null, null);
        getSpiceManager().execute(request, new RecommendeDoctorListRequestListener());
    }

    private void performDoctorSearch(String queryString) {
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_search).toString());
        if (TextUtils.isEmpty(queryString)) {
            queryString = " ";
        }
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(null, LoginInfo.userName,
                        LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique, queryString);
        getSpiceManager().execute(request, new DoSearchRequestListener());
    }


    public final class PopulateMyCareDoctorListRequestListener implements RequestListener<SearchDoctorsResponse> {

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
            if (getDoctorsResponse.status.code == HTTP_STATUS_OK) {
                doctorList = (ArrayList<Doctor>) getDoctorsResponse.data;
                if (doctorList.size() == 0) {
                    mycareDoctorContainer.setVisibility(View.GONE);
                    doctorsDivider.setVisibility(View.GONE);
                    if (recommendedDoctorList != null && recommendedDoctorList.size() > 0) {
                        recommendedDoctorContainer.setVisibility(View.VISIBLE);
                    } else {
                        recommendedDoctorContainer.setVisibility(View.GONE);
                    }
                } else if (doctorList.size() > 0) {
                    mycareDoctorAdapter = new DoctorsAdapter(getActivity(), doctorList);
                    mycareDoctorListView.setAdapter(mycareDoctorAdapter);
                    mycareDoctorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.i(TAG, "position = " + position);
                            Bundle data = new Bundle();
                            data.putParcelable(Constants.DOCTOR_DETAIL, doctorList.get(position));
                            final Activity activity = getActivity();
                            if (activity != null) {
                                Intent showDoctorIntent = new Intent(activity.getApplicationContext(), DoctorActivity.class);
                                showDoctorIntent.putExtra(Constants.DOCTOR_DETAIL, data);
                                showDoctorIntent.putExtra(ADD_DOCTOR_DISABLE_CHECK, true);
                                activity.startActivity(showDoctorIntent);
                                activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            }
                        }
                    });

                    mycareDoctorContainer.setVisibility(View.VISIBLE);
                    doctorsDivider.setVisibility(View.VISIBLE);

                    if (recommendedDoctorList != null && recommendedDoctorList.size() > 0) {
                        recommendedDoctorContainer.setVisibility(View.VISIBLE);
                    } else {
                        recommendedDoctorContainer.setVisibility(View.GONE);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get doctors: " + getDoctorsResponse.status.message, Toast.LENGTH_LONG).show();
            }
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }
    }

    public final class DoSearchRequestListener implements RequestListener<SearchDoctorsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse searchDoctorsResponse) {
            if (searchDoctorsResponse.status.code == HTTP_STATUS_OK) {
                ArrayList<Doctor> doctorList = (ArrayList<Doctor>) searchDoctorsResponse.data;
                Bundle data = new Bundle();
                data.putParcelableArrayList(Constants.DOCTOR_LIST, doctorList);
                final Activity activity = getActivity();
                if (activity != null) {
                    Intent searchIntent = new Intent(activity.getApplicationContext(), SearchActivity.class);
                    searchIntent.putExtra(Constants.DOCTOR_LIST, data);
                    activity.startActivity(searchIntent);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to search: " + searchDoctorsResponse.status.message, Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    }

    public final class RecommendeDoctorListRequestListener implements RequestListener<SearchDoctorsResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse getRecommendedDoctorsResponse) {
            if (getRecommendedDoctorsResponse.status.code == HTTP_STATUS_OK) {
                recommendedDoctorList = (ArrayList<Doctor>) getRecommendedDoctorsResponse.data;
                if (recommendedDoctorList.size() > 0) {
                    recommendedDoctorAdapter = new DoctorsAdapter(getActivity(), recommendedDoctorList);
                    recommendedDoctorListView.setAdapter(recommendedDoctorAdapter);
                    recommendedDoctorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                         @Override
                                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                             Log.i(TAG, "position = " + position);
                                                                             Bundle data = new Bundle();
                                                                             data.putParcelable(Constants.DOCTOR_DETAIL, recommendedDoctorList.get(position));
                                                                             final Activity activity = getActivity();
                                                                             if (activity != null) {
                                                                                 Intent showDoctorIntent = new Intent(activity.getApplicationContext(), DoctorActivity.class);
                                                                                 showDoctorIntent.putExtra(Constants.DOCTOR_DETAIL, data);
                                                                                 if (checkDocotorExist(position)) {
                                                                                     showDoctorIntent.putExtra(ADD_DOCTOR_DISABLE_CHECK, true);
                                                                                 } else {
                                                                                     showDoctorIntent.putExtra(ADD_DOCTOR_DISABLE_CHECK, false);
                                                                                 }
                                                                                 activity.startActivity(showDoctorIntent);
                                                                                 activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                                                             }
                                                                         }
                                                                     }

                    );

                    recommendedDoctorContainer.setVisibility(View.VISIBLE);

                    if (doctorList != null && doctorList.size() > 0)

                    {
                        mycareDoctorContainer.setVisibility(View.VISIBLE);
                        doctorsDivider.setVisibility(View.VISIBLE);
                    } else

                    {
                        mycareDoctorContainer.setVisibility(View.GONE);
                        doctorsDivider.setVisibility(View.GONE);
                    }
                } else if (recommendedDoctorList.size() == 0) {
                    recommendedDoctorContainer.setVisibility(View.GONE);
                    if (doctorList != null && doctorList.size() > 0) {
                        mycareDoctorContainer.setVisibility(View.VISIBLE);
                        doctorsDivider.setVisibility(View.VISIBLE);
                    } else {
                        mycareDoctorContainer.setVisibility(View.GONE);
                        doctorsDivider.setVisibility(View.GONE);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get doctors: " + getRecommendedDoctorsResponse.status.message, Toast.LENGTH_LONG).show();
            }
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }

        }
    }

    private boolean checkDocotorExist(int position) {
        Long id = ((Doctor) recommendedDoctorList.get(position)).doctorId;
        for (Doctor doctor : doctorList) {
            if (doctor.doctorId.equals(id)) {
                return true;
            }

        }
        return false;

    }


}
