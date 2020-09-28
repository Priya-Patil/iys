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
import java.util.Timer;

public class NewAddEditRoom extends BaseActivity implements View.OnClickListener{


   /* public String tag_string_req = "", hostelId,userId,userName,
            charges, mobileNo, person, name, date, time, formatedDate;
    int year , month, day, hours, minute;
    double latitude, longitude;
    String monthStr = "";*/

    public String tag_string_req = "", hostelName, hostelLocation, hostelImage,userId,userName,
            hostelTotal, hostelAvailability, roomCount, charges, mobileNo, person, name, date, time, formatedDate;
    int year , month, day, hours, minute;
    double latitude, longitude;
    String monthStr = "";
    Button btn_action, btn_edit, btn_delete;
    Timer refreshTimer;

    EditText  et_room_name,et_total,et_available;

    public  String id  , r_availability, r_total;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_edit_room);


        et_room_name= (EditText) findViewById(R.id.et_room_name);
        et_total= (EditText) findViewById(R.id.et_total);
        et_available= (EditText) findViewById(R.id.et_available);

        getDetailsByMobile();

    }

    public void submit(View v)
    {

        setNewEditAction();
        SelectTotalRoomCount();
       // InsertTotalCount();
        getAllDetails();

    }



    //for new user
    private void getDetailsByMobile() {
        tag_string_req = "HostelDetailsRequest";

        //final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlFind, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    Toast.makeText(getApplicationContext(), "responseArr"+responseArr, Toast.LENGTH_LONG).show();


                    //  JSONObject obj = responseArr.getJSONObject(room_no);

                    for(int i=0; i<=responseArr.length(); i++) {
                        JSONObject jsonObject = responseArr.getJSONObject(i);
                        id= jsonObject.getString("hostelId");


                        Toast.makeText(getApplicationContext(), "jsonObject"+jsonObject.getString("hostelId"), Toast.LENGTH_LONG).show();
                    }

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

                params.put("id", "0");

                //params.put("mob", HostelEditorActivity.et_mobile.getText().toString());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        //end
    }


    //new user

    private void setNewEditAction() {

        final int avai= Integer.parseInt(et_total.getText().toString().trim())-Integer.parseInt( et_available.getText().toString().trim());


        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlAddEditRoom, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
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
                params.put("ava", String.valueOf(avai));
                params.put("occu", et_available.getText().toString().trim());
                params.put("hostelId", id);
                return params;

            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getAllDetails() {
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

                           // Toast.makeText(getApplicationContext(), "responseArr", Toast.LENGTH_LONG).show();

                            TableLayout table = (TableLayout) findViewById(R.id.tl_room_data);
                            table.removeAllViews();

                            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                    (Context.LAYOUT_INFLATER_SERVICE);
                            TableRow row = (TableRow) inflater.inflate(R.layout.layout_addeditroom_row, null);
                            // Room No, Total Count, Occupancy Count, Availability Count
                            ((TextView) row.findViewById(R.id.tv_room_name)).setText("Room Name");
                            ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText("Total");
                            ((TextView) row.findViewById(R.id.tv_occ)).setText("Occupancy");
                            ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText("Availability");
                            table.addView(row);

                            // Parsing json
                            for (Integer room_no = 0; room_no < responseArr.length(); room_no++) {

                                JSONObject obj = responseArr.getJSONObject(room_no);
                                Toast.makeText(getApplicationContext(), "obj"+obj.getString("hostelId"), Toast.LENGTH_LONG).show();

                                if (room_no == 0) {
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

                                   /* ((TextView) findViewById(R.id.tv_name)).setText(hostelName);
                                    ((TextView) findViewById(R.id.tv_location)).setText(hostelLocation);
                                    ((TextView) findViewById(R.id.tv_room_count)).setText(roomCount);
                                    ((TextView) findViewById(R.id.tv_contact_no)).setText(mobileNo);
                                    ((TextView) findViewById(R.id.tv_person)).setText(person);
                                    ((TextView) findViewById(R.id.tv_availability)).setText("Total : " + hostelTotal + ", Availability : " + hostelAvailability);

                                    /*String picture = ApiConfig.urlHostelsImage + obj.getString("picture");
                                    if (!picture.equals(hostelImage)) {
                                        hostelImage = picture;
                                        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                        if (imageLoader == null)
                                            imageLoader = AppController.getInstance().getImageLoader();
                                        CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_thumbnail);

                                        thumbNail.setImageUrl(hostelImage, imageLoader);
                                    }*/
                                }

                                // Add rows of the room no dynamically
                                row = (TableRow) inflater.inflate(R.layout.layout_addeditroom_row, null);
                                ((TextView) row.findViewById(R.id.tv_room_name)).setText(obj.getString("r_name"));
                                ((TextView) row.findViewById(R.id.tv_total_bed_count)).setText(obj.getString("r_total"));
                                ((TextView) row.findViewById(R.id.tv_occ)).setText(obj.getString("r_occupancy"));
                                ((TextView) row.findViewById(R.id.tv_availability_bed_count)).setText(obj.getString("r_availability"));

                                table.addView(row);
                            }
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

                        params.put("id", id);
                        return params;
                    }
                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

                //end
            }





    private void SelectTotalRoomCount() {

        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlTotalCount, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);

                try {
                    JSONArray responseArr = new JSONArray(response);

                    for (Integer i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                         r_availability=obj.getString("sum(r_availability)");
                         r_total=obj.getString("sum(r_total)");

                        Toast.makeText(getApplicationContext(), "r_availability" + r_availability, Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "sum(r_total)" +r_total , Toast.LENGTH_LONG).show();

                    }
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getMessage());
                //Disimissing the progress dialog
               //
                Toast.makeText(getApplicationContext(), "VolleyError"+error, Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(), "prefManager.getAREA_SELECTED()"+prefManager.getAREA_SELECTED(), Toast.LENGTH_LONG).show();
                params.put("id","2");
                params.put("hostelId", id);
                return params;

            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }




    private void InsertTotalCount() {


               final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlTotalCount, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
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
                params.put("id","3");
                params.put("r_availability", r_availability);
                params.put("r_total", r_total);
                params.put("hostelId", id);
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