package com.netist.mygirlshostel.adapter;

/**
 * Created by Ganesh on 9/22/2017.
 */


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.web_api_handler.AppController;

public class HostelListAdapter  extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,String>> hostelItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public HostelListAdapter(Activity activity, ArrayList<HashMap<String,String>> hostelItems) {
        this.activity = activity;
        this.hostelItems = hostelItems;
    }

    @Override
    public int getCount() {
        return hostelItems.size();
    }

    @Override
    public Object getItem(int location) {
        return hostelItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_layout_hostel, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView availability = (TextView) convertView.findViewById(R.id.tv_availability);
        TextView tv_view = (TextView) convertView.findViewById(R.id.tv_view);
        TextView tv_facility = (TextView) convertView.findViewById(R.id.tv_facility);
        ImageView img_location = convertView.findViewById(R.id.img_location);

        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView)parent).performItemClick(view, position, 0);
            }
        });

        tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView)parent).performItemClick(view, position, 0);
            }
        });

        tv_facility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView)parent).performItemClick(view, position, 0);
            }
        });

        // getting movie data for the row
        HashMap<String,String> m = hostelItems.get(position);

        // thumbnail image
        if(!m.get("picture").trim().equals(""))
            thumbNail.setImageUrl( m.get("picture"), imageLoader);

        // name
        name.setText(m.get("name"));

        // total, availability
        availability.setText("Total : " + m.get("total") + " Availability : " + m.get("availability"));

        return convertView;
    }
}