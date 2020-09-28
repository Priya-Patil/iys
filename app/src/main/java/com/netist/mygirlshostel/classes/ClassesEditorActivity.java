package com.netist.mygirlshostel.classes;

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
import com.netist.mygirlshostel.components.BatchData;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClassesEditorActivity extends BaseActivity implements View.OnClickListener{

    EditText et_name, et_location, et_mobile, et_password, et_vpassword,
            et_batch_count, et_batch_name, et_batch_charge, et_person;
    CircularNetworkImageView classes_profile_photo;
    String classesId, name, location, picture, mobile, password, vpassword, person;
    Integer batch_count = 0;
    boolean isImageSelected = false;
    SparseArray<BatchData> batch_list = null;
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
        setContentView(R.layout.activity_classes_editor);

        et_name = (EditText) findViewById(R.id.et_name);
        et_location = (EditText) findViewById(R.id.et_location);
        et_person = (EditText) findViewById(R.id.et_person);
        et_batch_count = (EditText) findViewById(R.id.et_batch_count);
        et_batch_name = (EditText) findViewById(R.id.et_batch_name);
        et_batch_charge = (EditText) findViewById(R.id.et_batch_charge);
        et_mobile = (EditText) findViewById(R.id.et_mobile );
        et_password= (EditText) findViewById(R.id.et_password );
        et_vpassword= (EditText) findViewById(R.id.et_vpassword );
        classes_profile_photo = (CircularNetworkImageView) findViewById(R.id.classes_profile_photo);


        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_batch_count_set)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_batch_name_set)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.classes_profile_photo)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_google_map)).setOnClickListener(this);
        tv_map_postion = (TextView) findViewById(R.id.tv_map_postion);
        layout_fix_room= (LinearLayout) findViewById(R.id.layout_fix_room);
        layout_set_room=  (LinearLayout) findViewById(R.id.layout_set_room);
        ((Button) findViewById(R.id.btn_room)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.btn_room)).setOnClickListener(this);

        getSupportActionBar().setHomeButtonEnabled(true);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("classesId")) {

            setTitle("Edit Classes");
            ((Button) findViewById(R.id.btn_batch_count_set)).setText("INIT");
            ((Button) findViewById(R.id.btn_google_map)).setText("Edit Location");
            classesId = getIntent().getExtras().getString("classesId");

            //imp
            layout_fix_room.setVisibility(View.GONE);
            layout_set_room.setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_room)).setVisibility(View.VISIBLE);
            action = "update";
            setEditAction();
        } else {
            setTitle("Add Classes");
        }

        Firebase.setAndroidContext(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                UploadClasses();
                break;
            case R.id.btn_cancel:
                this.finish();
                break;
            case R.id.classes_profile_photo:
                ShowImageChooser();
                break;
            case R.id.btn_batch_count_set:
                SetBatchCount();
                break;
            case R.id.btn_batch_name_set:
                SetBatchData();
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
                Intent intent2 = new Intent(getApplicationContext(), EditClassType.class);
                startActivity(intent2);
                break;

        }
    }

    private  void  SetBatchCount()
    {
        try
        {
            batch_count = Integer.parseInt(et_batch_count.getText().toString().trim());
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of batch correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (batch_count <= 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of batch correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (batch_list != null)
        {
            batch_list.clear();
            batch_list = null;
        }

        batch_list = new SparseArray<BatchData>(batch_count);

        Toast.makeText(getApplicationContext(), "The number of batch : " + batch_count.toString(), Toast.LENGTH_SHORT).show();
    }

    private void SetBatchData()
    {
        String batch_name;
        Double batch_charge;

        if (batch_count <= 0 || batch_list == null)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of batch correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (batch_count <= batch_list.size())
        {
            Toast.makeText(getApplicationContext(), "The current number of batch be exceeded!", Toast.LENGTH_LONG).show();
            return;
        }

        batch_name = et_batch_name.getText().toString().trim();
        if (batch_name.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please enter the batch name correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        try
        {
            batch_charge = Double.parseDouble(et_batch_charge.getText().toString().trim());
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        BatchData batchData = new BatchData(batch_name, batch_charge);
        batch_list.put(batch_list.size(), batchData);

        String message = "Charge : " + batch_charge.toString() + " of Type Name : " + batch_name;
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        et_batch_name.setText("");
        et_batch_charge.setText("");
    }

    //------------Show File Chooser-------------------
    private void ShowImageChooser() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassesEditorActivity.this);
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
                    classes_profile_photo.setImageBitmap(thumbnail);
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

                classes_profile_photo.setImageBitmap(thumbnail);

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
        final String tag_string_req = "ClassesDetailsRequest";

        final ProgressDialog loading = ProgressDialog.show(this, "Updating...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int batch_no = 0; batch_no < responseArr.length(); batch_no++) {

                        JSONObject obj = responseArr.getJSONObject(batch_no);
                        if (batch_no == 0)
                        {
                            name = obj.getString("name");
                            location = obj.getString("location");
                            person = obj.getString("person");
                            mobile = obj.getString("mobile");
                           double latitude = obj.getDouble("latitude");
                            double longitude = obj.getDouble("longitude");
                            mMarkerPosition = new LatLng(latitude, longitude);
                            vpassword = password = obj.getString("password");
                            batch_count = Integer.parseInt(obj.getString("batch_count"));
                            String picture = obj.getString("picture");
                            isImageSelected = false;
                            if (picture != null && !picture.equals(""))
                                isImageSelected = true;
                            if (isImageSelected)
                            {
                                ImageLoader imageLoader = AppController.getInstance().getImageLoader();
                                if (imageLoader == null)
                                    imageLoader = AppController.getInstance().getImageLoader();
                                classes_profile_photo.setImageUrl(ApiConfig.urlClassesImage + picture, imageLoader);
                            }

                            et_name.setText(name);
                            et_location.setText(location);
                            et_person.setText(person);
                            et_mobile.setText(mobile);
                            tv_map_postion.setText(mMarkerPosition.toString());
                            et_password.setText(password);
                            et_vpassword.setText(password);
                            et_batch_count.setText(batch_count.toString());

                            if (batch_list != null)
                            {
                                batch_list.clear();
                                batch_list = null;
                            }

                            batch_list = new SparseArray<BatchData>(batch_count);
                        }

                        BatchData batchData = new BatchData(obj.getString("batch_name"),
                                Double.parseDouble(obj.getString("batch_charge")));
                        batch_list.put(batch_no, batchData);
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

                params.put("id", classesId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void UploadClasses() {

        // name, picture, gender, dob, age, availability, specialisation, expertise, experience, language, address,
        // email, mobile, password, isActive

        name = et_name.getText().toString().trim();
        location = et_location.getText().toString().trim();
        person = et_person.getText().toString().trim();
        mobile = et_mobile.getText().toString().trim();
        password = et_password.getText().toString().trim();
        vpassword = et_vpassword.getText().toString().trim();
        if (name.equals("") || location.equals("") || person.equals("") || mobile.equals("") ||
                tv_map_postion.getText().toString().equals("") ||
                password.equals("") || vpassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter the parameters correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(vpassword))
        {
            Toast.makeText(getApplicationContext(),"Verified password not matched.",Toast.LENGTH_SHORT).show();
            return;
        }

        if (batch_count <= 0)
        {
            Toast.makeText(getApplicationContext(), "Please enter the number of batch correctly!", Toast.LENGTH_LONG).show();
            return;
        }

        if (batch_list == null || batch_count != batch_list.size())
        {
            Toast.makeText(getApplicationContext(), "Please enter the data of all batch correctly! ", Toast.LENGTH_LONG).show();
            return;
        }

        for (Integer batch_no = 0; batch_no < batch_count; batch_no++)
        {
            BatchData batchData = batch_list.get(batch_no);

            if (batchData.name.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter the data of all batchs correctly!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String tag_string_req = "ClassesRegistrationRequest";
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesReg, new Response.Listener<String>() {

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

                        ClassesEditorActivity.this.finish();
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
                    params.put("action", "classes_reg");
                }
                else {
                    params.put("action", "classes_update");
                    params.put("classesId", classesId);

                }

                //Fields: name, location, person, batch_count, batch_list, total, isImageSelected, picture
                params.put("name", name);
                params.put("location", location);
                params.put("person", person);
                params.put("mobile", mobile);
                params.put("latitude", new Double(mMarkerPosition.latitude).toString());
                params.put("longitude", new Double(mMarkerPosition.longitude).toString());
                params.put("password", password);
                params.put("batch_count", batch_count.toString());

                for (Integer batch_no = 0; batch_no < batch_count; batch_no++)
                {
                    BatchData batchData = batch_list.get(batch_no);
                    params.put("batch_no_" + batch_no.toString() + "_name", batchData.name);
                    params.put("batch_no_" + batch_no.toString() + "_charge", batchData.charge.toString());
                }

                if (isImageSelected) {
                    params.put("isImageSelected", "yes");
                    params.put("picture", picture);
                }
                else
                    params.put("isImageSelected", "no");

                Log.e("URL", ApiConfig.urlClassesReg);
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
                ApiConfig.urlClassesBookingReg, new Response.Listener<String>() {

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
                params.put("classesId", classesId);

                Log.e("URL", ApiConfig.urlClassesReg);
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

                    Toast.makeText(ClassesEditorActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(mobile)) {
                            reference.child(mobile).child("password").setValue(mobile);
                            reference.child(mobile).child("fullName").setValue(name);
                            reference.child(mobile).child("role").setValue("h");

                            Toast.makeText(ClassesEditorActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ClassesEditorActivity.this, "Chat Disabled", Toast.LENGTH_SHORT).show();
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

        RequestQueue rQueue = Volley.newRequestQueue(ClassesEditorActivity.this);
        rQueue.add(request);


    }

}
