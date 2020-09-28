package com.netist.mygirlshostel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import java.util.HashMap;
import java.util.Map;

public class FCMSenderActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmsender);

        findViewById(R.id.btn_single_fcm_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "Booking Registered";
                String message = "Your booking has been registered. Please wait for the confirmation.";
                SessionHelper session = new SessionHelper(getApplicationContext());
                String id = session.getUserID();

                sendSinglePush(title, message, id);
            }
        });

        findViewById(R.id.btn_multiple_fcm_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMultiplePush();
            }
        });
    }

    public static void sendSinglePush(final String title, final String message, final String id) {
        final String type = "user";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.urlSendSinglePush,
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
                params.put("type", type);
                params.put("id", id);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void sendMultiplePush() {
        final String title = "Multiple Send demo";
        final String message = "Your booking has been registered. Please wait for the confirmation.";

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
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {

    }
}
