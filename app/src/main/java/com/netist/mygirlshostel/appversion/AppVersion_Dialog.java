package com.netist.mygirlshostel.appversion;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.SplashActivity;
import com.netist.mygirlshostel.utils.Utility;


/**
 * Created by s on 8/20/2018.
 */

public class AppVersion_Dialog extends Dialog implements View.OnClickListener {

    public Activity activity;


    static TextView btn_start;
    static RelativeLayout rl_start_exam;
    Bundle bundle;

    RecyclerView recycler_view_quizpackages;
    ProgressDialog progressDialog;


    public AppVersion_Dialog() {
        super(null);
    }
    public AppVersion_Dialog(Activity activity) {
        super(activity);
        this.activity = activity;
        this.bundle=bundle;

      }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.appversion_dialog);
        bindView();
        btnlistener();


        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.float_close);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Utility.launchActivity(activity, SplashActivity.class, false);
            }
        });


      }



    @Override
    public void onClick(View v) {

        Log.e("onClick", "Clicked");

        switch (v.getId()) {

            case R.id.btn_start:

               // Utility.launchActivity(activity, SplashActivity.class, false);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getContext().getResources().getString(R.string.rate_us_link)));
                getContext().startActivity(intent);

                break;

            case R.id.rl_start_exam:

                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(getContext().getResources().getString(R.string.rate_us_link)));
                getContext().startActivity(intent1);

              //  Utility.launchActivity(activity, SplashActivity.class, false);
                break;

        }
    }


    private void btnlistener() {
        btn_start.setOnClickListener(this);
        rl_start_exam.setOnClickListener(this);
    }
    private void bindView() {
        btn_start=findViewById(R.id.btn_start);
        rl_start_exam=findViewById(R.id.rl_start_exam);


        progressDialog=new ProgressDialog(activity);

    }



}
