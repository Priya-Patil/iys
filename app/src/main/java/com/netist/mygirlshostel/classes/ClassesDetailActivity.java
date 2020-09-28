package com.netist.mygirlshostel.classes;

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

public class ClassesDetailActivity extends BaseActivity implements View.OnClickListener{

    private String tag_string_req = "", classesId, classesName, classesLocation, classesImage, mobileNo, person, batchCount,
                                        userId,userName,name, date, time, formatedDate;
    double latitude, longitude;
    int year , month, day, hours, minute;
    String monthStr;
    Button btn_action, btn_edit, btn_delete;
    Timer refreshTimer;

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_detail);
        prefManager=new PrefManager(ClassesDetailActivity.this);

        //for table
        setclassDetails();

        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Classes Details");

        SessionHelper session = new SessionHelper(getApplicationContext());

        classesId = getIntent().getExtras().getString("classesId");
        prefManager.setCLASSID_SELECTED(classesId);
        Toast.makeText(getApplicationContext(), "hostelId"+classesId, Toast.LENGTH_LONG).show();


        userId = session.getUserID();
        userName = session.getUserName();
        classesName = getIntent().getExtras().getString("classesName");
        classesImage = getIntent().getExtras().getString("picture");

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
        else if(session.getUserType().equals("classes"))
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
                    Intent intent = new Intent(getApplicationContext(), ClassesBookingListActivity.class);
                    intent.putExtra("classesId", classesId);
                    startActivity(intent);
                }
                else if(btn_action.getText().toString().trim().equals("Edit Details")) {
                    Intent intent = new Intent(getApplicationContext(), ClassesEditorActivity.class);
                    intent.putExtra("classesId", classesId);
                    startActivity(intent);
                    ClassesDetailActivity.this.finish();
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
                        final ProgressDialog loading = ProgressDialog.show(ClassesDetailActivity.this,"Uploading...","Please wait...",false,false);

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                ApiConfig.urlClassesBookingReg, new Response.Listener<String>() {

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
                                        sendClassesPush();
                                        //Intent intent = new Intent(ClassesEditorActivity.this,ClassesListActivity.class);
                                        //startActivity(intent);
                                        ClassesDetailActivity.this.finish();
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
                                params.put("classesId",classesId);
                                //params.put("userType",type);
                                params.put("userName",name);

                                params.put("bookingDateTime",formatedDate);
                                //params.put("healthIssue",issue);

                                Log.e("URL", ApiConfig.urlClassesBookingReg);
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
                Intent intent = new Intent(getApplicationContext(), ClassesEditorActivity.class);
                intent.putExtra("classesId", classesId);
                startActivity(intent);
                ClassesDetailActivity.this.finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ClassesDetailActivity.this);
                alert.setMessage("Do you want to delete the "+classesName+"'s Details?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteClasses();
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

    private void sendClassesPush() {
        final String title = "Booking";
        final String message = "You got new  booking.. Please check it.";
        final String type = "classes";
        final String id = classesId;

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
        tag_string_req = "ClassesDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesList, new Response.Listener<String>() {

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
                            classesName = obj.getString("name");
                            classesLocation = obj.getString("location");
                            mobileNo = obj.getString("mobile");
                            person = obj.getString("person");
                            batchCount = obj.getString("batch_count");
                            latitude = Double.parseDouble(obj.getString("latitude"));
                            longitude = Double.parseDouble(obj.getString("longitude"));

                            ((TextView) findViewById(R.id.tv_name)).setText(classesName);
                            ((TextView) findViewById(R.id.tv_location)).setText(classesLocation);
                            ((TextView) findViewById(R.id.tv_batch_count)).setText(batchCount);
                            ((TextView) findViewById(R.id.tv_contact_no)).setText(mobileNo);
                            ((TextView) findViewById(R.id.tv_person)).setText(person);

                            String picture = ApiConfig.urlClassesImage + obj.getString("picture");
                            if (!picture.equals(classesImage)) {
                                classesImage = picture;
                                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                if (imageLoader == null)
                                    imageLoader = AppController.getInstance().getImageLoader();
                                CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

                                thumbNail.setImageUrl(classesImage, imageLoader);
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

                params.put("id",classesId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void deleteClasses()
    {
        tag_string_req = "DeleteClassesRequest";

        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesReg, new Response.Listener<String>() {

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
                        ClassesDetailActivity.this.finish();
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

                params.put("action","classes_delete");
                params.put("classesId",classesId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    //table

    //details
    private void setclassDetails()
    {
        tag_string_req = "HostelDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlAddClassType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    TableLayout table = (TableLayout)findViewById(R.id.tl_class_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_room_row,null);
                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView)row.findViewById(R.id.tv_room_name)).setText("Batch Name");
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
                        ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("batch_name"));
                        ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText(obj.getString("batch_charge"));
                        ((TextView) row.findViewById(R.id.tv_occupancy_bed_count)).setText(obj.getString("batch_name"));
                        ((TextView) row.findViewById(R.id.tv_occupancy_bed_count)).setVisibility(View.GONE);
                        ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText(obj.getString("batch_name"));
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
                params.put("classesId",classesId);
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
