package com.netist.mygirlshostel.mess;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditMessType extends BaseActivity implements View.OnClickListener{

    String tag_string_req = "";
    HashMap<String, String> detailItem = new HashMap<String, String>();

    TableLayout table;
    boolean bTableNull;
    boolean bEdit = false, bSet = false, bDelete=false;

    final String[] serviceString = {"", "", "", "","","","","","","","","","","","","","","","","","","","","","","","","","",""};

    public JSONObject obj;

    public String s,s1;

    public String rname,rroomid,rtotal,roccupancy,ravailability, d_rname,d_rroomid,d_rtotal,d_roccupancy,d_ravailability;

    public  String hostelIdfrom;
    PrefManager prefManager;

    EditText et_room_name,et_total,et_available;

    public String total, occu, avai,  SUM_r_total, sum_r_availability,  d_SUM_r_total, d_sum_r_availability;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mess_type);

        prefManager=new PrefManager(EditMessType.this);
        et_room_name= (EditText) findViewById(R.id.et_room_name);
        et_total= (EditText) findViewById(R.id.et_total);
        et_available= (EditText) findViewById(R.id.et_available);

        hostelIdfrom = prefManager.getMESSID_SELECTED();
        Toast.makeText(getApplicationContext(), "hostelIdfrom"+hostelIdfrom, Toast.LENGTH_LONG).show();

        setTitle("Mess Type Info");
        setSubscriptionDetail();
    }


    private void setSubscriptionDetail()
    {
        tag_string_req = "subscription";

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlEditMessType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Toast.makeText(getApplicationContext(), "responseArr"+responseArr, Toast.LENGTH_LONG).show();
                    table = (TableLayout) findViewById(R.id.tl_editroom_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_edithostelroom_row, null);



                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView) row.findViewById(R.id.tv_name)).setText("Name");
                    ((TextView) row.findViewById(R.id.tv_total)).setText("total");
                    ((TextView) row.findViewById(R.id.tv_occ)).setText("Occu");
                    ((TextView) row.findViewById(R.id.tv_occ)).setVisibility(View.GONE);
                    ((TextView) row.findViewById(R.id.tv_id)).setText("id");
                    ((TextView) row.findViewById(R.id.tv_id)).setVisibility(View.GONE);

                    ((TextView) row.findViewById(R.id.tv_edit)).setText("Edit");
                    ((TextView) row.findViewById(R.id.tv_set)).setText("Set");
                    ((TextView) row.findViewById(R.id.tv_delete)).setText("Delete");
                    table.addView(row);

                    for (int i = 0; i < responseArr.length(); i++)
                    {
                        obj = responseArr.getJSONObject(i);
                        // Toast.makeText(getApplicationContext(), "JSONObject"+obj, Toast.LENGTH_LONG).show();

                        rroomid=obj.getString("typeId");
                        // Toast.makeText(getApplicationContext(),"roomid"+obj.getString("hallId"),Toast.LENGTH_LONG);
                        row = (TableRow) inflater.inflate(R.layout.layout_edithostelroom_row, null);

                        ((TextView) row.findViewById(R.id.tv_name)).setText(obj.getString( serviceString[i]+ "type_name"));
                        ((TextView) row.findViewById(R.id.tv_total)).setText(obj.getString( serviceString[i] +"type_charge"));
                        ((TextView) row.findViewById(R.id.tv_occ)).setText(obj.getString( serviceString[i] +"type_name"));
                        ((TextView) row.findViewById(R.id.tv_id)).setText(obj.getString( serviceString[i] +"typeId"));
                        ((TextView) row.findViewById(R.id.tv_id)).setVisibility(View.GONE);
                        ((TextView) row.findViewById(R.id.tv_occ)).setVisibility(View.GONE);


                        (row.findViewById(R.id.tv_set)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_set).setVisibility(View.VISIBLE);
                        (row.findViewById(R.id.tv_edit)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_edit).setVisibility(View.VISIBLE);
                        (row.findViewById(R.id.tv_delete)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);



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
                                    bDelete=false;

                                    detailItem.put(serviceString[rowId]+"type_name", ((EditText)tableRow.findViewById(R.id.et_name)).getText().toString());
                                    tableRow.findViewById(R.id.et_name).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_name).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_name)).setText(((EditText) tableRow.findViewById(R.id.et_name)).getText().toString());
                                    rname=((EditText) tableRow.findViewById(R.id.et_name)).getText().toString();
                                    // Toast.makeText(getApplicationContext(), "rname"+rname, Toast.LENGTH_LONG).show();

                                    //   rroomid,rtotal,roccupancy,ravailability;
                                    detailItem.put(serviceString[rowId]+"type_charge" , ((EditText)tableRow.findViewById(R.id.et_total)).getText().toString());
                                    tableRow.findViewById(R.id.et_total).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_total).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_total)).setText(((EditText) tableRow.findViewById(R.id.et_total)).getText().toString());
                                    rtotal=((EditText) tableRow.findViewById(R.id.et_total)).getText().toString();
                                    // Toast.makeText(getApplicationContext(), "rtotal"+rtotal, Toast.LENGTH_LONG).show();


                                    detailItem.put(serviceString[rowId]+"type_name" , ((EditText)tableRow.findViewById(R.id.et_occ)).getText().toString());
                                    tableRow.findViewById(R.id.et_occ).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_occ).setVisibility(View.GONE);
                                    ((TextView)tableRow.findViewById(R.id.tv_occ)).setText(((EditText) tableRow.findViewById(R.id.et_occ)).getText().toString());
                                    roccupancy=((EditText) tableRow.findViewById(R.id.et_occ)).getText().toString();
                                    tableRow.findViewById(R.id.et_occ).setVisibility(View.GONE);
                                    //  Toast.makeText(getApplicationContext(), "roccupancy"+roccupancy, Toast.LENGTH_LONG).show();
