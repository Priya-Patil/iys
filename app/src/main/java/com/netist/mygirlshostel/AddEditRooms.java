package com.netist.mygirlshostel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
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
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class AddEditRooms extends BaseActivity implements View.OnClickListener{

    public String tag_string_req = "", hostelId, hostelName, hostelLocation, hostelImage,userId,userName,
            hostelTotal, hostelAvailability, roomCount, charges, mobileNo, person, name, date, time, formatedDate;
    int year , month, day, hours, minute;
    double latitude, longitude;
    String monthStr = "";
    Button btn_action, btn_edit, btn_delete;
    Timer refreshTimer;

    EditText  et_room_name,et_total,et_available;

    public  String id;


    TableLayout table;
    boolean bTableNull;
    boolean bEdit = false, bSet = false;
    final String[] serviceString = {"", "", "", "", "", "","","", "", "","","", "", "","", "", "", ""};
    HashMap<String, String> detailItem = new HashMap<String, String>();

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_rooms);
        prefManager=new PrefManager(AddEditRooms.this);

        et_room_name= (EditText) findViewById(R.id.et_room_name);
        et_total= (EditText) findViewById(R.id.et_total);
        et_available= (EditText) findViewById(R.id.et_available);



        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Hostel Details");

        final SessionHelper session = new SessionHelper(getApplicationContext());

        userId = session.getUserID();
        userName = session.getUserName();

        //  hostelId = getIntent().getExtras().getString("hostelId");
        hostelId=prefManager.getHOSTELID_SELECTED();



        //.makeText(getApplicationContext(), "hostelId"+hostelId , Toast.LENGTH_LONG).show();

        getDetails();
        //  getDetailsByMobile();

    }

    public void submit(View v)
    {
        setEditAction();
        getDetails();
        //  setNewEditAction();

    }
    private void getDetails() {
        tag_string_req = "HostelDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    //   Toast.makeText(getApplicationContext(), "responseArr"+responseArr, Toast.LENGTH_LONG).show();

                    final TableLayout table = (TableLayout) findViewById(R.id.tl_room_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_addeditroom_row, null);
                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView) row.findViewById(R.id.tv_room_name)).setText("Room");
                    ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText("Total");
                    // ((TextView) row.findViewById(R.id.tv_occupancy_bed_count)).setText("Occupancy");
                    ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText("Available");

                    ((TextView) row.findViewById(R.id.tv_edit)).setText("Edit");
                    ((TextView) row.findViewById(R.id.tv_set)).setText("Set");
                    ((TextView) row.findViewById(R.id.tv_delete)).setText("Delete");

                     table.addView(row);



                    // table.addView(row);


                    // Parsing json
                    for (Integer room_no = 0; room_no < responseArr.length(); room_no++) {

                        JSONObject obj = responseArr.getJSONObject(room_no);
                        //    Toast.makeText(getApplicationContext(), "obj"+obj, Toast.LENGTH_LONG).show();

                        row = (TableRow) inflater.inflate(R.layout.layout_addeditroom_row, null);

                        ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("r_name"));
                        ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText(obj.getString( "r_total"));
                        ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText(obj.getString( "r_availability"));



                        (row.findViewById(R.id.tv_set)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_set).setVisibility(View.VISIBLE);
                        (row.findViewById(R.id.tv_edit)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_edit).setVisibility(View.VISIBLE);
                        (row.findViewById(R.id.tv_delete)).setVisibility(View.GONE);
                        row.findViewById(R.id.btn_delete).setVisibility(View.VISIBLE);


                        ((TextView) row.findViewById(R.id.tv_edit)).setText("Edit");
                        ((TextView) row.findViewById(R.id.tv_set)).setText("Set");
                        ((TextView) row.findViewById(R.id.tv_delete)).setText("Delete");

                     //   table.addView(row);

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

                                    detailItem.put(serviceString[rowId] + "", ((EditText)tableRow.findViewById(R.id.et_room_name)).getText().toString());
                                    tableRow.findViewById(R.id.et_room_name).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_room_name).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_room_name)).setText(detailItem.get(serviceString[rowId] + ""));

                                    detailItem.put(serviceString[rowId] + "", ((EditText)tableRow.findViewById(R.id.et_total_bed_count)).getText().toString());
                                    tableRow.findViewById(R.id.et_total_bed_count).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_total_bed_count).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_total_bed_count)).setText(detailItem.get(serviceString[rowId] + ""));


                                    detailItem.put(serviceString[rowId] + "", ((EditText)tableRow.findViewById(R.id.et_availability_bed_count)).getText().toString());
                                    tableRow.findViewById(R.id.et_availability_bed_count).setVisibility(View.GONE);
                                    tableRow.findViewById(R.id.tv_availability_bed_count).setVisibility(View.VISIBLE);
                                    ((TextView)tableRow.findViewById(R.id.tv_availability_bed_count)).setText(detailItem.get(serviceString[rowId] + ""));


                                    //   updateData();
                                    //  setSubscriptionDetail();
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

                                tableRow.findViewById(R.id.tv_room_name).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_room_name).setVisibility(View.VISIBLE);
                               // ((EditText)tableRow.findViewById(R.id.et_room_name)).setText("pppp");
                                ((EditText)tableRow.findViewById(R.id.et_room_name)).setText(detailItem.get(serviceString[rowId] + ""));


                                tableRow.findViewById(R.id.tv_total_bed_count).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_total_bed_count).setVisibility(View.VISIBLE);
                              //  ((EditText)tableRow.findViewById(R.id.et_total_bed_count)).setText(detailItem.get( "pppp"));
                                ((EditText)tableRow.findViewById(R.id.et_total_bed_count)).setText(detailItem.get(serviceString[rowId] + ""));


                                tableRow.findViewById(R.id.tv_availability_bed_count).setVisibility(View.GONE);
                                tableRow.findViewById(R.id.et_availability_bed_count).setVisibility(View.VISIBLE);
                               // ((EditText)tableRow.findViewById(R.id.et_availability_bed_count)).setText("pppp");
                                ((EditText)tableRow.findViewById(R.id.et_availability_bed_count)).setText(detailItem.get(serviceString[rowId] + ""));


                            }
                        });


                        table.addView(row);

           /*             if (room_no == 0) {
                            hostelName = obj.getString("name");
                            //  Toast.makeText(getApplicationContext(), "hostelName"+hostelName, Toast.LENGTH_LONG).show();
                            hostelLocation = obj.getString("location");
                            hostelTotal = obj.getString("total");
                            hostelAvailability = obj.getString("availability");
                            charges = obj.getString("charges");
                            mobileNo = obj.getString("mobile");
                            person = obj.getString("person");
                            latitude = Double.parseDouble(obj.getString("latitude"));
                            longitude = Double.parseDouble(obj.getString("longitude"));

                            roomCount = obj.getString("room_count");
                            Integer room_count = Integer.parseInt(roomCount);
*/
                                   /* ((TextView) findViewById(R.id.tv_name)).setText(hostelName);
                                    ((TextView) findViewById(R.id.tv_location)).setText(hostelLocation);
                                    ((TextView) findViewById(R.id.tv_room_count)).setText(roomCount);
                                    ((TextView) findViewById(R.id.tv_contact_no)).setText(mobileNo);
                                    ((TextView) findViewById(R.id.tv_person)).setText(person);
                                    ((TextView) findViewById(R.id.tv_availability)).setText("Total : " + hostelTotal + ", Availability : " + hostelAvailability);

                                    String picture = ApiConfig.urlHostelsImage + obj.getString("picture");
                                    if (!picture.equals(hostelImage)) {
                                        hostelImage = picture;
                                        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                        if (imageLoader == null)
                                            imageLoader = AppController.getInstance().getImageLoader();
                                        CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

                                        thumbNail.setImageUrl(hostelImage, imageLoader);
                                    }
*/


                        }

                        // Add rows of the room no dynamically
                    /*    row = (TableRow) inflater.inflate(R.layout.layout_addeditroom_row, null);
                        ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("r_name"));
                        ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText(obj.getString("r_total"));
                        //  ((TextView) row.findViewById(R.id.tv_occupancy_bed_count)).setText(obj.getString("r_occupancy"));
                        ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText(obj.getString("r_availability"));

                        ((TextView) row.findViewById(R.id.tv_edit)).setText("Edit");
                        ((TextView) row.findViewById(R.id.tv_set)).setText("Set");
                        ((TextView) row.findViewById(R.id.tv_delete)).setText("Delete");
*/
/*

                        row.findViewById(R.id.et_availability_bed_count).setVisibility(View.GONE);
                        row.findViewById(R.id.et_room_name).setVisibility(View.GONE);
                        row.findViewById(R.id.et_total_bed_count).setVisibility(View.GONE);

*/


                      //  table.addView(row);
                   // }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "JSONException"+e, Toast.LENGTH_LONG).show();
                    Log.e(tag_string_req, e.getMessage());
                }
                //adapter.notifyDataSetChanged();
                //loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Disimissing the progress dialog
                //loading.dismiss();
                //  Toast.makeText(getApplicationContext(), "VolleyError"+error , Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", hostelId);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        //end
    }


    private void setEditAction() {
        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlAddEditRoom, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {

                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int room_no = 0; room_no < responseArr.length(); room_no++) {

                        JSONObject obj = responseArr.getJSONObject(room_no);


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

                // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                params.put("id","0");
                params.put("name", et_room_name.getText().toString().trim());
                params.put("total", et_total.getText().toString().trim());
                params.put("ava", et_available.getText().toString().trim());
                params.put("hostelId", hostelId);
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

                    for (int i = 0; i < 50; i++)
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