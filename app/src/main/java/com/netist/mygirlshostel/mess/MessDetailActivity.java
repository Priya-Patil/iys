package com.netist.mygirlshostel.mess;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MessDetailActivity extends BaseActivity implements View.OnClickListener{

    private String tag_string_req = "", messId, messName, messLocation, messImage, mobileNo, person, typeCount,
                        userId,userName,name, date, time, formatedDate;
    double latitude, longitude;
    int year , month, day, hours, minute;
    String monthStr;
    Button btn_action, btn_edit, btn_delete;
    Timer refreshTimer;
    TableLayout table;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_detail);
        prefManager=new PrefManager(MessDetailActivity.this);

        //for table
        setmessDetails();
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Mess Details");

        messId = getIntent().getExtras().getString("messId");
        prefManager.setMESSID_SELECTED(messId);
        Toast.makeText(getApplicationContext(), "hostelId"+messId, Toast.LENGTH_LONG).show();

        SessionHelper session = new SessionHelper(getApplicationContext());
        userId = session.getUserID();
        userName = session.getUserName();

        messName = getIntent().getExtras().getString("name");
        messImage = getIntent().getExtras().getString("picture");

        btn_action = (Button)findViewById(R.id.btn_get_booking);
        btn_edit = (Button)findViewById(R.id.btn_edit_profile);
        btn_delete = (Button)findViewById(R.id.btn_delete_profile);

        ((Button) findViewById(R.id.btn_google_map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng position = new LatLng(latitude, longitude);
                Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        if(session.getUserType().equals("admin"))
        {
            btn_action.setText("Track");
        }
        else if(session.getUserType().equals("mess"))
        {
            btn_action.setText("Edit Details");
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
        }
        else if(session.getUserType().equals("user"))
        {
            btn_action.setText("Booking");
            btn_action.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
        }

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_action.getText().toString().trim().equals("Track")) {
                    Intent intent = new Intent(getApplicationContext(), MessBookingListActivity.class);
                    intent.putExtra("messId", messId);
                    startActivity(intent);
                }
                else if(btn_action.getText().toString().trim().equals("Edit Details")) {
                    Intent intent = new Intent(getApplicationContext(), MessEditorActivity.class);
                    intent.putExtra("messId", messId);
                    startActivity(intent);
                    MessDetailActivity.this.finish();
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
                        final ProgressDialog loading = ProgressDialog.show(MessDetailActivity.this,"Uploading...","Please wait...",false,false);

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                ApiConfig.urlMessBookingReg, new Response.Listener<String>() {

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
                                        sendMessPush();
                                        //Intent intent = new Intent(MessEditorActivity.this,MessListActivity.class);
                                        //startActivity(intent);
                                        MessDetailActivity.this.finish();
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
                                params.put("messId",messId);
                                //params.put("userType",type);
                                params.put("userName",name);

                                params.put("bookingDateTime",formatedDate);
                                //params.put("healthIssue",issue);

                                Log.e("URL", ApiConfig.urlMessBookingReg);
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
                Intent intent = new Intent(getApplicationContext(), MessEditorActivity.class);
                intent.putExtra("messId", messId);
                startActivity(intent);
                MessDetailActivity.this.finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MessDetailActivity.this);
                alert.setMessage("Do you want to delete the "+messName+"'s Details?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMess();
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

    private void sendMessPush() {
        final String title = "Booking";
        final String message = "You got new  booking.. Please check it.";
        final String type = "mess";
        final String id = messId;

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
        if (refreshTimer != null)
        {
            refreshTimer.cancel();
            refreshTimer = null;
        }

        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        refreshTimer = new Timer("RefreshDetailsTimer", true);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setDetails();
            }
        }, 0, 4000);
    }

    private void setDetails()
    {
        tag_string_req = "MessDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlMessList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    for (int i = 0; i < responseArr.length(); i++)
                    {
                        JSONObject obj = responseArr.getJSONObject(i);

                        if (i == 0)
                        {
                            messName = obj.getString("name");
                            messLocation = obj.getString("location");
                            mobileNo = obj.getString("mobile");
                            person = obj.getString("person");
                            typeCount = obj.getString("type_count");
                            latitude = Double.parseDouble(obj.getString("latitude"));
                            longitude = Double.parseDouble(obj.getString("longitude"));

                            ((TextView) findViewById(R.id.tv_name)).setText(messName);
                            ((TextView) findViewById(R.id.tv_location)).setText(messLocation);
                            ((TextView) findViewById(R.id.tv_type_count)).setText(typeCount);
                            ((TextView) findViewById(R.id.tv_contact_no)).setText(mobileNo);
                            ((TextView) findViewById(R.id.tv_person)).setText(person);

                            String picture = ApiConfig.urlMessesImage + obj.getString("picture");
                            if (!picture.equals(messImage)) {
                                messImage = picture;
                                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                if (imageLoader == null)
                                    imageLoader = AppController.getInstance().getImageLoader();
                                CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

                                thumbNail.setImageUrl(messImage, imageLoader);
                            }
                        }

                    }

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

                params.put("id",messId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDate(int year, int month, int day) {

        if(month <10)
            monthStr = "0"+month;
        else
            monthStr = ""+month;


    }

    private void deleteMess()
    {
        tag_string_req = "DeleteMessRequest";

        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlMessReg, new Response.Listener<String>() {

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
                        MessDetailActivity.this.finish();
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

                params.put("action","mess_delete");
                params.put("messId",messId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //table

    //details
    private void setmessDetails()
    {
        tag_string_req = "HostelDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlAddMessType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    TableLayout table = (TableLayout)findViewById(R.id.tl_mess_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_room_row,null);
                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView)row.findViewById(R.id.tv_room_name)).setText("Mess Type");
                    ((TextView)row.findViewById(R.id.tv_total_bed_count)).setText("Charges");
                    ((TextView)row.findViewById(R.id.tv_occupancy_bed_count)).setText("Occupancy");
                    ((TextView)row.findViewById(R.id.tv_occupancy_bed_count)).setVisibility(View.GONE);
                    ((TextView)row.findViewById(R.id.tv_availability_bed_count)).setText("Availability");
                    ((TextView)row.findViewById(R.id.tv_availability_bed_count)).setVisibility(View.GONE);
                    table.addView(row);

                    // Parsing json
                    for (Integer room_no = 0; room_no < responseArr.length(); room_no++) {

                        JSONObject obj = responseArr.getJSONObject(room_no);
                        if (room_no == 0) {

                        }

                        // Add rows of the room no dynamically
                        row = (TableRow) inflater.inflate(R.layout.layout_room_row,null);
                        ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("type_name"));
                        ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText(obj.getString("type_charge"));
                        ((TextView) row.findViewById(R.id.tv_occupancy_bed_count)).setText(obj.getString("type_name"));
                        ((TextView) row.findViewById(R.id.tv_occupancy_bed_count)).setVisibility(View.GONE);
                        ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText(obj.getString("type_name"));
                        ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setVisibility(View.GONE);
                        table.addView(row);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                    Toast.makeText(getApplicationContext(), "jjjj"+e, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", "1");
                params.put("messId",messId);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        //end
    }

    @Override
    public void onClick(View v) {

    }
}
