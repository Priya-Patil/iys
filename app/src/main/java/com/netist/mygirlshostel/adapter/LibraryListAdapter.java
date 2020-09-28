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
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.web_api_handler.AppController;

public class LibraryListAdapter  extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,String>> libraryItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public LibraryListAdapter(Activity activity, ArrayList<HashMap<String,String>> libraryItems) {
        this.activity = activity;
        this.libraryItems = libraryItems;
    }

    @Override
    public int getCount() {
        return libraryItems.size();
    }

    @Override
    public Object getItem(int location) {
        return libraryItems.get(location);
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
            convertView = inflater.inflate(R.layout.list_layout_library, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView availability = (TextView) convertView.findViewById(R.id.tv_availability);

        // getting movie data for the row
        HashMap<String,String> m = libraryItems.get(position);

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