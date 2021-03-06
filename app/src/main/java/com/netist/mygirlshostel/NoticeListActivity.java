package com.netist.mygirlshostel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.adapter.NoticeListAdapter;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoticeListActivity extends BaseActivity implements View.OnClickListener{

    private ArrayList<HashMap<String,String>> noticeList ;

    private ListView listView;

    private String tag_string_req = "", id, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        setTitle("Notice List");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            if (bundle.containsKey("hostelId")) {
                id = bundle.getString("hostelId");
                type = "hostel";
            }
            else if (bundle.containsKey("messId"))
            {
                id = bundle.getString("messId");
                type = "mess";
            }
            else if (bundle.containsKey("classesId")){
                id = bundle.getString("classesId");
                type = "classes";
            }

            else if (bundle.containsKey("libraryId")){
                id = bundle.getString("libraryId");
                type = "library";
            }

        }

        SessionHelper session = new SessionHelper(getApplicationContext());
        if(session.getUserType().equals("user"))
        {
            findViewById(R.id.btn_add_notice).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.btn_add_notice).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_add_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), NoticeEditorActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        listView = (ListView)findViewById(R.id.lv_notice_list);
        setFacilityList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(getApplicationContext(), NoticeDetailsActivity.class);

                Bundle bundle = new Bundle();
                for (Map.Entry<String, String> entry : noticeList.get(position).entrySet()) {
                    bundle.putString(entry.getKey(), entry.getValue());
                }
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    private  void setFacilityList()
    {
        tag_string_req = "NoticeListRequest";

        noticeList = new ArrayList<HashMap<String,String>>();

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlNoticeList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        HashMap<String,String> listItem = new HashMap<String,String>();

                        listItem.put("type",obj.getString("type"));
                        listItem.put("id",obj.getString("id"));
                        listItem.put("noticeId",obj.getString("noticeId"));
                        listItem.put("title",obj.getString("title"));
                        listItem.put("details",obj.getString("details"));
                        listItem.put("dateTime",obj.getString("dateTime"));
                        listItem.put("imgFile",obj.getString("imgFile"));
                        listItem.put("docFile",obj.getString("docFile"));

                        noticeList.add(listItem);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if(noticeList.size()>0)
                {
                    NoticeListAdapter noticeListAdapter = new NoticeListAdapter(NoticeListActivity.this,noticeList);

                    listView.setAdapter(noticeListAdapter);

                }else
                {
                    listView.setAdapter(null);
                    Toast.makeText(NoticeListActivity.this, "Notice Not Available...!", Toast.LENGTH_SHORT).show();
                }

                //adapter.notifyDataSetChanged();
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("List Loading Error: ",  error.getMessage());
                //Disimissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("noticeId","0");
                params.put("id",id);
                params.put("type",type);

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
        setFacilityList();
    }

    @Override
    public void onClick(View v) {

    }
}
