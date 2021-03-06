package com.netist.mygirlshostel.hostel;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.netist.mygirlshostel.map.SearchNearMeActivity;
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
    Button btn_search, btn_map, btn_hostel_type;
    private ArrayList<HashMap<String, String>> vehicleList;
    EditText inputSearch;

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;

    ImageView iv_back;

    Button btn_add_hostel;
    RelativeLayout search;
            LinearLayout seek_barlayout;
    String type;
    Spinner spinner1;
    int typeIndex = 0;
    String htype;
    TextView txt_clocation, txt_area;

    LocationManager lm;
    boolean gps_enabled ;
    boolean network_enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_list);
       // setContentView(R.layout.activity_hostel_list);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
         lm = (LocationManager) HostelListActivity.this.getSystemService(Context.LOCATION_SERVICE);
         gps_enabled = false;
         network_enabled = false;

        chkLocationManager();
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        prefManager = new PrefManager(HostelListActivity.this);
        listView = (ListView) findViewById(R.id.lv_hostelList);
        btn_search = findViewById(R.id.btn_search);

        btn_map = findViewById(R.id.btn_map);
        btn_hostel_type = findViewById(R.id.btn_hostel_type);
        btn_search.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_hostel_type.setOnClickListener(this);
        inputSearch = findViewById(R.id.inputSearch);
        iv_back = findViewById(R.id.iv_back);
        btn_add_hostel = findViewById(R.id.btn_add_hostel);
        search = findViewById(R.id.search);
        seek_barlayout = findViewById(R.id.seek_barlayout);
        txt_area = findViewById(R.id.txt_area);
        txt_clocation = findViewById(R.id.txt_clocation);
        seek_barlayout.setOnClickListener(this);
        txt_area.setOnClickListener(this);
        txt_clocation.setOnClickListener(this);
        session = new SessionHelper(this);
        setTitle("Hostel List");
        Log.e( "chkrole: ", session.getUserType());
       /* if(prefManager.getLati()==null)
        {
           // dialog();
            Toast.makeText(HostelListActivity.this, "111", Toast.LENGTH_SHORT).show();
            fn_getlocation();
        }
        else {
            Toast.makeText(HostelListActivity.this, "2222", Toast.LENGTH_SHORT).show();

            setHostelListUsingLatLong(prefManager.getType(), prefManager.getLati(), prefManager.getLongi(), prefManager.getDistance());

        }
*/
        fn_getlocation();
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

