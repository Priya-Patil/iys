package com.netist.mygirlshostel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.adv.AdvHomeActivity;
import com.netist.mygirlshostel.adv.AdvRegistrationActivity;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netist.mygirlshostel.constants.CheckNetwork.isNetworkConnected;

public class WelcomeActivity extends AppCompatActivity {

    static EditText tv_mobile;
    EditText tv_password;
    Spinner sp_role_type;

    String mobile, password, role_type;
    String appToken=null;
    int typeIndex = 0;
    public static String type;

    PrefManager prefManager;
    ImageView iv_back;

    Button btn_user_register, btn_service_register,btn_adv;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        prefManager=new PrefManager(WelcomeActivity.this);
        tv_mobile= (EditText) findViewById(R.id.tv_mobile );
        iv_back =  findViewById(R.id.iv_back );

        tv_password= (EditText) findViewById(R.id.tv_password );

        sp_role_type = (Spinner) findViewById(R.id.sp_role_type);
        btn_adv =  findViewById(R.id.btn_adv);
        btn_user_register =  findViewById(R.id.btn_user_register);
        btn_service_register =  findViewById(R.id.btn_service_register);


        if(prefManager.getROLE().equals("Admin"))
        {
            btn_adv.setVisibility(View.GONE);
            btn_user_register.setVisibility(View.GONE);
            btn_service_register.setVisibility(View.GONE);

        } else if(prefManager.getROLE().equals("adv"))
        {
            btn_user_register.setVisibility(View.GONE);
            btn_service_register.setVisibility(View.GONE);

        }else if(prefManager.getROLE().equals("Common User"))
        {
            btn_adv.setVisibility(View.GONE);
            btn_service_register.setVisibility(View.GONE);
        }

        else {
            btn_user_register.setVisibility(View.GONE);
            btn_adv.setVisibility(View.GONE);
        }

