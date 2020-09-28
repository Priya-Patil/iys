package com.netist.mygirlshostel.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.components.HallData;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LibraryEditorActivity extends BaseActivity implements View.OnClickListener{

    EditText et_name, et_location, et_mobile, et_password, et_vpassword, et_person, et_hall_count, et_hall_name,
            et_total_seat_count, et_occupancy_seat_count, et_charges;
    CircularNetworkImageView library_profile_photo;
    String libraryId, name, location, picture, mobile, password, vpassword, person, charges, chargesDateTime, prevCharges;
    Integer hall_count = 0;
    boolean isImageSelected = false;
    SparseArray<HallData> hall_list = null;
    String action = "insert";
    String tag_string_req;
    LatLng mMarkerPosition;

    TextView tv_map_postion;

    private int REQUEST_FILE = 1;
    private int REQUEST_CAMERA = 2;

    LinearLayout layout_fix_room, layout_set_room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_editor);

        et_name = (EditText) findViewById(R.id.et_name);
        et_location = (EditText) findViewById(R.id.et_location);
        et_person = (EditText) findViewById(R.id.et_person);
        et_charges = (EditText) findViewById(R.id.et_charges);
        et_hall_count = (EditText) findViewById(R.id.et_hall_count);
        et_hall_name = (EditText) findViewById(R.id.et_hall_name);
        et_total_seat_count = (EditText) findViewById(R.id.et_total_seat_count);
        et_occupancy_seat_count = (EditText) findViewById(R.id.et_occupancy_seat_count);
        et_mobile = (EditText) findViewById(R.id.et_mobile );
        et_password= (EditText) findViewById(R.id.et_password );
        et_vpassword= (EditText) findViewById(R.id.et_vpassword );
        library_profile_photo = (CircularNetworkImageView) findViewById(R.id.library_profile_photo);


        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_hall_count_set)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_seat_count_set)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.library_profile_photo)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_google_map)).setOnClickListener(this);
        tv_map_postion = (TextView) findViewById(R.id.tv_map_postion);


        layout_fix_room= (LinearLayout) findViewById(R.id.layout_fix_room);
        layout_set_room=  (LinearLayout) findViewById(R.id.layout_set_room);
        ((Button) findViewById(R.id.btn_room)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.btn_room)).setOnClickListener(this);


        getSupportActionBar().setHomeButtonEnabled(true);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("libraryId")) {

            setTitle("Edit Library");
            ((Button) findViewById(R.id.btn_google_map)).setText("Edit Location");
            libraryId = getIntent().getExtras().getString("libraryId");
            prevCharges = getIntent().getExtras().getString("charges");

            //imp
            layout_fix_room.setVisibility(View.GONE);
            layout_set_room.setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_room)).setVisibility(View.VISIBLE);

            action = "update";
            setEditAction();
        } else {
            setTitle("Add Library");
        }

        Firebase.setAndroidContext(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                UploadLibrary();
                break;
            case R.id.btn_cancel:
                this.finish();
                break;
            case R.id.library_profile_photo:
                ShowImageChooser();
                break;
            case R.id.btn_hall_count_set:
                SetHallCount();
                break;
            case R.id.btn_seat_count_set:
                SetSeatCount();
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
                Intent intent2 = new Intent(getApplicationContext(), EditLibraryHall.class);
                startActivity(intent2);
                break;

        }
    }

    private  void  SetHallCount()
    {
        try
        {
            hall_count = Integer.parseInt(et_hall_count.getText().toString().trim());
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of halls correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (hall_count <= 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of halls correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (hall_list != null)
        {
            hall_list.clear();
            hall_list = null;
        }

        hall_list = new SparseArray<HallData>(hall_count);

        Toast.makeText(getApplicationContext(), "The number of halls : " + hall_count.toString(), Toast.LENGTH_SHORT).show();
    }

    private void SetSeatCount()
    {
        String hall_name;
        Integer total_seat_count = 0;
        Integer occupancy_seat_count = 0;

        if (hall_count <= 0 || hall_list == null)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of halls correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (hall_count <= hall_list.size())
        {
            Toast.makeText(getApplicationContext(), "The current number of halls be exceeded!", Toast.LENGTH_LONG).show();
            return;
        }

        hall_name = et_hall_name.getText().toString().trim();
        if (hall_name.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please enter the hall name correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        try
        {
            total_seat_count = Integer.parseInt(et_total_seat_count.getText().toString().trim());
            occupancy_seat_count = Integer.parseInt(et_occupancy_seat_count.getText().toString().trim());
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (total_seat_count <= 0 || occupancy_seat_count < 0 || total_seat_count < occupancy_seat_count)
        {
            Toast.makeText(getApplicationContext(), "Please enter the parameters correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        HallData hallData = new HallData(hall_name, total_seat_count, occupancy_seat_count);
        hall_list.put(hall_list.size(), hallData);

        String message = "Total : " + total_seat_count.toString() +
                " Occupancy : " + occupancy_seat_count.toString() +
                " in Hall Name : " + hall_name;
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        et_hall_name.setText("");
        et_total_seat_count.setText("");
        et_occupancy_seat_count.setText("");
    }

    //------------Show File Chooser-------------------
    private void ShowImageChooser() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryEditorActivity.this);
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
                    library_profile_photo.setImageBitmap(thumbnail);
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

                library_profile_photo.setImageBitmap(thumbnail);

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
        final String tag_string_req = "LibraryDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int hall_no = 0; hall_no < responseArr.length(); hall_no++) {

                        JSONObject obj = responseArr.getJSONObject(hall_no);
                        if (hall_no == 0)
                        {
                            name = obj.getString("name");
                            location = obj.getString("location");
                            person = obj.getString("person");
                            charges = obj.getString("charges");
                            mobile = obj.getString("mobile");
                            double latitude = obj.getDouble("latitude");
                            double longitude = obj.getDouble("longitude");
                            mMarkerPosition = new LatLng(latitude, longitude);
                            vpassword = password = obj.getString("password");
                            hall_count = Integer.parseInt(obj.getString("hall_count"));
                            String picture = obj.getString("picture");
                            isImageSelected = false;
                            if (picture != null && !picture.equals(""))
                                isImageSelected = true;
                            if (isImageSelected)
                            {
                                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                if (imageLoader == null)
                                    imageLoader = AppController.getInstance().getImageLoader();
                                library_profile_photo.setImageUrl(ApiConfig.urlLibrariesImage + picture, imageLoader);
                            }

                            et_name.setText(name);
                            et_location.setText(location);
                            et_person.setText(person);
                            et_charges.setText(charges);
                            et_mobile.setText(mobile);
                            tv_map_postion.setText(mMarkerPosition.toString());
                            et_password.setText(password);
                            et_vpassword.setText(password);

                            et_hall_count.setText(hall_count.toString());

                            if (hall_list != null)
                            {
                                hall_list.clear();
                                hall_list = null;
                            }

                            hall_list = new SparseArray<HallData>(hall_count);
                        }

                        HallData hallData = new HallData(obj.getString("h_name"),
                                Integer.parseInt(obj.getString("h_total")),
                                Integer.parseInt(obj.getString("h_occupancy")));

                        hall_list.put(hall_no, hallData);
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

                params.put("id", libraryId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void UploadLibrary() {

        // name, picture, gender, dob, age, availability, specialisation, expertise, experience, language, address,
        // email, mobile, password, isActive

        name = et_name.getText().toString().trim();
        location = et_location.getText().toString().trim();
        person = et_person.getText().toString().trim();
        charges = et_charges.getText().toString().trim();
        mobile = et_mobile.getText().toString().trim();
        password = et_password.getText().toString().trim();
        vpassword = et_vpassword.getText().toString().trim();
        if (name.equals("") || location.equals("") || person.equals("") || mobile.equals("") ||
                tv_map_postion.getText().toString().equals("") ||
                et_charges.equals("") || password.equals("") || vpassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter the parameters correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(vpassword))
        {
            Toast.makeText(getApplicationContext(),"Verified password not matched.",Toast.LENGTH_SHORT).show();
            return;
        }

        if (hall_count <= 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of halls correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (hall_list == null || hall_count != hall_list.size())
        {
            Toast.makeText(getApplicationContext(), "Please enter the data of all halls correctly! ", Toast.LENGTH_LONG).show();
            return;
        }

        for (Integer hall_no = 0; hall_no < hall_count; hall_no++)
        {
            HallData hallData = hall_list.get(hall_no);

            if (hallData.name.equals("") || hallData.total <= 0 || hallData.occupancy < 0)
            {
                Toast.makeText(getApplicationContext(), "Please enter the data of all halls correctly!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String tag_string_req = "LibraryRegistrationRequest";
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryReg, new Response.Listener<String>() {

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

                        LibraryEditorActivity.this.finish();
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
                    params.put("action", "library_reg");
                }
                else {
                    params.put("action", "library_update");
                    params.put("libraryId", libraryId);

                }

                //Fields: name, location, person, total, isImageSelected, picture
                params.put("name", name);
                params.put("location", location);
                params.put("person", person);
                params.put("charges", charges);
                chargesDateTime = Utils.currentServerDateTime();
                params.put("chargesDateTime", chargesDateTime);
                params.put("mobile", mobile);
                params.put("latitude", new Double(mMarkerPosition.latitude).toString());
                params.put("longitude", new Double(mMarkerPosition.longitude).toString());
                params.put("password", password);

                params.put("hall_count", hall_count.toString());

                Integer total = 0;
                Integer occupancy = 0;

                for (Integer hall_no = 0; hall_no < hall_count; hall_no++)
                {
                    HallData hallData = hall_list.get(hall_no);
                    total += hallData.total;
                    occupancy += hallData.occupancy;
                    params.put("hall_no_" + hall_no.toString() + "_name", hallData.name);
                    params.put("hall_no_" + hall_no.toString() + "_total", hallData.total.toString());
                    params.put("hall_no_" + hall_no.toString() + "_occupancy", hallData.occupancy.toString());
                }

                params.put("total", total.toString());
                params.put("occupancy", occupancy.toString());

                if (isImageSelected) {
                    params.put("isImageSelected", "yes");
                    params.put("picture", picture);
                }
                else
                    params.put("isImageSelected", "no");

                Log.e("URL", ApiConfig.urlLibraryReg);
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
                ApiConfig.urlLibraryBookingReg, new Response.Listener<String>() {

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
                params.put("libraryId", libraryId);
                params.put("prevCharges", prevCharges);
                params.put("chargesDateTime", chargesDateTime);

                Log.e("URL", ApiConfig.urlLibraryReg);
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

                    Toast.makeText(LibraryEditorActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(mobile)) {
                            reference.child(mobile).child("password").setValue(mobile);
                            reference.child(mobile).child("fullName").setValue(name);
                            reference.child(mobile).child("role").setValue("h");

                            Toast.makeText(LibraryEditorActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LibraryEditorActivity.this, "Chat Disabled", Toast.LENGTH_SHORT).show();
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

        RequestQueue rQueue = Volley.newRequestQueue(LibraryEditorActivity.this);
        rQueue.add(request);


    }

}
