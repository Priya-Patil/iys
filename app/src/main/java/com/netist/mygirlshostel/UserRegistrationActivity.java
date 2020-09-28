package com.netist.mygirlshostel;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserRegistrationActivity extends BaseActivity implements View.OnClickListener{

    EditText tv_name, /*tv_age, tv_email,*/ tv_mobile, tv_address, tv_password, tv_vpassword, et_otp ;
    //RadioButton rbtn_male,rbtn_female;
    //Button btn_dob;
    String name, /*dob, age, email,*/ address, mobile, password, vpassword;
    int year, month, day;
    String OTP;
    Dialog resultbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        setTitle("User Registration");

        Firebase.setAndroidContext(this);

        tv_name= (EditText) findViewById(R.id.tv_name );


        tv_mobile= (EditText) findViewById(R.id.tv_mobile );


        tv_address= (EditText) findViewById(R.id.tv_address );

        tv_password= (EditText) findViewById(R.id.tv_password );

        tv_vpassword= (EditText) findViewById(R.id.tv_vpassword );

        ((Button)findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((Button)findViewById(R.id.btn_cancel)).setOnClickListener(this);

        //-----------Set date----------------
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        requestPermission();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {

            OTP = GenerateRandomNumber(6);
            sendSMS(OTP);
        }
    }


    private void sendSMS(String OTP) {
        try {
             SmsManager sms = SmsManager.getDefault();
                {
                    // the message
                    String message = "Hello"+System.lineSeparator()+ "- My hostel.. Your OTP is: "+OTP;

                    sms.sendTextMessage(tv_mobile.getText().toString().trim(), null, message, null, null);


                    Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                            Toast.LENGTH_LONG).show();

                    dialogForConfirmOTP();

                }
        }
        catch (Exception e){

            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void dialogForConfirmOTP ( ){
        Log.e( "dialogForConfirmOTP: ", OTP);
        resultbox = new Dialog(UserRegistrationActivity.this);
        resultbox.setContentView(R.layout.delete_teacher_dialog);
        resultbox.setCanceledOnTouchOutside(false);
        Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);
        Button btn_resume = (Button) resultbox.findViewById(R.id.btn_resume);
        et_otp= (EditText) resultbox.findViewById(R.id.et_otp );

        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Utility.isNetworkAvailable(UserRegistrationActivity.this)) {

                    /**********Delete Code****************/
                    if(et_otp.getText().toString().equals(""))
                    {
                        Toast.makeText(UserRegistrationActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if(et_otp.getText().toString().equals(OTP))
                        {
                            Log.e( "onClick: ",et_otp.getText().toString() );
                            RegisterUser();
                        }
                        else
                        {
                            Toast.makeText(UserRegistrationActivity.this, "Enter proper OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    Toast.makeText(UserRegistrationActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_resume.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();
            }
        });

        resultbox.show();
    }

    SessionHelper session;
    String appToken=null;

    public void RegisterUser()
    {
        session = new SessionHelper(getApplicationContext());

        name = tv_name.getText().toString().trim();
        //dob = btn_dob.getText().toString().trim();
        //age = tv_age.getText().toString().trim();
        address = tv_address.getText().toString().trim();
        //email = tv_email.getText().toString().trim();
        mobile = tv_mobile.getText().toString().trim();
        password = tv_password.getText().toString().trim();
        vpassword = tv_vpassword.getText().toString().trim();

        appToken = SessionHelper.getInstance(this).getDeviceToken();

        if(name.equals("") || address.equals("") || mobile.equals("") || password.equals("") || vpassword.equals("") )
        {
            Toast.makeText(getApplicationContext(),"Fill all Mandatory Fields (*).",Toast.LENGTH_LONG).show();
        }
        else {
            if(!password.equals(vpassword))
            {
                Toast.makeText(getApplicationContext(),"Verified password not matched.",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Registration Request Send.",Toast.LENGTH_SHORT).show();

                String tag_string_req = "UserRegistrationRequest";
                final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        ApiConfig.urlUserReg, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e ("Response String", response);
                        //Dismissing the progress dialog
                        loading.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            Toast.makeText(getApplicationContext(),jObj.getString("msg"), Toast.LENGTH_LONG).show();
                            // Check for error node in json
                            if (!error) {
                                RegisterChatUser(jObj.getString("id"),name, mobile);
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
                        loading.dismiss();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("action","user_reg");
                        params.put("name",name);

                        //params.put("age",age);
                        params.put("address",address);
                        //params.put("email",email);
                        params.put("mobile",mobile);
                        params.put("password",password);

                        if (appToken == null)
                            params.put("appToken","");
                        else
                            params.put("appToken",appToken);


                        Log.e("URL", ApiConfig.urlUserReg);
                        Log.e("Register Params: ", params.toString());
                        return params;
                    }
                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        }
    }

    //region "Date Selector"
    private void showDate(int year, int month, int day) {
        String monthStr = "";
        if(month <10)
            monthStr = "0"+month;
        else
            monthStr = ""+month;

        /*btn_dob.setText(new StringBuilder().append(day).append("/")
                .append(monthStr).append("/").append(year));*/
    }

    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    // TODO Auto-generated method stub
                    showDate(year, month+1, day);
                }
            };

    //endregion

    private void RegisterChatUser(String id, final String name, final String mobile)
    {
            String url = "https://mygirlshostels.firebaseio.com/users";

           StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    Log.d("Response", s);
                    Firebase reference = new Firebase("https://mygirlshostels.firebaseio.com/users");

                    if(s.equals("null")) {
                        reference.child(mobile).child("password").setValue(mobile);
                        reference.child(mobile).child("fullName").setValue(name);
                        reference.child(mobile).child("role").setValue("u");

                        Toast.makeText(UserRegistrationActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            JSONObject obj = new JSONObject(s);

                            if (!obj.has(mobile)) {
                                reference.child(mobile).child("password").setValue(mobile);
                                reference.child(mobile).child("fullName").setValue(name);
                                reference.child(mobile).child("role").setValue("u");

                                Toast.makeText(UserRegistrationActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserRegistrationActivity.this, "Chat Disabled", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }

            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError );

                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(UserRegistrationActivity.this);
            rQueue.add(request);


            session.setLogin(true);
            session.setUserType("user");
            session.setUserID(id);
            session.setUserName(name);
            session.setUserMobile(mobile);

        resultbox.cancel();

        Intent intent = new Intent(UserRegistrationActivity.this, MainActivity.class);
            startActivity(intent);

            UserRegistrationActivity.this.finish();

    }


    String GenerateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //startService(new Intent(UserRegistrationActivity.this, MyLocationService.class));

                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            // showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! "+error, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


}
