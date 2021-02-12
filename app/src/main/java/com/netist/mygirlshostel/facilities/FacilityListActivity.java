package com.netist.mygirlshostel.facilities;

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
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.adapter.FacilityListAdapter;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FacilityListActivity extends BaseActivity implements View.OnClickListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private ArrayList<HashMap<String,String>> facilityList ;

    private ListView listView;

    private String tag_string_req = "", id, type;
    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list);

        setTitle("Facility List");
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("hostelId")) {
                id = bundle.getString("hostelId");
                type = "hostel";
            } else if (bundle.containsKey("messId")) {
                id = bundle.getString("messId");
                type = "mess";
            } else if (bundle.containsKey("classesId")) {
                id = bundle.getString("classesId");
                type = "classes";
            } else if (bundle.containsKey("libraryId")) {
                id = bundle.getString("libraryId");
                type = "library";
            }
        }

            SessionHelper session = new SessionHelper(getApplicationContext());
        if(session.getUserType().equals("user"))
        {
            findViewById(R.id.btn_add_facility).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.btn_add_facility).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_add_facility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), FacilityEditorActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        listView = (ListView)findViewById(R.id.lv_facility_list);
        setFacilityList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent= new Intent(getApplicationContext(), FacilityDetailsActivity.class);

                Bundle bundle = new Bundle();
                for (Map.Entry<String, String> entry : facilityList.get(position).entrySet()) {
                    bundle.putString(entry.getKey(), entry.getValue());
                }
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }


    private  void setFacilityList()
    {
        tag_string_req = "FacilityListRequest";

        facilityList = new ArrayList<HashMap<String,String>>();

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlFacilityList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        HashMap<String,String> listItem = new HashMap<String,String>();

                        listItem.put("id",obj.getString("id"));
                        listItem.put("type",obj.getString("type"));
                        listItem.put("facilityId",obj.getString("facilityId"));
                        listItem.put("title",obj.getString("title"));
                        listItem.put("details",obj.getString("details"));
                        listItem.put("startDateTime",obj.getString("startDateTime"));
                        listItem.put("endDateTime",obj.getString("endDateTime"));
                        listItem.put("imgFile",obj.getString("imgFile"));
                        listItem.put("docFile",obj.getString("docFile"));

                        facilityList.add(listItem);
                    }
                    slider(facilityList);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if(facilityList.size()>0)
                {
                    FacilityListAdapter facilityListAdapter = new FacilityListAdapter(FacilityListActivity.this,facilityList);

                    listView.setAdapter(facilityListAdapter);

                }else
                {
                    listView.setAdapter(null);
                    Toast.makeText(FacilityListActivity.this, "Facilities Not Available...!", Toast.LENGTH_SHORT).show();
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

                params.put("facilityId", "0");
                params.put("type", type);
                params.put("id",id);

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

    public void slider( ArrayList<HashMap<String,String>> noticeList ) {
        HashMap<String, String> url_maps1 = new HashMap<String, String>();
        int id = 1;
        for (int i=0; i<noticeList.size(); i++) {
            Log.e("checkLists",""+noticeList.get(i).get("imgFile"));

            String img=noticeList.get(i).get("imgFile").substring(0, noticeList.get(i).get("imgFile").length() - 1);;
            String imgPath = ApiConfig.urlFacilityImage+img;
            //String imgPath = model.getImages();
            Log.e( "slider: ",ApiConfig.urlFacilityImage+noticeList.get(i).get("imgFile"));
            url_maps1.put(imgPath,imgPath);
        }
        for (String name : url_maps1.keySet()) {
            //Log.e("checkImg",""+name);
            DefaultSliderView defaultSliderView = new DefaultSliderView(this);
            defaultSliderView.image(url_maps1.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            defaultSliderView.bundle(new Bundle());
            defaultSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(defaultSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);//Fade
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        // mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
