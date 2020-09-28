package com.netist.mygirlshostel.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
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

public class ViewEditorActivity extends AppCompatActivity implements  View.OnClickListener {


    ImageView notice_banner, notice_file;
    Button btn_date, btn_time, btn_submit;
    int year, month, day, hours, minute;
    String action = "insert", noticeId = "0", id, type;
    boolean isImageSelected = false, isDocSelected = false;
    int i, imageCount = 0, docCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_editor);

        setTitle("Add View");

        id = getIntent().getExtras().getString("id");
        type = getIntent().getExtras().getString("type");

        notice_banner = (ImageView) findViewById(R.id.img_notice_banner);
        notice_file = (ImageView) findViewById(R.id.img_notice_file);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_time = (Button) findViewById(R.id.btn_time);
        //btn_submit = (Button) findViewById(R.id.btn_submit);

        notice_banner.setOnClickListener(this);
        notice_file.setOnClickListener(this);
        btn_date.setOnClickListener(this);
        btn_time.setOnClickListener(this);
        //btn_submit.setOnClickListener(this);

        //-----------Set date----------------
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        //-----------Set date----------------
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        showTime(hours, minute);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        if (v == notice_banner) {
            ShowImageChooser();
        }
        if (v == notice_file) {
            ShowFileChooser();
        }
        if (v == btn_date) {
            showDialog(999);
        }
        if (v == btn_time) {
            showDialog(998);
        }
    }

    // region "Date Time Selecter"
    //--------------Date Selection-----------

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        if (id == 998) {
            return new TimePickerDialog(this, myTimeListener, hours, minute, true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    // TODO Auto-generated method stub
                    showDate(year, month + 1, day);
                }
            };

    private void showDate(int year, int month, int day) {
        String monthStr = "";
        if (month < 10)
            monthStr = "0" + month;
        else
            monthStr = "" + month;

        btn_date.setText(new StringBuilder().append(day).append("/")
                .append(monthStr).append("/").append(year));
    }

    //--------------Time Selection-----------

    private TimePickerDialog.OnTimeSetListener myTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    showTime(hourOfDay, minute);
                }
            };

    private void showTime(int hours, int minute) {
        btn_time.setText(new StringBuilder().append(hours).append(":")
                .append(minute));
    }

    //endregion

    //region "File Selector"
    private int REQUEST_IMAGE_FILE = 1;
    private int REQUEST_CAMERA = 2;
    private int REQUEST_DOC_FILE = 3;

    private void ShowImageChooser() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEditorActivity.this);
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
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_DOC_FILE);
    }

    //------------Show File Into Image View-------------------

    //private Bitmap thumbnail[] = null;
    private ArrayList<String> fileBase64Strings = new ArrayList<String>(), imageBase64Strings = new ArrayList<String>();
    private byte[] byteArray = null;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_FILE) {
                try {
                    // When an Image is picked
                    if (data.getClipData() != null && data.getClipData().getItemCount() > 1) {

                        ClipData mClipData = data.getClipData();

                        int SelectionCount = mClipData.getItemCount();

                        Toast.makeText(getApplicationContext(), "You picked " + SelectionCount + " Image",
                                Toast.LENGTH_LONG).show();

                        for (int i = 0; i < SelectionCount; i++) {

                            Uri selectedFileURI = mClipData.getItemAt(i).getUri();
                            try {

                                imageBase64Strings.add(getBase64Encode(selectedFileURI));
                                imageCount++;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (data.getData() != null) {
                        Toast.makeText(getApplicationContext(), "You picked 1 Image",
                                Toast.LENGTH_LONG).show();

                        Uri selectedFileURI = data.getData();
                        try {
                            imageBase64Strings.add(getBase64Encode(selectedFileURI));
                            imageCount++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "You haven't picked any Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: Something went wrong " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
                super.onActivityResult(requestCode, resultCode, data);
                isImageSelected = true;
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteBuffer);

                imageBase64Strings.add(Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP));
                imageCount++;

                isImageSelected = true;
            }
            if (requestCode == REQUEST_DOC_FILE) {
                try {
                    // When File is picked
                    if (data.getClipData() != null && data.getClipData().getItemCount() > 1) {

                        ClipData mClipData = data.getClipData();

                        int SelectionCount = mClipData.getItemCount();

                        Toast.makeText(getApplicationContext(), "You picked " + SelectionCount + " Files",
                                Toast.LENGTH_LONG).show();
                        for (int i = 0; i < SelectionCount; i++) {
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
                    } else if (data.getData() != null) {
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

                    } else {
                        Toast.makeText(getApplicationContext(), "You haven't picked any Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: Something went wrong " + e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
                isDocSelected = true;
            }

            ((TextView) findViewById(R.id.tv_image_count)).setText(imageCount + " Image Selected");
            ((TextView) findViewById(R.id.tv_doc_count)).setText(docCount + " File Selected");
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
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    String title, details, date, time, imgName = "";

    public void UploadView(View v) {

        title = ((EditText) findViewById(R.id.et_title)).getText().toString().trim();
        details = ((EditText) findViewById(R.id.et_details)).getText().toString().trim();
        date = btn_date.getText().toString().trim();
        time = btn_time.getText().toString().trim();

        if (title.equals("") || date.equals("") || time.equals("")) {
            Toast.makeText(getApplicationContext(), "Fill all Mandatory Fields (*).", Toast.LENGTH_LONG).show();
        } else {
            String tag_string_req = "ViewRequest";
            final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlViewReg, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e("Response String", response);
                    //Disimissing the progress dialog
                    loading.dismiss();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        Toast.makeText(getApplicationContext(), jObj.getString("msg"), Toast.LENGTH_LONG).show();
                        // Check for error node in json
                        if (!error) {

                            if (jObj.has("imgName"))
                                imgName = jObj.getString("imgName");
                            sendMultiplePush();

                            ViewEditorActivity.this.finish();
                        }
                    } catch (JSONException e) {
                        //Dismissing the progress dialog
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
                    //Dismissing the progress dialog
                    loading.dismiss();
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();

                    //type, name, age, gender, date, time, issue;

                    params.put("action", "view_reg");
                    params.put("type", type);
                    params.put("id", id);
                    params.put("title", title);
                    params.put("details", details);

                    try {
                        params.put("dateTime", Utils.formatDate(date, time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (isImageSelected) {
                        params.put("isImageSelected", "yes");
                        if (imageBase64Strings != null) {
                            for (i = 0; i < imageBase64Strings.size(); i++) {
                                String image = imageBase64Strings.get(i);
                                params.put("imgFileStream[" + i + "]", image);
                            }
                        }
                    } else
                        params.put("isImageSelected", "no");

                    if (isDocSelected) {
                        params.put("isDocSelected", "yes");
                        if (fileBase64Strings != null) {
                            for (i = 0; i < fileBase64Strings.size(); i++) {
                                params.put("docFileStream[" + i + "]", fileBase64Strings.get(i));
                            }
                        }
                    } else
                        params.put("isDocSelected", "no");

                    Log.e("URL", ApiConfig.urlViewReg);
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
        final String title = "View : " + this.title;
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
                if (!imgName.trim().equals(""))
                    params.put("image", ApiConfig.urlViewImage + imgName);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

}
