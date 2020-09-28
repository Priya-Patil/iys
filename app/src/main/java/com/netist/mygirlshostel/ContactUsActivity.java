package com.netist.mygirlshostel;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
    }

    public void map(View v)
    {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.co.in/maps/place/Dr.+Jyoti+Mahadik/@19.8633276,75.3393409,17z/data=!3m1!4b1!4m5!3m4!1s0x3bdb987f80686815:0xf4139ff050245e31!8m2!3d19.8633226!4d75.3415296"));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}
