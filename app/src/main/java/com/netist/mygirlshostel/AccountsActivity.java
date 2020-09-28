package com.netist.mygirlshostel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.classes.ClassesListActivity;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.hostel.HostelListActivity;
import com.netist.mygirlshostel.library.LibraryListActivity;
import com.netist.mygirlshostel.mess.MessListActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 12/19/2017.
 */

public class AccountsActivity extends BaseActivity implements View.OnClickListener{

    String hostelId, messId, classesId, libraryId, charges;
    Timer refreshTimer;
    String tag_string_req;
    String picture,account_type="";
    private SessionHelper session;
    Button btn_charges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        setTitle("Accounts Statement");
        session = new SessionHelper(this);
        Bundle bundle = getIntent().getExtras();
        btn_charges=findViewById(R.id.btn_charges);

        if (bundle.getString("hostelId") != null)
        {
            hostelId = bundle.getString("hostelId");
            charges = bundle.getString("charges");
            ((TextView) findViewById(R.id.tv_count)).setText(bundle.getString("room_count"));
            ((TextView)findViewById(R.id.tv_count_label)).setText("Number of Rooms");
            ((TextView) findViewById(R.id.tv_availability)).setText("Total : " + bundle.getString("total") + ", Availability : " + bundle.get("availability"));
            picture = ApiConfig.urlHostelsImage + bundle.getString("picture");
        }
        if (bundle.getString("messId") != null)
        {
            messId = bundle.getString("messId");
            ((TextView) findViewById(R.id.tv_count)).setText(bundle.getString("type_count"));
            ((TextView)findViewById(R.id.tv_count_label)).setText("Number of Types");
            ((TextView) findViewById(R.id.tv_availability)).setText("");
            picture = ApiConfig.urlMessesImage + bundle.getString("picture");
        }
        if (bundle.getString("classesId") != null)
        {
            classesId = bundle.getString("classesId");
            ((TextView) findViewById(R.id.tv_count)).setText(bundle.getString("batch_count"));
            ((TextView)findViewById(R.id.tv_count_label)).setText("Number of Batches");
            ((TextView) findViewById(R.id.tv_availability)).setText("");
            picture = ApiConfig.urlClassesImage + bundle.getString("picture");
        }
        if (bundle.getString("libraryId") != null)
        {
            libraryId = bundle.getString("libraryId");
            charges = bundle.getString("charges");
            ((TextView) findViewById(R.id.tv_count)).setText(bundle.getString("hall_count"));
            ((TextView)findViewById(R.id.tv_count_label)).setText("Number of Halls");
            ((TextView) findViewById(R.id.tv_availability)).setText("Total : " + bundle.getString("total") + ", Availability : " + bundle.get("availability"));
            picture = ApiConfig.urlHostelsImage + bundle.getString("picture");
        }

        ((TextView) findViewById(R.id.tv_name)).setText(bundle.getString("name"));
        ((TextView) findViewById(R.id.tv_location)).setText(bundle.getString("location"));

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

        thumbNail.setImageUrl(picture, imageLoader);

