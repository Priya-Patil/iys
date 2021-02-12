package com.netist.mygirlshostel;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.netist.mygirlshostel.FCM.MyFirebaseInstanceIDService;
import com.netist.mygirlshostel.adv.AdvHomeActivity;
import com.netist.mygirlshostel.adv.ApiStatusCallBack;
import com.netist.mygirlshostel.appversion.AppVersionModel;
import com.netist.mygirlshostel.appversion.AppVersionServices;
import com.netist.mygirlshostel.appversion.AppVersion_Dialog;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {


    ProgressBar bar;
    TextView txt;
    int total = 0;
    boolean isRunning = false;

    Handler handler = new Handler();
    String version_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //animations
        ((ImageView)findViewById(R.id.imageView1)).startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_1000));

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        // current version
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version_code = info.versionName;
        getAppVersion();

    }

    void animation()
    {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                SessionHelper.getInstance(getApplicationContext()).saveDeviceToken(refreshedToken);

                SessionHelper session = new SessionHelper(getApplicationContext());

                if(session.isLoggedIn()) {
                    if(session.getUserType().equals("adv"))
                    {
                        Intent startActivityIntent = new Intent(SplashActivity.this, AdvHomeActivity.class);
                        startActivity(startActivityIntent);
                        SplashActivity.this.finish();
                    }
                    else
                    {
                        Intent startActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(startActivityIntent);
                        SplashActivity.this.finish();
                    }
                }
                else
                {
                    // Intent startActivityIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    Intent startActivityIntent = new Intent(SplashActivity.this, SelectRoleActivity.class);
                    startActivity(startActivityIntent);
                    SplashActivity.this.finish();
                }
            }
        }, 2000);

    }

    private void getAppVersion() {
        try {
            if (Utility.isNetworkAvailable(SplashActivity.this)) {

                Log.e("CheckReponseVideostry", "Called");
                AppVersionServices.getInstance(SplashActivity.this).getAppVersion(
                        new ApiStatusCallBack<ArrayList<AppVersionModel>>() {

                            @Override
                            public void onSuccess(ArrayList<AppVersionModel> arraylist) {
                                Log.e("CheckReponseVideosSucs", arraylist.get(0).getVersionno());

                                Log.e("cHkVersion", "" + arraylist.get(0).getVersionno());
                                //Change code
                                if (version_code.equals(arraylist.get(0).getVersionno())) {
                                    animation();

                                } else {
                                    final AppVersion_Dialog dialog = new AppVersion_Dialog(SplashActivity.this);
                                    dialog.show();
                                    dialog.setCanceledOnTouchOutside(false);
                                }

                                //  prefManager.setAPP_VERSION(String.valueOf(arraylist.get(0).getAppversionid()));

                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e("CheckReponseanError", "" + anError.getMessage());
                               // Utility.showErrorMessage(SplashActivity.this, "Network:" + anError.getMessage(), Snackbar.LENGTH_LONG);
                            }

                            @Override
                            public void onUnknownError(Exception e) {
                                Log.e("CheckReponseUnknown", "Called");
                                //   Utility.showErrorMessage(getActivity(), e.getMessage(), Snackbar.LENGTH_LONG);
                            }
                        });
            } else {
               // Utility.showErrorMessage(SplashActivity.this, "Could not connect to the internet", Snackbar.LENGTH_LONG);
            }
        } catch (Exception ex) {
            Log.e("CheckReponseOther", "Called");
            Log.e("GetVideoPackages", "InsideGetVideoPackagesExtra" + ex);
          //  Utility.showErrorMessage(SplashActivity.this, "No record found", Snackbar.LENGTH_LONG);
        }
    }

}
