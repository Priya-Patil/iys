package com.netist.mygirlshostel;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class demo extends BaseActivity implements View.OnClickListener{

    String tag_string_req = "";
    HashMap<String, String> detailItem = new HashMap<String, String>();

    TableLayout table;
    boolean bTableNull;
    boolean bEdit = false, bSet = false;

    final String[] serviceString = {"", "", "", ""};

    public JSONObject obj;

    String fee,total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        setSubscriptionDetail();
    }

    private void setSubscriptionDetail()
    {
        tag_string_req = "subscription";

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlEditHostelRoom, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    Toast.makeText(getApplicationContext(),"responseArr"+responseArr,Toast.LENGTH_LONG).show();

                    table = (TableLayout) findViewById(R.id.tl_subscription_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_subscription_row, null);

                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView) row.findViewById(R.id.tv_service_type)).setText("Service");
                    ((TextView) row.findViewById(R.id.tv_fee)).setText("Fee/M");
                    ((TextView) row.findViewById(R.id.tv_till_date)).setText("Till Date");

                    table.addView(row);

                    for (int i = 0; i < responseArr.length(); i++)
                    {

                        obj = responseArr.getJSONObject(i);
                        row = (TableRow) inflater.inflate(R.layout.layout_subscription_row, null);

                        ((TextView) row.findViewById(R.id.tv_service_type)).setText(serviceString[i]);
                        ((TextView) row.findViewById(R.id.tv_fee)).setText(obj.getString(serviceString[i] + "r_name"));
                        ((TextView) row.findViewById(R.id.tv_till_date)).setText(obj.getString(serviceString[i] + "r_total"));


                     //   Toast.makeText(getApplicationContext(),"obj.getString"+obj.getString("r_name"),Toast.LENGTH_LONG).show();
                        (row.findViewById(R.id.tv_set)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_set).setVisibility(View.VISIBLE);
                        (row.findViewById(R.id.tv_edit)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_edit).setVisibility(View.VISIBLE);


                        Button btnSet = (Button)row.findViewById(R.id.btn_set);
                        btnSet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final TableRow tableRow = (TableRow) v.getParent();
                                int rowId = table.indexOfChild(tableRow) - 1;

                                if (bEdit)
                                {
                                    bSet = true;
                                    bEdit = false;

                                    detailItem.put(serviceString[rowId] + "r_name", ((EditText)tableRow.findViewById(R.id.et_fee)).getText().toString());
                                    tableRow.findViewById(R.id.et_fee).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_fee).setVisibility(View.VISIBLE);
                                    fee=((EditText) tableRow.findViewById(R.id.et_fee)).getText().toString();
                                    Toast.makeText(getApplicationContext(), "feeerow"+fee, Toast.LENGTH_LONG).show();
                                    ((TextView)tableRow.findViewById(R.id.tv_fee)).setText(((EditText) tableRow.findViewById(R.id.et_fee)).getText().toString());



                                    detailItem.put(serviceString[rowId] + "r_total", ((EditText)tableRow.findViewById(R.id.et_till_date)).getText().toString());
                                    tableRow.findViewById(R.id.et_till_date).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_till_date).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_till_date)).setText(((EditText) tableRow.findViewById(R.id.et_till_date)).getText().toString());

                                    updateData();
                                    setSubscriptionDetail();
                                }
                            }
                        });

                        Button btnEdit = (Button)row.findViewById(R.id.btn_edit);
                        btnEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                bEdit = true;
                                bSet = false;

                                final TableRow tableRow = (TableRow) v.getParent();
                                int rowId = table.indexOfChild(tableRow) - 1;

                                tableRow.findViewById(R.id.tv_fee).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_fee).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_fee)).setText(((TextView) tableRow.findViewById(R.id.tv_fee)).getText().toString());
                            /*    fee=((TextView) tableRow.findViewById(R.id.tv_fee)).getText().toString();
                                Toast.makeText(getApplicationContext(), "feeerow"+fee, Toast.LENGTH_LONG).show();
*/

                                tableRow.findViewById(R.id.tv_till_date).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_till_date).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_till_date)).setText(((TextView) tableRow.findViewById(R.id.tv_till_date)).getText().toString());
                               // Toast.makeText(getApplicationContext(), "iddd"+rowId, Toast.LENGTH_LONG).show();
                                /* total=((TextView) tableRow.findViewById(R.id.tv_till_date)).getText().toString();
                                 Toast.makeText(getApplicationContext(), "totalrow"+fee, Toast.LENGTH_LONG).show();
*/

                            }
                        });

                        table.addView(row);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "0");

                Log.e("URL", ApiConfig.urlEditHostelRoom);
                Log.e("Subscription Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void updateData()
    {
        tag_string_req = "subscription";

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlEditHostelRoom, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    table = (TableLayout) findViewById(R.id.tl_subscription_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_subscription_row, null);

                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView) row.findViewById(R.id.tv_service_type)).setText("Service");
                    ((TextView) row.findViewById(R.id.tv_fee)).setText("Fee/M");
                    ((TextView) row.findViewById(R.id.tv_till_date)).setText("Till Date");
                    table.addView(row);



                    for (int i = 0; i < responseArr.length(); i++)
                    {
                        obj = responseArr.getJSONObject(0);
                        row = (TableRow) inflater.inflate(R.layout.layout_subscription_row, null);

                        ((TextView) row.findViewById(R.id.tv_service_type)).setText(serviceString[i]);
                        ((TextView) row.findViewById(R.id.tv_fee)).setText(obj.getString(serviceString[i] + "r_name"));
                        ((TextView) row.findViewById(R.id.tv_till_date)).setText(obj.getString(serviceString[i] + "r_total"));

                        (row.findViewById(R.id.tv_set)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_set).setVisibility(View.VISIBLE);
                        (row.findViewById(R.id.tv_edit)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_edit).setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "1");
                params.put("r_name", fee);
                params.put("r_id", "341");

                bSet = false;


                Log.e("URL", ApiConfig.urlEditHostelRoom);
                Log.e("Subscription Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onClick(View v) {

    }
}