        btn_charges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (session.getUserType())
                {
                    case "admin":
                    {

                        Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                        intent.putExtra("charges", "adminSubCharges");
                        startActivity(intent);
                    }
                    break;
                    case "hostel":
                    {
                        preferenceManager.setAREA_SELECTED("details");
                        Intent intent = new Intent(getApplicationContext(), HostelListActivity.class);
                        intent.putExtra("charges", "");
                        startActivity(intent);
                    }
                    break;
                    case "mess":
                    {
                        Intent intent = new Intent(getApplicationContext(), MessListActivity.class);
                        intent.putExtra("charges", "");
                        startActivity(intent);
                    }
                    break;
                    case "classes":
                    {
                        Intent intent = new Intent(getApplicationContext(), ClassesListActivity.class);
                        intent.putExtra("charges", "");
                        startActivity(intent);
                    }
                    break;
                    case "library":
                        Intent intent = new Intent(getApplicationContext(), LibraryListActivity.class);
                        intent.putExtra("charges", "");
                        startActivity(intent);
                        break;
                }

            }
        });
    }

    @Override
    protected void onPause() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshTimer = new Timer("RefreshDetailsTimer", true);
        Intent getIntent=getIntent();
        if (getIntent.getStringExtra("account_type") != null && getIntent.getStringExtra("account_type") != "") {
            account_type = getIntent.getStringExtra("account_type");
        }

        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.e("eeeeeedfff",""+account_type+session.getUserType());
                if (account_type != "" && session.getUserType().equals("admin")){
                    getServiceProviderAccountList();

                }else {
                    setDetails();
                }
            }
        }, 0, 4000);
    }

    private void setDetails() {
        tag_string_req = "AccountsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        if (hostelId != null)
        {
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlHostelBookingList, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("Response String", response);
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    try {
                        JSONArray responseArr = new JSONArray(response);
                        TableLayout table = (TableLayout) findViewById(R.id.tl_accounts_data);
                        table.removeAllViews();

                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        TableRow row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);
                        // Room No, Total Count, Occupancy Count, Availability Count
                        ((TextView) row.findViewById(R.id.tv_room_name)).setText("Room");
                        ((TextView) row.findViewById(R.id.tv_joining_date)).setText("JD");
                        ((TextView) row.findViewById(R.id.tv_user_name)).setText("Name");
                        ((TextView) row.findViewById(R.id.tv_months)).setText("Months");
                        ((TextView) row.findViewById(R.id.tv_total_charges)).setText("TC");
                        ((TextView) row.findViewById(R.id.tv_paid)).setText("Paid");
                        ((TextView) row.findViewById(R.id.tv_balance)).setText("Balance");
                        table.addView(row);

                        // Parsing json
                        for (Integer i = 0; i < responseArr.length(); i++) {

                            JSONObject obj = responseArr.getJSONObject(i);
                            // Add rows of the room no dynamically
                            row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);

                            String joiningDate = obj.getString("joiningDateTime");

                            ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("roomName"));
                            ((TextView) row.findViewById(R.id.tv_joining_date)).setText(Utils.formatDate(joiningDate));
                            ((TextView) row.findViewById(R.id.tv_user_name)).setText(obj.getString("actualUserName"));

                            String totalCharges = obj.getString("totalCharges");
                            String startChargesDateTime = obj.getString("startChargesDateTime");
                            String status = obj.getString("status");

                            Float months = 0.0f;
                            String endDate = "";
                            if (status.equals("Entered")) {
                                endDate = Utils.currentServerDateTime();
                            } else if (status.equals("Left")) {
                                endDate = obj.getString("leavingDateTime");
                            }

                            months = Utils.getMonths(startChargesDateTime, endDate);

                            ((TextView) row.findViewById(R.id.tv_months)).setText(months.toString());

                            Float total_charges = Float.parseFloat(totalCharges) + Float.parseFloat(charges) * months;
                            ((TextView) row.findViewById(R.id.tv_total_charges)).setText(total_charges.toString());

                            Float paid = new Float(obj.getString("paid"));
                            ((TextView) row.findViewById(R.id.tv_paid)).setText(paid.toString());

                            Float balance = total_charges - paid;
                            ((TextView) row.findViewById(R.id.tv_balance)).setText(balance.toString());

                            table.addView(row);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(tag_string_req, e.getMessage());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                    //loading.dismiss();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("Registration Error: ", error.getMessage());
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("action", "booking_list");
                    params.put("hostelId", hostelId);

                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        if (messId != null)
        {
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlMessBookingList, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("Response String", response);
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    try {
                        JSONArray responseArr = new JSONArray(response);
                        TableLayout table = (TableLayout) findViewById(R.id.tl_accounts_data);
                        table.removeAllViews();

                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        TableRow row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);
                        // Room No, Total Count, Occupancy Count, Availability Count
                        ((TextView) row.findViewById(R.id.tv_room_name)).setText("Type");
                        ((TextView) row.findViewById(R.id.tv_joining_date)).setText("JD");
                        ((TextView) row.findViewById(R.id.tv_user_name)).setText("Name");
                        ((TextView) row.findViewById(R.id.tv_months)).setText("Months");
                        ((TextView) row.findViewById(R.id.tv_total_charges)).setText("TC");
                        ((TextView) row.findViewById(R.id.tv_paid)).setText("Paid");
                        ((TextView) row.findViewById(R.id.tv_balance)).setText("Balance");
                        table.addView(row);

                        // Parsing json
                        for (Integer i = 0; i < responseArr.length(); i++) {

                            JSONObject obj = responseArr.getJSONObject(i);
                            // Add rows of the room no dynamically
                            row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);

                            String joiningDate = obj.getString("joiningDateTime");

                            ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("typeName"));
                            ((TextView) row.findViewById(R.id.tv_joining_date)).setText(Utils.formatDate(joiningDate));
                            ((TextView) row.findViewById(R.id.tv_user_name)).setText(obj.getString("actualUserName"));

                            String totalCharges = obj.getString("totalCharges");
                            String startChargesDateTime = obj.getString("startChargesDateTime");
                            Double charge = obj.getDouble("typeCharge");
                            String status = obj.getString("status");

                            Float months = 0.0f;
                            String endDate = "";
                            if (status.equals("Entered")) {
                                endDate = Utils.currentServerDateTime();
                            } else if (status.equals("Left")) {
                                endDate = obj.getString("leavingDateTime");
                            }

                            months = Utils.getMonths(startChargesDateTime, endDate);

                            ((TextView) row.findViewById(R.id.tv_months)).setText(months.toString());

                            Float total_charges = Float.parseFloat(totalCharges) + charge.floatValue() * months;
                            ((TextView) row.findViewById(R.id.tv_total_charges)).setText(total_charges.toString());

                            Float paid = new Float(obj.getString("paid"));
                            ((TextView) row.findViewById(R.id.tv_paid)).setText(paid.toString());

                            Float balance = total_charges - paid;
                            ((TextView) row.findViewById(R.id.tv_balance)).setText(balance.toString());

                            table.addView(row);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(tag_string_req, e.getMessage());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                    //loading.dismiss();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("Registration Error: ", error.getMessage());
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("action", "booking_list");
                    params.put("messId", messId);

                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        if (classesId != null)
        {
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlClassesBookingList, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("Response String", response);
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    try {
                        JSONArray responseArr = new JSONArray(response);
                        TableLayout table = (TableLayout) findViewById(R.id.tl_accounts_data);
                        table.removeAllViews();

                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        TableRow row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);
                        // Room No, Total Count, Occupancy Count, Availability Count
                        ((TextView) row.findViewById(R.id.tv_room_name)).setText("Batch");
                        ((TextView) row.findViewById(R.id.tv_joining_date)).setText("JD");
                        ((TextView) row.findViewById(R.id.tv_user_name)).setText("Name");
                        ((TextView) row.findViewById(R.id.tv_months)).setText("Months");
                        ((TextView) row.findViewById(R.id.tv_total_charges)).setText("TC");
                        ((TextView) row.findViewById(R.id.tv_paid)).setText("Paid");
                        ((TextView) row.findViewById(R.id.tv_balance)).setText("Balance");
                        table.addView(row);

                        // Parsing json
                        for (Integer i = 0; i < responseArr.length(); i++) {

                            JSONObject obj = responseArr.getJSONObject(i);
                            // Add rows of the room no dynamically
                            row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);

                            String joiningDate = obj.getString("joiningDateTime");

                            ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("batchName"));
                            ((TextView) row.findViewById(R.id.tv_joining_date)).setText(Utils.formatDate(joiningDate));
                            ((TextView) row.findViewById(R.id.tv_user_name)).setText(obj.getString("actualUserName"));

                            String totalCharges = obj.getString("totalCharges");
                            String startChargesDateTime = obj.getString("startChargesDateTime");
                            String status = obj.getString("status");
                            Double charge = obj.getDouble("batchCharge");

                            Float months = 0.0f;
                            String endDate = "";
                            if (status.equals("Entered")) {
                                endDate = Utils.currentServerDateTime();
                            } else if (status.equals("Left")) {
                                endDate = obj.getString("leavingDateTime");
                            }

                            months = Utils.getMonths(startChargesDateTime, endDate);

                            ((TextView) row.findViewById(R.id.tv_months)).setText(months.toString());

                            Float total_charges = Float.parseFloat(totalCharges) + charge.floatValue() * months;
                            ((TextView) row.findViewById(R.id.tv_total_charges)).setText(total_charges.toString());

                            Float paid = new Float(obj.getString("paid"));
                            ((TextView) row.findViewById(R.id.tv_paid)).setText(paid.toString());

                            Float balance = total_charges - paid;
                            ((TextView) row.findViewById(R.id.tv_balance)).setText(balance.toString());

                            table.addView(row);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(tag_string_req, e.getMessage());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                    //loading.dismiss();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("Registration Error: ", error.getMessage());
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("action", "booking_list");
                    params.put("classesId", classesId);

                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }


        if (libraryId != null)
        {
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlLibraryBookingList, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("Response String", response);
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    try {
                        JSONArray responseArr = new JSONArray(response);
                        TableLayout table = (TableLayout) findViewById(R.id.tl_accounts_data);
                        table.removeAllViews();

                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        TableRow row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);
                        // Room No, Total Count, Occupancy Count, Availability Count
                        ((TextView) row.findViewById(R.id.tv_room_name)).setText("Hall");
                        ((TextView) row.findViewById(R.id.tv_joining_date)).setText("JD");
                        ((TextView) row.findViewById(R.id.tv_user_name)).setText("Name");
                        ((TextView) row.findViewById(R.id.tv_months)).setText("Months");
                        ((TextView) row.findViewById(R.id.tv_total_charges)).setText("TC");
                        ((TextView) row.findViewById(R.id.tv_paid)).setText("Paid");
                        ((TextView) row.findViewById(R.id.tv_balance)).setText("Balance");
                        table.addView(row);

                        // Parsing json
                        for (Integer i = 0; i < responseArr.length(); i++) {

                            JSONObject obj = responseArr.getJSONObject(i);
                            // Add rows of the room no dynamically
                            row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);

                            String joiningDate = obj.getString("joiningDateTime");

                            ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("hallName"));
                            ((TextView) row.findViewById(R.id.tv_joining_date)).setText(Utils.formatDate(joiningDate));
                            ((TextView) row.findViewById(R.id.tv_user_name)).setText(obj.getString("actualUserName"));

                            String totalCharges = obj.getString("totalCharges");
                            String startChargesDateTime = obj.getString("startChargesDateTime");
                            String status = obj.getString("status");

                            Float months = 0.0f;
                            String endDate = "";
                            if (status.equals("Entered")) {
                                endDate = Utils.currentServerDateTime();
                            } else if (status.equals("Left")) {
                                endDate = obj.getString("leavingDateTime");
                            }

                            months = Utils.getMonths(startChargesDateTime, endDate);

                            ((TextView) row.findViewById(R.id.tv_months)).setText(months.toString());

                            Float total_charges = Float.parseFloat(totalCharges) + Float.parseFloat(charges) * months;
                            ((TextView) row.findViewById(R.id.tv_total_charges)).setText(total_charges.toString());

                            Float paid = new Float(obj.getString("paid"));
                            ((TextView) row.findViewById(R.id.tv_paid)).setText(paid.toString());

                            Float balance = total_charges - paid;
                            ((TextView) row.findViewById(R.id.tv_balance)).setText(balance.toString());

                            table.addView(row);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(tag_string_req, e.getMessage());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //adapter.notifyDataSetChanged();
                    //loading.dismiss();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("Registration Error: ", error.getMessage());
                    //Disimissing the progress dialog
                    //loading.dismiss();
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("action", "booking_list");
                    params.put("libraryId", libraryId);

                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

    }



    private  void getServiceProviderAccountList() {
        Log.e("INSIDE", "INNNSIDEE");
        final String tag_string_req = "AccountActivity";

        try {
            //    hostelList = new ArrayList<HashMap<String,String>>();

       //     final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlGetSubChargesAccountList, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("Response eeeeeeee", response);
                    //Disimissing the progress dialog
               //     loading.dismiss();
                    try {

                        Log.e("e", "" + response + "c");
                        JSONArray responseArr = new JSONArray(response);
                        TableLayout table = (TableLayout) findViewById(R.id.tl_accounts_data);
                        table.removeAllViews();

                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        TableRow row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);
                        // Room No, Total Count, Occupancy Count, Availability Count

                        ((TextView) row.findViewById(R.id.tv_room_name)).setText("Type");
                        ((TextView) row.findViewById(R.id.tv_joining_date)).setText("JD");
                        ((TextView) row.findViewById(R.id.tv_user_name)).setText("Name");
                        ((TextView) row.findViewById(R.id.tv_months)).setText("Months");
                        ((TextView) row.findViewById(R.id.tv_total_charges)).setText("TC");
                        ((TextView) row.findViewById(R.id.tv_paid)).setText("Paid");
                        ((TextView) row.findViewById(R.id.tv_balance)).setText("Balance");
                        table.addView(row);

                        // Parsing json
                        for (Integer i = 0; i < responseArr.length(); i++) {

                            JSONObject obj = responseArr.getJSONObject(i);
                            // Add rows of the room no dynamically
                            row = (TableRow) inflater.inflate(R.layout.layout_account_row, null);

                            String type = "";
                            if (account_type.equals("1")) {
                                type = "Classes";
                            } else if (account_type.equals("2")) {
                                type = "Hostel";
                            } else if (account_type.equals("3")) {
                                type = "Mess";
                            } else if (account_type.equals("4")) {
                                type = "Library";
                            }


                            ((TextView) row.findViewById(R.id.tv_room_name)).setText("" + type);
                            ((TextView) row.findViewById(R.id.tv_joining_date)).setText(obj.getString("setDate"));
                            ((TextView) row.findViewById(R.id.tv_user_name)).setText(obj.getString("name"));

           /*                 ((TextView) row.findViewById(R.id.tv_months)).setVisibility(View.GONE);

                            ((TextView) row.findViewById(R.id.tv_total_charges)).setVisibility(View.GONE);

                            ((TextView) row.findViewById(R.id.tv_paid)).setVisibility(View.GONE);

                            ((TextView) row.findViewById(R.id.tv_balance)).setVisibility(View.GONE);

*/
                            String totalCharges = obj.getString("totalCharges");
                            String startChargesDateTime = obj.getString("startChargesDateTime");
                            //String status = obj.getString("status");

                            Float months = 0.0f;
                            String endDate = "";
                            //if (status.equals("Entered")) {
                                endDate = Utils.currentServerDateTime();
                         //   } else if (status.equals("Left")) {
                             //   endDate = obj.getString("leavingDateTime");
                            //}

                            months = Utils.getMonths(startChargesDateTime, endDate);

                            ((TextView) row.findViewById(R.id.tv_months)).setText(months.toString());

                            Float total_charges = Float.parseFloat(totalCharges) + Float.parseFloat(totalCharges) * months;
                            ((TextView) row.findViewById(R.id.tv_total_charges)).setText(total_charges.toString());

                           // Float paid = new Float(obj.getString("paid"));
                            Float paid =0.0f;
                                    ((TextView) row.findViewById(R.id.tv_paid)).setText("0.00");

                            Float balance = total_charges - paid;
                            ((TextView) row.findViewById(R.id.tv_balance)).setText(balance.toString());




                            table.addView(row);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(tag_string_req, e.getMessage());
                    }
                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data


                //    loading.dismiss();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("Registration Error: ",  error.getMessage());
                    //Dismissing the progress dialog
                 //   loading.dismiss();
                    Toast.makeText(getApplicationContext(), "onErrorResponse" + error, Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();


                    params.put("account_type", account_type);

                    Log.e("Register Params: ", params.toString());

                    return params;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
         }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {

    }
}