package com.netist.mygirlshostel.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.constants.PrefManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Piyuuu on 9/20/2018.
 */

public class ViewAllVehicleAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,String>> hostelItems;
    PrefManager prefManager;
   /* ImageLoader imageLoader = AppController.getInstance().getImageLoader();*/

    public ViewAllVehicleAdapter(Activity activity, ArrayList<HashMap<String,String>> hostelItems) {
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
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_layout_vehicle_view, null);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView)parent).performItemClick(view, position, 0);
            }
        });
        

       /* if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) convertView.findViewById(R.id.thumbnail);
       */

        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView mobile = (TextView) convertView.findViewById(R.id.tv_mobile);

        prefManager=new PrefManager(activity);



        // getting movie data for the row
        HashMap<String,String> m = hostelItems.get(position);
        name.setText("Amount : " + m.get("amount" ));
        mobile.setText("Date : " + m.get("created") );
        return convertView;
    }
}