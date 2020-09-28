package com.netist.mygirlshostel.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.netist.mygirlshostel.NoticeListActivity;
import com.netist.mygirlshostel.PaymentTezz;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.adapter.HostelListAdapter;
import com.netist.mygirlshostel.adapter.ViewAllVehicleAdapter;
import com.netist.mygirlshostel.components.GPSTracker;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.facilities.FacilityListActivity;
import com.netist.mygirlshostel.hostel.HostelDetailActivity;
import com.netist.mygirlshostel.hostel.HostelEditorActivity;
import com.netist.mygirlshostel.hostel.HotelListChargesActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.view.ViewListActivity;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.netist.mygirlshostel.web_api_handler.ApiConfig.urlView;

public class PaymentHistoryActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<HashMap<String, String>> hostelList;
    private ArrayList<HashMap<String, String>> hostelList2;

    private ListView listView;

    private String tag_string_req = "";

    private GPSTracker gpsTracker;
    private SessionHelper session;

    public String mob;
    public boolean service;

    PrefManager prefManager;
    Button btn_search;
    private ArrayList<HashMap<String, String>> vehicleList;
    String id,type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymenthistory);


        prefManager = new PrefManager(PaymentHistoryActivity.this);
        listView = (ListView) findViewById(R.id.lv_hostelList);
        setTitle("Payment History");


        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            if (bundle.containsKey("hostelId")) {
                id = bundle.getString("hostelId");
                type = "hostel";
                prefManager.setTYPEID("2");
                setAllVehicleForUser(id,"2");
                Log.e( "onCreate: ", id );
                Log.e( "chk: ", String.valueOf(bundle.containsKey("hostelId")));
            }
            else if (bundle.containsKey("messId"))
            {
                id = bundle.getString("messId");
                type = "mess";
                prefManager.setTYPEID("3");
                setAllVehicleForUser(id,"3");
                Log.e( "onCreate: ", id );
            }
            else if (bundle.containsKey("classesId")){
                id = bundle.getString("classesId");
                type = "classes";
                prefManager.setTYPEID("1");
                setAllVehicleForUser(id,"1");
                Log.e( "onCreate: ", id );
            }

            else if (bundle.containsKey("libraryId")){
                id = bundle.getString("libraryId");
                type = "library";
                prefManager.setTYPEID("4");
                setAllVehicleForUser(id,"4");
                Log.e( "onCreate: ", id );
            }
        }





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

    }

    private  void setAllVehicleForUser(final  String userid, final  String type)
    {
        String  tag_string_req = "req";

        vehicleList = new ArrayList<HashMap<String,String>>();

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlView, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    //  Toast.makeText(getApplicationContext(), "responseArr"+responseArr, Toast.LENGTH_LONG).show();
                    vehicleList.clear();
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        // Toast.makeText(getApplicationContext(), "id"+obj.getString("id"), Toast.LENGTH_LONG).show();
                        HashMap<String,String> listItem = new HashMap<String,String>();

                        listItem.put("id",obj.getString("id"));
                        listItem.put("userid",obj.getString("userid"));
                        listItem.put("type",obj.getString("type"));
                        listItem.put("amount",obj.getString("amount"));
                        listItem.put("created",obj.getString("created"));

                        vehicleList.add(listItem);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if(vehicleList.size() > 0)
                {
                    ViewAllVehicleAdapter viewStaffAdapter = new ViewAllVehicleAdapter(PaymentHistoryActivity.this,vehicleList);
                    listView.setAdapter(viewStaffAdapter);
                }
                else
                {
                    listView.setAdapter(null);
                    //pp Toast.makeText(HostelListActivity.this, "Hostels Not Available...!", Toast.LENGTH_SHORT).show();

                    Toast.makeText(getApplicationContext(), "Staff member Not Available..!", Toast.LENGTH_SHORT).show();

                }

                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                // Toast.makeText(getApplicationContext(), "onErrorResponse"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("action", "view_payment");
                params.put("userid", userid);
                params.put("type", type);

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
           /* case R.id.btn_search:

                     break;*/
        }
    }

}