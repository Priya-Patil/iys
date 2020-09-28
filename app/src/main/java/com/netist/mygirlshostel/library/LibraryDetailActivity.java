package com.netist.mygirlshostel.library;

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

public class LibraryDetailActivity extends BaseActivity implements View.OnClickListener{

    private String tag_string_req = "", libraryId, libraryName, libraryLocation,userId,userName,
            libraryTotal, libraryAvailability, hallCount,libraryImage, mobileNo, person, name, date, time, formatedDate;
    int year , month, day, hours, minute;
    String monthStr = "";
    double latitude, longitude;
    Button btn_action, btn_edit, btn_delete;
    Timer refreshTimer;
    public String d_SUM_r_total, d_sum_r_availability;
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_detail);
        prefManager=new PrefManager(LibraryDetailActivity.this);

        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Library Details");
        SessionHelper session = new SessionHelper(getApplicationContext());

        userId = session.getUserID();
        userName = session.getUserName();
        libraryId = getIntent().getExtras().getString("libraryId");
        prefManager.setLIBRARYID_SELECTED(libraryId);
        Toast.makeText(getApplicationContext(), "hostelId"+libraryId, Toast.LENGTH_LONG).show();
        //imp
        selectTotalCountAfterDelete();
        updateTotalCountAfterDlete();



        libraryName = getIntent().getExtras().getString("libraryName");
        libraryImage = getIntent().getExtras().getString("picture");

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
        else if(session.getUserType().equals("library"))
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
                    Intent intent = new Intent(getApplicationContext(), LibraryBookingListActivity.class);
                    intent.putExtra("libraryId", libraryId);
                    startActivity(intent);
                }
                else if(btn_action.getText().toString().trim().equals("Edit Details")) {
                    Intent intent = new Intent(getApplicationContext(), LibraryEditorActivity.class);
                    intent.putExtra("libraryId", libraryId);
                    startActivity(intent);
                    LibraryDetailActivity.this.finish();
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
                        final ProgressDialog loading = ProgressDialog.show(LibraryDetailActivity.this,"Uploading...","Please wait...",false,false);

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                ApiConfig.urlLibraryBookingReg, new Response.Listener<String>() {

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
                                        sendLibraryPush();
                                        //Intent intent = new Intent(LibraryEditorActivity.this,LibraryListActivity.class);
                                        //startActivity(intent);
                                        LibraryDetailActivity.this.finish();
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
                                params.put("libraryId",libraryId);
                                //params.put("userType",type);
                                params.put("userName",name);

                                params.put("bookingDateTime",formatedDate);
                                //params.put("healthIssue",issue);

                                Log.e("URL", ApiConfig.urlLibraryBookingReg);
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
                Intent intent = new Intent(getApplicationContext(), LibraryEditorActivity.class);
                intent.putExtra("libraryId", libraryId);
                startActivity(intent);
                LibraryDetailActivity.this.finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(LibraryDetailActivity.this);
                alert.setMessage("Do you want to delete the "+libraryName+"'s Details?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteLibrary();
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

    private void sendLibraryPush() {
        final String title = "Booking";
        final String message = "You got new  booking.. Please check it.";
        final String type = "library";
        final String id = libraryId;

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
        tag_string_req = "LibraryDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    TableLayout table = (TableLayout)findViewById(R.id.tl_hall_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_hall_row,null);
                    // Hall No, Total Count, Occupancy Count, Availability Count
                    ((TextView)row.findViewById(R.id.tv_hall_name)).setText("Hall Name");
                    ((TextView)row.findViewById(R.id.tv_total_count)).setText("Total");
                    ((TextView)row.findViewById(R.id.tv_occupancy_count)).setText("Occupancy");
                    ((TextView)row.findViewById(R.id.tv_availability_count)).setText("Availability");
                    table.addView(row);

                    for (int i = 0; i < responseArr.length(); i++)
                    {
                        JSONObject obj = responseArr.getJSONObject(i);

                        if (i == 0)
                        {
                            libraryName = obj.getString("name");
                            libraryLocation = obj.getString("location");
                            libraryTotal = obj.getString("total");
                            libraryAvailability = obj.getString("availability");
                            mobileNo = obj.getString("mobile");
                            person = obj.getString("person");
                            latitude = Double.parseDouble(obj.getString("latitude"));
                            longitude = Double.parseDouble(obj.getString("longitude"));

                            hallCount = obj.getString("hall_count");
                            Integer hall_count = Integer.parseInt(hallCount);

                            ((TextView) findViewById(R.id.tv_name)).setText(libraryName);
                            ((TextView) findViewById(R.id.tv_location)).setText(libraryLocation);
                            ((TextView) findViewById(R.id.tv_hall_count)).setText(hallCount);
                            ((TextView) findViewById(R.id.tv_contact_no)).setText(mobileNo);
                            ((TextView) findViewById(R.id.tv_person)).setText(person);
                            ((TextView) findViewById(R.id.tv_availability)).setText("Total : " + libraryTotal + ", Availability : " + libraryAvailability);

                            String picture = ApiConfig.urlLibrariesImage + obj.getString("picture");
                            if (!picture.equals(libraryImage)) {
                                libraryImage = picture;
                                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                if (imageLoader == null)
                                    imageLoader = AppController.getInstance().getImageLoader();
                                CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

                                thumbNail.setImageUrl(libraryImage, imageLoader);
                            }
                        }

                        // Add rows of the hall no dynamically
                        row = (TableRow) inflater.inflate(R.layout.layout_hall_row,null);
                        ((TextView) row.findViewById(R.id.tv_hall_name)).setText(obj.getString("h_name"));
                        ((TextView) row.findViewById(R.id.tv_total_count)).setText(obj.getString("h_total"));
                        ((TextView) row.findViewById(R.id.tv_occupancy_count)).setText(obj.getString("h_occupancy"));
                        ((TextView) row.findViewById(R.id.tv_availability_count)).setText(obj.getString("h_availability"));

                        table.addView(row);
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

                params.put("id",libraryId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void deleteLibrary()
    {
        tag_string_req = "DeleteLibraryRequest";

        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryReg, new Response.Listener<String>() {

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
                        LibraryDetailActivity.this.finish();
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

                params.put("action","library_delete");
                params.put("libraryId",libraryId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //imp
    //after delete

    private void selectTotalCountAfterDelete() {

        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,

                ApiConfig.urlAddEditLibraryHall, new Response.Listener<String>() {

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
                        d_SUM_r_total=obj.getString("SUM(h_total)");
                        d_sum_r_availability=obj.getString("sum(h_availability)");

                        //  Toast.makeText(getApplicationContext(), "ddtotal"+d_SUM_r_total, Toast.LENGTH_LONG).show();
                          //Toast.makeText(getApplicationContext(), "dddd_availability"+d_sum_r_availability, Toast.LENGTH_LONG).show();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "eeeee"+e, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "kkkkkkk"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id","5");
                params.put("hostelId", libraryId);
                return params;

            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //after delete

    private void updateTotalCountAfterDlete() {

        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,

                ApiConfig.urlAddEditLibraryHall, new Response.Listener<String>() {

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
                params.put("hostelId", libraryId);
                return params;

            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public void onClick(View v) {

    }
}
