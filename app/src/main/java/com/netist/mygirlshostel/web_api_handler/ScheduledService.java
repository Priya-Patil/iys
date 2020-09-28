package com.netist.mygirlshostel.web_api_handler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.session_handler.SessionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ganesh on 20-06-2017.
 */

public class ScheduledService extends Service
{

    private Timer timer = new Timer();
    int count = 1;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Your code here
                UploadAppToken();
            }
        }, 0, 10*1000);//5*60*1000 -> 5 Minutes
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    String appToken=null ;
    SessionHelper session;

    public void UploadAppToken()
    {

        session = new SessionHelper(getApplicationContext());
        appToken = session.getDeviceToken();
        //if token is not null
        if (appToken != null && !session.isTokenUpdated()) {

            //Toast.makeText(getApplicationContext(),"Registration Request Send.",Toast.LENGTH_SHORT).show();

            String tag_string_req = "TokenUpdateRequest";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlLogin, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e ("Response String", response);

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (error) {
                            Toast.makeText(getApplicationContext(), jObj.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                        else{

                            session.setLogin(true);
                            session.setUserType(jObj.getString("type"));
                            session.setUserID(jObj.getString("id"));
                            session.setUserName(jObj.getString("name"));

                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Server error, Please Contact to SystemAdmin.", Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                   // Log.e("Registration Error: ",  error.getMessage());
                   //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();

                    params.put("type",session.getUserType());
                    params.put("userId",session.getUserID());
                    params.put("token",session.getDeviceToken());

                    Log.e("URL", ApiConfig.urlLogin);
                    Log.e("Register Params: ", params.toString());
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }
}
