package com.netist.mygirlshostel.advertisement.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.constants.PrefManager;

import java.util.ArrayList;

/**
 * Created by s on 8/20/2018.
 */

public class ImagePreview_Dialog extends Dialog implements View.OnClickListener {

    public Activity activity;

    ProgressDialog progressDialog;

    PrefManager prefManager;
    Button btn_start_exam;
    String fullpath;
    ImageView img1;

    public ImagePreview_Dialog() {
        super(null);
    }
    public ImagePreview_Dialog(Activity activity, String fullpath)  {
        super(activity);
        this.activity = activity;
        this.fullpath = fullpath;
      }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.startexam_dialog);
        bindView();
        btnlistener();

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.float_close);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                dismiss();
            }
        });


        img1.setBackground(Drawable.createFromPath(fullpath));

    }



    @Override
    public void onClick(View v) {

        Log.e("onClick", "Clicked");

        switch (v.getId()) {

            case R.id.btn_start_exam:
                dismiss();
                break;
        }
    }

 //   {"type":1,"Action":1,"Orderid":0, "Status":1,"Deliveryboyid":1,"Deliverydate":"Deliverydate",
     //       "Address":"Address","Paymentmode":1,"Totalprice":1,"Savedprice":1, "LogedinUserId":1}

    private void btnlistener() {
        btn_start_exam.setOnClickListener(this);
    }
    private void bindView() {
        btn_start_exam=findViewById(R.id.btn_start_exam);
        img1=findViewById(R.id.img1);

        progressDialog=new ProgressDialog(activity);
        prefManager=new PrefManager(activity);
    }

}
