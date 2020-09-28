package com.netist.mygirlshostel.advertisement;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.advertisement.dialogs.ImagePreview_Dialog;
import com.netist.mygirlshostel.payment.PayMentGateWayForAdv;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.netist.mygirlshostel.web_api_handler.ApiConfig.urlView;

public class AddAdvertisementActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddAdvertisementActivity.class.getSimpleName();
    /**
     *
     * sub
     */
    public static final int RequestPermissionCode = 2;
    private EditText tv_sub_name;
    /**
     * Topic Name
     */

    private EditText et_topicname;
    /**
     * Choose Submission Date
     */
    private TextView tv_Date;
    /**
     * Attachment
     */
    private TextView et_attach;
    private TextView tv_ExpDate;
    /**
     * Your Message Here
     */
    private EditText et_message;
    /**
     * Submit
     */
    private Button btn_submit;
    ProgressDialog progressDialog;

    public Calendar date1;
    String formattedDate1;
    File path;
    ArrayList<String> a;
    private int PICK_IMAGE_REQUEST = 1;
    String fullpath;
    String title,details,expiryDate;
    int userId;
    RelativeLayout rl_img_delete;
    Uri uri;
    SessionHelper sessionHelper;
    ImageView iv_backprofile, iv_home;
    Double amount;
    TextView txt_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advertisement);
        initView();
        a=new ArrayList<>();
        EnableRuntimePermission();

        getAmount("5");

        btn_submit.setVisibility(View.VISIBLE);

        Date date = new Date();
        SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c1 = Calendar.getInstance();
        String currentDate = df.format(date);// get current date here

        // now add 30 day in Calendar instance
        c1.add(Calendar.DAY_OF_YEAR, 90);
        df = new SimpleDateFormat("dd-MM-yyyy");
        Date resultDate = c1.getTime();
        String     dueDate = df.format(resultDate);

        Toast.makeText(this, ""+dueDate, Toast.LENGTH_SHORT).show();
        tv_Date.setText(dueDate);
        // print the result
      //  Utils.printLog("DATE_DATE :-> "+currentDate);
       // Utils.printLog("DUE_DATE :-> "+dueDate);
    }

    private void createAdvertise() {

        // for(int i=0 ; i<= a.size(); i++)
        //{
        //  Toast.makeText(AddHomeworkActivity.this, "chk "+a.get(i).toString(), Toast.LENGTH_SHORT).show();

        //InsertMessageWithFile(fullpath,title,details,0,expiryDate);
        InsertMessageWithFile(fullpath,title,details, Integer.parseInt(sessionHelper.getUserID()),
                expiryDate,"1");

        //}
    }

    private void initView() {
        tv_sub_name = (EditText) findViewById(R.id.tv_sub_name);
        rl_img_delete = findViewById(R.id.rl_img_delete);
        et_topicname = (EditText) findViewById(R.id.et_topicname);
        tv_Date = (TextView) findViewById(R.id.tv_Date);
       // tv_Date.setOnClickListener(this);
        tv_ExpDate = (TextView) findViewById(R.id.tv_ExpDate);
        et_attach = (TextView) findViewById(R.id.et_attach);
        et_attach.setOnClickListener(this);
        et_message = (EditText) findViewById(R.id.et_message);
        iv_backprofile =  findViewById(R.id.iv_backprofile);
        iv_home =  findViewById(R.id.iv_home);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        txt_amount =  findViewById(R.id.txt_amount);
        btn_submit.setOnClickListener(this);
        iv_backprofile.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        progressDialog = new ProgressDialog(AddAdvertisementActivity.this);
        sessionHelper = new SessionHelper(AddAdvertisementActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_Date:
                showDateTimePicker1();
                break;
            case R.id.iv_backprofile:
                onBackPressed();
                break;
            case R.id.iv_home:

                Utility.launchActivity(AddAdvertisementActivity.this, AdvHomeActivity.class, true);
            break;
            case R.id.et_attach:

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

                break;
            case R.id.btn_submit:

                expiryDate = tv_Date.getText().toString();
                details = et_message.getText().toString();

                if ( expiryDate.length()==0 || details.length() == 0) {
                    Toast.makeText(AddAdvertisementActivity.this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
                } else {
                    // startActivity(new Intent(AddHomeworkActivity.this, MainActivity.class));
                    //Toast.makeText(AddHomeworkActivity.this, "Homework Updated", Toast.LENGTH_SHORT).show();
                    Log.e(TAG," Userid "+userId //null
                            +" topicnm  "+title+" Dt "+expiryDate );

                    String getFname = "fffffff";
                    String getPhone = "Mobile number";
                    // String getEmail = "demoemail@gmail.com";
                    String getEmail = "Enter email";
                    // String getAmt   = "1";//rechargeAmt.getText().toString().trim();
                    String getAmt   = amount.toString();

                    Intent intent1 = new Intent(getApplicationContext(), PayMentGateWayForAdv.class);
                    intent1.putExtra("FIRST_NAME",getFname);
                    intent1.putExtra("PHONE_NUMBER",getPhone);
                    intent1.putExtra("EMAIL_ADDRESS",getEmail);
                    intent1.putExtra("RECHARGE_AMT",getAmt);
                    intent1.putExtra("C_DATE",Utility.getCurrentDateTime());

                    intent1.putExtra("fullpath",fullpath);
                    intent1.putExtra("title","title");
                    intent1.putExtra("details",details);
                    intent1.putExtra("Userid",sessionHelper.getUserID());
                    intent1.putExtra("expiryDate",expiryDate);
                    intent1.putExtra("amount",getAmt);
                    startActivity(intent1);
                    finish();

                  /*  InsertMessageWithFile(fullpath,title,details, Integer.parseInt(sessionHelper.getUserID()),
                            expiryDate, String.valueOf(amount));
*/

                }

                break;
        }
    }


    /*  private void createAdvertise(int Homeworkid, int Classid, int Subjectid,
                                  String Title, String Topicname, String Subdate, String Details) {

          progressDialog.setTitle("Please Wait...");
          progressDialog.setCancelable(false);
          progressDialog.show();

          HomeworkServices.getInstance(getApplicationContext()).
                  createHomework(Homeworkid, Classid, Subjectid, Title, Topicname, Subdate,
                          Details, new ApiStatusCallBack<Response>() {
                              @Override
                              public void onSuccess(Response response) {
                                  //Check here selected file is coming or not
                                  Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();

                                  homeworkId=response.getResult();
                                  Log.e(TAG,"noticeData "+ Classid +" "+Topicname +"Result "+response.getResult());
                                  sendNotice(Classid,"Homework","Topicname");
                                  //sendNotice(Classid,"Homework",Topicname);
                                  progressDialog.dismiss();

                                  for(int i=0 ; i<= a.size(); i++)
                                  {
                                      //  Toast.makeText(AddHomeworkActivity.this, "chk "+a.get(i).toString(), Toast.LENGTH_SHORT).show();

                                      InsertMessageWithFile(a.get(i).toString(), homeworkId);
                                  }

                              }

                              @Override
                              public void onError(ANError anError) {
                                  progressDialog.dismiss();
                                  Log.e(TAG, "ANError " + anError.getMessage());
                                  Utility.showErrorMessage(AddHomeworkActivity.this, "Server Error", Snackbar.LENGTH_SHORT);
                              }

                              @Override
                              public void onUnknownError(Exception e) {
                                  progressDialog.dismiss();
                                  Log.e(TAG, "exc " + e.getMessage());
                                  Utility.showErrorMessage(AddHomeworkActivity.this, "Server Error", Snackbar.LENGTH_SHORT);
                              }

                          });

      }
  */
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //1 img //3 audio //4 video //5 document

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {

               /* if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        // display your images
                        //fullpath=  getRealPathFromURI(AddAdvertisementActivity.this, uri);
                        fullpath = UriUtils.getPathFromUri(this,uri);

                        Log.e( "onActivityResult: 1",fullpath);

                        if(fullpath.endsWith("jpg") || fullpath.endsWith("jpeg")  || fullpath.endsWith("png"))
                        {
                            //Toast.makeText(AddAdvertisementActivity.this, "correct", Toast.LENGTH_SHORT).show();
                            a.add(fullpath);
                        }
                        else {
                            Toast.makeText(AddAdvertisementActivity.this, "Select only images", Toast.LENGTH_SHORT).show();
                        }
                        // a.add(fullpath);

                        // imageView.setImageURI(uri);
                    }
                }
                else*/ if (data.getData() != null) {
                    uri = data.getData();
                    Log.e(TAG,"uri "+uri);

                    //fullpath= getPath(uri);
                    fullpath = UriUtils.getPathFromUri(this,uri);

                    Log.e(TAG,"fullpath "+fullpath);
                    if(fullpath.endsWith("jpg") || fullpath.endsWith("jpeg")  || fullpath.endsWith("png"))
                    {
                        //Toast.makeText(AddAdvertisementActivity.this, "correct", Toast.LENGTH_SHORT).show();
                        a.add(fullpath);
                    }
                    else {
                        Toast.makeText(AddAdvertisementActivity.this, "Select only images", Toast.LENGTH_SHORT).show();
                    }
                    //a.add(uri);
                }
                if (a.size()==0)
                {
                    et_attach.setText("You selected total "+ 1 +" Images");
                    Log.e(TAG,"size "+(a.size()+1));
                    final ImagePreview_Dialog dialog = new ImagePreview_Dialog(AddAdvertisementActivity.this, fullpath);
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                }
                else if (a.size()>0)
                {
                    et_attach.setText("You selected total "+ 1 +" Images");
                    Toast.makeText(AddAdvertisementActivity.this, "You can Pick only single image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"size elseif "+(a.size()));
                    final ImagePreview_Dialog dialog = new ImagePreview_Dialog(AddAdvertisementActivity.this, fullpath);
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                }else
                {
                    Toast.makeText(AddAdvertisementActivity.this, "You can Pick only single image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"else "+(a.size()));
                }


                /*if (a.size()>0)
                        {
                            Toast.makeText(AddAdvertisementActivity.this, "You can Pick only single image", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            et_attach.setText("You selected total "+ a.size()+" Images");
                        }*/
            }
            //  Log.e( "onActivityResult: ", a.get(0));
        }
        Log.e( "onActivityResult:2 ", a.toString());


        /* try {
            if (requestCode == OPEN_IMAGE_PICKER) {
                // Make sure the request was successful
                files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                //ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                Log.e(TAG, "onActivityResult: e " + files.toString());
                for (MediaFile media : files) {
                    InsertMessageWithFile(media, 1);
                }
                Log.e(TAG, "onActivityResult: e " + files.toString() );
            }

        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: e " + e.getMessage());
        }*/


    }

    private void InsertMessageWithFile(String imgpath,String Title ,String Details , int Userid,
                                       String Expirydate, String Amount ) {
        try {
            progressDialog.show();
            //  uriData = media.getUri();
            path = new File(imgpath);
            Log.e(TAG, "uploadWithFilePath: " + path.toString()+Userid);

            //{"type":3,"Action":1,"Attachmentid":0,"Homeworkid":3,"Typeid":1,"Filename":"Filename","LogedinUserId":1}
            int UserId = 1;
            AndroidNetworking.upload("http://iysonline.club/iys/api/processes/images.php/")
                    // .addFileToUpload("", "certificate") //Adding file
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("ImagesId", "0")
                    .addMultipartParameter("Title", Title)
                    .addMultipartParameter("Details",Details)
                    .addMultipartFile("images", path)
                    .addMultipartParameter("Userid", String.valueOf(Userid))
                    .addMultipartParameter("Expirydate", Expirydate)
                    .addMultipartParameter("Isapprove", "0")
                    .addMultipartParameter("Amount", Amount)
                    .addMultipartParameter("LogedinUserId", "1")
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            Log.e(TAG, "uploadImage: totalBytes: " + totalBytes);
                            Log.e(TAG, "uploadImage: bytesUploaded: " + bytesUploaded);
                            progressDialog.setMessage("Attaching File, Please wait...");
                            progressDialog.show();
                            progressDialog.setCancelable(false);
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.hide();
                            Log.e(TAG, "FileonRes: " + response.toString());
                            Toast.makeText(AddAdvertisementActivity.this,"Created Succesfully ",Toast.LENGTH_LONG).show();
                          /*  Intent intent= new Intent(AddAdvertisementActivity.this,AdvertisementListActivity.class);
                            startActivity(intent);*/
                          Utility.launchActivity(AddAdvertisementActivity.this, AdvertisementListActivity.class,true);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "FileonError: ", error);
                            progressDialog.hide();
                        }
                    });
        } catch (Exception exc) {
            Log.e(TAG, "InsertMessageWithPdf: " + exc.getMessage());
            Toast.makeText(AddAdvertisementActivity.this, "Please select Image", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDateTimePicker1() {
        final Calendar currentDate = Calendar.getInstance();
        date1 = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                date1.set(year, monthOfYear, dayOfMonth);

                Calendar c = Calendar.getInstance();
                c.set(year, monthOfYear, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
              //  formattedDate1 = sdf.format(c.getTime());
                formattedDate1 = Utility.getCurrentDateTime();
                Log.e(TAG, "formattedDate1 " + formattedDate1);

                c.add(Calendar.DATE, 90);
                //c.add(sdf.format(c.getTime()), 30);

                Date expDate = c.getTime();
                SimpleDateFormat dest = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                String result = dest.format(expDate);
                 // Log.e(TAG,"exp "+result);
                // tv_Date.setText(formattedDate1);
                tv_Date.setText(result);

                //use this date as per your requirement
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddAdvertisementActivity.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        //cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

        String path = null;
        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        cursor.close();

        return path;
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddAdvertisementActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(AddAdvertisementActivity.this, "Storage permission allows us to Access Storage", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(AddAdvertisementActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCode);

        }
    }

    private  void getAmount(final String hostel_id)
    {
        String  tag_string_req = "req";

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlView, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);
                    //  Toast.makeText(getApplicationContext(), "responseArr"+responseArr, Toast.LENGTH_LONG).show();
                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                        //24-06
                        amount= Double.valueOf(obj.getString("charges"));

                        //  listItem.put("id",obj.getString("id"));
                        Log.e( "chkamount: ", obj.getString("charges") );
                        txt_amount.setText("You have to pay Rs."+amount+" for 90 days, please click on continue ");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e(tag_string_req,e.getMessage());
                }

                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                // Toast.makeText(getApplicationContext(), "onErrorResponse"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("action", "viewChargesAdv");
                params.put("hostel_id", hostel_id);

                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



}
