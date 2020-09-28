package com.netist.mygirlshostel.facilities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.components.Utils;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static com.netist.mygirlshostel.web_api_handler.ApiConfig.urlAddClassType;

public class FacilityDetailsActivity extends BaseActivity implements View.OnClickListener{

    Bundle facility;
    String facilityId, id;
    String title, details, imgName;
    Button btn_delete_facility;

    SessionHelper session ;
    LinearLayout delete_image_panel, delete_pdf_panel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_details);

        setTitle("Facility Details");
        session = new SessionHelper(getApplicationContext());
        btn_delete_facility=findViewById(R.id.btn_delete_facility);
        delete_image_panel=findViewById(R.id.delete_image_panel);
        delete_pdf_panel=findViewById(R.id.delete_pdf_panel);

        facility = getIntent().getExtras();
        facilityId = facility.getString("facilityId");
        id = facility.getString("id");

        setFacilityDetails(facility);

        if(!session.getUserType().equals("admin"))
        {
            ((LinearLayout)findViewById(R.id.admin_panel)).setVisibility(View.VISIBLE);
        }
        if(session.getUserType().equals("user"))
        {
            btn_delete_facility.setVisibility(View.GONE);

        }

        ((Button)findViewById(R.id.btn_delete_facility)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(FacilityDetailsActivity.this);
                alert.setMessage("Do you want to delete this facility?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFacility();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert.show();
            }
        });

        ((Button)findViewById(R.id.btn_resend_facility)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMultiplePush();
            }
        });
    }

    private void setFacilityDetails(Bundle obj) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        ((TextView)findViewById(R.id.tv_title)).setText(obj.getString("title"));
        ((TextView)findViewById(R.id.tv_details)).setText(obj.getString("details"));


        try {
            String schedule = Utils.formatDate(obj.getString("startDateTime"))+" To "+ Utils.formatDate(obj.getString("endDateTime"));

            ((TextView)findViewById(R.id.tv_date_time)).setText(schedule);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // thumbnail image
        final String images[] = obj.getString("imgFile").split(";");
        ((NetworkImageView)findViewById(R.id.img_thumbnail)).setImageUrl(ApiConfig.urlFacilityImage + images[0], imageLoader);

        title = obj.getString("title");
        details = obj.getString("details");
        imgName = images[0];

        LinearLayout.LayoutParams lprams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout dynamicImageView = (LinearLayout)findViewById(R.id.image_panel);

        for(int i=0;i<images.length;i++) {
            if (!images[i].trim().equals("")) {
                Button btn = new Button(this);
                btn.setId(1000 + i + 1);
                btn.setText("Image " + (i + 1));
                btn.setLayoutParams(lprams);
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_download, 0, 0, 0);
                //btn.setBackgroundResource(R.color.colorAccent);
                //btn.setTextColor(new ColorStateList());
                final int index = i;

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TAG", "The index is" + index);
                        startDownload(ApiConfig.urlFacilityImage + images[index]);
                    }
                });

                dynamicImageView.addView(btn);
            }
        }

        LinearLayout dynamicview1 = (LinearLayout)findViewById(R.id.delete_image_panel);
        LinearLayout.LayoutParams lprams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for(int i=0;i<images.length;i++) {
            if (!images[i].trim().equals("")) {
                Button btn = new Button(this);
                btn.setId(1000 + i + 1);
                // btn.setText("Image " + (i + 1));
                btn.setLayoutParams(lprams);
                if(session.getUserType().equals("user"))
                {
                    btn.setVisibility(View.GONE);
                    delete_image_panel.setVisibility(View.GONE);
                    delete_pdf_panel.setVisibility(View.GONE);

                }
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete_black_24dp, 0, 0, 0);
                //btn.setBackgroundResource(R.color.colorAccent);
                //btn.setTextColor(new ColorStateList());
                final int index = i;

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(FacilityDetailsActivity.this);
                        alert.setMessage("Do you want to delete this?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                EditImgDetailsForDelete(facilityId, "");

                                // deleteNotice();
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();
                        Log.e("delete", "The index is" + index);
                        // startDownload(ApiConfig.urlViewImage+ images[index]);
                    }
                });

                dynamicview1.addView(btn);
            }
        }



        final String docs[] = obj.getString("docFile").split(";");
        LinearLayout dynamicDocView = (LinearLayout)findViewById(R.id.document_panel);

        for(int i=0;i<docs.length;i++) {
            if (!docs[i].trim().equals("")) {
                Button btn = new Button(this);
                btn.setId(1000 + i + 1);
                btn.setText("File " + (i + 1));
                btn.setLayoutParams(lprams);
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_download, 0, 0, 0);
                //btn.setBackgroundResource(R.color.colorAccent);
                //btn.setTextColor(new ColorStateList());
                final int index = i;

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TAG", "The index is" + index);
                        startDownload(ApiConfig.urlFacilityDocument + docs[index]);
                    }
                });

                dynamicDocView.addView(btn);
            }
        } LinearLayout dynamicDocView1 = (LinearLayout)findViewById(R.id.delete_pdf_panel);

        for(int i=0;i<docs.length;i++) {
            if (!docs[i].trim() .equals("")) {
                Button btn = new Button(this);
                btn.setId(1000 + i + 1);
                // btn.setText("File " + (i + 1));
                btn.setLayoutParams(lprams);
                if(session.getUserType().equals("user"))
                {
                    btn.setVisibility(View.GONE);
                    delete_image_panel.setVisibility(View.GONE);
                    delete_pdf_panel.setVisibility(View.GONE);

                }
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete_black_24dp, 0, 0, 0);
                //btn.setBackgroundResource(R.color.colorAccent);
                //btn.setTextColor(new ColorStateList());
                final int index = i;

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TAG", "The index is" + index);
                        AlertDialog.Builder alert = new AlertDialog.Builder(FacilityDetailsActivity.this);
                        alert.setMessage("Do you want to delete this ?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditDocDetailsForDelete(facilityId, "");

                                // deleteNotice();
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();
                        // startDownload(ApiConfig.urlViewDocument + docs[index]);
                    }
                });

                dynamicDocView1.addView(btn);
            }
        }
    }

    public void startDownload(String fileUrl) {

        Uri uri = Uri.parse(fileUrl); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void deleteFacility()
    {
        final String tag_string_req = "DeleteFacilityRequest";

        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlFacilityReg, new Response.Listener<String>() {

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
                        FacilityDetailsActivity.this.finish();
                    }
                } catch (JSONException e) {
                    //Dismissing the progress dialog
                    loading.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

                params.put("action","facility_delete");
                params.put("facilityId",facilityId);
//                params.put("hostelId", hostelId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void sendMultiplePush() {
        final String title = "Facility : "+this.title;
        final String message = this.details;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.urlSendMultiplePush,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Push Response", response);
                        Toast.makeText(FacilityDetailsActivity.this, "Notification send successfully.", Toast.LENGTH_LONG).show();
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
                if(!imgName.trim().equals(""))
                    params.put("image", ApiConfig.urlFacilityImage+imgName);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {

    }

    //edit
    public void EditImgDetailsForDelete(final String facilityId, final  String imgFile) {


        String   tag_string_req = "edit";


        final ProgressDialog loading = ProgressDialog.show(FacilityDetailsActivity.this, "", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlAddClassType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();

                onBackPressed();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                loading.dismiss();
                Toast.makeText(getApplicationContext(), "error"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "6");

                params.put("facilityId", facilityId);
                params.put("imgFile", imgFile);

                Log.e("URL", urlAddClassType);
                Log.e("Register Params: ", params.toString());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    //edit
    public void EditDocDetailsForDelete(final String facilityId, final  String imgFile) {


        String   tag_string_req = "edit";


        final ProgressDialog loading = ProgressDialog.show(FacilityDetailsActivity.this, "", "Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlAddClassType, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response String", response);
                //Dismissing the progress dialog
                loading.dismiss();

                onBackPressed();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                loading.dismiss();
                Toast.makeText(getApplicationContext(), "error"+error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", "7");

                params.put("facilityId", facilityId);
                params.put("imgFile", imgFile);

                Log.e("URL", urlAddClassType);
                Log.e("Register Params: ", params.toString());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}
