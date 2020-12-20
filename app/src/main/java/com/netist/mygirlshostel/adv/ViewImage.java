package com.netist.mygirlshostel.adv;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


import com.netist.mygirlshostel.R;
import com.squareup.picasso.Picasso;


public class ViewImage extends Dialog {

    ImageView myImage;
    String url = "",profile_path;
    Activity activity;
    Bundle attachmentModel;
    ProgressDialog progressDialog;
    ImageView iv_delete;
    public ViewImage(@NonNull Context context) {
        super(context);
    }

    public ViewImage(Activity activity, Bundle attachmentModel) {
        super(activity);
        this.activity = activity;
        this.attachmentModel=attachmentModel;
    }

    public ViewImage(Activity activity, String profile_path) {
        super(activity);
        this.activity = activity;
        this.profile_path=profile_path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_view_image);
        bindView();
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        url = activity.getIntent().getStringExtra("image_url");
        Log.e("imgDIalog ", " "+ url +" "+profile_path);

        callImage();

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             dismiss();
            }
        });

    }

    private void callImage() {
        progressDialog.dismiss();
        Picasso.with(activity).load(profile_path).fit().centerCrop()
                .error(R.drawable.male_avatar)
                .placeholder(R.drawable.ic_image_black_24dp)
                .into(myImage);
    }

    private void bindView() {
        myImage = (ImageView) findViewById(R.id.myImage);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        progressDialog=new ProgressDialog(activity);
    }

    }
