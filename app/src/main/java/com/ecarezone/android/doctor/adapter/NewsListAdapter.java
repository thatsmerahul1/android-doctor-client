package com.ecarezone.android.doctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ecarezone.android.doctor.R;
import com.ecarezone.android.doctor.model.News;

import java.util.ArrayList;

/**
 * Created by L&T Technology Services on 2/25/2016.
 */
public class NewsListAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<News> mNews = null;

    public NewsListAdapter(Context context, ArrayList<News> news) {
        mContext = context;
        mNews = news;
    }

    @Override
    public int getCount() {
        return mNews.size();
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
        NewsListItem newsListItem = null;

        if (convertView == null) {
            Context context = ((parent.getContext() == null) ? mContext : parent.getContext());
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.template_news_list_item, parent, false);
            newsListItem = new NewsListItem(convertView);
            convertView.setTag(newsListItem);
        } else {
            newsListItem = ((NewsListItem) convertView.getTag());
        }

        // Set the news list item with title, and description.
        if ((newsListItem != null) && (mNews != null)) {
            News item = mNews.get(position);
            newsListItem.title.setText(item.newsTitle);
            newsListItem.description.setText(item.newsAbstract);
        }

        return convertView;
    }

    // View holder of each list item
    static class NewsListItem {
        final TextView title;
        final TextView description;

        NewsListItem(final View view) {
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
        }
    }
}
