package com.netist.mygirlshostel.classes;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.adapter.ClassesBookingListAdapter;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClassesBookingListActivity extends BaseActivity implements View.OnClickListener{

    ListView lv_booking_list;
    TextView tv_title;
    ArrayList<HashMap<String, String>> bookingList, filteredBookingList, batchList;
    Integer selectedBatchIndex = 0;

    String tag_string_req = "", classesId = null, userId = null, action = "", nextStatusDateTime;
    Button btn_filter, currentSelectedButton;
    boolean isFilter = false;
    SessionHelper session;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_booking_list);

        setTitle("Booking List");
        session = new SessionHelper(getApplicationContext());

        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_booking_list = (ListView) findViewById(R.id.lv_booking_list);

        if (session.getUserType().equals("admin")) {
            classesId = getIntent().getExtras().getString("classesId");
            SetBookingList();
            lv_booking_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> booking = bookingList.get(position);

                    switch (view.getId())
                    {
                        case R.id.btn_date_time:
                        {
                            showDateTimePicker((Button) view, booking, ((Button)view).getText().toString());
                        }
                        break;

                        case R.id.tv_no: {
                            // MakeCalling();
                            //   Toast.makeText(getApplicationContext(), "noooo"+ booking.get("userName"), Toast.LENGTH_LONG).show();
                            String no1 = "tel:" + booking.get("mobile");
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(no1));
                            if (ActivityCompat.checkSelfPermission(ClassesBookingListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(intent);


                        }
                        break;


                        default:
                        {
                            currentSelectedButton = (Button) view.findViewById(R.id.btn_date_time);
                            selectedBatchIndex = 0;
                            RefreshBatchInfo(booking);
                        }
                    }
                }
            });
        } else if (session.getUserType().equals("classes")) {
            classesId = getIntent().getExtras().getString("classesId");
            SetBookingList();
            lv_booking_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> booking = bookingList.get(position);

                    switch (view.getId())
                    {
                        case R.id.btn_date_time:
                        {
                            showDateTimePicker((Button) view, booking, ((Button)view).getText().toString());
                        }
                        break;
                        case R.id.tv_no: {
                            // MakeCalling();
                            //   Toast.makeText(getApplicationContext(), "noooo"+ booking.get("userName"), Toast.LENGTH_LONG).show();
                            String no1 = "tel:" + booking.get("mobile");
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(no1));
                            if (ActivityCompat.checkSelfPermission(ClassesBookingListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(intent);


                        }
                        break;
                        default:
                        {
                            currentSelectedButton = (Button) view.findViewById(R.id.btn_date_time);
                            selectedBatchIndex = 0;
                            RefreshBatchInfo(booking);
                        }
                    }
                }
            });
        } else {
            userId = getIntent().getExtras().getString("userId");
            SetBookingList();
        }

        btn_filter = (Button) findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFilterDialog();
            }
        });
    }

    private void showDateTimePicker(final Button btn, final HashMap<String, String> booking, String dateTime)
    {
        if (dateTime.equals(""))
            return;

        try {
            final Date initDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateTime);

            final View dialogView = View.inflate(ClassesBookingListActivity.this, R.layout.date_time_picker, null);

            final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
            datePicker.init(initDate.getYear() + 1900, initDate.getMonth(), initDate.getDate(), null);

            final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
            timePicker.setCurrentHour(initDate.getHours());
            timePicker.setCurrentMinute(initDate.getMinutes());

            final AlertDialog alertDialog = new AlertDialog.Builder(ClassesBookingListActivity.this).create();

            dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    StringBuilder newDateTime = new StringBuilder()
                            .append(datePicker.getYear()).append("-")
                            .append(datePicker.getMonth() + 1).append("-")
                            .append(datePicker.getDayOfMonth()).append(" ")
                            .append(timePicker.getCurrentHour()).append(":")
                            .append(timePicker.getCurrentMinute()).append(":")
                            .append("00");
                    UpdateDateTime(btn, booking, newDateTime.toString());

                    alertDialog.dismiss();
                }});
            alertDialog.setView(dialogView);
            alertDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void UpdateDateTime(final Button btn, final HashMap<String, String> booking, final String dateTime)
    {
        tag_string_req = "UpdateDateTime Request";
        final ProgressDialog loading = ProgressDialog.show(this, null, "Updating Record Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesBookingReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dis missing the progress dialog
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");

                    if (!error)
                    {
                        try {
                            btn.setText(Utils.formatDate(dateTime) + " " + Utils.formatTime(dateTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                loading.dismiss();
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
                params.put("action", "booking_datetime_update");


                params.put("id", booking.get("bookingId"));
                params.put("dateTime", dateTime);
                params.put("status", booking.get("status"));

                Log.e("URL", ApiConfig.urlClassesBookingReg);
                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void RefreshBatchInfo(final HashMap<String, String> booking) {
        if (batchList != null && batchList.size() > 0) {
            batchList.clear();
            batchList = null;
        }

        batchList = new ArrayList<HashMap<String, String>>();

        tag_string_req = "RefreshBatchInfoRequest";

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

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
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);

                        // Fields : batch_name, batch_charge, classesId
                        String batchId = obj.getString("batchId");
                        String batch_name = obj.getString("batch_name");
                        String batch_charge = obj.getString("batch_charge");
                        String classesId = obj.getString("classesId");

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("batchId", batchId);
                        listItem.put("batch_name", batch_name);
                        listItem.put("batch_charge", batch_charge);
                        listItem.put("classesId", classesId);

                        batchList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                if (batchList.size() > 0) {
                    if (booking != null)
                        SetAction(booking);
                } else {
                    Toast.makeText(ClassesBookingListActivity.this, "Classes's Batchs Not Available...!", Toast.LENGTH_SHORT).show();
                }

                loading.dismiss();
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

                params.put("id", classesId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void SetBookingList() {
        tag_string_req = "ClassesBookingListRequest";

        bookingList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesBookingList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {

                        JSONObject obj = responseArr.getJSONObject(i);
                        //notice_id,  dept,  subject,  description,  date

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("bookingId", obj.getString("bookingId"));
                        listItem.put("classesId", obj.getString("classesId"));
                        listItem.put("userId", obj.getString("userId"));
                        listItem.put("userName", obj.getString("actualUserName"));
                        listItem.put("classesName", obj.getString("classesName"));
                        listItem.put("bookingDateTime", obj.getString("bookingDateTime"));
                        listItem.put("joiningDateTime", obj.getString("joiningDateTime"));
                        listItem.put("leavingDateTime", obj.getString("leavingDateTime"));
                        listItem.put("startChargesDateTime", obj.getString("startChargesDateTime"));
                        listItem.put("selectedBatch", obj.getString("selectedBatch"));
                        listItem.put("batchName", obj.getString("batchName"));
                        listItem.put("status", obj.getString("status"));
                        listItem.put("mobile", obj.getString("mobile"));
                        bookingList.add(listItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }

                if (bookingList.size() > 0) {
                    ClassesBookingListAdapter bookingListAdapter = new ClassesBookingListAdapter(ClassesBookingListActivity.this, bookingList);

                    lv_booking_list.setAdapter(bookingListAdapter);

                } else {
                    lv_booking_list.setAdapter(null);
                    Toast.makeText(ClassesBookingListActivity.this, "No Pending Bookings...!", Toast.LENGTH_SHORT).show();
                }

                //adapter.notifyDataSetChanged();
                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ", error.toString());
                //Disimissing the progress dialog
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("action", "booking_list");

                if (classesId != null)
                    params.put("classesId", classesId);
                else
                    params.put("userId", userId);

                if (isFilter) {
                    params.put("filter", "yes");

                    String fromDate = btn_from_date.getText().toString().trim();
                    String toDate = btn_to_date.getText().toString().trim();

                    try {
                        params.put("fromDate", Utils.formatDate(fromDate, "00:00"));
                        params.put("toDate", Utils.formatDate(toDate, "23:59"));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                Log.e("URL", ApiConfig.urlClassesBookingList);
                Log.e("Params", params.toString());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void SetAction(final HashMap<String, String> booking) {
        if (batchList.size() <= 0)
            return;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("User : " + booking.get("userName"));

        String status = booking.get("status");

        if (status.equals("Pending")) {

            alert.setNegativeButton("Approve", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ExecuteAction(booking, "Approved");
                }
            });
            alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ExecuteAction(booking, "Canceled");
                }
            });
        } else if (status.equals("Approved")) {
            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ExecuteAction(booking, "Entered");
                }
            });

            CharSequence[] batchStringList = new CharSequence[batchList.size()];
            for (int i = 0; i < batchList.size(); i++) {
                HashMap<String, String> item = batchList.get(i);
                batchStringList[i] = "Batch : " + item.get("batch_name");
            }

            alert.setSingleChoiceItems(batchStringList, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectedBatchIndex = i;
                }
            });
        } else if (status.equals("Entered")) {
            alert.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ExecuteAction(booking, "Left");
                }
            });

            CharSequence[] selectedBatchString = new CharSequence[1];
            selectedBatchString[0] = "Batch : " + batchList.get(selectedBatchIndex).get("batch_name");

            alert.setSingleChoiceItems(selectedBatchString, 0, null);
        } else if (status.equals("Left") || status.equals("Canceled")) {
            alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ExecuteAction(booking, "Delete");
                }
            });

        }

        alert.show();

    }


    private void ExecuteAction(final HashMap<String, String> booking, final String action) {
        this.action = action;

        userId = booking.get("userId");

        final ProgressDialog loading = ProgressDialog.show(this, null, "Updating Record Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesBookingReg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dis missing the progress dialog
                loading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String msg = jObj.getString("msg");

                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    if (!error && (action.equals("Approved") || action.equals("Canceled"))) {
                        sendSinglePush();
                    }

                    SetBookingList();
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Server error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                loading.dismiss();
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
                params.put("action", "booking_update");


                params.put("id", booking.get("bookingId"));
                params.put("classesId", booking.get("classesId"));
                params.put("status", action);

                String currentDateTime = Utils.currentServerDateTime();
                String dateTime = currentSelectedButton.getText().toString();

                if (action.equals("Entered")) {
                    HashMap<String, String> item = batchList.get(selectedBatchIndex);
                    params.put("selectedBatch", selectedBatchIndex.toString());
                    params.put("batchId", item.get("batchId"));
                    params.put("batchName", item.get("batch_name"));
                    params.put("batchCharge", item.get("batch_charge"));



                    try {
                        if (!Utils.isPrevDate(currentDateTime, Utils.formatDate2(dateTime)))
                            currentDateTime = Utils.formatDate2(dateTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    params.put("joiningDateTime", currentDateTime);
                    params.put("startChargesDateTime", currentDateTime);
                } else if (action.equals("Left")) {
                   //8 params.put("batchId", batchList.get(Integer.parseInt(booking.get("selectedBatch"))).get("batchId"));
                    params.put("batchName", booking.get("batchName"));
                    try {
                        if (!Utils.isPrevDate(currentDateTime, Utils.formatDate2(dateTime)))
                            currentDateTime = Utils.formatDate2(dateTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    params.put("leavingDateTime", currentDateTime);
                }

                Log.e("URL", ApiConfig.urlClassesBookingReg);
                Log.e("Register Params: ", params.toString());


                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //region "Reschedule Task"

    int year, month, day, hours, minute;
    Button btn_date, btn_time;
    AlertDialog.Builder reschedule_dialog;

    private void ShowRescheduleDialog(final HashMap<String, String> booking) {
        reschedule_dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_re_schedule_booking, null);
        reschedule_dialog.setView(dialogView);

        reschedule_dialog.setTitle("Re-Schedule Booking.");

        btn_date = (Button) dialogView.findViewById(R.id.btn_date);

        btn_time = (Button) dialogView.findViewById(R.id.btn_time);

        getValuesFormServerDateTime(booking.get("dateTime"));
        showDate(year, month, day);
        showTime(hours, minute);

        reschedule_dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    RescheduleDialogSubmit(booking);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        reschedule_dialog.setNegativeButton("Cancel", null);
        reschedule_dialog.show();
    }

    String date, time, rescheduledDate;

    private void RescheduleDialogSubmit(final HashMap<String, String> booking) throws ParseException {

        date = btn_date.getText().toString().trim();
        time = btn_time.getText().toString().trim();

        if (date.equals("") || time.equals("")) {
            Toast.makeText(getApplicationContext(), "Select both date and time .", Toast.LENGTH_LONG).show();
        } else {
            rescheduledDate = Utils.formatDate(date, time);
            ExecuteAction(booking, "Re-Schedule");

        }
    }

    private void getValuesFormServerDateTime(String dateTime) {

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
            //SimpleDateFormat formatter = new SimpleDateFormat("dd");
            day = Integer.parseInt(new SimpleDateFormat("dd").format(initDate));
            month = Integer.parseInt(new SimpleDateFormat("MM").format(initDate));
            year = Integer.parseInt(new SimpleDateFormat("yyyy").format(initDate));
            hours = Integer.parseInt(new SimpleDateFormat("HH").format(initDate));
            minute = Integer.parseInt(new SimpleDateFormat("mm").format(initDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //endregion

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month - 1, day);
        }
        if (id == 998) {
            return new TimePickerDialog(this, myTimeListener, hours, minute, true);
        }
        if (id == 997) {
            return new DatePickerDialog(this,
                    myFilterFromDateListener, year, month, day);
        }
        if (id == 996) {
            return new DatePickerDialog(this,
                    myFilterToDateListener, year, month, day);
        }
        return null;
    }

    //region "DateTime Selector"
    private void showDate(int year, int month, int day) {
        String monthStr = "";
        if (month < 10)
            monthStr = "0" + month;
        else
            monthStr = "" + month;

        btn_date.setText(new StringBuilder().append(day).append("/").append(monthStr).append("/").append(year));
    }

    private void showTime(int hours, int minute) {
        btn_time.setText(new StringBuilder().append(hours).append(":")
                .append(minute));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
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

    //endregion

    //region "Filter Task"

    Button btn_from_date, btn_to_date;
    AlertDialog.Builder filter_dialog;

    private void ShowFilterDialog() {
        filter_dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_filter_booking, null);
        filter_dialog.setView(dialogView);

        filter_dialog.setTitle("Filter Booking.");

        btn_from_date = (Button) dialogView.findViewById(R.id.btn_from_date);

        btn_to_date = (Button) dialogView.findViewById(R.id.btn_to_date);

        filter_dialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    isFilter = true;
                    SetBookingList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        filter_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                isFilter = false;
            }
        });
        filter_dialog.show();
    }

    //--------------Date Selection-----------

    //---------From Date---------

    @SuppressWarnings("deprecation")
    public void setFromDate(View view) {

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDialog(997);
    }

    private DatePickerDialog.OnDateSetListener myFilterFromDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    // TODO Auto-generated method stub
                    showFromDate(year, month + 1, day);
                }
            };

    private void showFromDate(int year, int month, int day) {
        String monthStr = "";
        if (month < 10)
            monthStr = "0" + month;
        else
            monthStr = "" + month;

        btn_from_date.setText(new StringBuilder().append(day).append("/").append(monthStr).append("/").append(year));
    }

    //---------To Date---------

    @SuppressWarnings("deprecation")
    public void setToDate(View view) {

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDialog(996);
    }

    private DatePickerDialog.OnDateSetListener myFilterToDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int year, int month, int day) {
                    // TODO Auto-generated method stub
                    showToDate(year, month + 1, day);
                }
            };

    private void showToDate(int year, int month, int day) {
        String monthStr = "";
        if (month < 10)
            monthStr = "0" + month;
        else
            monthStr = "" + month;

        btn_to_date.setText(new StringBuilder().append(day).append("/").append(monthStr).append("/").append(year));
    }

    //endregion


    String title, message, type, id;

    private void sendSinglePush() {

        if (action.trim().equals("Approved")) {
            title = "Booking : " + action;
            message = "Your booking has been Approved.";
        }
        if (action.trim().equals("Canceled")) {
            title = "Booking : Canceled";
            message = "Your booking has been Canceled.";
        }
        if (action.trim().equals("Re-Schedule")) {
            title = "Booking : Re-Scheduled";
            message = "Your booking has been Re-Scheduled.";
        }

        type = "user";
        id = userId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.urlSendSinglePush,
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
                Log.e("url", ApiConfig.urlSendSinglePush);
                Log.e("params", params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {

    }
}
