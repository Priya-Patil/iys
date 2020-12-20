package com.netist.mygirlshostel;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.error.ANError;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.netist.mygirlshostel.adv.AdvertisementServices;
import com.netist.mygirlshostel.adv.ApiStatusCallBack;
import com.netist.mygirlshostel.adv.model.SliderImageModel;
import com.netist.mygirlshostel.classes.ClassesBookingListActivity;
import com.netist.mygirlshostel.classes.ClassesListActivity;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.hostel.HostelBookingListActivity;
import com.netist.mygirlshostel.hostel.HostelListActivity;
import com.netist.mygirlshostel.library.LibraryBookingListActivity;
import com.netist.mygirlshostel.library.LibraryListActivity;
import com.netist.mygirlshostel.mess.MessBookingListActivity;
import com.netist.mygirlshostel.mess.MessListActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.SEND_SMS;
import static com.netist.mygirlshostel.constants.CheckNetwork.isNetworkConnected;

public class MainActivity extends HomeBaseActivity implements View.OnClickListener,BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    SessionHelper session;

    PrefManager prefManager;

    // permission

    private static final int PERMISSION_CALLBACK_CONSTANT = 101;
    private static final int REQUEST_PERMISSION_SETTING = 102;
    private View view;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


    CircleImageView ib_twitter;
    CircleImageView ib_facebook;
    CircleImageView ib_instagram;
    CircleImageView ib_whatsapp;
    CircleImageView ib_linkedin;
    CircleImageView ib_telegram;
    CircleImageView ib_email;

    private SliderLayout mDemoSlider;
    ProgressDialog progressDialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        ib_twitter = findViewById(R.id.ib_twitter);
        ib_facebook = findViewById(R.id.ib_facebook);
        ib_instagram = findViewById(R.id.ib_instagram);
        ib_whatsapp =   findViewById(R.id.ib_whatsapp);
        ib_linkedin =findViewById(R.id.ib_linkedin);
        ib_telegram =findViewById(R.id.ib_telegram);
        ib_email =findViewById(R.id.ib_email);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        progressDialog=new ProgressDialog(MainActivity.this);


        ib_twitter.setOnClickListener(this);
        ib_facebook.setOnClickListener(this);
        ib_instagram.setOnClickListener(this);
        ib_whatsapp.setOnClickListener(this);
        ib_linkedin.setOnClickListener(this);
        ib_telegram.setOnClickListener(this);
        ib_email.setOnClickListener(this);

        requestPermission();
              //phone call permission
        prefManager=new PrefManager(MainActivity.this);
        prefManager.setLati(null);
        prefManager.setLongi(null);
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE))
            {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs phone permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
           /* else if (permissionStatus.getBoolean(Manifest.permission.READ_PHONE_STATE,false))
            {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getApplicationContext(), "Go to Permissions to Grant Phone", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }*/
            else
            {
                //just request the permission
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},PERMISSION_CALLBACK_CONSTANT);
            }
            //  txtPermissions.setText("Permissions Required");

           /* SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.READ_PHONE_STATE,true);
            editor.commit();*/
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }






        // end


        //end


        prefManager.setAREA_SELECTED("area1");


        session = new SessionHelper(getApplicationContext());

        if(session.isLoggedIn())
        {
            //Toast.makeText(getApplicationContext(), "Welcome : " + session.getUserName(), Toast.LENGTH_LONG).show();

            TextView tv_username = (TextView)findViewById(R.id.tv_username);
            tv_username.setText("User Name : "+session.getUserName() + " (" + session.getUserType() + ")");
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
        }

        if(session.getUserType().equals("admin"))
        {
            ((RelativeLayout)findViewById(R.id.admin_panel)).setVisibility(View.VISIBLE);
            ((RelativeLayout)findViewById(R.id.user_panel)).setVisibility(View.GONE);
        }
        else {
            ((RelativeLayout) findViewById(R.id.admin_panel)).setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.user_panel)).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_business).setOnClickListener(this);
         findViewById(R.id.btn_notice).setOnClickListener(this);
        findViewById(R.id.btn_notice2).setOnClickListener(this);
        findViewById(R.id.btn_booking).setOnClickListener(this);
        //findViewById(R.id.btn_contact).setOnClickListener(this);

        if(!session.getUserType().equals("admin") && !session.isTokenUpdated()) {
            TokenUpdate();
        }

        getImages();
    }

    @Override
    public void onClick(View v) {

        if (isNetworkConnected(MainActivity.this) == true) {

            if (v.getId() == R.id.btn_business) {
                Intent intent = new Intent(getApplicationContext(), BusinessListActivity.class);
                intent.putExtra("business", "");
                startActivity(intent);
            }  else if (v.getId() == R.id.btn_notice || v.getId() == R.id.btn_notice2) {
                if (session.getUserType().equals("admin")) {
                    Intent intent = new Intent(getApplicationContext(), BusinessListActivity.class);
                    intent.putExtra("notice", "");
                    startActivity(intent);
                } else if (session.getUserType().equals("hostel")) {
                    preferenceManager.setAREA_SELECTED("details");
                    Intent intent = new Intent(getApplicationContext(), HostelListActivity.class);
                    intent.putExtra("hostelId", session.getUserID());
                    intent.putExtra("notice", session.getUserID());
                    startActivity(intent);
                } else if (session.getUserType().equals("mess")) {
                    Intent intent = new Intent(getApplicationContext(), MessListActivity.class);
                    intent.putExtra("notice", session.getUserID());
                    startActivity(intent);
                } else if (session.getUserType().equals("classes")) {
                    Intent intent = new Intent(getApplicationContext(), ClassesListActivity.class);
                    intent.putExtra("notice", session.getUserID());
                    startActivity(intent);
                } else if (session.getUserType().equals("library")) {
                    Intent intent = new Intent(getApplicationContext(), LibraryListActivity.class);
                    intent.putExtra("notice", session.getUserID());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), BusinessListActivity.class);
                    intent.putExtra("notice", "");
                    startActivity(intent);
                }
            } else if (v.getId() == R.id.btn_booking) {
                if (session.getUserType().equals("hostel")) {
                    Intent intent = new Intent(getApplicationContext(), HostelBookingListActivity.class);
                    intent.putExtra("hostelId", session.getUserID());
                    startActivity(intent);
                } else if (session.getUserType().equals("mess")) {
                    Intent intent = new Intent(getApplicationContext(), MessBookingListActivity.class);
                    intent.putExtra("messId", session.getUserID());
                    startActivity(intent);
                } else if (session.getUserType().equals("classes")) {
                    Intent intent = new Intent(getApplicationContext(), ClassesBookingListActivity.class);
                    intent.putExtra("classesId", session.getUserID());
                    startActivity(intent);
                } else if (session.getUserType().equals("library")) {
                    Intent intent = new Intent(getApplicationContext(), LibraryBookingListActivity.class);
                    intent.putExtra("libraryId", session.getUserID());
                    startActivity(intent);
                } else {
//                Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    Intent intent = new Intent(getApplicationContext(), BusinessListActivity.class);
                    intent.putExtra("booking", "");
                    startActivity(intent);
                }
            } else if (v.getId() == R.id.ib_whatsapp) {
                String url = "https://api.whatsapp.com/send?phone=" + "+91 94237 80567";
                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                intentWhatsapp.setData(Uri.parse(url));
                startActivity(intentWhatsapp);
            } else if (v.getId() == R.id.ib_linkedin) {
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=you"));
                startActivity(intent);*/
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.ib_email) {
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=you"));
                startActivity(intent);*/
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"devendramahadik@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            } else if (v.getId() == R.id.ib_telegram) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, ("https://play.google.com/store/apps/details?id=com.iys.inyourservice"));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }

            else if (v.getId() == R.id.ib_twitter) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:9423780567"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                    }
                    startActivity(callIntent);
            }



            else {

                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
            }

            //end
        }
    }

    public void TokenUpdate()
    {

        final String type = session.getUserType();
        final String userId = session.getUserID();
        final String appToken = session.getDeviceToken();

        String tag_string_req = "TokenRegRequest";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    ApiConfig.urlTokenReg, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e ("Response String", response);

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (!error) {

                            if(jObj.has("isTokenStored") && jObj.getBoolean("isTokenStored"))
                                session.setTokenUpdated(true);

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
                    //Log.e("Registration Error: ",  error.getMessage());
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();

                    params.put("type",type);
                    params.put("id",userId);

                    if (appToken != null) {
                        params.put("token",appToken);
                    }
                    else
                        params.put("token","");

                    Log.e("URL", ApiConfig.urlTokenReg);
                    Log.e("Request Params: ", params.toString());
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    //for permission


    //override methods for permission


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                proceedAfterPermission();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)){
                // txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs phone permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE},PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //Toast.makeText(getApplicationContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        //  txtPermissions.setText("We've got all permissions");
        // Toast.makeText(getApplicationContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
    }


    //  Runtime permission
    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        CALL_PHONE,
                        SEND_SMS,
                        CAMERA,
                        ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
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

    private void getImages() {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {
                progressDialog.setTitle("Please Wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                //Add porogressDialog
                Log.e("CheckReponseVideostry", "Called");
                AdvertisementServices.getInstance(getApplicationContext()).
                        fetchSliderImages( new ApiStatusCallBack<ArrayList<SliderImageModel>>() {

                                    @Override
                                    public void onSuccess(ArrayList<SliderImageModel> arraylist) {
                                        Log.e("CheckReponseVideosSucs", "Called");
                                        progressDialog.dismiss();
                                        slider(arraylist);
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e("CheckReponseanError", ""+anError.getMessage());
                                        progressDialog.dismiss();
                                      //  Utility.showErrorMessage(ProductDetailsActivity.this, "Network:" + anError.getMessage(), Snackbar.LENGTH_LONG);
                                    }

                                    @Override
                                    public void onUnknownError(Exception e) {
                                        Log.e("CheckReponseUnknown", "Called");
                                        progressDialog.dismiss();
                                        //   Utility.showErrorMessage(getActivity(), e.getMessage(), Snackbar.LENGTH_LONG);
                                    }
                                });
            } else {
               // Utility.showErrorMessage(ProductDetailsActivity.this, "Could not connect to the internet", Snackbar.LENGTH_LONG);
            }
        }catch (Exception ex) {
            Log.e("CheckReponseOther", "Called");
            Log.e("GetVideoPackages","InsideGetVideoPackagesExtra"+ex);
          //  Utility.showErrorMessage(ProductDetailsActivity.this, "No record found", Snackbar.LENGTH_LONG);
        }
    }


    public void slider(ArrayList<SliderImageModel> arraylist) {
        progressDialog.dismiss();
        HashMap<String, String> url_maps1 = new HashMap<String, String>();
        int id = 1;
        for (SliderImageModel model : arraylist) {
            Log.e("checkLists",""+arraylist);

            String imgPath = "http://iysonline.club/iys/api/attachments/sliderimages/"+model.getImages();
            //String imgPath = model.getImages();
            //Log.e( "slider: ",imgPath);
            url_maps1.put(model.getTitle(),imgPath);
        }
        for (String name : url_maps1.keySet()) {
            //Log.e("checkImg",""+name);
            DefaultSliderView defaultSliderView = new DefaultSliderView(this);
            defaultSliderView.image(url_maps1.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            defaultSliderView.bundle(new Bundle());
            defaultSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(defaultSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);//Fade
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        // mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
      /*  final SliderImagePreview_Dialog dialog = new SliderImagePreview_Dialog(MainActivity.this, slider.getUrl());
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        Toast.makeText(this, "chk" +slider.getUrl(), Toast.LENGTH_SHORT).show();
        Log.e( "onSliderClick: ",slider.getUrl() );
*/
       String ipath=slider.getUrl().replaceAll(" ", "%20");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(ipath));
        startActivity(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
