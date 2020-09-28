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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.hostel.HostelBookingListActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class HostelBookingListAdapter extends BaseAdapter {

private Activity activity;
private LayoutInflater inflater;
private ArrayList<HashMap<String,String>> bookingList;
private SessionHelper session;
private HashMap<String,String> m;
        Button btn_date_time,btn_edit,btn_date ;
public static TextView tv_room_name;
public HostelBookingListAdapter(Activity activity, ArrayList<HashMap<String,String>> bookingList) {
        this.activity = activity;
        this.bookingList = bookingList;
        this.session = new SessionHelper(activity);
        }

@Override
public int getCount() {
        return bookingList.size();
        }

@Override
public Object getItem(int location) {
        return bookingList.get(location);
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
        convertView = inflater.inflate(R.layout.list_layout_hostel_booking, null);

        convertView.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        ((ListView)parent).performItemClick(view, position, 0);
        }
        });
        TextView tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
        TextView tv_hostel_name = (TextView) convertView.findViewById(R.id.tv_hostel_name);
        tv_room_name = (TextView) convertView.findViewById(R.id.tv_room_name);
        TextView tv_no = (TextView) convertView.findViewById(R.id.tv_no);

        //  Toast.makeText(HostelBookingListActivity.this, "No Pending Bookings...!", Toast.LENGTH_SHORT).show();

        //TextView tv_age = (TextView) convertView.findViewById(R.id.tv_age);
        //TextView tv_issue = (TextView) convertView.findViewById(R.id.tv_issue);
        btn_date_time = (Button) convertView.findViewById(R.id.btn_date_time);
        btn_edit = (Button) convertView.findViewById(R.id.btn_edit);
        btn_date = (Button) convertView.findViewById(R.id.btn_date);
        btn_date_time.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        ((ListView)parent).performItemClick(view, position, 0);
        }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        ((ListView)parent).performItemClick(view, position, 0);
        }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        ((ListView)parent).performItemClick(view, position, 0);
        }
        });

        TextView tv_status = (TextView) convertView.findViewById(R.id.tv_status);

        m = bookingList.get(position);

        tv_user_name.setText(m.get("userName"));
        tv_no.setText(m.get("mobile"));

        tv_no.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        ((ListView)parent).performItemClick(view, position, 0);
        }
        });

        String hostelName = m.get("hostelName");
        if (!hostelName.equals(""))
        {
        tv_hostel_name.setVisibility(View.VISIBLE);
        tv_hostel_name.setText(hostelName);
        }

        String roomName = m.get("roomName");
        if (!roomName.equals(""))
        {
        tv_room_name.setVisibility(View.VISIBLE);
        tv_room_name.setText(roomName);
        }


        String status = m.get("status");
        try {
        if (status.equals("Pending") || status.equals("Approved"))
        {
                btn_date_time.setText(formatServerDate(m.get("bookingDateTime")) );
                //btn_edit.setText(formatServerDate(m.get("bookingDateTime")) );

                /* btn_date_time.setText(formatServerDate(m.get("bookingDateTime")) + " " + formatServerTime(m.get("bookingDateTime")));
                btn_edit.setText(formatServerDate(m.get("bookingDateTime")) + " " + formatServerTime(m.get("bookingDateTime")));*/
        }
        else if (status.equals("Entered"))
        {
                btn_date_time.setText(formatServerDate(m.get("joiningDateTime")));
               // btn_edit.setText(formatServerDate(m.get("joiningDateTime")) );
                /*  btn_date_time.setText(formatServerDate(m.get("joiningDateTime")) + " " + formatServerTime(m.get("joiningDateTime")));
                btn_edit.setText(formatServerDate(m.get("joiningDateTime")) + " " + formatServerTime(m.get("joiningDateTime")));*/
        }
        else if (status.equals("Left"))
        {
                btn_date_time.setText(formatServerDate(m.get("leavingDateTime")) );
              //  btn_edit.setText(formatServerDate(m.get("leavingDateTime")) );
                /*  btn_date_time.setText(formatServerDate(m.get("leavingDateTime")) + " " + formatServerTime(m.get("leavingDateTime")));
                btn_edit.setText(formatServerDate(m.get("leavingDateTime")) + " " + formatServerTime(m.get("leavingDateTime")))*/;
        }

        } catch (ParseException e) {
        e.printStackTrace();
        }

        tv_status.setText(status);


       /* if(session.getUserType().trim().equals("hostel"))
            tv_hostel_name.setVisibility(View.GONE);*/

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

private void SetAction(String id, String action)
        {
        Toast.makeText(activity, id + " : " +action, Toast.LENGTH_SHORT).show();
        }

        }