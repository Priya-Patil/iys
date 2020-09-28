package com.netist.mygirlshostel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.payment.PayMentGateWay;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.netist.mygirlshostel.web_api_handler.ApiConfig.urlView;

public class PaymentTezz extends BaseActivity implements View.OnClickListener{


    TextView tv_no;
    Button tv_applink;
    SessionHelper session;
    LinearLayout llActivityPaymentTezzDynamicNumbers;
    private String id, type;
    PrefManager prefManager;
    Double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_tezz);

         session= new SessionHelper(getApplicationContext());
         prefManager= new PrefManager(getApplicationContext());
        tv_no= (TextView) findViewById(R.id.tv_no);
        tv_applink= (Button) findViewById(R.id.tv_applink);
        llActivityPaymentTezzDynamicNumbers=findViewById(R.id.llActivityPaymentTezzDynamicNumbers);
        llActivityPaymentTezzDynamicNumbers.removeAllViews();
        llActivityPaymentTezzDynamicNumbers.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            if (bundle.containsKey("hostelId")) {
                id = bundle.getString("hostelId");
                type = "hostel";
                prefManager.setTYPEID("2");
                getAmount("2");
                Log.e( "onCreate: ", id );
                Log.e( "chk: ", String.valueOf(bundle.containsKey("hostelId")));
            }
            else if (bundle.containsKey("messId"))
            {
                id = bundle.getString("messId");
                type = "mess";
                prefManager.setTYPEID("3");
                getAmount("3");
                Log.e( "onCreate: ", id );
            }
            else if (bundle.containsKey("classesId")){
                id = bundle.getString("classesId");
                type = "classes";
                prefManager.setTYPEID("1");
                getAmount("1");
                Log.e( "onCreate: ", id );
            }

            else if (bundle.containsKey("libraryId")){
                id = bundle.getString("libraryId");
                type = "library";
                prefManager.setTYPEID("4");
                getAmount("4");
                Log.e( "onCreate: ", id );
            }
        }

        getRentalPaidList();
        // tv_applink.setText("https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.paisa.user");

        tv_applink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.paisa.user"));
                startActivity(i);*/

                String getFname = "fffffff";
                String getPhone = "Mobile number";
                // String getEmail = "demoemail@gmail.com";
                String getEmail = "Enter email";
               // String getAmt   = "1";//rechargeAmt.getText().toString().trim();
                String getAmt   = amount.toString();

                Intent intent = new Intent(getApplicationContext(), PayMentGateWay.class);
                intent.putExtra("FIRST_NAME",getFname);
                intent.putExtra("PHONE_NUMBER",getPhone);
                intent.putExtra("EMAIL_ADDRESS",getEmail);
                intent.putExtra("RECHARGE_AMT",getAmt);
                intent.putExtra("C_DATE",Utility.getCurrentDateTime());
                startActivity(intent);            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:9423780567"));
                startActivity(intent);
            }
        });
          // New User Has Been Added Ha
        if (session.getUserType().equals("user")){
            getRentalPaidList();

        }
    }

    private  void getRentalPaidList()
    {
          final String   tag_string_req = "HostelListRequest";
    //    hostelList = new ArrayList<HashMap<String,String>>();

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlPaymentNumber, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response StringYessss", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {

                    JSONObject  jsonObject=new JSONObject(response);
                    Log.e("GETTTTTTTT",""+jsonObject.length());

                    ArrayList<String> name=new ArrayList<>();
                    final ArrayList<String> phone=new ArrayList<>();
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();

                        JSONObject obj = jsonObject.getJSONObject(key);
                        Log.e("SSSSSSSSS",""+obj.getString("name")+" Phone "+obj.getString("mobile"));

                        if (!name.contains(obj.getString("name"))){
                            name.add(obj.getString("name"));
                            phone.add(obj.getString("mobile"));
                        }
                    }

                    if (name.size() > 0) {
                        for (int i = 0; i < name.size(); i++) {
                            TextView text = new TextView(PaymentTezz.this);
                            LinearLayout.LayoutParams params=  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            text.setLayoutParams(params);
                            text.setText("Name: "+name.get(i)+"\n"+"To make subscription fee payment please save "+phone.get(i)+" in your contact list.");
                            llActivityPaymentTezzDynamicNumbers.addView(text);
                            text.setGravity(Gravity.CENTER);
                            params.setMargins(10,10,10,10);

                            final int finalI = i;
                            text.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:"+phone.get(finalI)));
                                    startActivity(intent);
                                    // G
                                }
                            });
                        }
                        llActivityPaymentTezzDynamicNumbers.setVisibility(View.VISIBLE);
                        tv_no.setVisibility(View.GONE);
                    }else{
                        tv_no.setVisibility(View.VISIBLE);
                        llActivityPaymentTezzDynamicNumbers.setVisibility(View.GONE);
                    }

                    Log.e("SIZZZZZZZZZe",""+name.size());

                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse"+error, Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",session.getUserID());
                Log.e("Register Params: ", params.toString());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    @Override
    public void onClick(View v) {

    }

    private  void getAmount(final  String hostel_id)
    {
        String  tag_string_req = "req";

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
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                            //24-06
                            amount= Double.valueOf(obj.getString("charges"));

                      //  listItem.put("id",obj.getString("id"));
                        Log.e( "onResponse: ", obj.getString("charges") );
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e(tag_string_req,e.getMessage());
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

                params.put("action", "viewCharges");
                params.put("hostel_id", hostel_id);

                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
