package com.netist.mygirlshostel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

public class SubscriptionActivity extends BaseActivity implements View.OnClickListener{
    String tag_string_req = "";
    HashMap<String, String> detailItem = new HashMap<String, String>();

    TableLayout table;
    boolean bTableNull;
    boolean bEdit = false, bSet = false;

    final String[] serviceString = {"Hostel", "Mess", "Classes", "Library"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        setTitle("Subscription Fee Setting");

        setSubscriptionDetail();
    }

    private void setSubscriptionDetail()
    {
        tag_string_req = "subscription";

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlSubScription, new Response.Listener<String>() {

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

                    JSONObject obj = responseArr.getJSONObject(0);

                    for (int i = 0; i < 4; i++)
                    {
                        row = (TableRow) inflater.inflate(R.layout.layout_subscription_row, null);

                        ((TextView) row.findViewById(R.id.tv_service_type)).setText(serviceString[i]);
                        ((TextView) row.findViewById(R.id.tv_fee)).setText(obj.getString(serviceString[i] + "Fee"));
                        ((TextView) row.findViewById(R.id.tv_till_date)).setText(obj.getString(serviceString[i] + "Date"));

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

                                    detailItem.put(serviceString[rowId] + "Fee", ((EditText)tableRow.findViewById(R.id.et_fee)).getText().toString());
                                    tableRow.findViewById(R.id.et_fee).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_fee).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_fee)).setText(detailItem.get(serviceString[rowId] + "Fee"));

                                    detailItem.put(serviceString[rowId] + "Date", ((EditText)tableRow.findViewById(R.id.et_till_date)).getText().toString());
                                    tableRow.findViewById(R.id.et_till_date).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_till_date).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_till_date)).setText(detailItem.get(serviceString[rowId] + "Date"));

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
                                ((EditText)tableRow.findViewById(R.id.et_fee)).setText(detailItem.get(serviceString[rowId] + "Fee"));

                                tableRow.findViewById(R.id.tv_till_date).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_till_date).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_till_date)).setText(detailItem.get(serviceString[rowId] + "Date"));
                            }
                        });

                        table.addView(row);
                    }

                    // Parsing json
                    detailItem.put("HostelFee", obj.getString("HostelFee"));
                    detailItem.put("HostelDate", obj.getString("HostelDate"));
                    detailItem.put("MessFee", obj.getString("MessFee"));
                    detailItem.put("MessDate", obj.getString("MessDate"));
                    detailItem.put("ClassesFee", obj.getString("ClassesFee"));
                    detailItem.put("ClassesDate", obj.getString("ClassesDate"));
                    detailItem.put("LibraryFee", obj.getString("LibraryFee"));
                    detailItem.put("LibraryDate", obj.getString("LibraryDate"));

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

                Log.e("URL", ApiConfig.urlSubScription);
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
                ApiConfig.urlSubScription, new Response.Listener<String>() {

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

                    JSONObject obj = responseArr.getJSONObject(0);

                    for (int i = 0; i < 4; i++)
                    {
                        row = (TableRow) inflater.inflate(R.layout.layout_subscription_row, null);

                        ((TextView) row.findViewById(R.id.tv_service_type)).setText(serviceString[i]);
                        ((TextView) row.findViewById(R.id.tv_fee)).setText(obj.getString(serviceString[i] + "Fee"));
                        ((TextView) row.findViewById(R.id.tv_till_date)).setText(obj.getString(serviceString[i] + "Date"));
                        (row.findViewById(R.id.tv_set)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_set).setVisibility(View.VISIBLE);
                        (row.findViewById(R.id.tv_edit)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_edit).setVisibility(View.VISIBLE);
                    }

                    // Parsing json
                    detailItem.put("HostelFee", obj.getString("HostelFee"));
                    detailItem.put("HostelDate", obj.getString("HostelDate"));
                    detailItem.put("MessFee", obj.getString("MessFee"));
                    detailItem.put("MessDate", obj.getString("MessDate"));
                    detailItem.put("ClassesFee", obj.getString("ClassesFee"));
                    detailItem.put("ClassesDate", obj.getString("ClassesDate"));
                    detailItem.put("LibraryFee", obj.getString("LibraryFee"));
                    detailItem.put("LibraryDate", obj.getString("LibraryDate"));

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
                params.put("set_value", " ");

                for (int i = 0; i < 4; i++){
                    params.put(serviceString[i] + "Fee", detailItem.get(serviceString[i] + "Fee"));
                    params.put(serviceString[i] + "Date", detailItem.get(serviceString[i] + "Date"));
                }

                bSet = false;
//                setSubscriptionDetail();

                Log.e("URL", ApiConfig.urlSubScription);
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
