package com.ecarezone.android.doctor.fragment;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ecarezone.android.doctor.MainActivity;
import com.ecarezone.android.doctor.NewsListActivity;
import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.adapter.NewsCategoriesAdapter;
import com.ecarezone.android.doctor.model.News;
import com.ecarezone.android.doctor.model.rest.GetNewsResponse;
import com.ecarezone.android.doctor.model.rest.GetNewsRequest;
import com.ecarezone.android.doctor.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

/**
 * Created by CHAO WEI on 5/25/2015.
 */
public class NewsCategoriesFragment extends EcareZoneBaseFragment implements GridView.OnItemClickListener {

    public static String NEWS_PARCELABLE = "news";
    public static String NEWS_BUNDLE = "newsBundle";
    public static String NEWS_CATEGORY_NAME = "newsCategoryName";

    private GetNewsResponse mGetNewsResonse = null;
    private GridView mGridView;
    private ProgressDialog mProgressDialog;

    @Override
    protected String getCallerName() {
        return NewsCategoriesFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mProgressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getActivity().getString(R.string.progress_dialog_loading));

        GetNewsRequest getNewsRequest = new GetNewsRequest();
        getSpiceManager().execute(getNewsRequest, "news", DurationInMillis.ONE_MINUTE, new NewsRequestListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_news_categories, container, false);
        mGridView = (GridView) view.findViewById(R.id.grid_view_categories_list);

        mGridView.setOnItemClickListener(this);
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getText(R.string.news_actionbar_title));
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<News> newsList = (ArrayList<News>) mGetNewsResonse.data[position].newsAbstractList;
        // Add the news list & its category name to the bundle and pass it to the intent
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(NEWS_PARCELABLE, newsList);
        bundle.putString(NEWS_CATEGORY_NAME, mGetNewsResonse.data[position].newsCategory);

        startActivity(new Intent(getApplicationContext(), NewsListActivity.class).putExtra(NEWS_BUNDLE, bundle));
    }

    private class NewsRequestListener implements RequestListener<GetNewsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            spiceException.printStackTrace();
        }

        @Override
        public void onRequestSuccess(GetNewsResponse response) {
            mGetNewsResonse = response;
            NewsCategoriesAdapter mNewsCategoriesAdapter
                    = new NewsCategoriesAdapter(getApplicationContext(), mGetNewsResonse.data);

            mGridView.setAdapter(mNewsCategoriesAdapter);
            mProgressDialog.dismiss();
        }
    }
}
