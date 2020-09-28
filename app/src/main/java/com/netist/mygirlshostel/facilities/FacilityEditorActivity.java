package com.netist.mygirlshostel.facilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FacilityEditorActivity extends BaseActivity implements View.OnClickListener{

    ImageView facility_banner, facility_file;

    Button btn_start_date, btn_start_time, btn_end_date, btn_end_time;
    int year , month, day, hours, minute;
    boolean isImageSelected = false, isDocSelected = false;
    int i, imageCount =0, docCount=0;

    String id, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_editor);

        id = getIntent().getExtras().getString("id");
        type = getIntent().getExtras().getString("type");

        facility_banner = (ImageView) findViewById(R.id.img_facility_banner);
        facility_file = (ImageView) findViewById(R.id.img_facility_file);
        btn_start_date = (Button) findViewById(R.id.btn_start_date);
        btn_start_time = (Button) findViewById(R.id.btn_start_time);
        btn_end_date = (Button) findViewById(R.id.btn_end_date);
        btn_end_time = (Button) findViewById(R.id.btn_end_time);
        //btn_submit = (Button) findViewById(R.id.btn_submit);

        facility_banner.setOnClickListener(this);
        facility_file.setOnClickListener(this);
        btn_start_date.setOnClickListener(this);
        btn_start_time.setOnClickListener(this);
        btn_end_date.setOnClickListener(this);
        btn_end_time.setOnClickListener(this);
        //btn_submit.setOnClickListener(this);

        //-----------Set date----------------
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showStartDate(year, month + 1, day);
        showEndDate(year, month + 1, day);

        //-----------Set date----------------
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        showStartTime(hours, minute);
        showEndTime(hours, minute);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        if (v == facility_banner) {
            ShowImageChooser();
        }
        else if(v == facility_file)
        {
            ShowFileChooser();
        }
        else if (v == btn_start_date) {
            showDialog(999);
        }
        else if (v == btn_start_time) {
            showDialog(998);
        }
        else if (v == btn_end_date) {
            showDialog(997);
        }
        else if (v == btn_end_time) {
            showDialog(996);
        }
    }

    //region "Date Time Selector"
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myStartDateListener, year, month, day);
        }
        else if (id == 998) {
            return new TimePickerDialog(this,
                    myStartTimeListener, hours, minute, true);
        }
        else if (id == 997) {
            return new DatePickerDialog(this,
                    myEndDateListener, year, month, day);
        }
        else if (id == 996) {
            return new TimePickerDialog(this,
                    myEndTimeListener, hours, minute, true);
        }
        return null;
    }
    //--------------Start Date Selection-----------

    private DatePickerDialog.OnDateSetListener myStartDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    // TODO Auto-generated method stub
                    showStartDate(year, month + 1, day);
                }
            };

    private void showStartDate(int year, int month, int day) {
        String monthStr = "";
        if (month < 10)
            monthStr = "0" + month;
        else
            monthStr = "" + month;

        btn_start_date.setText(new StringBuilder().append(day).append("/")
                .append(monthStr).append("/").append(year));
    }

    //--------------start Time Selection-----------

    private TimePickerDialog.OnTimeSetListener myStartTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    showStartTime(hourOfDay, minute);
                }
            };

    private void showStartTime(int hours, int minute) {
        btn_start_time.setText(new StringBuilder().append(hours).append(":")
                .append(minute));
    }

    //--------------End Date Selection-----------

    private DatePickerDialog.OnDateSetListener myEndDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    // TODO Auto-generated method stub
                    showEndDate(year, month + 1, day);
                }
            };

    private void showEndDate(int year, int month, int day) {
        String monthStr = "";
        if (month < 10)
            monthStr = "0" + month;
        else
            monthStr = "" + month;

        btn_end_date.setText(new StringBuilder().append(day).append("/")
                .append(monthStr).append("/").append(year));
    }

    //--------------End Time Selection-----------

    private TimePickerDialog.OnTimeSetListener myEndTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    showEndTime(hourOfDay, minute);
                }
            };

    private void showEndTime(int hours, int minute) {
        btn_end_time.setText(new StringBuilder().append(hours).append(":")
                .append(minute));
    }
    //endregion

    //region "File Selector"

    private int REQUEST_IMAGE_FILE = 1;
    private int REQUEST_CAMERA = 2;
    private int REQUEST_DOC_FILE = 3;

    private void ShowImageChooser() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(FacilityEditorActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[item].equals("Choose from Library")) {

                    Intent intent = new Intent();
                    intent.setType("image/*");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_FILE);

                } else if (items[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void ShowFileChooser() {
        Intent intent = new Intent();
        //intent.setType("image/*");
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_DOC_FILE);
    }

    //------------Show File Into Image View-------------------

    //private Bitmap thumbnail[] = null;
    private ArrayList<String> fileBase64Strings = new ArrayList<String>(), imageBase64Strings = new ArrayList<String>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
        {
            if (requestCode == REQUEST_IMAGE_FILE) {
                try {
                    // When an Image is picked
                    if (data.getClipData() != null && data.getClipData().getItemCount()>1) {

                        ClipData mClipData = data.getClipData();

                        int SelectionCount = mClipData.getItemCount();

                        Toast.makeText(getApplicationContext(), "You picked "+ SelectionCount + " Image",
                                Toast.LENGTH_LONG).show();

                        for(int i = 0; i < SelectionCount; i++) {

                            Uri selectedFileURI = mClipData.getItemAt(i).getUri();
                            try {

                                imageBase64Strings.add(getBase64Encode(selectedFileURI));

                                imageCount++;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if (data.getData() != null )
                    {
                        Toast.makeText(getApplicationContext(), "You picked 1 Image",
                                Toast.LENGTH_LONG).show();

                        Uri selectedFileURI = data.getData();
                        try {
                            imageBase64Strings.add(getBase64Encode(selectedFileURI));
                            imageCount++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "You haven't picked any Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: Something went wrong " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
                super.onActivityResult(requestCode, resultCode, data);
                isImageSelected=true;
            }
            else if (requestCode == REQUEST_CAMERA)
            {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteBuffer);
                imageBase64Strings.add(Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP));
                imageCount++;

                isImageSelected=true;
            }
            if (requestCode == REQUEST_DOC_FILE)
            {
                try {
                    // When File is picked
                    if (data.getClipData() != null && data.getClipData().getItemCount()>1) {

                        ClipData mClipData = data.getClipData();

                        int SelectionCount = mClipData.getItemCount();

                        Toast.makeText(getApplicationContext(), "You picked "+ SelectionCount + " Files",
                                Toast.LENGTH_LONG).show();
                        for(int i = 0; i < SelectionCount; i++) {
                            ClipData.Item item = mClipData.getItemAt(i);

                            Uri selectedFileURI = item.getUri();
                            try {
                                fileBase64Strings.add(getBase64Encode(selectedFileURI));
                                docCount++;

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("IOException", e.getMessage());
                            }
                        }
                    }
                    else if (data.getData() != null )
                    {
                        Toast.makeText(getApplicationContext(), "You picked 1 File",
                                Toast.LENGTH_LONG).show();

                        Uri selectedFileURI = data.getData();

                        try {
                            fileBase64Strings.add(getBase64Encode(selectedFileURI));
                            docCount++;

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("IOException", e.getMessage());
                        }

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "You haven't picked any Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: Something went wrong " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
                isDocSelected=true;
            }

            ((TextView)findViewById(R.id.tv_image_count)).setText(imageCount+" Image Selected");
            ((TextView)findViewById(R.id.tv_doc_count)).setText(docCount+" File Selected");
        }
    }

    public String getBase64Encode(Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);
    }

    //-------------convert Bitmap to base64 String.------------

    //endregion

    //--------------Upload Notice--------------------------
    String title,details,start_date,start_time,end_date,end_time, imgName="";
    public void UploadFacility(View v) {

        title = ((EditText) findViewById(R.id.et_title)).getText().toString().trim();
        details = ((EditText) findViewById(R.id.et_details)).getText().toString().trim();

        start_date = btn_start_date.getText().toString().trim();
        start_time = btn_start_time.getText().toString().trim();
        end_date = btn_end_date.getText().toString().trim();
        end_time = btn_end_time.getText().toString().trim();

        if (title.equals("") || start_date.equals("") || start_time.equals("")) {
            Toast.makeText(getApplicationContext(), "Fill all Mandatory Fields (*).", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getApplicationContext(),"DateTime = "+formatedDate, Toast.LENGTH_SHORT).show();

            String tag_string_req = "NoticeRequest";
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlFacilityReg, new Response.Listener<String>() {

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

                            if(jObj.has("imgName"))
                                imgName= jObj.getString("imgName");
                            sendMultiplePush();
                            //Intent intent = new Intent(HostelEditorActivity.this,HostelListActivity.class);
                            //startActivity(intent);
                            FacilityEditorActivity.this.finish();
                        }
                    } catch (JSONException e) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

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

                    //type, name, age, gender, date, time, issue;

                    params.put("action", "facility_reg");

                    params.put("type", type);
                    params.put("id", id);
                    params.put("title", title);
                    params.put("details", details);

                    try {
                        params.put("startDateTime", Utils.formatDate(start_date, start_time));
                        params.put("endDateTime", Utils.formatDate(end_date, end_time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(isImageSelected) {
                        params.put("isImageSelected", "yes");
                        if(imageBase64Strings!=null) {
                            for(i = 0; i < imageBase64Strings.size(); i++)
                            {
                                String image = imageBase64Strings.get(i);
                                params.put("imgFileStream["+i+"]", image);
                            }
                        }
                    }
                    else
                        params.put("isImageSelected", "no");

                    if(isDocSelected) {
                        params.put("isDocSelected", "yes");
                        if(fileBase64Strings!=null) {
                            for(i = 0; i < fileBase64Strings.size(); i++)
                            {
                                params.put("docFileStream["+i+"]", fileBase64Strings.get(i));
                            }
                        }
                    }
                    else
                        params.put("isDocSelected", "no");


                    Log.e("URL", ApiConfig.urlFacilityReg);
                    Log.e("Register Params: ", params.toString());

                    return params;
                }
            };

            strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }
    }

    private void sendMultiplePush() {
        final String title = "Facility : "+this.title;
        final String message = this.details;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.urlSendMultiplePush,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Push Response", response);
                        //Toast.makeText(SendPushActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                if(!imgName.trim().equals(""))
                    params.put("image", ApiConfig.urlFacilityImage+imgName);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}