        btn_user_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserRegistrationActivity.class);
                startActivity(intent);
            }
        });
        btn_adv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdvRegistrationActivity.class);
                startActivity(intent);
            }
        });

        btn_service_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefManager.setHOSTEL_SERVICE("fromlogin");

                Intent intent = new Intent(getApplicationContext(), BusinessListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* ValidateLogin();

                appToken = SessionHelper.getInstance(WelcomeActivity.this).getDeviceToken();*/

                if (isNetworkConnected(WelcomeActivity.this) == true) {

                    ValidateLogin(tv_mobile.getText().toString().trim(), tv_password.getText().toString().trim());

                    appToken = SessionHelper.getInstance(WelcomeActivity.this).getDeviceToken();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.tv_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowForgotPasswordDialog();
            }
        });

        sp_role_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type = String.valueOf(sp_role_type.getSelectedItem());
                if (type.equals("Service Provider"))
                {
                    final CharSequence[] typeList = new CharSequence[] {
                            "Hostel",
                            "Mess",
                            "Classes",
                            "Library"
                    };

                    final AlertDialog.Builder alert = new AlertDialog.Builder(WelcomeActivity.this);
                    alert.setSingleChoiceItems(typeList, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            typeIndex = i;
                        }
                    });

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            role_type = typeList[typeIndex].toString();
                            Log.d("role_type", role_type);
                        }
                    });
                    alert.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //checkAppToken();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectRoleActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkAppToken()
    {
        appToken = SessionHelper.getInstance(this).getDeviceToken();

        //appToken = FirebaseInstanceId.getInstance().getToken();

        //if token is not null
        if (appToken != null) {
            //displaying the token
            ((TextView)findViewById(R.id.tv_slogan)).setText(appToken);
            return true;
        } else {
            //if token is null that means something wrong
            Toast.makeText(this, "Token not generated", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    SessionHelper session;

    private void ValidateLogin(String mob, String pass)
    {

        mobile = tv_mobile.getText().toString().trim();

        prefManager.setMOBILE_SELECTED(tv_mobile.getText().toString().trim());

        password = tv_password.getText().toString().trim();
        type = String.valueOf(sp_role_type.getSelectedItem());
        if (!type.equals("Service Provider"))
            role_type = type;

        this.session = new SessionHelper(getApplicationContext());

        if(mobile.equals("") || password.equals("") || role_type.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Fill all Mandatory Fields (*).",Toast.LENGTH_LONG).show();
            return;
        }

        switch (role_type)
        {
           /* case "Management":
                role_type = "Admin";
                break;
            case "Service":
                role_type = "Service Provider";
                break;
            case "Student":
                role_type = "Common User";
                break;*/
        }

        String tag_string_req = "LoginRequest";
        final ProgressDialog loading = ProgressDialog.show(this,"Validating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLogin, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);

                if (response.equals("Connection Fail"))
                {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                    return;
                }
                //Dismissing the progress dialog
                loading.dismiss();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (error)
                    {
                        Toast.makeText(getApplicationContext(), jObj.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        session.setLogin(true);
                        session.setUserType(jObj.getString("type"));
                        session.setUserID(jObj.getString("id"));
                        session.setUserName(jObj.getString("name"));

                        if(!session.getUserType().trim().equals("admin"))
                        {
                            session.setUserMobile(jObj.getString("mobile"));
                            //session.setUserAge(jObj.getString("age"));
                            //session.setUserGender(jObj.getString("gender"));

                        }

                        Log.e( "onResponse: ",session.getUserType() );
                        if(jObj.has("isTokenStored") && jObj.getBoolean("isTokenStored"))
                            session.setTokenUpdated(true);

                        if(session.getUserType().trim().equals("adv"))
                        {
                            Utility.launchActivity(WelcomeActivity.this, AdvHomeActivity.class, true);

                        }
                        else
                        {
                            Utility.launchActivity(WelcomeActivity.this, MainActivity.class, true);

                        }
                    }
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error, Please Contact to SystemAdmin.", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("action","check_login");
                params.put("mobile",mob);
                params.put("pwd",pass);
                params.put("role_type", prefManager.getROLE());

                if (appToken != null) {
                    params.put("token",appToken);
                }
                else
                    params.put("token","");

                Log.e("URL", ApiConfig.urlLogin);
                Log.e("Request Params: ", params.toString());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    AlertDialog.Builder forgot_dialog;
    EditText et_mobile;
    private void ShowForgotPasswordDialog()
    {
        //Toast.makeText(getApplicationContext(), "Forgot Password.", Toast.LENGTH_SHORT).show();

        forgot_dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_forgot_password,null);
        forgot_dialog.setView(dialogView);

        forgot_dialog.setTitle("Forgot Password.");

        et_mobile = (EditText)dialogView.findViewById(R.id.et_mobile);

        forgot_dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    SendForgotPassword();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        forgot_dialog.setNegativeButton("Cancel",null);
        forgot_dialog.show();

    }

    private void SendForgotPassword()
    {
        mobile = et_mobile.getText().toString().trim();

        if(mobile.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Enter Mobile No.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), ""+mobile, Toast.LENGTH_SHORT).show();

            String tag_string_req = "ForgotPasswordRequest";
            final ProgressDialog loading = ProgressDialog.show(this,"Validating...","Please wait...",false,false);

            final AlertDialog.Builder passwordAlert = new AlertDialog.Builder(this);
            passwordAlert.setTitle("Your password is.");
            passwordAlert.setPositiveButton("Ok",null);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlLogin, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e ("Response String", response);
                    //Dismissing the progress dialog
                    loading.dismiss();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (error) {
                            Toast.makeText(getApplicationContext(), jObj.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                        else{

                            passwordAlert.setMessage(jObj.getString("password"));
                            passwordAlert.show();
                        }
                    } catch (JSONException e) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Server error, Please Contact to SystemAdmin.", Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("Registration Error: ",  error.getMessage());
                    //Dismissing the progress dialog
                    loading.dismiss();
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();

                    params.put("action","forgot_password");

                    params.put("mobile",mobile);

                    Log.e("URL", ApiConfig.urlLogin);
                    Log.e("Request Params: ", params.toString());
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

    }
}
