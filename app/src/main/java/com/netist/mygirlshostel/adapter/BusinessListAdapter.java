package com.netist.mygirlshostel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.web_api_handler.AppController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 1/14/2018.
 */

public class BusinessListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,String>> businessItems;

    public BusinessListAdapter(Activity activity, ArrayList<HashMap<String,String>> businessItems) {
        this.activity = activity;
        this.businessItems = businessItems;
    }

    @Override
    public int getCount() {
        return businessItems.size();
    }

    @Override
    public Object getItem(int location) {
        return businessItems.get(location);
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
            convertView = inflater.inflate(R.layout.list_layout_business, null);

        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.tv_business_name);

        // getting movie data for the row
        HashMap<String, String> m = businessItems.get(position);

        // thumbnail image
        thumbNail.setImageResource(Integer.parseInt(m.get("picture")));

        // name
        name.setText(m.get("name"));

        return convertView;
    }
}