//


                                    detailItem.put(serviceString[rowId]+"typeId" , ((EditText)tableRow.findViewById(R.id.et_id)).getText().toString());
                                    tableRow.findViewById(R.id.et_id).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_id).setVisibility(View.GONE);
                                    ((TextView)tableRow.findViewById(R.id.tv_id)).setText(((EditText) tableRow.findViewById(R.id.et_id)).getText().toString());
                                    tableRow.findViewById(R.id.tv_id).setVisibility(View.GONE);
                                    rroomid=((EditText) tableRow.findViewById(R.id.et_id)).getText().toString();
                                    tableRow.findViewById(R.id.et_id).setVisibility(View.GONE);
                                    // Toast.makeText(getApplicationContext(), "rroomid"+rroomid, Toast.LENGTH_LONG).show();

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
                                bDelete=false;

                                final TableRow tableRow = (TableRow) v.getParent();
                                int rowId = table.indexOfChild(tableRow) - 1;

                                tableRow.findViewById(R.id.tv_name).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_name).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_name)).setText(((TextView) tableRow.findViewById(R.id.tv_name)).getText().toString());


                                tableRow.findViewById(R.id.tv_total).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_total).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_total)).setText(((TextView) tableRow.findViewById(R.id.tv_total)).getText().toString());


                                tableRow.findViewById(R.id.tv_occ).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_occ).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_occ)).setText(((TextView) tableRow.findViewById(R.id.tv_occ)).getText().toString());
                                tableRow.findViewById(R.id.et_occ).setVisibility(View.GONE);

                                tableRow.findViewById(R.id.tv_id).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_id).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_id)).setText(((TextView) tableRow.findViewById(R.id.tv_id)).getText().toString());
                                tableRow.findViewById(R.id.et_id).setVisibility(View.GONE);

                            }
                        });

                        //delete

                        Button btnDelete = (Button)row.findViewById(R.id.btn_delete);
                        btnDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bEdit = false;
                                bSet = false;
                                bDelete=true;


                                final TableRow tableRow = (TableRow) v.getParent();
                                int rowId = table.indexOfChild(tableRow) - 1;

                                tableRow.findViewById(R.id.tv_name).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_name).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_name)).setText(((TextView) tableRow.findViewById(R.id.tv_name)).getText().toString());
                                d_rname=((EditText) tableRow.findViewById(R.id.et_name)).getText().toString();
                                // Toast.makeText(getApplicationContext(), "d_rname"+d_rname, Toast.LENGTH_LONG).show();


                                tableRow.findViewById(R.id.tv_total).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_total).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_total)).setText(((TextView) tableRow.findViewById(R.id.tv_total)).getText().toString());
                                d_rtotal=((EditText) tableRow.findViewById(R.id.et_total)).getText().toString();
                                // Toast.makeText(getApplicationContext(), "d_rtotal"+d_rtotal, Toast.LENGTH_LONG).show();


                                tableRow.findViewById(R.id.tv_occ).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_occ).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_occ)).setText(((TextView) tableRow.findViewById(R.id.tv_occ)).getText().toString());
                                d_roccupancy=((EditText) tableRow.findViewById(R.id.et_occ)).getText().toString();
                                // Toast.makeText(getApplicationContext(), "d_roccupancy"+d_roccupancy, Toast.LENGTH_LONG).show();
                                tableRow.findViewById(R.id.et_occ).setVisibility(View.GONE);

                                tableRow.findViewById(R.id.tv_id).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_id).setVisibility(View.VISIBLE);
                                ((EditText)tableRow.findViewById(R.id.et_id)).setText(((TextView) tableRow.findViewById(R.id.tv_id)).getText().toString());
                                d_rroomid=((EditText) tableRow.findViewById(R.id.et_id)).getText().toString();
                                // Toast.makeText(getApplicationContext(), "d_rroomid"+d_rroomid, Toast.LENGTH_LONG).show();
                                tableRow.findViewById(R.id.et_id).setVisibility(View.GONE);


                                //dia;og

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( EditMessType.this);

                                // set title
                                alertDialogBuilder.setTitle("Delete Room");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Click yes to delete!")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                // EditHostelRoom.this.finish();

                                                deleteData();
                                                setSubscriptionDetail();


                                            }
                                        })
                                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // if this button is clicked, just close
                                                // the dialog box and do nothing
                                                dialog.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();

                                // deleteData();
                                // setSubscriptionDetail();

                            }
                        });

                        table.addView(row);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                    //  Toast.makeText(getApplicationContext(), "JSONException"+e, Toast.LENGTH_LONG).show();

                }
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                // Toast.makeText(getApplicationContext(), "onErrorResponse1"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "0");

                params.put("prefhostelId",hostelIdfrom);

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
                ApiConfig.urlEditMessType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                // Toast.makeText(getApplicationContext(), "VolleyErrorppppp"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "1");
                params.put("r_name", rname);
                params.put("r_total", rtotal);
               // params.put("r_occupancy", roccupancy);
                params.put("r_id", rroomid);
                bSet = false;
//                setSubscriptionDetail();

                Log.e("URL", ApiConfig.urlEditHostelRoom);
                Log.e(" Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void deleteData()
    {
        tag_string_req = "subscription";

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlEditMessType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                // loading.dismiss();

                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), "VolleyErrorppppp"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "2");
                params.put("r_id", d_rroomid);
                bSet = false;
//                setSubscriptionDetail();

                Log.e("URL", ApiConfig.urlEditHostelRoom);
                Log.e(" Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void submit(View v)
    {
        if(et_total.getText().toString().length()==0 || et_room_name.getText().toString().length()==0 )
        {
            Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_LONG).show();
        }
        else {
                setEditRoomAction();
                setSubscriptionDetail();
                //updateHostelTotal();
        }
    }


    private void setEditRoomAction() {

   final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,

                ApiConfig.urlAddMessType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {

                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                    }
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
                //Log.e("Registration Error: ", error.getMessage());
                //Disimissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id","0");
                params.put("name", et_room_name.getText().toString().trim());
                params.put("total", et_total.getText().toString().trim());
              //  params.put("occu", et_available.getText().toString().trim());
               // params.put("ava", avai);
                params.put("hostelId", hostelIdfrom);
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
