package com.netist.mygirlshostel.advertisement;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.netist.mygirlshostel.HomeBaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.SplashActivity;
import com.netist.mygirlshostel.WelcomeActivity;
import com.netist.mygirlshostel.advertisement.model.SliderImageModel;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.SEND_SMS;

public class AdvHomeActivity extends AppCompatActivity implements View.OnClickListener ,BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    Button btn_view,btn_add;

    CircleImageView ib_twitter;
    CircleImageView ib_facebook;
    CircleImageView ib_instagram;
    CircleImageView ib_whatsapp;
    CircleImageView ib_linkedin;
    CircleImageView ib_telegram;
    CircleImageView ib_email;
    CircleImageView img_logout;
    SessionHelper session;
    ProgressDialog progressDialog;
    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_adv_home);

        btn_add=findViewById(R.id.btn_add);
        btn_view=findViewById(R.id.btn_view);
        ib_twitter = findViewById(R.id.ib_twitter);
        ib_facebook = findViewById(R.id.ib_facebook);
        ib_instagram = findViewById(R.id.ib_instagram);
        ib_whatsapp =   findViewById(R.id.ib_whatsapp);
        ib_linkedin =findViewById(R.id.ib_linkedin);
        ib_telegram =findViewById(R.id.ib_telegram);
        ib_email =findViewById(R.id.ib_email);
        img_logout =findViewById(R.id.img_logout);
        session=new SessionHelper(AdvHomeActivity.this);
        progressDialog=new ProgressDialog(AdvHomeActivity.this);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        ib_twitter.setOnClickListener(this);
        ib_facebook.setOnClickListener(this);
        ib_instagram.setOnClickListener(this);
        ib_whatsapp.setOnClickListener(this);
        ib_linkedin.setOnClickListener(this);
        ib_telegram.setOnClickListener(this);
        ib_email.setOnClickListener(this);
        img_logout.setOnClickListener(this);

        requestPermission();

        getImages();
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


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdvHomeActivity.this,AddAdvertisementActivity.class));

            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdvHomeActivity.this,AdvertisementListActivity.class));

            }
        });

        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( AdvHomeActivity.this);

                // set title
                alertDialogBuilder.setTitle("Logout");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Click yes to logout!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                SessionHelper session = new SessionHelper(getApplicationContext());
                                session.setLogin(false);
                                session.setUserType("");
                                session.setUserID("");
                                Utility.launchActivity(AdvHomeActivity.this, SplashActivity.class,
                                        true);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
        });

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.ib_twitter:
               /* Intent intentTwitter = null;
                intentTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/@teammandroid"));
                this.startActivity(intentTwitter);
               */

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:9423780567"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

               break;

            case R.id.ib_facebook:
                /*Intent intentFacebook = null;
                try {
                    // intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
                    intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/balasahebbodkhepage/"));
                } catch (Exception e) {
                    // intentFacebook =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
                    intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/balasahebbodkhepage/"));
                }
                startActivity(intentFacebook);*/
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ib_instagram:
                // Uri uri2 = Uri.parse("http://instagram.com/_u/teammandroid");
                /*Uri uri2 = Uri.parse("https://www.instagram.com/balasaheb.bodkhe.7/");
                Intent intentInstagram = new Intent(Intent.ACTION_VIEW, uri2);

                intentInstagram.setPackage("com.instagram.android");

                try {
                    startActivity(intentInstagram);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            // Uri.parse("http://instagram.com/teammandroid")));
                            Uri.parse("https://www.instagram.com/balasaheb.bodkhe.7/")));
                }
*/
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();

                break;

            case R.id.ib_whatsapp:
                String url = "https://api.whatsapp.com/send?phone=" + "+91 94237 80567";
                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                intentWhatsapp.setData(Uri.parse(url));
                startActivity(intentWhatsapp);
                break;

            case R.id.ib_linkedin:
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=you"));
                startActivity(intent);*/
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ib_email:
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=you"));
                startActivity(intent);*/
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"devendramahadik@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AdvHomeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.ib_telegram:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, ("https://play.google.com/store/apps/details?id=com.iys.inyourservice"));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }
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
            url_maps1.put(imgPath,imgPath);
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


}
