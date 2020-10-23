package com.netist.mygirlshostel.hostel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.AccountsActivity;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.BusinessListActivity;
import com.netist.mygirlshostel.ChargesListActivity;
import com.netist.mygirlshostel.map.MapsActivity;
import com.netist.mygirlshostel.PaymentTezz;
import com.netist.mygirlshostel.facilities.FacilityListActivity;
import com.netist.mygirlshostel.NoticeListActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.payment.PaymentHistoryActivity;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.view.ViewListActivity;
import com.netist.mygirlshostel.adapter.HostelListAdapter;
import com.netist.mygirlshostel.components.GPSTracker;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HostelListActivity extends BaseActivity implements View.OnClickListener, LocationListener {

    private ArrayList<HashMap<String, String>> hostelList;
    private ArrayList<HashMap<String, String>> hostelList2;

    private ListView listView;

    private String tag_string_req = "";

    private GPSTracker gpsTracker;
    private SessionHelper session;

    public String mob;
    public boolean service;

    PrefManager prefManager;
    Button btn_search, btn_map, btn_seekbar, btn_hostel_type;
    private ArrayList<HashMap<String, String>> vehicleList;
    EditText inputSearch;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;

    ImageView iv_back;

    Button btn_add_hostel;
    RelativeLayout search, seek_barlayout;
    String type;
    Spinner spinner1;
    int typeIndex = 0;
    String htype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_list);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        prefManager = new PrefManager(HostelListActivity.this);
        listView = (ListView) findViewById(R.id.lv_hostelList);
        btn_search = findViewById(R.id.btn_search);
        btn_seekbar = findViewById(R.id.btn_seekbar);
        btn_map = findViewById(R.id.btn_map);
        btn_hostel_type = findViewById(R.id.btn_hostel_type);
        btn_search.setOnClickListener(this);
        btn_seekbar.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_hostel_type.setOnClickListener(this);
        inputSearch = findViewById(R.id.inputSearch);
        iv_back = findViewById(R.id.iv_back);
        btn_add_hostel = findViewById(R.id.btn_add_hostel);
        search = findViewById(R.id.search);
        seek_barlayout = findViewById(R.id.seek_barlayout);
        seek_barlayout.setOnClickListener(this);
        session = new SessionHelper(this);
        setTitle("Hostel List");

        Log.e( "chkrole: ", session.getUserType());


        if(prefManager.getLati()==null)
        {
            dialog();
        }
        else {

            setHostelListUsingLatLong(prefManager.getType(), prefManager.getLati(), prefManager.getLongi(), prefManager.getDistance());

        }

        if(session.getUserType().equals("hostel"))
        {
            search.setVisibility(View.GONE);
            seek_barlayout.setVisibility(View.GONE);
        }
        //   gpsTracker = new GPSTracker(this);


        Bundle bundle = getIntent().getExtras();

        if (session.getUserType().equals("user") && bundle != null) {
            findViewById(R.id.btn_add_hostel).setVisibility(View.GONE);
            if (bundle.containsKey("booking"))
                findViewById(R.id.btn_list_charges).setVisibility(View.VISIBLE);
           // Toast.makeText(this, "okkk", Toast.LENGTH_SHORT).show();
        } else {
          //  Toast.makeText(this, "noooo", Toast.LENGTH_SHORT).show();

            findViewById(R.id.btn_add_hostel).setVisibility(View.VISIBLE);
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BusinessListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_add_hostel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent= new Intent(getApplicationContext(), HostelEditorActivity.class);
                startActivity(intent);*/
                Utility.launchActivity(HostelListActivity.this, HostelEditorActivity.class, true);

            }
        });


        //10
        findViewById(R.id.btn_list_charges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HotelListChargesActivity.class);
                startActivity(intent);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getIntent().getExtras();

                if(view.getId()==R.id.tv_view) {
                    Bundle bundle1=new Bundle();
                    bundle1.putString("hostelId" , hostelList.get(position).get("hostelId"));
                    Utility.launchActivity(HostelListActivity.this, ViewListActivity.class,false,bundle1);

                }
                else if(view.getId()==R.id.tv_facility) {
                    Bundle bundle1=new Bundle();
                    bundle1.putString("hostelId" , hostelList.get(position).get("hostelId"));
                    Utility.launchActivity(HostelListActivity.this, FacilityListActivity.class,false,bundle1);

                }
                else if(view.getId()==R.id.img_location) {

                   /* String uri = String.format(Locale.ENGLISH, "geo:%f,%f",
                            Float.parseFloat(hostelList.get(position).get("latitude"))
                            , Float.parseFloat(hostelList.get(position).get("longitude")));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
*/
                   // Uri uri = Uri.parse("geo:0,0?q="+"22.99948365856307,72.60040283203125(Maninagar)");
                    Uri uri = Uri.parse("geo:0,0?q="+Float.parseFloat(hostelList.get(position).get("latitude"))+","+
                            Float.parseFloat(hostelList.get(position).get("longitude")));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else {
                    if (bundle != null) {
                        if (bundle.containsKey("charges")) {
                            Intent intent = new Intent(getApplicationContext(), ChargesListActivity.class);
                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else if (bundle.containsKey("accounts")) {
                            Intent intent = new Intent(getApplicationContext(), AccountsActivity.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            bundle.putString("account_type", "2");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else if (bundle.containsKey("payment")) {

                            prefManager.setPAYMENT("hpayment");
                            Intent intent = new Intent(getApplicationContext(), PaymentTezz.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            bundle.putString("account_type", "2");
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else if (bundle.containsKey("paymenthistory")) {

                            Intent intent = new Intent(getApplicationContext(), PaymentHistoryActivity.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            bundle.putString("account_type", "2");
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else if (bundle.containsKey("views")) {
                            Intent intent = new Intent(getApplicationContext(), ViewListActivity.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else if (bundle.containsKey("notice")) {
                            Intent intent = new Intent(getApplicationContext(), NoticeListActivity.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else if (bundle.containsKey("facilities")) {
                            Intent intent = new Intent(getApplicationContext(), FacilityListActivity.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else if (bundle.containsKey("booking")) {
                            Intent intent = new Intent(getApplicationContext(), HostelDetailActivity.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else {
                            //String name = hostelList.get(position).get("name");
                            //Toast.makeText(getApplication(), "Selected : "+name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HostelDetailActivity.class);

                            bundle = new Bundle();
                            for (Map.Entry<String, String> entry : hostelList.get(position).entrySet()) {
                                bundle.putString(entry.getKey(), entry.getValue());
                            }
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                    }

                }
            }
        });

        btn_add_hostel.setVisibility(View.GONE);

    }


    private void setHostelList(String type, String offset, String limit) {
        tag_string_req = "HostelListRequest";

        hostelList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response StringYessss", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    hostelList.clear();
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        // Fields : hostelId, name, picture, location, room_count, total, availability, occupancy

                        listItem.put("hostelId", obj.getString("hostelId"));
                        listItem.put("name", obj.getString("name"));
                        listItem.put("picture", ApiConfig.urlHostelsImage + obj.getString("picture"));
                        listItem.put("location", obj.getString("location"));
                        listItem.put("room_count", obj.getString("room_count"));
                        listItem.put("total", obj.getString("total"));
                        listItem.put("availability", obj.getString("availability"));
                        listItem.put("occupancy", obj.getString("occupancy"));
                        listItem.put("chargesDateTime", obj.getString("chargesDateTime"));
                        listItem.put("charges", obj.getString("charges"));
                        listItem.put("latitude", obj.getString("latitude"));
                        listItem.put("longitude", obj.getString("longitude"));

                        hostelList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if (hostelList.size() > 0) {
                    HostelListAdapter hostelListAdapter = new HostelListAdapter(HostelListActivity.this, hostelList);
                    listView.setAdapter(hostelListAdapter);
                } else {
                    listView.setAdapter(null);
                    //pp Toast.makeText(HostelListActivity.this, "Hostels Not Available...!", Toast.LENGTH_SHORT).show();

                    Toast.makeText(HostelListActivity.this, "Hostels Not Available..!", Toast.LENGTH_SHORT).show();

                }

                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse" + error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                if (prefManager.getAREA_SELECTED().equals("area1")) {
                    params.put("id", "0");
                    params.put("type", type);
                    params.put("offset", offset);
                    params.put("limit", limit);

                    //  params.put("id",session.getUserID());
                } else if (prefManager.getAREA_SELECTED().equals("details")) {
                    params.put("id", "1");
                    params.put("type", type);
                    params.put("mobile", prefManager.getMOBILE_SELECTED());
                    params.put("offset", offset);
                    params.put("limit", limit);

                }

               /* if (!session.getUserNearby())
                {
                   // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
//                   Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                    Log.e("eeeeeeeeeeeeeeeeeeeeeee",prefManager.getAREA_SELECTED());
                    Log.e("test",session.getUserID());
                    if(prefManager.getAREA_SELECTED().equals("area1"))
                      {
                        params.put("id","0");
                        //  params.put("id",session.getUserID());
                      }

                      else if(prefManager.getAREA_SELECTED().equals("details"))
                      {
                        params.put("id", "1");
                        params.put("mobile",prefManager.getMOBILE_SELECTED());
                     }
                }*/

             /*
              else if (service=true)

                {
                    mob=WelcomeActivity.tv_mobile.getText().toString();
                    params.put("id","1");
                    params.put("mobile",mob);
                }
*/
                /*else {
                    params.put("distance", new Integer(session.getUserDistance()).toString());

                    if (session.getUserLocationMode()) {
                        if (gpsTracker.canGetLocation()) {
                            params.put("param_latitude", new Double(gpsTracker.getLatitude()).toString());
                            params.put("param_longitude", new Double(gpsTracker.getLongitude()).toString());
                        } else {
                            gpsTracker.showSettingsAlert();
                            params.put("invalid_location", "");
                        }
                    } else {
                        params.put("param_latitude", new Double(session.getUserLatitude()).toString());
                        params.put("param_longitude", new Double(session.getUserLongitude()).toString());
                    }
                }*/

                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void setHostelListUsingLatLong(String type, String lati, String longi, String range) {
        tag_string_req = "HostelListRequest";

        hostelList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response StringYessss", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    hostelList.clear();
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        // Fields : hostelId, name, picture, location, room_count, total, availability, occupancy

                        listItem.put("hostelId", obj.getString("hostelId"));
                        listItem.put("name", obj.getString("name"));
                        listItem.put("picture", ApiConfig.urlHostelsImage + obj.getString("picture"));
                        listItem.put("location", obj.getString("location"));
                        listItem.put("room_count", obj.getString("room_count"));
                        listItem.put("total", obj.getString("total"));
                        listItem.put("availability", obj.getString("availability"));
                        listItem.put("occupancy", obj.getString("occupancy"));
                        listItem.put("chargesDateTime", obj.getString("chargesDateTime"));
                        listItem.put("charges", obj.getString("charges"));
                        listItem.put("latitude", obj.getString("latitude"));
                        listItem.put("longitude", obj.getString("longitude"));

                        hostelList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if (hostelList.size() > 0) {
                    HostelListAdapter hostelListAdapter = new HostelListAdapter(HostelListActivity.this, hostelList);
                    listView.setAdapter(hostelListAdapter);
                } else {
                    listView.setAdapter(null);
                    //pp Toast.makeText(HostelListActivity.this, "Hostels Not Available...!", Toast.LENGTH_SHORT).show();

                    Toast.makeText(HostelListActivity.this, "Hostels Not Available, please go to search setting menu and add location points proper..!", Toast.LENGTH_SHORT).show();

                }

                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse" + error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                if (prefManager.getAREA_SELECTED().equals("area1")) {
                    params.put("id", "3");
                    params.put("type", type);
                    params.put("lati", lati);
                    params.put("longi", longi);
                    params.put("range", range);

                    //  params.put("id",session.getUserID());
                } else if (prefManager.getAREA_SELECTED().equals("details")) {
                    params.put("id", "4");
                    params.put("type", type);
                    params.put("mobile", prefManager.getMOBILE_SELECTED());
                    params.put("lati", lati);
                    params.put("longi", longi);
                    params.put("range", range);

                }


                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e("Restart", "Done");
        // setHostelList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                if(inputSearch.getText().toString().equals(""))
                {
                    Toast.makeText(HostelListActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                }
                else {
                    setAllForSearchForUser(inputSearch.getText().toString());

                }

                break;

            case R.id.btn_seekbar:
             //   fn_getlocation();
                Utility.launchActivity(HostelListActivity.this, MapsActivity.class, false);
                break;
            case R.id.btn_map:
                setAllForSearchForUser(inputSearch.getText().toString());
                break;

            case R.id.btn_hostel_type:
                dialog();
                break;

            case R.id.seek_barlayout:
                Utility.launchActivity(HostelListActivity.this, MapsActivity.class, false);
                break;
        }
    }

    private void setAllForSearchForUser(String name) {
        tag_string_req = "HostelListRequest";
        hostelList2 = new ArrayList<HashMap<String, String>>();
        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlsearch_hostel, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response StringYessss", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    hostelList2.clear();
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        // Fields : hostelId, name, picture, location, room_count, total, availability, occupancy

                        listItem.put("hostelId", obj.getString("hostelId"));
                        listItem.put("name", obj.getString("name"));
                        listItem.put("picture", ApiConfig.urlHostelsImage + obj.getString("picture"));
                        listItem.put("location", obj.getString("location"));
                        listItem.put("room_count", obj.getString("room_count"));
                        listItem.put("total", obj.getString("total"));
                        listItem.put("availability", obj.getString("availability"));
                        listItem.put("occupancy", obj.getString("occupancy"));
                        listItem.put("chargesDateTime", obj.getString("chargesDateTime"));
                        listItem.put("charges", obj.getString("charges"));
                        listItem.put("latitude", obj.getString("latitude"));
                        listItem.put("longitude", obj.getString("longitude"));

                        hostelList2.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if (hostelList2.size() > 0) {
                    HostelListAdapter hostelListAdapter = new HostelListAdapter(HostelListActivity.this, hostelList2);
                    listView.setAdapter(hostelListAdapter);
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(HostelListActivity.this, "Hostels Not Available...!", Toast.LENGTH_SHORT).show();

                    //   Toast.makeText(HostelListActivity.this, "Hostels Not Available, please go to search setting menu and add location points proper..!", Toast.LENGTH_SHORT).show();

                }

                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse" + error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                if (prefManager.getAREA_SELECTED().equals("area1")) {
                    params.put("id", "0");
                    params.put("text", name);
                    //  params.put("id",session.getUserID());
                } else if (prefManager.getAREA_SELECTED().equals("details")) {
                    params.put("id", "1");
                    params.put("text", name);
                    params.put("mobile", prefManager.getMOBILE_SELECTED());
                }

               /* if (!session.getUserNearby())
                {
                   // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
//                   Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                    Log.e("eeeeeeeeeeeeeeeeeeeeeee",prefManager.getAREA_SELECTED());
                    Log.e("test",session.getUserID());
                    if(prefManager.getAREA_SELECTED().equals("area1"))
                      {
                        params.put("id","0");
                        //  params.put("id",session.getUserID());
                      }

                      else if(prefManager.getAREA_SELECTED().equals("details"))
                      {
                        params.put("id", "1");
                        params.put("mobile",prefManager.getMOBILE_SELECTED());
                     }
                }*/

             /*
              else if (service=true)

                {
                    mob=WelcomeActivity.tv_mobile.getText().toString();
                    params.put("id","1");
                    params.put("mobile",mob);
                }
*/
                /*else {
                    params.put("distance", new Integer(session.getUserDistance()).toString());

                    if (session.getUserLocationMode()) {
                        if (gpsTracker.canGetLocation()) {
                            params.put("param_latitude", new Double(gpsTracker.getLatitude()).toString());
                            params.put("param_longitude", new Double(gpsTracker.getLongitude()).toString());
                        } else {
                            gpsTracker.showSettingsAlert();
                            params.put("invalid_location", "");
                        }
                    } else {
                        params.put("param_latitude", new Double(session.getUserLatitude()).toString());
                        params.put("param_longitude", new Double(session.getUserLongitude()).toString());
                    }
                }*/

                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void setAllForSearchForUserUsingLocation(final  String lat, final  String lon, final  String range) {
        tag_string_req = "HostelListRequest";
        hostelList2 = new ArrayList<HashMap<String, String>>();
        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlView, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response StringYessss", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    hostelList2.clear();
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        // Fields : hostelId, name, picture, location, room_count, total, availability, occupancy

                        listItem.put("hostelId", obj.getString("hostelId"));
                        listItem.put("name", obj.getString("name"));
                        listItem.put("picture", ApiConfig.urlHostelsImage + obj.getString("picture"));
                        listItem.put("location", obj.getString("location"));
                        listItem.put("room_count", obj.getString("room_count"));
                        listItem.put("total", obj.getString("total"));
                        listItem.put("availability", obj.getString("availability"));
                        listItem.put("occupancy", obj.getString("occupancy"));
                        listItem.put("chargesDateTime", obj.getString("chargesDateTime"));
                        listItem.put("charges", obj.getString("charges"));
                        listItem.put("latitude", obj.getString("latitude"));
                        listItem.put("longitude", obj.getString("longitude"));

                        hostelList2.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if (hostelList2.size() > 0) {
                    HostelListAdapter hostelListAdapter = new HostelListAdapter(HostelListActivity.this, hostelList2);
                    listView.setAdapter(hostelListAdapter);
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(HostelListActivity.this, "Hostels Not Available...!", Toast.LENGTH_SHORT).show();

                    //   Toast.makeText(HostelListActivity.this, "Hostels Not Available, please go to search setting menu and add location points proper..!", Toast.LENGTH_SHORT).show();

                }

                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse" + error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("action", "viewhostellist");
                params.put("lat", lat);
                params.put("lon", lon);
                params.put("range", range);


               /* if (!session.getUserNearby())
                {
                   // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
//                   Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                    Log.e("eeeeeeeeeeeeeeeeeeeeeee",prefManager.getAREA_SELECTED());
                    Log.e("test",session.getUserID());
                    if(prefManager.getAREA_SELECTED().equals("area1"))
                      {
                        params.put("id","0");
                        //  params.put("id",session.getUserID());
                      }

                      else if(prefManager.getAREA_SELECTED().equals("details"))
                      {
                        params.put("id", "1");
                        params.put("mobile",prefManager.getMOBILE_SELECTED());
                     }
                }*/

             /*
              else if (service=true)

                {
                    mob=WelcomeActivity.tv_mobile.getText().toString();
                    params.put("id","1");
                    params.put("mobile",mob);
                }
*/
                /*else {
                    params.put("distance", new Integer(session.getUserDistance()).toString());

                    if (session.getUserLocationMode()) {
                        if (gpsTracker.canGetLocation()) {
                            params.put("param_latitude", new Double(gpsTracker.getLatitude()).toString());
                            params.put("param_longitude", new Double(gpsTracker.getLongitude()).toString());
                        } else {
                            gpsTracker.showSettingsAlert();
                            params.put("invalid_location", "");
                        }
                    } else {
                        params.put("param_latitude", new Double(session.getUserLatitude()).toString());
                        params.put("param_longitude", new Double(session.getUserLongitude()).toString());
                    }
                }*/

                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    void dialog()
    {
        final CharSequence[] typeList = new CharSequence[] {
                "Girls hostel",
                "Boys hostel",

        };
        final AlertDialog.Builder alert = new AlertDialog.Builder(HostelListActivity.this);
        alert.setSingleChoiceItems(typeList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                typeIndex = i;
            }
        });
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                htype = typeList[typeIndex].toString();
                if(htype.equals("Girls hostel"))
                {
                    type="1";
                    prefManager.setType("1");
                    Toast.makeText(HostelListActivity.this, "girls hostel", Toast.LENGTH_SHORT).show();
                    setHostelList(type, "0","100");
                }
                else
                {
                    type="2";
                    prefManager.setType("2");
                    Toast.makeText(HostelListActivity.this, "boys hostel", Toast.LENGTH_SHORT).show();
                    setHostelList(type,"0","100");
                }
            }
        });
        alert.show();
    }

}