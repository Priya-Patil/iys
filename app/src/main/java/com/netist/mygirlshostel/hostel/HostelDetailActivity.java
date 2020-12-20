package com.netist.mygirlshostel.hostel;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.GoogleMapActivity;
import com.netist.mygirlshostel.MainActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class HostelDetailActivity extends BaseActivity implements View.OnClickListener{

    private String tag_string_req = "", hostelId, hostelName, hostelLocation, hostelImage,userId,userName,
            hostelTotal, hostelAvailability, roomCount, charges, mobileNo, person, name, date, time, formatedDate;
    int year , month, day, hours, minute;
    double latitude, longitude;
    String monthStr = "";
    Button btn_action, btn_edit, btn_delete;
    Timer refreshTimer;
   public String d_SUM_r_total, d_sum_r_availability;

    PrefManager prefManager;
    int total_rooms=0;

    ArrayList<HashMap<String, String>> chargesList;
    TableLayout table;
    TextView tv_contact_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_detail);

        tv_contact_no=findViewById(R.id.tv_contact_no);
        prefManager=new PrefManager(HostelDetailActivity.this);

        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Hostel Details");

        chargesList = new ArrayList<HashMap<String, String>>();

        final SessionHelper session = new SessionHelper(getApplicationContext());

        userId = session.getUserID();
        userName = session.getUserName();

        hostelId = getIntent().getExtras().getString("hostelId");
        prefManager.setHOSTELID_SELECTED(hostelId);
        // Toast.makeText(getApplicationContext(), "hostelId"+hostelId, Toast.LENGTH_LONG).show();
        hostelName = getIntent().getExtras().getString("name");
        hostelImage = getIntent().getExtras().getString("picture");
        hostelAvailability = getIntent().getExtras().getString("availability");


        //imp
        selectTotalCountAfterDelete();
        updateTotalCountAfterDlete();
       // setDetails();
        setDetailsNew();


        btn_action = (Button)findViewById(R.id.btn_get_booking);
      //  btn_action.setVisibility(View.GONE);
        btn_edit = (Button)findViewById(R.id.btn_edit_profile);
        btn_delete = (Button)findViewById(R.id.btn_delete_profile);


        tv_contact_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+tv_contact_no.getText().toString()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                startActivity(callIntent);

            }
        });
        ((Button) findViewById(R.id.btn_google_map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng position = new LatLng(latitude, longitude);
                Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });


