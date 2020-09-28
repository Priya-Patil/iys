package com.netist.mygirlshostel.view;

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
import static com.netist.mygirlshostel.web_api_handler.ApiConfig.urlViewReg;

//public class ViewDetailsActivity extends AppCompatActivity implements  BaseSliderView.OnSliderClickListener, View.OnClickListener, ViewPagerEx.OnPageChangeListener {
public class ViewDetailsActivity extends BaseActivity implements View.OnClickListener{

    Bundle notice;
    String viewId, id;
    String title, details, imgName;
    //private SliderLayout mDemoSlider;
    SessionHelper session;
    Button btn_delete_view;
    LinearLayout delete_image_panel, delete_pdf_panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
        this.session = new SessionHelper(getApplicationContext());

        btn_delete_view=findViewById(R.id.btn_delete_view);
        delete_image_panel=findViewById(R.id.delete_image_panel);
        delete_pdf_panel=findViewById(R.id.delete_pdf_panel);

        //  mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        setTitle("View Details");
        notice = getIntent().getExtras();
        viewId = notice.getString("viewId");
        id = notice.getString("id");
//        hostelId = notice.getString("hostelId");

        Log.e( "onCreate: ", session.getUserType());

       /* if(session.getUserType().equals("user"))
        {
            btn_delete_view.setVisibility(View.GONE);
        }
*/
        setNoticeDetails(notice);

        if(!session.getUserType().equals("admin"))
        {
            ((LinearLayout)findViewById(R.id.admin_panel)).setVisibility(View.VISIBLE);
        }
        if(session.getUserType().equals("user"))
        {
            ((LinearLayout)findViewById(R.id.admin_panel)).setVisibility(View.GONE);
        }

        btn_delete_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ViewDetailsActivity.this);
                alert.setMessage("Do you want to delete this View?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNotice();
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

        ((Button)findViewById(R.id.btn_resend_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMultiplePush();
            }
        });
    }

    private void setNoticeDetails(Bundle obj) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ((TextView)findViewById(R.id.tv_title)).setText(obj.getString("title"));
        ((TextView)findViewById(R.id.tv_details)).setText(obj.getString("details"));
        try {
            ((TextView)findViewById(R.id.tv_date_time)).setText(Utils.formatDate(obj.getString("dateTime")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // thumbnail image

        final String imgurl=obj.getString("imgFile");
        final String images[] = obj.getString("imgFile").split(";");
        ((NetworkImageView)findViewById(R.id.img_thumbnail)).setImageUrl(ApiConfig.urlViewImage + images[0], imageLoader);

        title = obj.getString("title");
        details = obj.getString("details");
        imgName = images[0];

        Log.e( "imgName: ",imgurl );

        //  Newslider(images);
        LinearLayout dynamicview = (LinearLayout)findViewById(R.id.image_panel);
        LinearLayout.LayoutParams lprams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

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
                        startDownload(ApiConfig.urlViewImage+ images[index]);
                    }
                });

                dynamicview.addView(btn);
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
                    ((LinearLayout)findViewById(R.id.admin_panel)).setVisibility(View.GONE);
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

                        AlertDialog.Builder alert = new AlertDialog.Builder(ViewDetailsActivity.this);
                        alert.setMessage("Do you want to delete this?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String hellWrld = imgurl.replace(images[index],"");

                                Log.e( "chk: ",viewId );

                                EditImgDetailsForDelete(viewId, "");

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
            if (!docs[i].trim() .equals("")) {
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
                        startDownload(ApiConfig.urlViewDocument + docs[index]);
                    }
                });

                dynamicDocView.addView(btn);
            }
        }


        LinearLayout dynamicDocView1 = (LinearLayout)findViewById(R.id.delete_pdf_panel);

        for(int i=0;i<docs.length;i++) {
            if (!docs[i].trim() .equals("")) {
                Button btn = new Button(this);
                btn.setId(1000 + i + 1);
                // btn.setText("File " + (i + 1));
                btn.setLayoutParams(lprams);
                if(session.getUserType().equals("user"))
                {
                    btn.setVisibility(View.GONE);
                    ((LinearLayout)findViewById(R.id.admin_panel)).setVisibility(View.GONE);
                    // btn_delete_view.setVisibility(View.GONE);
                    // delete_pdf_panel.setVisibility(View.GONE);

                }
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete_black_24dp, 0, 0, 0);
                //btn.setBackgroundResource(R.color.colorAccent);
                //btn.setTextColor(new ColorStateList());
                final int index = i;

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TAG", "The index is" + index);
                        AlertDialog.Builder alert = new AlertDialog.Builder(ViewDetailsActivity.this);
                        alert.setMessage("Do you want to delete this ?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditDocDetailsForDelete(viewId, "");

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

    private void deleteNotice() {
        final String tag_string_req = "DeleteNoticeRequest";

        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlViewReg, new Response.Listener<String>() {

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
                        ViewDetailsActivity.this.finish();
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

                params.put("action","view_delete");
                params.put("viewId",viewId);
                //params.put("id", id);
//                params.put("hostelId", hostelId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void deleteItem() {
        final String tag_string_req = "DeleteNoticeRequest";

        final ProgressDialog loading = ProgressDialog.show(this,"Updating...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                urlViewReg, new Response.Listener<String>() {

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
                        ViewDetailsActivity.this.finish();
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

                params.put("action","view_item_delete");
                params.put("viewId",viewId);
                //params.put("id", id);
//                params.put("hostelId", hostelId);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void sendMultiplePush() {
        final String title = "View : "+this.title;
        final String message = this.details;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.urlSendMultiplePush,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Push Response", response);
                        Toast.makeText(ViewDetailsActivity.this, "viewId send successfully.", Toast.LENGTH_LONG).show();
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
                    params.put("image", ApiConfig.urlViewImage+imgName);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }


 /*   public void Newslider(final String images[]) {
        // dialog.dismiss();
        HashMap<String, String> url_maps = new HashMap<String, String>();
        int id = 1;
        //String path = "http://192.168.2.3/Ornfit/dashboard/api/attachments/sliderimages/";
        for(int i=0;i<images.length;i++) {
            String imgPath = ApiConfig.urlViewDocument  +images;
            url_maps.put(imgPath,imgPath);
        }

        // url_maps.put("Most Selling",  R.drawable.saarthi_logo);

        for (String name : url_maps.keySet()) {
            Log.e("checkImg",""+name);
            DefaultSliderView defaultSliderView = new DefaultSliderView(ViewDetailsActivity.this);
            defaultSliderView.image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            mDemoSlider.addSlider(defaultSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

    }*/
/*
    @Override
    public void onSliderClick(BaseSliderView slider) {

    }*/

    @Override
    public void onClick(View v) {

    }
/*
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }*/


    //edit
    public void EditImgDetailsForDelete(final String viewId, final  String imgFile) {


        String   tag_string_req = "edit";


        final ProgressDialog loading = ProgressDialog.show(ViewDetailsActivity.this, "", "Please wait...", false, false);

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

                params.put("id", "2");

                params.put("viewId", viewId);
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
    public void EditDocDetailsForDelete(final String viewId, final  String imgFile) {


        String   tag_string_req = "edit";


        final ProgressDialog loading = ProgressDialog.show(ViewDetailsActivity.this, "", "Please wait...", false, false);

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

                params.put("id", "3");

                params.put("viewId", viewId);
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