/*
                Bundle bundle1=new Bundle();
                bundle1.putSerializable("chklist", hostelList);
                Utility.launchActivity(HostelListActivity.this,HotelListChargesActivity.class, false,bundle1);
*/

                Intent intent = new Intent(HostelListActivity.this, HotelListChargesActivity.class);
                intent.putExtra("chklist", hostelList);
                startActivity(intent);

                /*Intent intent = new Intent(getApplicationContext(), HotelListChargesActivity.class);
                startActivity(intent);*/
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
                    Log.e( "onResponse: ",  responseArr.toString());
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

            case R.id.txt_area:


                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!gps_enabled && !network_enabled) {

                    Toast.makeText(HostelListActivity.this, "GPS not enabled", Toast.LENGTH_SHORT).show();

                }
                else {
                    //  Toast.makeText(HostelListActivity.this, "gps enabled", Toast.LENGTH_SHORT).show();
                    prefManager.setSearchType("searcharea");
                    Utility.launchActivity(HostelListActivity.this, MapsActivity.class, false);
                }


                break;

            case R.id.txt_clocation:
                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!gps_enabled && !network_enabled) {

                    Toast.makeText(HostelListActivity.this, "GPS not enabled", Toast.LENGTH_SHORT).show();

                   }
                else {
                  //  Toast.makeText(HostelListActivity.this, "gps enabled", Toast.LENGTH_SHORT).show();
                    prefManager.setSearchType("clocation");
                    dialogForSetLimit();
                }
                break;
            case R.id.btn_map:
                setAllForSearchForUser(inputSearch.getText().toString());
                break;

            case R.id.btn_hostel_type:
                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!gps_enabled && !network_enabled) {

                    Toast.makeText(HostelListActivity.this, "GPS not enabled", Toast.LENGTH_SHORT).show();

                }
                else {
                    //  Toast.makeText(HostelListActivity.this, "gps enabled", Toast.LENGTH_SHORT).show();
                    fn_getlocation();
                }

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

       // Toast.makeText(HostelListActivity.this, "pppp", Toast.LENGTH_SHORT).show();
        final CharSequence[] typeList = new CharSequence[] {
                "Girls hostel",
                "Boys hostel",

        };
        AlertDialog.Builder alert = new AlertDialog.Builder(HostelListActivity.this);
        alert.setCancelable(false);
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
                   // imp setHostelList(type, "0","100");
                    dialogInterface.dismiss();
                    dialogForSetLimit2KM(type);
                }
                else
                {
                    type="2";
                    prefManager.setType("2");
                    Toast.makeText(HostelListActivity.this, "boys hostel", Toast.LENGTH_SHORT).show();
                  // imp  setHostelList(type,"0","100");
                    dialogInterface.dismiss();
                    dialogForSetLimit2KM(type);

                }
            }
        });
        alert.show();
    }

    private void dialogForSetLimit( ) {
        // this.correct = correct;
        final Dialog resultbox = new Dialog(this);
        resultbox.setContentView(R.layout.dialog_set_limit);
        resultbox.setCanceledOnTouchOutside(false);
        SeekBar seek_bar = resultbox.findViewById(R.id.seek_bar);
        Button btn_finish =  resultbox.findViewById(R.id.btn_finish);
        Button btn_cancel =  resultbox.findViewById(R.id.btn_resume);
        TextView  perText = resultbox.findViewById(R.id.percent_text);
        //   perText.setText(Integer.toString(session.getUserDistance()) +"km");

        perText.setText("20" +"km");
        prefManager.setDistance("20");

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                prefManager.setDistance(String.valueOf(progress));
                perText.setText(Integer.toString(progress) +"km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Utility.launchActivity(HostelListActivity.this, SearchNearMeActivity.class, false);

            }

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
                //resultbox.cancel();
            }
        });

        resultbox.show();
    }

    private void dialogForSetLimit2KM( String type ) {
        // this.correct = correct;
        final Dialog resultbox = new Dialog(this);
        resultbox.setContentView(R.layout.dialog_set_limit);
        resultbox.setCanceledOnTouchOutside(false);
        SeekBar seek_bar = resultbox.findViewById(R.id.seek_bar);
        Button btn_finish =  resultbox.findViewById(R.id.btn_finish);
        Button btn_cancel =  resultbox.findViewById(R.id.btn_resume);
        TextView  perText = resultbox.findViewById(R.id.percent_text);
        //   perText.setText(Integer.toString(session.getUserDistance()) +"km");

        perText.setText("2" +"km");
        prefManager.setDistance("2");

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                prefManager.setDistance(String.valueOf(progress));
                perText.setText(Integer.toString(progress) +"km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.e( "onClick: ",prefManager.getLati()+"   "+prefManager.getLongi() );
               // Toast.makeText(HostelListActivity.this, "rrrr", Toast.LENGTH_SHORT).show();
                resultbox.dismiss();
                setHostelListUsingLatLong(type, prefManager.getLati(), prefManager.getLongi(), prefManager.getDistance());
               // setHostelListUsingLatLong("1", "19.9173237", "75.3367078", "20");

            }

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
                //resultbox.cancel();
            }
        });

        resultbox.show();
    }

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){

                        Log.e("latitudeGgl",location.getLatitude()+"");
                        Log.e("longitudeGgl",location.getLongitude()+"");

                       // latitude = location.getLatitude();
                       // longitude = location.getLongitude();

                        prefManager.setLati(String.valueOf(location.getLatitude()));
                        prefManager.setLongi(String.valueOf(location.getLongitude()));

                        dialog();
                        // fn_update(latitude,longitude,prefManager.getUSER_ID());
                    }
                }

            }

            if (isGPSEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Log.e("latitude_service",location.getLatitude()+"");
                        Log.e("longitude_service",location.getLongitude()+"");
                     //   latitude = location.getLatitude();
                     //   longitude = location.getLongitude();

                        prefManager.setLati(String.valueOf(latitude));
                        prefManager.setLongi(String.valueOf(latitude));
                        dialog();

                    }
                }
            }

        }
    }

    void chkLocationManager()
    {
        LocationManager lm = (LocationManager) HostelListActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new android.app.AlertDialog.Builder(HostelListActivity.this)
                    .setMessage("GPS network not enabled")
                    .setCancelable(false)

                    .setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HostelListActivity.this.onBackPressed();
                            //Toast.makeText(activity, "nooo", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        }
        //enabled
        else {
            Toast.makeText(HostelListActivity.this, "gps enabled", Toast.LENGTH_SHORT).show();


        }
    }
}