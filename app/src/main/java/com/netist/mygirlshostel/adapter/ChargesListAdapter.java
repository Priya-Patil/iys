package com.netist.mygirlshostel.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.session_handler.SessionHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 12/18/2017.
 */

public class ChargesListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,String>> chargesList;
    private SessionHelper session;
    private HashMap<String,String> m;

    public ChargesListAdapter(Activity activity, ArrayList<HashMap<String,String>> chargesList) {
        this.activity = activity;
        this.chargesList = chargesList;
        this.session = new SessionHelper(activity);
    }

    @Override
    public int getCount() {
        return chargesList.size();
    }

    @Override
    public Object getItem(int location) {
        return chargesList.get(location);
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
            convertView = inflater.inflate(R.layout.list_layout_charges, null);

        TextView tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
        //TextView tv_age = (TextView) convertView.findViewById(R.id.tv_age);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_room_name);
        TextView tv_date_time = (TextView) convertView.findViewById(R.id.tv_date_time);
        TextView tv_paid = (TextView) convertView.findViewById(R.id.tv_paid);
        TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);
        TextView tv_room_price_adapter=convertView.findViewById(R.id.tv_room_price_adapter);
        TextView tvEditCharge=convertView.findViewById(R.id.tvEditCharge);
        m = chargesList.get(position);

        tv_user_name.setText(m.get("userName"));

        String roomName = m.get("roomName");
        String hallName = m.get("hallName");
        String typeName = m.get("typeName");
        String batchName = m.get("batchName");


        if (roomName != null)
        {
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText(roomName);
        }
        else if (hallName != null){
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText(hallName);
        }
        else if (typeName != null){
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText(typeName);
        }
        else if (batchName != null){
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText(batchName);
        }
        tvEditCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView)parent).performItemClick(view,position,23);
            }
        });

        //tv_age.setText("Age : "+m.get("age"));


        String status = m.get("status");
         if (status.equals("Entered"))
         {
             tv_status.setText("Enter Amount");
         }
         else {
             tv_status.setText(status);
         }

        try {
            if (status.equals("Pending") || status.equals("Approved"))
                tv_date_time.setText(formatServerDate(m.get("bookingDateTime")) + " " + formatServerTime(m.get("bookingDateTime")));
            else if (status.equals("Entered"))
                tv_date_time.setText(formatServerDate(m.get("joiningDateTime")) + " " + formatServerTime(m.get("joiningDateTime")));
            else if (status.equals("Left"))
                tv_date_time.setText(formatServerDate(m.get("leavingDateTime")) + " " + formatServerTime(m.get("leavingDateTime")));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_paid.setText("Total: "+m.get("paid"));
        tv_room_price_adapter.setText("Charge : "+m.get("totalCharges"));

        return convertView;
    }

    private static String formatServerDate (String dateTime) throws ParseException {

        Date initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }

    private static String formatServerTime (String dateTime) throws ParseException {

        Date initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String parsedDate = formatter.format(initDate);

        return parsedDate;
    }
}
