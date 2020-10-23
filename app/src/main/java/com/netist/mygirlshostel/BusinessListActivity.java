package com.netist.mygirlshostel;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.adapter.BusinessListAdapter;
import com.netist.mygirlshostel.classes.ClassesEditorActivity;
import com.netist.mygirlshostel.classes.ClassesListActivity;
import com.netist.mygirlshostel.hostel.AddHostelActivity;
import com.netist.mygirlshostel.hostel.HostelEditorActivity;
import com.netist.mygirlshostel.hostel.HostelListActivity;
import com.netist.mygirlshostel.library.LibraryEditorActivity;
import com.netist.mygirlshostel.library.LibraryListActivity;
import com.netist.mygirlshostel.mess.MessEditorActivity;
import com.netist.mygirlshostel.mess.MessListActivity;
import com.netist.mygirlshostel.payment.SubPaymentHistoryActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessListActivity extends BaseActivity implements View.OnClickListener{

    ListView listView;
    private ArrayList<HashMap<String,String>> businessList;

    SessionHelper session;
    String charges="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list);

        setTitle("Service Type List");

        session = new SessionHelper(getApplicationContext());

        Log.e( "role: ", session.getUserType());

        Intent intent=getIntent();
        try {
            if (intent.getStringExtra("charges") != null && intent.getStringExtra("charges") != "") {
                charges = intent.getStringExtra("charges");
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        businessList = new ArrayList<HashMap<String,String>>();
        listView = (ListView)findViewById(R.id.lv_businessList);


        setBusinessList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String,String> item = businessList.get(i);
                String type = item.get("name");
                if (charges.equals("adminSubCharges")){
                 // imp   getSubCharges(type);
                    getSubCharges(type);
                }

                else if(session.getUserType().equals("admin") || session.getUserType().equals("user")) {
                    switch (type) {
                        case "Hostel": {
                            Intent intent = new Intent(getApplicationContext(), HostelListActivity.class);
                            Bundle bundle = getIntent().getExtras();
                            if (bundle != null)
                                intent.putExtras(bundle);
                                startActivity(intent);
                        }
                        break;
                        case "Mess": {
                          /*  Intent intent = new Intent(getApplicationContext(), MessListActivity.class);
                            Bundle bundle = getIntent().getExtras();
                            if (bundle != null)
                                intent.putExtras(bundle);

                            startActivity(intent);*/
                            Toast.makeText(BusinessListActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                        }
                        break;
                        case "Classes": {
                            /*Intent intent = new Intent(getApplicationContext(), ClassesListActivity.class);
                            Bundle bundle = getIntent().getExtras();
                            if (bundle != null)
                                intent.putExtras(bundle);
                               startActivity(intent);*/
                            Toast.makeText(BusinessListActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();

                        }
                        break;
                        case "Library": {
                            /*Intent intent = new Intent(getApplicationContext(), LibraryListActivity.class);
                            Bundle bundle = getIntent().getExtras();
                            if (bundle != null)
                                intent.putExtras(bundle);
                            startActivity(intent);*/
                            Toast.makeText(BusinessListActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();

                        }
                        break;
                        case "View Accounts": {
                          //  Toast.makeText(BusinessListActivity.this, "ccccc", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SubPaymentHistoryActivity.class);
                            Bundle bundle = getIntent().getExtras();
                            if (bundle != null)
                                intent.putExtras(bundle);

                            startActivity(intent);
                        }
                        break;

                    }
                }
                else{
                    switch (type) {
                        case "Hostel": {
                            //addHostel();
                            Utility.launchActivity(BusinessListActivity.this, AddHostelActivity.class,true);

                        }
                        break;
                        case "Mess": {
                            //addMess();
                            Intent intent = new Intent(getApplicationContext(), MessEditorActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case "Classes": {
                            //addClasses();
                            Intent intent = new Intent(getApplicationContext(), ClassesEditorActivity.class);
                            startActivity(intent);
                        }
                        break;
                        case "Library": {
                            //addLibrary();
                            Intent intent = new Intent(getApplicationContext(), LibraryEditorActivity.class);
                            startActivity(intent);
                        }
                        break;

                    }
                }
            }
        });
    }





    private  void getSubCharges(final String type)
    {
        final String   tag_string_req = "BusinessListActivity";

        //    hostelList = new ArrayList<HashMap<String,String>>();

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlGetSubChargesList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response eeeeeeee", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {

                    Log.e("e",""+response+"c");
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("GETTTTTTTT", "" + jsonObject.getString("phone_number"));
                        Log.e("GETTTTTTTT", "" + jsonObject.getString("charges"));
                        Log.e("GETTTTTTTT", "" + jsonObject.length());

                        if(type.equals("View Accounts"))
                        {
                            Intent intent = new Intent(getApplicationContext(), SubPaymentHistoryActivity.class);
                            Bundle bundle = getIntent().getExtras();
                            if (bundle != null)
                                intent.putExtras(bundle);
                            startActivity(intent);
                        }

                        else {
                            dialogAddSubCharges(jsonObject.getString("charges"), jsonObject.getString("phone_number"), type);
                        }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data


                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("account_type",type);

                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    private void dialogAddSubCharges(String Charges, String phone_number, final String type) {


        try {
            LayoutInflater li = LayoutInflater.from(BusinessListActivity.this);
            final View dialogView = li.inflate(R.layout.dialog_admin_add_sub_charges, null);


            final Dialog alertDialogBuilder = new Dialog(BusinessListActivity.this, R.style.MyCustomTheme);

            // set custom_dialog.xml to alertdialog builder
            alertDialogBuilder.setContentView(dialogView);


            //   View v = context.getWindow().getDecorView();
            //v.setBackgroundResource(android.R.color.transparent);
            alertDialogBuilder.setCancelable(false);


            final EditText etDialogAdminAddSubChargesPrice=dialogView.findViewById(R.id.etDialogAdminAddSubChargesPrice);
            final EditText etDialogAdminAddSubChargesNumber=dialogView.findViewById(R.id.etDialogAdminAddSubChargesNumber);

            etDialogAdminAddSubChargesPrice.setText(""+Charges);
            etDialogAdminAddSubChargesNumber.setText(""+11);
           // etDialogAdminAddSubChargesNumber.setText(""+phone_number);



            Button btnDialogAdminAddSubChargesCancel=dialogView.findViewById(R.id.btnDialogAdminAddSubChargesCancel);
            btnDialogAdminAddSubChargesCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialogBuilder.cancel();
                }
            });


            Button btnDialogAdminAddSubChargesSave=dialogView.findViewById(R.id.btnDialogAdminAddSubChargesSave);
            btnDialogAdminAddSubChargesSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  /*  if (etDialogAdminAddSubChargesNumber.getText().toString().length() == 0){

                        Toast.makeText(BusinessListActivity.this, "Enter Correct Number ", Toast.LENGTH_SHORT).show();
                        return;
                    }
*/
                    if (etDialogAdminAddSubChargesPrice.getText().toString().length() == 0){

                        Toast.makeText(BusinessListActivity.this, "Enter Correct Number ", Toast.LENGTH_SHORT).show();
                        return ;
                    }

                    saveSubChargesData(etDialogAdminAddSubChargesPrice.getText().toString(),
                                       etDialogAdminAddSubChargesNumber.getText().toString(),type,alertDialogBuilder);


                }
            });



            // create alert dialog
            alertDialogBuilder.show();
            alertDialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void saveSubChargesData(final String Price, final String Number, final String type, final Dialog alertDialog){

        final String   tag_string_req = "BusinessListActivity";

        //    hostelList = new ArrayList<HashMap<String,String>>();

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlSaveSubChargesList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response SAVEE", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {

                    Log.e("e",""+response+"c");
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("GETTTTTTTT", "" + jsonObject.length());
                    if (jsonObject.getString("message") != null){
                        if (jsonObject.getInt("status") == 1){
                            alertDialog.dismiss();
                        }
                        Toast.makeText(BusinessListActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data


                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                params.put("account_type",type);
                params.put("phone_number",Number);
                params.put("charges",Price);

                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



    }



    private void addHostel()
    {
        final String tag_string_req = "HostelListRequest";

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    if (responseArr.length() > 0)
                    {
                        Intent intent = new Intent(getApplicationContext(), HostelEditorActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(BusinessListActivity.this, "You are unable to create new hostel", Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                loading.dismiss();
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

                params.put("id","0");

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addMess()
    {
        final String tag_string_req = "MessListRequest";

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlMessList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    if (responseArr.length() > 0)
                    {
                        Intent intent = new Intent(getApplicationContext(), MessEditorActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(BusinessListActivity.this, "You are unable to create new mess", Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                loading.dismiss();
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

                params.put("id","0");

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addClasses()
    {
        final String tag_string_req = "ClassesListRequest";

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlClassesList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    if (responseArr.length() > 0)
                    {
                        Intent intent = new Intent(getApplicationContext(), ClassesEditorActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(BusinessListActivity.this, "You are unable to create new class", Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                loading.dismiss();
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

                params.put("id","0");

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addLibrary()
    {
        final String tag_string_req = "LibraryListRequest";

        final ProgressDialog loading = ProgressDialog.show(this,null,"Loading List, Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlLibraryList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e ("Response String", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    if (responseArr.length() > 0)
                    {
                        Intent intent = new Intent(getApplicationContext(), LibraryEditorActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(BusinessListActivity.this, "You are unable to create new mess", Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(tag_string_req,e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data

                loading.dismiss();
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

                params.put("id","0");

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void setBusinessList(){
        HashMap<String,String> listItem = new HashMap<String,String>();
        Intent intent=getIntent();
        if (intent.getStringExtra("charges") != null && intent.getStringExtra("charges") != "") {
            if (session.getUserType().equals("admin")) {
                listItem = new HashMap<String, String>();
                listItem.put("picture", new Integer(R.drawable.hostel_reg).toString());
                listItem.put("name", "Advertisement");
                businessList.add(listItem);
            } else {
            }
        }
        listItem = new HashMap<String,String>();
        listItem.put("picture", new Integer(R.drawable.hostel_reg).toString());
        listItem.put("name", "Hostel");
        businessList.add(listItem);

        listItem = new HashMap<String,String>();
        listItem.put("picture", new Integer(R.drawable.mess_reg).toString());
        listItem.put("name", "Mess");
        businessList.add(listItem);

        listItem = new HashMap<String,String>();
        listItem.put("picture", new Integer(R.drawable.classes_reg).toString());
        listItem.put("name", "Classes");
        businessList.add(listItem);

        listItem = new HashMap<String,String>();
        listItem.put("picture", new Integer(R.drawable.library_reg).toString());
        listItem.put("name", "Library");
        businessList.add(listItem);

        if (intent.getStringExtra("charges") != null && intent.getStringExtra("charges") != "") {
            if (session.getUserType().equals("admin")) {
                listItem = new HashMap<String, String>();
                listItem.put("picture", new Integer(R.drawable.hostel_reg).toString());
                listItem.put("name", "View Accounts");
                businessList.add(listItem);
            } else {
            }
        }

        BusinessListAdapter businessListAdapter = new BusinessListAdapter(BusinessListActivity.this, businessList);
        listView.setAdapter(businessListAdapter);
    }

    @Override
    public void onClick(View v) {

    }
}
