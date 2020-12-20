package com.netist.mygirlshostel.adv;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditAdvertisementActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditAdvertisementActivity.class.getSimpleName();
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
    String title,details,expiryDate,imageName;
    String Id,Userid;
    Intent intent ;
    ImageView img_title,iv_delete;
    String profile_path;

    RelativeLayout rl_img_delete;
    ImageView iv_backprofile, iv_home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advertisement);

        a=new ArrayList<>();
        EnableRuntimePermission();
        initView();

        rl_img_delete.setVisibility(View.VISIBLE);

        intent = getIntent();
        Id = intent.getStringExtra("id");
        Userid = intent.getStringExtra("Userid");
        title = intent.getStringExtra("title");
        details = intent.getStringExtra("details");
        imageName = intent.getStringExtra("imageName");
        expiryDate = intent.getStringExtra("expiryDate");
        profile_path = intent.getStringExtra("profile_path");

        Log.e(TAG,"Id "+Id+" "+title +" "+profile_path +"userID "+Userid);

        Picasso.with(EditAdvertisementActivity.this).load(profile_path).into(img_title);

        tv_Date.setText(expiryDate);
        tv_Date.setEnabled(false);
        et_topicname.setEnabled(false);
        et_message.setEnabled(false);
        et_topicname.setText(title);
        et_message.setText(details);
        //et_attach.setText(imageName);


        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ViewImage dialog = new ViewImage(EditAdvertisementActivity.this,profile_path);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
            }
        });
    }



    private void initView() {
        rl_img_delete =  findViewById(R.id.rl_img_delete);
        tv_sub_name = (EditText) findViewById(R.id.tv_sub_name);
        et_topicname = (EditText) findViewById(R.id.et_topicname);
        tv_Date = (TextView) findViewById(R.id.tv_Date);
        tv_Date.setOnClickListener(this);
        tv_ExpDate = (TextView) findViewById(R.id.tv_ExpDate);
        et_attach = (TextView) findViewById(R.id.et_attach);
        et_attach.setOnClickListener(this);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        img_title =  findViewById(R.id.img_title);
        img_title.setOnClickListener(this);
        iv_delete =  findViewById(R.id.iv_delete);
        iv_delete.setOnClickListener(this);

        iv_backprofile =  findViewById(R.id.iv_backprofile);
        iv_home =  findViewById(R.id.iv_home);
        iv_backprofile.setOnClickListener(this);
        iv_home.setOnClickListener(this);

        progressDialog = new ProgressDialog(EditAdvertisementActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;

            case R.id.tv_Date:
               // showDateTimePicker1();
                break;

           case R.id.iv_delete:
               //dialogForDeleteImage();
              // dialogForDeleteImage(Id,profile_path,title,details,Userid,expiryDate);

               break;

            case R.id.iv_backprofile:
                onBackPressed();
                break;
            case R.id.iv_home:

                Utility.launchActivity(EditAdvertisementActivity.this, AdvHomeActivity.class, true);
                break;

            case R.id.et_attach:
/*

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
*/

                break;
            case R.id.btn_submit:

               /* title = et_topicname.getText().toString();
                expiryDate = tv_Date.getText().toString();
                details = et_message.getText().toString();

                if (title.length()==0 || expiryDate.length()==0 || details.length() == 0) {
                    Toast.makeText(EditAdvertisementActivity.this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
                } else {
                    // startActivity(new Intent(AddHomeworkActivity.this, MainActivity.class));
                    //Toast.makeText(AddHomeworkActivity.this, "Homework Updated", Toast.LENGTH_SHORT).show();
                    Log.e(TAG," id "+Id +" "+profile_path+" "+details +" "+Userid
                            +" title  "+title+" Dt "+expiryDate );

                    InsertMessageWithFile(Id,profile_path,title,details,Userid,expiryDate);

                }*/


                break;
        }
    }

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
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        // display your images
                        fullpath=  getRealPathFromURI(EditAdvertisementActivity.this, uri);
                        Log.e( "onActivityResult: ",fullpath );

                        if(fullpath.endsWith("jpg") || fullpath.endsWith("jpeg")  || fullpath.endsWith("png"))
                        {
                            //Toast.makeText(activity, "correct", Toast.LENGTH_SHORT).show();
                            a.add(fullpath);
                        }
                        else {
                            Toast.makeText(EditAdvertisementActivity.this, "Select only images", Toast.LENGTH_SHORT).show();
                        }
                        // a.add(fullpath);

                        // imageView.setImageURI(uri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                }

                if (a.size()>1)
                {
                    Toast.makeText(EditAdvertisementActivity.this, "You can Pick only single image", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    rl_img_delete.setVisibility(View.VISIBLE);
                    et_attach.setText("You selected total "+ a.size()+" Images");
                }


            }
            //  Log.e( "onActivityResult: ", a.get(0));
        }
        Log.e( "onActivityResult: ", a.toString());


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

    private void InsertMessageWithFile(String id,String imgpath,String Title ,String Details , String Userid,String Expirydate ) {
        try {
            //  uriData = media.getUri();
            path = new File(imgpath);
            Log.e(TAG, "uploadWithFilePath: " + path.toString()+" id "+Id +" "+profile_path+" "+details +" "+Userid
                    +" title  "+title+" Dt "+expiryDate );
            //{"type":3,"Action":1,"Attachmentid":0,"Homeworkid":3,"Typeid":1,"Filename":"Filename","LogedinUserId":1}
            int UserId = 1;
            AndroidNetworking.upload("http://iysonline.club/iys/api/processes/images.php/")
                    // .addFileToUpload("", "certificate") //Adding file
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("ImagesId",id)
                    .addMultipartParameter("Title", Title)
                    .addMultipartParameter("Details",Details)
                    .addMultipartFile("images", path)
                    .addMultipartParameter("Userid", String.valueOf(Userid))
                    .addMultipartParameter("Expirydate", Expirydate)
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
                            Toast.makeText(EditAdvertisementActivity.this,"Created Succesfully ",Toast.LENGTH_LONG).show();
                            Intent intent= new Intent(EditAdvertisementActivity.this,AdvertisementListActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "FileonError: ", error);
                            progressDialog.hide();
                        }
                    });
        } catch (Exception exc) {
            Log.e(TAG, "InsertMessageWithPdf: " + exc.getMessage());
            Toast.makeText(EditAdvertisementActivity.this, "Please select Image", Toast.LENGTH_SHORT).show();
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
                formattedDate1 = sdf.format(c.getTime());
                Log.e(TAG, "formattedDate1 " + formattedDate1);
                //Toast.makeText(getApplicationContext(), formattedDate1, Toast.LENGTH_LONG).show();
                // Toast.makeText(getActivity(), "formattedDate" + formattedDate1, Toast.LENGTH_LONG).show();
                c.add(Calendar.DATE, 30);
                //c.add(sdf.format(c.getTime()), 30);

                Date expDate = c.getTime();
                SimpleDateFormat dest = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

                String result = dest.format(expDate);

                Log.e(TAG,"exp "+result);
                //tv_Date.setText(formattedDate1);
                tv_Date.setText(result);
                //use this date as per your requirement
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditAdvertisementActivity.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
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
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(EditAdvertisementActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(EditAdvertisementActivity.this, "Storage permission allows us to Access Storage", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(EditAdvertisementActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCode);

        }
    }

    private void dialogForDeleteImage(String id,String imgpath,String Title ,String Details , String Userid,String Expirydate ) {
        // this.correct = correct;
        final Dialog resultbox = new Dialog(EditAdvertisementActivity.this);
        resultbox.setContentView(R.layout.delete_subject_dialog);
        // resultbox.setCanceledOnTouchOutside(false);
        Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);
        Button btn_cancel = (Button) resultbox.findViewById(R.id.btn_resume);
        TextView text_title =  resultbox.findViewById(R.id.text_title);
        text_title.setText(" Are you sure you want to delete this Attachment ? ");
        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Utility.isNetworkAvailable(EditAdvertisementActivity.this)) {

                    /**********Delete Code****************/

                    AndroidNetworking.upload("http://iysonline.club/iys/api/processes/images.php/")
                            // .addFileToUpload("", "certificate") //Adding file
                            .addMultipartParameter("type", "1")
                            .addMultipartParameter("Action", "0")
                            .addMultipartParameter("ImagesId", id)
                            .addMultipartParameter("Title", Title)
                            .addMultipartParameter("Details",Details)
                            .addMultipartParameter("images",imgpath)
                            .addMultipartParameter("Userid", String.valueOf(Userid))
                            .addMultipartParameter("Expirydate", Expirydate)
                            .addMultipartParameter("LogedinUserId", "1")
                            .setTag("uploadTest")
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                    resultbox.cancel();
                                    Log.e("adapter1", "uploadImage: totalBytes: " + totalBytes);
                                    Log.e("adapter1", "uploadImage: bytesUploaded: " + bytesUploaded);
                                    progressDialog.setMessage("Deleting File, Please wait...");
                                    progressDialog.show();
                                    progressDialog.setCancelable(false);
                                }
                            })
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressDialog.hide();
                                    Log.e("adapter1", "FileonRes: " + response.toString());
                                    Toast.makeText(EditAdvertisementActivity.this,"Deleted Succesfully ",Toast.LENGTH_LONG).show();
                                    //Intent intent= new Intent(activity,AdvertisementListActivity.class);
                                    //startActivity(intent);
                                    rl_img_delete.setVisibility(View.GONE);
                                    resultbox.cancel();
                                   // finish();
                                    //overridePendingTransition( 0, 0);
                                    //startActivity(getIntent());
                                   // overridePendingTransition( 0, 0);
                                }

                                @Override
                                public void onError(ANError error) {
                                    resultbox.cancel();
                                    Log.e("adapter1", "FileonError: ", error);
                                    progressDialog.hide();
                                }
                            });
                } else {
                    resultbox.cancel();
                    Toast.makeText(EditAdvertisementActivity.this, "No Internet Connection",Toast.LENGTH_SHORT).show();                }
            }

            ;

            /**************************************/
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();
            }
        });

        resultbox.show();
    }


}
