package com.netist.mygirlshostel.hostel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.GoogleMapActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.WelcomeActivity;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.components.RoomData;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddHostelActivity extends BaseActivity implements View.OnClickListener{

    EditText et_name, et_location, et_mobile, et_charges, et_password, et_vpassword, et_room_count, et_room_name,
            et_total_bed_count, et_occupancy_bed_count, et_person;
    CircularNetworkImageView hostel_profile_photo;
    String hostelId, name, location, picture, mobile, charges, chargesDateTime, prevCharges, password, vpassword, person;
    Integer room_count = 0;
    boolean isImageSelected = false;
    SparseArray<RoomData> room_list = null;
    String action = "insert";
    String tag_string_req;
    LatLng mMarkerPosition;

    TextView tv_map_postion;

    private int REQUEST_FILE = 1;
    private int REQUEST_CAMERA = 2;

    LinearLayout layout_fix_room, layout_set_room;
    RadioGroup radiotype;
    RadioButton radioButton;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_editor);

        radiotype = findViewById(R.id.radiotype);
        et_name = (EditText) findViewById(R.id.et_name);
        et_location = (EditText) findViewById(R.id.et_location);
        et_person = (EditText) findViewById(R.id.et_person);
        et_room_count = (EditText) findViewById(R.id.et_room_count);
        et_room_name = (EditText) findViewById(R.id.et_room_name);
        et_total_bed_count = (EditText) findViewById(R.id.et_total_bed_count);
        et_occupancy_bed_count = (EditText) findViewById(R.id.et_occupancy_bed_count);
        et_mobile = (EditText) findViewById(R.id.et_mobile );
        et_charges = (EditText) findViewById(R.id.et_charges );
        et_password= (EditText) findViewById(R.id.et_password );
        et_vpassword= (EditText) findViewById(R.id.et_vpassword );
        hostel_profile_photo = (CircularNetworkImageView) findViewById(R.id.hostel_profile_photo);



        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_room_count_set)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_bed_count_set)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.hostel_profile_photo)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_google_map)).setOnClickListener(this);

        tv_map_postion = (TextView) findViewById(R.id.tv_map_postion);

        layout_fix_room= (LinearLayout) findViewById(R.id.layout_fix_room);
        layout_set_room=  (LinearLayout) findViewById(R.id.layout_set_room);
        ((Button) findViewById(R.id.btn_room)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.btn_room)).setOnClickListener(this);


        getSupportActionBar().setHomeButtonEnabled(true);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("hostelId")) {

            setTitle("Edit Hostel");
            ((Button) findViewById(R.id.btn_room_count_set)).setText("Edit");
            ((Button) findViewById(R.id.btn_google_map)).setText("Pick");
            //((Button) findViewById(R.id.btn_goog+le_map)).setText("Edit Location");
            hostelId = getIntent().getExtras().getString("hostelId");
            prevCharges = getIntent().getExtras().getString("charges");

            //imp
            layout_fix_room.setVisibility(View.GONE);
            layout_set_room.setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_room)).setVisibility(View.VISIBLE);
            radiotype.setVisibility(View.GONE);
            action = "update";
            setEditAction();
        } else {
            setTitle("Add Hostel");
        }

        Firebase.setAndroidContext(this);

        //hostel type


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:

                int selectedID = radiotype.getCheckedRadioButtonId();
                radioButton = findViewById(selectedID);


                if(radioButton.getText().equals("Girls hostel"))
                {
                    type=1;
                    UploadHostel(String.valueOf(type));
                    //   Toast.makeText(HostelEditorActivity.this, ""+1,Toast.LENGTH_SHORT).show();

                }
                else  if(radioButton.getText().equals("Boys hostel")){
                  //  Toast.makeText(HostelEditorActivity.this, ""+2,Toast.LENGTH_SHORT).show();
                    type=2;
                    UploadHostel(String.valueOf(type));
                }

                break;
            case R.id.btn_cancel:
                this.finish();
                break;
            case R.id.hostel_profile_photo:
                ShowImageChooser();
                break;
            case R.id.btn_room_count_set:
                SetRoomCount();
                break;
            case R.id.btn_bed_count_set:
                SetBedCount();
                break;
            case R.id.btn_date:
                break;
            case R.id.btn_time:
                break;
            case R.id.btn_google_map:
                Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                if (mMarkerPosition != null)
                    intent.putExtra("position", mMarkerPosition);
                intent.putExtra("map_setting", "");
                startActivityForResult(intent, 10);
                break;

            case R.id.btn_room:
                Intent intent2 = new Intent(getApplicationContext(), EditHostelRoom.class);
                startActivity(intent2);
                break;

        }
    }

    private  void  SetRoomCount()
    {
        try
        {
            room_count = Integer.parseInt(et_room_count.getText().toString().trim());
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of rooms correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (room_count <= 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of rooms correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (room_list != null)
        {
            room_list.clear();
            room_list = null;
        }

        room_list = new SparseArray<RoomData>(room_count);

        Toast.makeText(getApplicationContext(), "The number of rooms : " + room_count.toString(), Toast.LENGTH_SHORT).show();
    }

    private void SetBedCount()
    {
        String room_name;
        Integer total_bed_count = 0;
        Integer occupancy_bed_count = 0;
        Integer price=0;

        if (room_count <= 0 || room_list == null)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of rooms correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (room_count <= room_list.size())
        {
            Toast.makeText(getApplicationContext(), "The current number of rooms be exceeded!", Toast.LENGTH_LONG).show();
            return;
        }

        /*if (et_charges.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please enter room price!", Toast.LENGTH_LONG).show();
            return;
        }*/


        room_name = et_room_name.getText().toString().trim();
        if (room_name.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please enter the room name correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        try
        {
            total_bed_count = Integer.parseInt(et_total_bed_count.getText().toString().trim());
            occupancy_bed_count = Integer.parseInt(et_occupancy_bed_count.getText().toString().trim());
            price=Integer.parseInt(et_charges.getText().toString().trim());

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (total_bed_count <= 0 || occupancy_bed_count < 0 || total_bed_count < occupancy_bed_count)
        {
            Toast.makeText(getApplicationContext(), "Please enter the parameters correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        RoomData roomData = new RoomData(room_name, total_bed_count, occupancy_bed_count,price);
        room_list.put(room_list.size(), roomData);

        String message = "Total : " + total_bed_count.toString() +
                " Occupancy : " + occupancy_bed_count.toString() +
                " in Room Name : " + room_name+
                " Price "+price;

        Log.e( "SetBedCount: ", message);

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        et_room_name.setText("");
        et_total_bed_count.setText("");
        et_occupancy_bed_count.setText("");
        et_charges.setText("");
    }

    //------------Show File Chooser-------------------
    private void ShowImageChooser() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddHostelActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[item].equals("Choose from Library")) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_FILE);

                } else if (items[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap thumbnail = null;

        if (resultCode == 10)
        {
            String address = data.getStringExtra("MESSAGE");
            if (!address.equals(""))
            {
                tv_map_postion.setText(address);
                mMarkerPosition = (LatLng) data.getParcelableExtra("position");
            }
        }
        else if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == REQUEST_FILE) {
                Uri fileUri = data.getData();

                try {
                    //Getting the Bitmap from Gallery
                    thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                    //Setting the Bitmap to ImageView
                    hostel_profile_photo.setImageBitmap(thumbnail);
                    picture = getStringImage(thumbnail);
                    isImageSelected = true;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CAMERA) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                picture = getStringImage(thumbnail);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                hostel_profile_photo.setImageBitmap(thumbnail);

                isImageSelected = true;
            }
        }
    }

    //-------------convert Bitmap to base64 String.------------
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void setEditAction() {
        final String tag_string_req = "HostelDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

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
                        if (room_no == 0)
                        {
                            name = obj.getString("name");
                            location = obj.getString("location");
                            person = obj.getString("person");
                            mobile = obj.getString("mobile");
                            charges = obj.getString("charges");
                            double latitude = obj.getDouble("latitude");
                            double longitude = obj.getDouble("longitude");
                            mMarkerPosition = new LatLng(latitude, longitude);
                            vpassword = password = obj.getString("password");
                            room_count = Integer.parseInt(obj.getString("room_count"));
                            String picture = obj.getString("picture");
                            isImageSelected = false;
                            if (picture != null && !picture.equals(""))
                                isImageSelected = true;
                            if (isImageSelected)
                            {
                                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                if (imageLoader == null)
                                    imageLoader = AppController.getInstance().getImageLoader();
                                hostel_profile_photo.setImageUrl(ApiConfig.urlHostelsImage + picture, imageLoader);
                            }

                            et_name.setText(name);
                            et_location.setText(location);
                            et_person.setText(person);
                            et_mobile.setText(mobile);
                            et_charges.setText(charges);
                            tv_map_postion.setText(mMarkerPosition.toString());
                            et_password.setText(password);
                            et_vpassword.setText(password);
                            et_room_count.setText(room_count.toString());

                            if (room_list != null)
                            {
                                room_list.clear();
                                room_list = null;
                            }

                            room_list = new SparseArray<RoomData>(room_count);
                        }

                        RoomData roomData = new RoomData(obj.getString("r_name"),
                                Integer.parseInt(obj.getString("r_total")),
                                Integer.parseInt(obj.getString("r_occupancy")),0);

                        room_list.put(room_no, roomData);
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

                params.put("id", hostelId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void UploadHostel(String type) {

        // name, picture, gender, dob, age, availability, specialisation, expertise, experience, language, address,
        // email, mobile, password, isActive

        name = et_name.getText().toString().trim();
        location = et_location.getText().toString().trim();
        person = et_person.getText().toString().trim();
        mobile = et_mobile.getText().toString().trim();
        charges = et_charges.getText().toString().trim();
        password = et_password.getText().toString().trim();
        vpassword = et_vpassword.getText().toString().trim();
        if (name.equals("") || location.equals("") || person.equals("") || mobile.equals("") ||
                tv_map_postion.getText().toString().equals("") || password.equals("") || vpassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter the parameters correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(vpassword))
        {
            Toast.makeText(getApplicationContext(),"Verified password not matched.",Toast.LENGTH_SHORT).show();
            return;
        }

       /* if (room_count <= 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of rooms correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (room_list == null || room_count != room_list.size())
        {
            Toast.makeText(getApplicationContext(), "Please enter the data of all rooms correctly! ", Toast.LENGTH_LONG).show();
            return;
        }*/

        for (Integer room_no = 0; room_no < room_count; room_no++)
        {
            RoomData roomData = room_list.get(room_no);

            if (roomData.name.equals("") || roomData.total <= 0 || roomData.occupancy < 0)
            {
                Toast.makeText(getApplicationContext(), "Please enter the data of all rooms correctly!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String tag_string_req = "HostelRegistrationRequest";
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    Toast.makeText(getApplicationContext(), jObj.getString("msg"), Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (!error) {
                        if (action.equals("insert"))
                            RegisterChatUser(name, mobile);
                        else
                            UpdateBookingTotalCharges();

                      //  HostelEditorActivity.this.finish();
                      //  Utility.launchActivity(HostelEditorActivity.this,HostelListActivity.class,true);
                        Utility.launchActivity(AddHostelActivity.this, WelcomeActivity.class,true);
                    }
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.getLocalizedMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                if (action.equals("insert")) {
                    params.put("action", "hostel_reg");
                }
                else {
                    params.put("action", "hostel_update");
                    params.put("hostelId", hostelId);

                }

                //Fields: name, location, person, room_count, room_list, total, isImageSelected, picture
                params.put("name", name);
                params.put("location", location);
                params.put("person", person);
                params.put("mobile", mobile);
                params.put("charges", charges);
                params.put("latitude", new Double(mMarkerPosition.latitude).toString());
                params.put("longitude", new Double(mMarkerPosition.longitude).toString());
                chargesDateTime = Utils.currentServerDateTime();
                params.put("chargesDateTime", chargesDateTime);
                params.put("password", password);
                params.put("type", type);
                params.put("room_count", room_count.toString());

                Integer total = 0;
                Integer occupancy = 0;

                for (Integer room_no = 0; room_no < room_count; room_no++)
                {
                    RoomData roomData = room_list.get(room_no);
                    total += roomData.total;
                    occupancy += roomData.occupancy;
                    params.put("room_no_" + room_no.toString() + "_name", roomData.name);
                    params.put("room_no_" + room_no.toString() + "_total", roomData.total.toString());
                    params.put("room_no_" + room_no.toString() + "_occupancy", roomData.occupancy.toString());
                    params.put("room_no_" + room_no.toString() + "_price", roomData.price.toString());
                }

                params.put("total", total.toString());
                params.put("occupancy", occupancy.toString());

                if (isImageSelected) {
                    params.put("isImageSelected", "yes");
                    params.put("picture", picture);
                }
                else
                    params.put("isImageSelected", "no");

                Log.e("URL", ApiConfig.urlHostelReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void UpdateBookingTotalCharges()
    {
        tag_string_req = "TotalChargesRequest";

//        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelBookingReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                //loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(tag_string_req,  error.getMessage());
                //Dismissing the progress dialog
                //loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("action", "booking_update_totalCharges");
                params.put("hostelId", hostelId);
                params.put("prevCharges", prevCharges);
                params.put("chargesDateTime", chargesDateTime);

                Log.e("URL", ApiConfig.urlHostelReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void RegisterChatUser(final String name, final String mobile)
    {
        String url = "https://healtreeapp.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://healtreeapp.firebaseio.com/users");

                if (s.equals("null")) {
                    reference.child(mobile).child("password").setValue(mobile);
                    reference.child(mobile).child("fullName").setValue(name);
                    reference.child(mobile).child("role").setValue("h");

                    Toast.makeText(AddHostelActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(mobile)) {
                            reference.child(mobile).child("password").setValue(mobile);
                            reference.child(mobile).child("fullName").setValue(name);
                            reference.child(mobile).child("role").setValue("h");

                            Toast.makeText(AddHostelActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddHostelActivity.this, "Chat Disabled", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(AddHostelActivity.this);
        rQueue.add(request);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* Bundle bundle=new Bundle();
        bundle.putString("hostelId",hostelId);
        Utility.launchActivity(HostelEditorActivity.this, HostelDetailActivity.class,false, bundle);*/
    }
}
