package com.netist.mygirlshostel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netist.mygirlshostel.utils.FontManager;

public class NewSplashActivity extends AppCompatActivity {

    ImageView eduorange, edu;
    RelativeLayout edubg;
    TextView title;
    FontManager FM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splash);

        FM = new FontManager(getApplicationContext());
        edu = (ImageView) findViewById(R.id.edu);
        edubg = (RelativeLayout) findViewById(R.id.edubg);
        eduorange = (ImageView) findViewById(R.id.eduorange);
        title = (TextView) findViewById(R.id.edutitle);

        edu.setVisibility(View.INVISIBLE);
        edubg.setVisibility(View.INVISIBLE);
        eduorange.setVisibility(View.INVISIBLE);
        title.setVisibility(View.INVISIBLE);

        FM.setBebasRegular(title);


    }
}