//        SessionHelper session = new SessionHelper(getApplicationContext());
        if(session.getUserType().equals("admin"))
        {
            btn_action.setText("Track");
        }
        else if(session.getUserType().equals("hostel"))
        {
            btn_action.setText("Edit Details");
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
        }
        else if(session.getUserType().equals("user"))
        {
          //  btn_action.setText("Booking");
            btn_action.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
        }

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_action.getText().toString().trim().equals("Track")) {
                    Intent intent = new Intent(getApplicationContext(), HostelBookingListActivity.class);
                    intent.putExtra("hostelId", hostelId);
                    startActivity(intent);
                }
                else if(btn_action.getText().toString().trim().equals("Edit Details")) {

                    Intent intent = new Intent(getApplicationContext(), HostelEditorActivity.class);
                    intent.putExtra("hostelId", hostelId);
                    startActivity(intent);
                    HostelDetailActivity.this.finish();
                }
                else {

                    //-----------Set date----------------
                    final Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    showDate(year, month+1, day);

                    //-----------Set date----------------
                    hours = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);


                    name = userName;
                    Log.d("userName:", name);
                    date = String.valueOf(new StringBuilder().append(day).append("/").append(monthStr).append("/").append(year));
                    Log.d("date:", date);
                    time = String.valueOf(new StringBuilder().append(hours).append(":").append(minute));
                    Log.d("time:", time);



                    {
                        try {
                            formatedDate = Utils.formatDate(date, time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String tag_string_req = "BookingRequest";
                        final ProgressDialog loading = ProgressDialog.show(HostelDetailActivity.this,"Uploading...","Please wait...",false,false);

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                ApiConfig.urlHostelBookingReg, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.e ("Response String", response);
                                //Dismissing the progress dialog
                                loading.dismiss();

                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean error = jObj.getBoolean("error");

                                    Toast.makeText(getApplicationContext(),jObj.getString("msg"), Toast.LENGTH_LONG).show();
                                    // Check for error node in json
                                    if (!error) {

                                        sendUserPush();
                                        sendHostelPush();
                                        //Intent intent = new Intent(HostelEditorActivity.this,HostelListActivity.class);
                                        //startActivity(intent);
                                        HostelDetailActivity.this.finish();
                                    }
                                } catch (JSONException e) {

                                    // JSON error
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.e("Registration Error: ",  error.getMessage());
                                //Dismissing the progress dialog
                                loading.dismiss();
                                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                // Posting parameters to login url
                                Map<String, String> params = new HashMap<String, String>();

                                //type, name, age, gender, date, time, issue;

                                params.put("action","booking_reg");

                                params.put("userId",userId);
                                params.put("hostelId",hostelId);
                                //params.put("userType",type);
                                params.put("userName",name);

                              params.put("bookingDateTime",formatedDate);
                                //params.put("healthIssue",issue);

                                Log.e("URL", ApiConfig.urlHostelBookingReg);
                                Log.e("Register Params: ", params.toString());

                                return params;
                            }
                        };

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

                    }

                }


            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (charges == null || charges.equals(""))
                    return;

                prefManager.setHOSTEL_SERVICE("hostel");
                Intent intent = new Intent(getApplicationContext(), HostelEditorActivity.class);
               // Intent intent = new Intent(getApplicationContext(), AddEditRooms.class);
                intent.putExtra("hostelId", hostelId);
                intent.putExtra("charges", charges);
                startActivity(intent);
                HostelDetailActivity.this.finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HostelDetailActivity.this);
                alert.setMessage("Do you want to delete the "+hostelName+"'s Details?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteHostel();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });
    }

    private void showDate(int year, int month, int day) {

            if(month <10)
                monthStr = "0"+month;
            else
                monthStr = ""+month;


    }


    private void sendHostelPush() {
        final String title = "Booking";
        final String message = "You got new  booking.. Please check it.";
        final String type = "hostel";
        final String id = hostelId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.urlSendSinglePush,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Push Response", response);
                        //Toast.makeText(SendPushActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("type", type);
                params.put("id", id);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void sendUserPush() {
        final String title = "Booking : Registered";
        final String message = "Your booking has been registered. Please wait for the confirmation.";
        final String type = "user";
        final String id = userId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.urlSendSinglePush,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Push Response", response);
                        //Toast.makeText(SendPushActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("type", type);
                params.put("id", id);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    protected void onPause()
    {
        /*if (refreshTimer != null)
        {
            refreshTimer.cancel();
            refreshTimer = null;
        }
*/
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        /*refreshTimer = new Timer("RefreshDetailsTimer", true);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setDetails();
            }
        }, 0, 4000);*/
    }


  /*  //details
    private void setDetails()
    {
        tag_string_req = "HostelDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    TableLayout table = (TableLayout)findViewById(R.id.tl_room_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_room_row,null);
                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView)row.findViewById(R.id.tv_room_name)).setText("Room");
                    ((TextView)row.findViewById(R.id.tv_total_bed_count)).setText("Total");
                    ((TextView)row.findViewById(R.id.tv_occupancy_bed_count)).setText("Occupancy");
                    ((TextView)row.findViewById(R.id.tv_availability_bed_count)).setText("Availability");

                    table.addView(row);

                    // Parsing json
                    for (Integer room_no = 0; room_no < responseArr.length(); room_no++) {

                        JSONObject obj = responseArr.getJSONObject(room_no);
                        if (room_no == 0) {
                            hostelName = obj.getString("name");

                            Log.e( "onResponse: ", room_no.toString() );
                            //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            hostelLocation = obj.getString("location");
                            hostelTotal = obj.getString("total");
                            hostelAvailability = obj.getString("availability");
                            charges = obj.getString("charges");
                            mobileNo = obj.getString("mobile");
                            person = obj.getString("person");
                            latitude = Double.parseDouble(obj.getString("latitude"));
                            longitude = Double.parseDouble(obj.getString("longitude"));

                            roomCount = obj.getString("room_count");
                            Integer room_count = Integer.parseInt(roomCount);

                            ((TextView) findViewById(R.id.tv_name)).setText(hostelName);
                            ((TextView) findViewById(R.id.tv_location)).setText(hostelLocation);
                            ((TextView) findViewById(R.id.tv_room_count)).setText(roomCount);
                            ((TextView) findViewById(R.id.tv_contact_no)).setText(mobileNo);
                            ((TextView) findViewById(R.id.tv_person)).setText(person);

                            String picture = ApiConfig.urlHostelsImage + obj.getString("picture");
                            if (!picture.equals(hostelImage)) {
                                hostelImage = picture;
                                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                if (imageLoader == null)
                                    imageLoader = AppController.getInstance().getImageLoader();
                                CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

                                thumbNail.setImageUrl(hostelImage, imageLoader);
                            }


                        }

                        // Add rows of the room no dynamically
                        row = (TableRow) inflater.inflate(R.layout.layout_room_row,null);
                        total_rooms=total_rooms+Integer.parseInt(obj.getString("total_rooms"));
                       // Log.e( "room: ", obj.getString("total_rooms"));

                        ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("r_name"));
                        ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText(obj.getString("total_rooms"));
                        ((TextView) row.findViewById(R.id.tv_occupancy_bed_count)).setText(obj.getString("r_occupancy"));
                        ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText(obj.getString("r_availability"));
                      //  ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText(obj.getString("total_rooms"));

                        table.addView(row);

                    }

                    ((TextView) findViewById(R.id.tv_availability)).setText("Total : " + total_rooms + ", Availability : " + hostelAvailability);

                    Log.e( "onResponse: ", String.valueOf(total_rooms));

                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                //adapter.notifyDataSetChanged();
                //loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Disimissing the progress dialog
                //loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id",hostelId);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        //end
    }
*/
    private void deleteHostel()
    {
        tag_string_req = "DeleteHostelRequest";

        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    Toast.makeText(getApplicationContext(),jObj.getString("msg"), Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                       // HostelDetailActivity.this.finish();

                        Utility.launchActivity(HostelDetailActivity.this, MainActivity.class,true);
                       // onBackPressed();
                    }
                } catch (JSONException e) {
                    //Disimissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Disimissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("action","hostel_delete");
                params.put("hostelId",hostelId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    //imp

    private void selectTotalCountAfterDelete() {

        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,

                ApiConfig.urlAddEditRoom, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {

                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                        d_SUM_r_total=obj.getString("SUM(total_rooms)");
                        d_sum_r_availability=obj.getString("sum(r_availability)");

                        Log.e( "onResponse: ",d_sum_r_availability );
                        //  Toast.makeText(getApplicationContext(), "ddtotal"+d_SUM_r_total, Toast.LENGTH_LONG).show();
                         //Toast.makeText(getApplicationContext(), "dddd_availability"+d_sum_r_availability, Toast.LENGTH_LONG).show();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                //adapter.notifyDataSetChanged();
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Disimissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id","5");
                params.put("hostelId", hostelId);
                return params;

            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateTotalCountAfterDlete() {

        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,

                ApiConfig.urlAddEditRoom, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {

                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                //adapter.notifyDataSetChanged();
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Disimissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id","6");
                params.put("DSUM_r_total", d_SUM_r_total);
                params.put("Dsum_r_availability", d_sum_r_availability);
                params.put("hostelId", hostelId);
                return params;

            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void setDetailsNew() {

        tag_string_req = "RefreshRoomInfoRequest";
      //  chargesList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    chargesList.clear();

                    table = (TableLayout) findViewById(R.id.tl_room_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_charges_row, null);

                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView) row.findViewById(R.id.tv_name)).setText("Hostel");
                    ((TextView) row.findViewById(R.id.tv_sub_name)).setText("Room");
                    ((TextView) row.findViewById(R.id.tv_rate)).setText("Charge");
                    ((TextView) row.findViewById(R.id.tv_availability)).setText("Availability");
                    ((TextView) row.findViewById(R.id.tv_select)).setText("Booking");
                    table.addView(row);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        final JSONObject obj = responseArr.getJSONObject(i);

                        row = (TableRow) inflater.inflate(R.layout.layout_charges_row, null);

                        ((TextView) row.findViewById(R.id.tv_name)).setText(obj.getString("name"));
                        ((TextView) row.findViewById(R.id.tv_sub_name)).setText(obj.getString("r_name"));
                        ((TextView) row.findViewById(R.id.tv_rate)).setText(obj.getString("r_total"));
                        ((TextView) row.findViewById(R.id.tv_availability)).setText(obj.getString("r_availability"));
                        ((TextView) row.findViewById(R.id.tv_select)).setVisibility(View.GONE);

                        Switch aSwitch =(Switch) row.findViewById(R.id.sw_select);
                        aSwitch.setVisibility(View.VISIBLE);

                        //imp
                        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked){
                                    final TableRow tableRow = (TableRow) buttonView.getParent();
                                    hostelId = chargesList.get(table.indexOfChild(tableRow) -1).get("hostelId");
                                    String roomName = chargesList.get(table.indexOfChild(tableRow) -1).get("r_name");
                                    String price = null;
                                    try {
                                        price = obj.getString("r_total");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getApplicationContext(), "roomName"+roomName, Toast.LENGTH_LONG).show();
                                    // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                  //  setBooking(roomName,price);
                                }else {

                                }
                            }
                        });

                        table.addView(row);
                        //imp
                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("name", obj.getString("name"));
                        listItem.put("r_name", obj.getString("r_name"));
                        listItem.put("charges", obj.getString("charges"));
                        listItem.put("availability", obj.getString("r_availability"));
                        listItem.put("hostelId", obj.getString("hostelId"));
                        //   listItem.put("mobile", prefManager.getMOBILE_SELECTED());

                        chargesList.add(listItem);

                        hostelLocation = obj.getString("location");
                        hostelAvailability = obj.getString("availability");
                        mobileNo = obj.getString("mobile");
                        person = obj.getString("person");
                        latitude = Double.parseDouble(obj.getString("latitude"));
                        longitude = Double.parseDouble(obj.getString("longitude"));

                        roomCount = obj.getString("room_count");
                        Integer room_count = Integer.parseInt(roomCount);

                        ((TextView) findViewById(R.id.tv_name)).setText(hostelName);
                        ((TextView) findViewById(R.id.tv_location)).setText(hostelLocation);
                        ((TextView) findViewById(R.id.tv_room_count)).setText(roomCount);
                        ((TextView) findViewById(R.id.tv_contact_no)).setText(mobileNo);
                        ((TextView) findViewById(R.id.tv_person)).setText(person);

                        String picture = ApiConfig.urlHostelsImage + obj.getString("picture");
                        if (!picture.equals(hostelImage)) {
                            hostelImage = picture;
                            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                            if (imageLoader == null)
                                imageLoader = AppController.getInstance().getImageLoader();
                            CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

                            thumbNail.setImageUrl(hostelImage, imageLoader);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data


                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

               // params.put("list_charges", "");
                params.put("id",hostelId);



                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
