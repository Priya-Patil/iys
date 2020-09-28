package com.netist.mygirlshostel.adapter;

/**
 * Created by Ganesh on 9/22/2017.
 */


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class NoticeListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> noticeList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public NoticeListAdapter(Activity activity, ArrayList<HashMap<String, String>> noticeList) {
        this.activity = activity;
        this.noticeList = noticeList;
    }

    @Override
    public int getCount() {
        return noticeList.size();
    }

    @Override
    public Object getItem(int location) {
        return noticeList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_layout_notice, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.tv_title);
        TextView details = (TextView) convertView.findViewById(R.id.tv_details);
        TextView dateTime = (TextView) convertView.findViewById(R.id.tv_date_time);

        // getting movie data for the row
        HashMap<String, String> m = noticeList.get(position);

        // thumbnail image
        String images[] = m.get("imgFile").split(";");
        thumbNail.setImageUrl(ApiConfig.urlNoticeImage + images[0], imageLoader);

        title.setText(m.get("title"));
        details.setText(m.get("details"));
        try {
            dateTime.setText(Utils.formatDate(m.get("dateTime")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}