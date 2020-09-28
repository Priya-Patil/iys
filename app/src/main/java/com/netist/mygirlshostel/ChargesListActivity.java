package com.netist.mygirlshostel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.adapter.ChargesListAdapter;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 12/18/2017.
 */

public class ChargesListActivity extends BaseActivity implements View.OnClickListener{

   public String hostelId;
    String messId;
    String classesId;
    String libraryId;

    ArrayList<HashMap<String, String>> chargesList;
    ListView lv_charges_list;
    String tag_string_req;
    String chargesValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charges_list);

        setTitle("Charges List");

        Log.d("loyalty", "chargeList........................");

        lv_charges_list = (ListView) findViewById(R.id.lv_charges_list);

        if (getIntent().getExtras().getString("hostelId") != null){
            hostelId = getIntent().getExtras().getString("hostelId");
            chargesValue = getIntent().getExtras().getString("charges");
            Toast.makeText(ChargesListActivity.this, "hostelId"+hostelId, Toast.LENGTH_LONG).show();
            SetChargesList();
        }
        else if (getIntent().getExtras().getString("libraryId") != null){
            libraryId = getIntent().getExtras().getString("libraryId");
            chargesValue = getIntent().getExtras().getString("charges");
            SetLibraryChargesList();
        }
        else if (getIntent().getExtras().getString("messId") != null){
            messId = getIntent().getExtras().getString("messId");
            chargesValue = getIntent().getExtras().getString("charges");
            SetMessChargesList();
        }
        else if (getIntent().getExtras().getString("classesId") != null){
            classesId = getIntent().getExtras().getString("classesId");
            chargesValue = getIntent().getExtras().getString("charges");
            SetClassChargesList();
        }


        lv_charges_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getId() ==R.id.tvEditCharge){
                    dialogChangeCharges(chargesList.get(position).get("roomName"),chargesList.get(position).get("totalCharges"),
                            chargesList.get(position).get("hostelId"));
                }else{
                    HashMap<String, String> charges = chargesList.get(position);
                    SetAction(charges);

                }
                 }
        });
    }


    private void dialogChangeCharges(final String roomName, String totalCharges, final String hostlerIdd) {


        try {
            LayoutInflater li = LayoutInflater.from(ChargesListActivity.this);
            final View dialogView = li.inflate(R.layout.dialog_hostler_change_charge, null);


            final Dialog alertDialogBuilder = new Dialog(ChargesListActivity.this, R.style.MyCustomTheme);

            // set custom_dialog.xml to alertdialog builder
            alertDialogBuilder.setContentView(dialogView);


            //   View v = context.getWindow().getDecorView();
            //v.setBackgroundResource(android.R.color.transparent);
            alertDialogBuilder.setCancelable(false);


            final EditText etDialogHostelerChangeChargePrice=dialogView.findViewById(R.id.etDialogHostelerChangeChargePrice);

            etDialogHostelerChangeChargePrice.setText(""+totalCharges);



            Button btnDialogHostelerChangeChargeCancel=dialogView.findViewById(R.id.btnDialogHostelerChangeChargeCancel);
            btnDialogHostelerChangeChargeCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialogBuilder.cancel();
                }
            });


            Button btnDialogHostelerChangeChargeUpdate=dialogView.findViewById(R.id.btnDialogHostelerChangeChargeUpdate);
            btnDialogHostelerChangeChargeUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (etDialogHostelerChangeChargePrice.getText().toString().length() == 0){

                        Toast.makeText(ChargesListActivity.this, "Enter Correct Number ", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    UpdateChangeCharge(roomName,etDialogHostelerChangeChargePrice.getText().toString(),hostlerIdd,alertDialogBuilder);

                }
            });



            // create alert dialog
            alertDialogBuilder.show();
            alertDialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void SetAction(final HashMap<String, String> charges) {
        String status = charges.get("status");

        if (status.equals("Entered")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("User : " + charges.get("userName")/* + "(Age:" + charges.get("age") + ")"*/);
            alert.setMessage("Please Enter the paid.");
            final EditText input = new EditText(ChargesListActivity.this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            input.setPadding(15, 0, 0, 0);
            input.setBackground(getResources().getDrawable(R.drawable.border_accent));

            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

            alert.setView(input);

            alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (hostelId != null)
                        ExecuteAction(charges, input.getText().toString());
                    else if (libraryId != null)
                        ExecuteLibraryAction(charges, input.getText().toString());
                    else if (classesId != null)
                        ExecuteClassAction(charges, input.getText().toString());
                    else if (messId != null)
                        ExecuteMessAction(charges, input.getText().toString());
                }
            });

            alert.show();
        } else
            Toast.makeText(getApplicationContext(), "You can not select it.", Toast.LENGTH_LONG).show();
    }


    private void UpdateChangeCharge(final String roomName, final String totalCHarges, final String hostelrId, final Dialog alertDialog) {

        final ProgressDialog loading = ProgressDialog.show(this, null, "Updating Record Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelUpdateChargePrice, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dis missing the progress dialog
                loading.dismiss();
                try {
                 /*   JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");
*/
                  //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    alertDialog.dismiss();
                    SetChargesList();

                } catch (Exception e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("action", "booking_paid_update");

                params.put("roomName", roomName);
                params.put("hostelId", hostelrId);
                params.put("r_total", totalCHarges);

                Log.e("URL", ApiConfig.urlHostelBookingReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private void ExecuteAction(final HashMap<String, String> charges, final String paid) {
        if (paid.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter the value exactly!", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog loading = ProgressDialog.show(this, null, "Updating Record Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelBookingReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dis missing the progress dialog
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    if (!error)
                        FCMSenderActivity.sendSinglePush("Charges to be paid", "You paid the charges successfully(" + paid + ").", charges.get("userId"));

                    SetChargesList();
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("action", "booking_paid_update");

                params.put("id", charges.get("bookingId"));
                params.put("paid", paid);

                Log.e("URL", ApiConfig.urlHostelBookingReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void ExecuteLibraryAction(final HashMap<String, String> charges, final String paid) {
        if (paid.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter the value exactly!", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog loading = ProgressDialog.show(this, null, "Updating Record Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryBookingReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dis missing the progress dialog
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    if (!error)
                        FCMSenderActivity.sendSinglePush("Charges be paid", "You paid the charges successfully(" + paid + ").", charges.get("userId"));

                   // SetChargesList();
                    SetLibraryChargesList();
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("action", "booking_paid_update");

                params.put("id", charges.get("bookingId"));
                params.put("paid", paid);

                Log.e("URL", ApiConfig.urlHostelBookingReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void ExecuteMessAction(final HashMap<String, String> charges, final String paid) {
        if (paid.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter the value exactly!", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog loading = ProgressDialog.show(this, null, "Updating Record Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlMessBookingReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dis missing the progress dialog
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    if (!error)
                        FCMSenderActivity.sendSinglePush("Charges be paid", "You paid the charges successfully(" + paid + ").", charges.get("userId"));

                    //SetChargesList();
                    SetMessChargesList();
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("action", "booking_paid_update");

                params.put("id", charges.get("bookingId"));
                params.put("paid", paid);

                Log.e("URL", ApiConfig.urlHostelBookingReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void ExecuteClassAction(final HashMap<String, String> charges, final String paid) {
        if (paid.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter the value exactly!", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog loading = ProgressDialog.show(this, null, "Updating Record Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesBookingReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dis missing the progress dialog
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    if (!error)
                        FCMSenderActivity.sendSinglePush("Charges be paid", "You paid the charges successfully(" + paid + ").", charges.get("userId"));

                   // SetChargesList();
                    SetClassChargesList();
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("action", "booking_paid_update");

                params.put("id", charges.get("bookingId"));
                params.put("paid", paid);

                Log.e("URL", ApiConfig.urlHostelBookingReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void SetChargesList() {
        tag_string_req = "ChargesListRequest";

        chargesList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelBookingList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                   // Toast.makeText(getApplicationContext(), "responseArr"+responseArr, Toast.LENGTH_LONG).show();

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                        //notice_id,  dept,  subject,  description,  date
                      //  Toast.makeText(getApplicationContext(), "obj"+obj, Toast.LENGTH_LONG).show();
                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("bookingId", obj.getString("bookingId"));
                        listItem.put("hostelId", obj.getString("hostelId"));
                        listItem.put("userId", obj.getString("userId"));
                        listItem.put("userName", obj.getString("actualUserName"));
//                      listItem.put("userName", obj.getString("UserName"));
                        listItem.put("hostelName", obj.getString("hostelName"));
//                      listItem.put("age", obj.getString("age"));
                        listItem.put("bookingDateTime", obj.getString("bookingDateTime"));
                        listItem.put("joiningDateTime", obj.getString("joiningDateTime"));
                        listItem.put("leavingDateTime", obj.getString("leavingDateTime"));
                        listItem.put("selectedRoom", obj.getString("selectedRoom"));
                        listItem.put("roomName", obj.getString("roomName"));
                        listItem.put("status", obj.getString("status"));
                        listItem.put("paid", obj.getString("paid"));
                        listItem.put("totalCharges", obj.getString("totalCharges"));

                        chargesList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                    Toast.makeText(ChargesListActivity.this, "JSONException"+e, Toast.LENGTH_SHORT).show();
                }

                if (chargesList.size() > 0) {
                    ChargesListAdapter chargesListAdapter = new ChargesListAdapter(ChargesListActivity.this, chargesList);

                    lv_charges_list.setAdapter(chargesListAdapter);

                } else {
                    lv_charges_list.setAdapter(null);
                    Toast.makeText(ChargesListActivity.this, "No Pending Bookings...!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "eee"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("action", "booking_list");
                params.put("Id", hostelId);
              //  params.put("Entered", "Entered");

                Log.e("URL", ApiConfig.urlHostelBookingList);
                Log.e("Params", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void SetMessChargesList() {
        tag_string_req = "ChargesListRequest";

        chargesList = new ArrayList<HashMap<String, String>>();

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

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                        //notice_id,  dept,  subject,  description,  date

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("bookingId", obj.getString("bookingId"));
                        listItem.put("messId", obj.getString("messId"));
                        listItem.put("userId", obj.getString("userId"));
                        listItem.put("userName", obj.getString("actualUserName"));
//                        listItem.put("userName", obj.getString("UserName"));
                        listItem.put("messName", obj.getString("messName"));
//                        listItem.put("age", obj.getString("age"));
                        listItem.put("bookingDateTime", obj.getString("bookingDateTime"));
                        listItem.put("joiningDateTime", obj.getString("joiningDateTime"));
                        listItem.put("leavingDateTime", obj.getString("leavingDateTime"));
                        listItem.put("selectedType", obj.getString("selectedType"));
                        listItem.put("typeName", obj.getString("typeName"));
                        listItem.put("status", obj.getString("status"));
                        listItem.put("paid", obj.getString("paid"));

                        chargesList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }

                if (chargesList.size() > 0) {
                    ChargesListAdapter chargesListAdapter = new ChargesListAdapter(ChargesListActivity.this, chargesList);

                    lv_charges_list.setAdapter(chargesListAdapter);

                } else {
                    lv_charges_list.setAdapter(null);
                    Toast.makeText(ChargesListActivity.this, "No Pending Bookings...!", Toast.LENGTH_SHORT).show();
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
                params.put("Id", messId);

                Log.e("URL", ApiConfig.urlMessBookingList);
                Log.e("Params", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void SetClassChargesList() {
        tag_string_req = "ChargesListRequest";

        chargesList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesBookingList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                        //notice_id,  dept,  subject,  description,  date

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("bookingId", obj.getString("bookingId"));
                        listItem.put("classesId", obj.getString("classesId"));
                        listItem.put("userId", obj.getString("userId"));
                        listItem.put("userName", obj.getString("actualUserName"));
//                        listItem.put("userName", obj.getString("UserName"));
                        listItem.put("classesName", obj.getString("classesName"));
//                        listItem.put("age", obj.getString("age"));
                        listItem.put("bookingDateTime", obj.getString("bookingDateTime"));
                        listItem.put("joiningDateTime", obj.getString("joiningDateTime"));
                        listItem.put("leavingDateTime", obj.getString("leavingDateTime"));
                        listItem.put("selectedBatch", obj.getString("selectedBatch"));
                        listItem.put("batchName", obj.getString("batchName"));
                        listItem.put("status", obj.getString("status"));
                        listItem.put("paid", obj.getString("paid"));

                        chargesList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }

                if (chargesList.size() > 0) {
                    ChargesListAdapter chargesListAdapter = new ChargesListAdapter(ChargesListActivity.this, chargesList);

                    lv_charges_list.setAdapter(chargesListAdapter);

                } else {
                    lv_charges_list.setAdapter(null);
                    Toast.makeText(ChargesListActivity.this, "No Pending Bookings...!", Toast.LENGTH_SHORT).show();
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
                params.put("Id", classesId);

                Log.e("URL", ApiConfig.urlClassesBookingList);
                Log.e("Params", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void SetLibraryChargesList() {
        tag_string_req = "ChargesListRequest";

        chargesList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryBookingList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                        //notice_id,  dept,  subject,  description,  date

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("bookingId", obj.getString("bookingId"));
                        listItem.put("libraryId", obj.getString("libraryId"));
                        listItem.put("userId", obj.getString("userId"));
                        listItem.put("userName", obj.getString("actualUserName"));
//                        listItem.put("userName", obj.getString("UserName"));
                        listItem.put("libraryName", obj.getString("libraryName"));
//                        listItem.put("age", obj.getString("age"));
                        listItem.put("bookingDateTime", obj.getString("bookingDateTime"));
                        listItem.put("joiningDateTime", obj.getString("joiningDateTime"));
                        listItem.put("leavingDateTime", obj.getString("leavingDateTime"));
                        listItem.put("selectedHall", obj.getString("selectedHall"));
                        listItem.put("hallName", obj.getString("hallName"));
                        listItem.put("status", obj.getString("status"));
                        listItem.put("paid", obj.getString("paid"));

                        chargesList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }

                if (chargesList.size() > 0) {
                    ChargesListAdapter chargesListAdapter = new ChargesListAdapter(ChargesListActivity.this, chargesList);

                    lv_charges_list.setAdapter(chargesListAdapter);

                } else {
                    lv_charges_list.setAdapter(null);
                    Toast.makeText(ChargesListActivity.this, "No Pending Bookings...!", Toast.LENGTH_SHORT).show();
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
                params.put("Id", libraryId);

                Log.e("URL", ApiConfig.urlLibraryBookingList);
                Log.e("Params", params.toString());

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
