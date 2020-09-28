package com.netist.mygirlshostel.library;

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
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.facilities.FacilityListActivity;
import com.netist.mygirlshostel.NoticeListActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.view.ViewListActivity;
import com.netist.mygirlshostel.adapter.LibraryListAdapter;
import com.netist.mygirlshostel.components.GPSTracker;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LibraryListActivity extends BaseActivity implements View.OnClickListener{

    private ArrayList<HashMap<String,String>> libraryList ;

    private ListView listView;

    private String tag_string_req = "";
    private String libraryId, libraryName, libraryLocation,userId,userName,
            libraryTotal, libraryAvailability, hallCount,libraryImage, mobileNo, person, name, date, time, formatedDate;
    int year , month, day, hours, minute;
    String monthStr = "";

    private SessionHelper session;
    private GPSTracker gpsTracker;
    boolean isSearch = false;

    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_list);

        prefManager=new PrefManager(LibraryListActivity.this);
        setTitle("Library List");

        session = new SessionHelper(this);
        gpsTracker = new GPSTracker(this);

        userId = session.getUserID();
        userName = session.getUserName();

        listView = (ListView)findViewById(R.id.lv_libraryList);

        Bundle bundle = getIntent().getExtras();

        if(session.getUserType().equals("user") && bundle != null) {
            findViewById(R.id.btn_add_library).setVisibility(View.GONE);
            if (bundle.containsKey("booking"))
                findViewById(R.id.btn_list_charges).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.btn_add_library).setVisibility(View.VISIBLE);
        }

        setLibraryList();

        findViewById(R.id.btn_add_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), LibraryEditorActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_list_charges).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), LibraryListChargesActivity.class);
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
                    if (bundle.containsKey("charges"))
                    {
                        Intent intent = new Intent(getApplicationContext(), ChargesListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : libraryList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    else if (bundle.containsKey("accounts"))
                    {
                        Intent intent = new Intent(getApplicationContext(), AccountsActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : libraryList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        bundle.putString("account_type","4");
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    else if (bundle.containsKey("views"))
                    {
                        Intent intent = new Intent(getApplicationContext(), ViewListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : libraryList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    else if (bundle.containsKey("notice"))
                    {
                        Intent intent = new Intent(getApplicationContext(), NoticeListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : libraryList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    else if (bundle.containsKey("facilities"))
                    {
                        Intent intent = new Intent(getApplicationContext(), FacilityListActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : libraryList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                    else if (bundle.containsKey("booking"))
                    {
                        Intent intent = new Intent(getApplicationContext(), LibraryDetailActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : libraryList.get(position).entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else {
                        //String name = libraryList.get(position).get("name");
                        //Toast.makeText(getApplication(), "Selected : "+name, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LibraryDetailActivity.class);

                        bundle = new Bundle();
                        for (Map.Entry<String, String> entry : libraryList.get(position).entrySet()) {
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
                    setLibraryList();
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


    private  void setLibraryList()
    {
        tag_string_req = "LibraryListRequest";

        libraryList = new ArrayList<HashMap<String,String>>();

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    libraryList.clear();
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        // Fields : libraryId, name, picture, location, room_count, total, availability, occupancy

                        listItem.put("libraryId", obj.getString("libraryId"));
                        listItem.put("name", obj.getString("name"));
                        listItem.put("picture", ApiConfig.urlLibrariesImage + obj.getString("picture"));
                        listItem.put("location", obj.getString("location"));
                        listItem.put("latitude", obj.getString("latitude"));
                        listItem.put("longitude", obj.getString("longitude"));
                        listItem.put("total", obj.getString("total"));
                        listItem.put("availability", obj.getString("availability"));
                        listItem.put("hall_count", obj.getString("hall_count"));
                        listItem.put("occupancy", obj.getString("occupancy"));
                        listItem.put("chargesDateTime", obj.getString("chargesDateTime"));
                        listItem.put("charges", obj.getString("charges"));

                        libraryList.add(listItem);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if(libraryList.size()>0)
                {
                    LibraryListAdapter libraryListAdapter = new LibraryListAdapter(LibraryListActivity.this,libraryList);
                    listView.setAdapter(libraryListAdapter);
                }
                else
                {
                    listView.setAdapter(null);
                    Toast.makeText(LibraryListActivity.this, "Libraries Not Available...!", Toast.LENGTH_SHORT).show();
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

                if (!session.getUserNearby()) {
                    //    params.put("id","0");

                    if (prefManager.getAREA_SELECTED().equals("area1")) {
                        params.put("id", "0");

                    } else if (prefManager.getAREA_SELECTED().equals("details")) {
                        params.put("id", "1");
                        params.put("mobile", prefManager.getMOBILE_SELECTED());
                    }


                }

                else
                {
                    params.put("nearby", "");
                    params.put("distance", new Integer(session.getUserDistance()).toString());

                    if (!session.getUserLocationMode())
                    {
                        if (gpsTracker.canGetLocation())
                        {
                            params.put("param_latitude", new Double(gpsTracker.getLatitude()).toString());
                            params.put("param_longitude", new Double(gpsTracker.getLongitude()).toString());
                        }else{
                            gpsTracker.showSettingsAlert();
                            params.put("invalid_location", "");
                        }
                    }
                    else
                    {
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
    protected void onRestart()
    {
        super.onRestart();

        Log.e("Restart","Done");
        setLibraryList();
    }

    @Override
    public void onClick(View v) {

    }
}

