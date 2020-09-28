package com.netist.mygirlshostel.classes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ClassesListChargesActivity extends BaseActivity implements View.OnClickListener{
    String tag_string_req = "";
    static String TAG = "ExelLog";
    private ArrayList<HashMap<String, String>> chargesList;
    TableLayout table;
    private SessionHelper session;
    private String classesId, classesName, classesLocation, classesImage, mobileNo, person, batchCount,
            userId,userName,name, date, time, formatedDate;
    double latitude, longitude;
    int year , month, day, hours, minute;
    String monthStr;
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_list_charges);
        prefManager=new PrefManager(ClassesListChargesActivity.this);
        Toast.makeText(getApplicationContext(), "ppppp"+prefManager.getMOBILE_SELECTED(), Toast.LENGTH_LONG).show();

        setTitle("Classes List Charges");

        session = new SessionHelper(this);

        userId = session.getUserID();
        userName = session.getUserName();

        setDetails();

        findViewById(R.id.btn_save_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcelFile(ClassesListChargesActivity.this, "class_charges.xls");
            }
        });
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void showDate(int year, int month, int day) {

        if(month <10)
            monthStr = "0"+month;
        else
            monthStr = ""+month;
    }

    private void sendClassesPush() {
        final String title = "Booking";
        final String message = "You got new  booking.. Please check it.";
        final String type = "classes";
        final String id = classesId;

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

    private void setBooking(final String batchName, final String batchCharge)
    {
        //-----------Set date----------------
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        //-----------Set date----------------
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        name = userName;
        Log.d("userName:", name);
        date = String.valueOf(new StringBuilder().append(day).append("/").append(monthStr).append("/").append(year));
        Log.d("date:", date);
        time = String.valueOf(new StringBuilder().append(hours).append(":").append(minute));
        Log.d("time:", time);

        {
            try {
                formatedDate = Utils.formatDate(date, time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String tag_string_req = "BookingRequest";
            final ProgressDialog loading = ProgressDialog.show(ClassesListChargesActivity.this,"Uploading...","Please wait...",false,false);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlClassesBookingReg, new Response.Listener<String>() {

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
                            sendClassesPush();
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
                    params.put("classesId",classesId);
                    //params.put("userType",type);
                    params.put("userName",name);
                    params.put("batchName", batchName);
                    params.put("batchCharge", batchCharge);

                    params.put("bookingDateTime",formatedDate);
                    params.put("mobile", prefManager.getMOBILE_SELECTED());
                    //params.put("healthIssue",issue);

                    Log.e("URL", ApiConfig.urlClassesBookingReg);
                    Log.e("Register Params: ", params.toString());

                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }

    }

    private boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Classes Charges List");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Class Name");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Batch Name");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Charges");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Availability");
        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Booking");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));
        sheet1.setColumnWidth(4, (15 * 500));

        //content
        for (int i = 0; i < chargesList.size(); i++){
            Row contentRow = sheet1.createRow(i+1);

            c = contentRow.createCell(0);
            c.setCellValue(chargesList.get(i).get("name"));
            c.setCellStyle(cs);

            c = contentRow.createCell(1);
            c.setCellValue(chargesList.get(i).get("batch_name"));
            c.setCellStyle(cs);

            c = contentRow.createCell(2);
            c.setCellValue(chargesList.get(i).get("batch_charge"));
            c.setCellStyle(cs);

            c = contentRow.createCell(3);
            c.setCellValue(chargesList.get(i).get("availability"));
            c.setCellStyle(cs);

            c = contentRow.createCell(4);
            Switch swSelect = (Switch)table.getChildAt(i + 1).findViewById(R.id.sw_select);
            if (swSelect.isChecked())
                c.setCellValue("On");
            else
                c.setCellValue("Off");
            c.setCellStyle(cs);
        }


        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        showFilePath(file.getAbsolutePath());

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            Toast.makeText(ClassesListChargesActivity.this, "Writing file" + file, Toast.LENGTH_LONG).show();
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
            Toast.makeText(ClassesListChargesActivity.this, "Error writing " + file, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
            Toast.makeText(ClassesListChargesActivity.this, "Failed to save file", Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    private void showFilePath(String str)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Show Excelfile Path");
        alert.show();
    }


    private void setDetails() {

        tag_string_req = "RefreshRoomInfoRequest";
        chargesList = new ArrayList<HashMap<String, String>>();

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
                    chargesList.clear();

                    table = (TableLayout) findViewById(R.id.tl_hatchs_data);
                    table.removeAllViews();

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);

                    TableRow row = (TableRow) inflater.inflate(R.layout.layout_charges_row, null);

                    // Room No, Total Count, Occupancy Count, Availability Count
                    ((TextView) row.findViewById(R.id.tv_name)).setText("Classes Name");
                    ((TextView) row.findViewById(R.id.tv_sub_name)).setText("Batch Name");
                    ((TextView) row.findViewById(R.id.tv_rate)).setText("Charges");
                    ((TextView) row.findViewById(R.id.tv_availability)).setText("Availability");
                    ((TextView) row.findViewById(R.id.tv_select)).setText("Booking");
                    table.addView(row);

                    // Parsing json
                    for (int i = 0; i < responseArr.length(); i++) {
                        JSONObject obj = responseArr.getJSONObject(i);

                        row = (TableRow) inflater.inflate(R.layout.layout_charges_row, null);

                        ((TextView) row.findViewById(R.id.tv_name)).setText(obj.getString("name"));
                        ((TextView) row.findViewById(R.id.tv_sub_name)).setText(obj.getString("batch_name"));
                        ((TextView) row.findViewById(R.id.tv_rate)).setText(obj.getString("batch_charge"));
                        ((TextView) row.findViewById(R.id.tv_select)).setVisibility(View.GONE);
                        ((Switch)row.findViewById(R.id.sw_select)).setVisibility(View.VISIBLE);

                        Switch aSwitch =(Switch) row.findViewById(R.id.sw_select);
                        aSwitch.setVisibility(View.VISIBLE);

                        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked){
                                    final TableRow tableRow = (TableRow) buttonView.getParent();
                                    classesId = chargesList.get(table.indexOfChild(tableRow) -1).get("classesId");
                                    String batchName = chargesList.get(table.indexOfChild(tableRow) -1).get("batch_name");
                                    String batchCharge = chargesList.get(table.indexOfChild(tableRow) -1).get("batch_charge");
                                    setBooking(batchName, batchCharge);
                                }else {

                                }
                            }
                        });
                        table.addView(row);

                        HashMap<String, String> listItem = new HashMap<String, String>();

                        listItem.put("name", obj.getString("name"));
                        listItem.put("batch_name", obj.getString("batch_name"));
                        listItem.put("batch_charge", obj.getString("batch_charge"));
                        listItem.put("availability", "");
                        listItem.put("classesId", obj.getString("classesId"));

                        chargesList.add(listItem);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data


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

                params.put("list_charges", "");

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();

        Log.e("Restart","Done");
        setDetails();
    }

    @Override
    public void onClick(View v) {

    }
}
