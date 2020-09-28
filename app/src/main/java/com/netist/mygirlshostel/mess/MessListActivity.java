package com.netist.mygirlshostel.mess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.AccountsActivity;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.ChargesListActivity;
import com.netist.mygirlshostel.facilities.FacilityListActivity;
import com.netist.mygirlshostel.NoticeListActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.view.ViewListActivity;
import com.netist.mygirlshostel.adapter.MessListAdapter;
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

public class MessListActivity extends BaseActivity implements View.OnClickListener{

    private ArrayList<HashMap<String, String>> messList;
    private ListView listView;

    private String tag_string_req = "";

    private SessionHelper session;
    private GPSTracker gpsTracker;
    boolean isSearch = false;
    private String messId, messName, messLocation, messImage, mobileNo, person, typeCount,
            userId,userName,name, date, time, formatedDate;
    double latitude, longitude;
    int year , month, day, hours, minute;
    String monthStr;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_list);

        prefManager=new PrefManager(MessListActivity.this);
       // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();

       // Toast.makeText(getApplicationContext(), "prefManager.getMOBILE_SELECTED()"+prefManager.getMOBILE_SELECTED(), Toast.LENGTH_LONG).show();
        setTitle("Mess List");

        gpsTracker = new GPSTracker(this);
        session = new SessionHelper(this);

        userId = session.getUserID();
        userName = session.getUserName();


        listView = (ListView) findViewById(R.id.lv_messList);

        Bundle bundle = getIntent().getExtras();

        if (session.getUserType().equals("user") && bundle != null) {
            findViewById(R.id.btn_add_mess).setVisibility(View.GONE);
            if (bundle.containsKey("booking"))
                findViewById(R.id.btn_list_charges).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_add_mess).setVisibility(View.VISIBLE);
        }

        setMessList();

        findViewById(R.id.btn_add_mess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MessEditorActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_list_charges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MessListChargesActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    if (bundle.containsKey("charges")) {
                        Intent intent = new Intent(getApplicationContext(), ChargesListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : messList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else if (bundle.containsKey("accounts")) {
                        setAccountsActivity(position);

                    } else if (bundle.containsKey("views")) {
                        Intent intent = new Intent(getApplicationContext(), ViewListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : messList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else if (bundle.containsKey("notice")) {
                        Intent intent = new Intent(getApplicationContext(), NoticeListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : messList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else if (bundle.containsKey("facilities")) {
                        Intent intent = new Intent(getApplicationContext(), FacilityListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : messList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else if (bundle.containsKey("booking")) {
                        Intent intent = new Intent(getApplicationContext(), MessDetailActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : messList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        //String name = messList.get(position).get("name");
                        //Toast.makeText(getApplication(), "Selected : "+name, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MessDetailActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : messList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                }
            }
        });

    }


    EditText nameEdit;
    String nameString;
    AlertDialog.Builder search_dialog;

    private void showSearchDialog()
    {
        search_dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_search, null);
        search_dialog.setView(dialogView);

        search_dialog.setTitle("Search Booking.");

        nameEdit = (EditText)dialogView.findViewById(R.id.et_name);

        search_dialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    isSearch = true;
                    nameString = nameEdit.getText().toString();
                    setMessList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        search_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                isSearch = false;
            }
        });
        search_dialog.show();
    }

    private void setAccountsActivity(final int position) {
        tag_string_req = "MessBookingListRequest";

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlMessBookingList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    JSONObject obj = responseArr.getJSONObject(0);
                    // Parsing json

                    Intent intent = new Intent(getApplicationContext(), AccountsActivity.class);

                    Bundle bundle = new Bundle();
                    for (Map.Entry<String, String> entry : messList.get(position).entrySet()) {
                        bundle.putString(entry.getKey(), entry.getValue());
                    }

                    bundle.putString("charges", obj.getString("typeCharge"));
                    bundle.putString("account_type","3");
                    intent.putExtras(bundle);

                    startActivity(intent);
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
                //Log.e("Registration Error: ", error.toString());
                //Disimissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("action", "booking_list");

                params.put("messId", messList.get(position).get("messId"));

                Log.e("URL", ApiConfig.urlMessBookingList);
                Log.e("Params", params.toString());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void setMessList() {
        tag_string_req = "MessListRequest";

        messList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlMessList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    messList.clear();
                    // Parsing json

                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        // Fields : messId, name, picture, location, room_count, total, availability, occupancy
                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("messId", obj.getString("messId"));
                        listItem.put("name", obj.getString("name"));
                        listItem.put("picture", ApiConfig.urlMessesImage + obj.getString("picture"));
                        listItem.put("location", obj.getString("location"));
                        listItem.put("type_count", obj.getString("type_count"));
                        listItem.put("latitude", obj.getString("latitude"));
                        listItem.put("longitude", obj.getString("longitude"));

                        messList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if (messList.size() > 0) {
                    MessListAdapter messListAdapter = new MessListAdapter(MessListActivity.this, messList);
                    listView.setAdapter(messListAdapter);
                } else {
                    listView.setAdapter(null);
                    Toast.makeText(MessListActivity.this, "Messes Not Available...!", Toast.LENGTH_SHORT).show();
                }

                loading.dismiss();
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

                params.put("usertype", session.getUserType());

               /* if (!session.getUserNearby())
                    params.put("id", "0");
               */

                if (!session.getUserNearby())
                {
                    // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                    // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                    if(prefManager.getAREA_SELECTED().equals("area1"))
                    {
                        params.put("id","0");
                    }

                    else if(prefManager.getAREA_SELECTED().equals("details"))
                    {
                        params.put("id", "1");
                        params.put("mobile",prefManager.getMOBILE_SELECTED());
                    }
                }


                else {
                    params.put("nearby", "");
                    params.put("distance", new Integer(session.getUserDistance()).toString());

                    if (!session.getUserLocationMode()) {
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
                }

                if (isSearch){
                    params.put("isSearch", "");
                    params.put("searchname", nameString);

                    Log.d("loyalty", nameString);
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
        setMessList();
    }

    @Override
    public void onClick(View v) {

    }
}

