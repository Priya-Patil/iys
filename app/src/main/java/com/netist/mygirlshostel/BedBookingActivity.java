package com.netist.mygirlshostel;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.components.CircularNetworkImageView;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BedBookingActivity extends BaseActivity implements View.OnClickListener{

    EditText et_name, et_age;//, et_issue;
    //RadioButton rbtn_male, rbtn_female;
    //Spinner spn_type;
    Button btn_date, btn_time;

    String userId, hostelId, hostelName, hostelImage, hostelAvailability;
    String /*type, */name, date, time, /*issue,*/ formatedDate;
    //ArrayAdapter userTypesAdapter;
    int year , month, day, hours, minute;

    //String userTypes[] = {"Select","Myself", "Relative", "Friend"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        setTitle("Booking");

        final SessionHelper session = new SessionHelper(getApplicationContext());

        userId = session.getUserID();

        hostelId = getIntent().getExtras().getString("hostelId");
        hostelName = getIntent().getExtras().getString("hostelName");
        hostelImage = getIntent().getExtras().getString("hostelImage");
        hostelAvailability = getIntent().getExtras().getString("hostelAvailability");

        ((TextView)findViewById(R.id.tv_name)).setText(hostelName);
        ((TextView)findViewById(R.id.tv_availability)).setText("Availability : " + hostelAvailability);

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.img_profile_thumbnail);

        thumbNail.setImageUrl(hostelImage, imageLoader);


        //spn_type = (Spinner)findViewById(R.id.spn_type);
        et_name = (EditText) findViewById(R.id.et_name);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_time = (Button) findViewById(R.id.btn_time);
        //et_issue = (EditText) findViewById(R.id.et_issue);
        //rbtn_male = (RadioButton)findViewById(R.id.rbtn_male);
        //rbtn_female = (RadioButton)findViewById(R.id.rbtn_female);


        //-----------Set User Type----------------

        //userTypesAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_layout_user_type, userTypes);

        //spn_type.setAdapter(userTypesAdapter);
/*
        spn_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), userTypes[position] , Toast.LENGTH_SHORT).show();

                if(position==0){

                }

                if(position==1)
                {
                    et_name.setText(session.getUserName());
                    et_age.setText(session.getUserAge());

                    if(session.getUserGender().trim().equals("Male"))
                    {
                        rbtn_male.setChecked(true);
                        rbtn_female.setChecked(false);
                    }
                    else
                    {
                        rbtn_male.setChecked(false);
                        rbtn_female.setChecked(true);
                    }
                }
                else
                {
                    et_name.setText("");
                    et_age.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        //-----------Set date----------------
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        //-----------Set date----------------
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        showTime(hours, minute);

    }

    //--------------Date Selection-----------
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        if (id == 998) {
            return new TimePickerDialog(this,myTimeListener,hours,minute,true);
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

    private void showDate(int year, int month, int day) {
        String monthStr = "";
        if(month <10)
            monthStr = "0"+month;
        else
            monthStr = ""+month;

        btn_date.setText(new StringBuilder().append(day).append("/").append(monthStr).append("/").append(year));
    }

    //--------------Time Selection-----------
    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(998);
    }

    private TimePickerDialog.OnTimeSetListener myTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    showTime(hourOfDay, minute);
                }
            };

    //--------------Send Request-----------
    private void showTime(int hours, int minute) {
        btn_time.setText(new StringBuilder().append(hours).append(":")
                .append(minute));
    }

    public void RegisterBooking(View view) throws ParseException {

        /*type = spn_type.getSelectedItem().toString();
        if(type.trim().equals("Select"))
        {
            Toast.makeText(getApplicationContext(), "Please select the User Type.",Toast.LENGTH_LONG).show();
            return;
        }
*/
        name = et_name.getText().toString().trim();
        date = btn_date.getText().toString().trim();
        time = btn_time.getText().toString().trim();
        //issue = et_issue.getText().toString().trim();

        if(name.equals("") || date.equals("") || time.equals("")/* || issue.equals("")*/ )
        {
            Toast.makeText(getApplicationContext(),"Fill all Mandatory Fields (*).",Toast.LENGTH_LONG).show();
        }
        else
        {
                formatedDate = Utils.formatDate(date, time);

                String tag_string_req = "BookingRequest";
                final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        ApiConfig.urlHostelBookingReg, new Response.Listener<String>() {

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

                                sendUserPush();
                                sendHostelPush();
                                //Intent intent = new Intent(HostelEditorActivity.this,HostelListActivity.class);
                                //startActivity(intent);
                                BedBookingActivity.this.finish();
                            }
                        } catch (JSONException e) {

                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Map<String, String> params = new HashMap<String, String>();

                        //type, name, age, gender, date, time, issue;

                        params.put("action","booking_reg");

                        params.put("userId",userId);
                        params.put("hostelId",hostelId);
                        //params.put("userType",type);
                        params.put("userName",name);

                        /*if(rbtn_male.isChecked())
                            params.put("gender","Male");
                        else
                            params.put("gender","Female");
                        */

                        params.put("bookingDateTime",formatedDate);
                        //params.put("healthIssue",issue);

                        Log.e("URL", ApiConfig.urlHostelBookingReg);
                        Log.e("Register Params: ", params.toString());

                        return params;
                    }
                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }
    }



    private void sendUserPush() {
        final String title = "Booking : Registered";
        final String message = "Your booking has been registered. Please wait for the confirmation.";
        final String type = "user";
        final String id = userId;

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

    private void sendHostelPush() {
        final String title = "Booking";
        final String message = "You got new  booking.. Please check it.";
        final String type = "hostel";
        final String id = hostelId;

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

    @Override
    public void onClick(View v) {

    }
}
