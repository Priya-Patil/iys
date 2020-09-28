package com.netist.mygirlshostel;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.netist.mygirlshostel.FCM.MyFirebaseInstanceIDService;
import com.netist.mygirlshostel.advertisement.AdvHomeActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;

public class SplashActivity extends AppCompatActivity {


    ProgressBar bar;
    TextView txt;
    int total = 0;
    boolean isRunning = false;

    Handler handler = new Handler();

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


        startService(new Intent(this, MyFirebaseInstanceIDService.class));

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SessionHelper.getInstance(getApplicationContext()).saveDeviceToken(refreshedToken);
        //SessionHelper.getInstance(this).getDeviceToken();

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
}
