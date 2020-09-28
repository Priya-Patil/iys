package com.netist.mygirlshostel;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.netist.mygirlshostel.session_handler.SessionHelper;

public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_map_postion;
    private SessionHelper session;
    private LatLng mMarkerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        session = new SessionHelper(getApplicationContext());
        //pp
        session.setUserNearby(false);
        tv_map_postion = (TextView) findViewById(R.id.tv_map_postion);
        mMarkerPosition = new LatLng(session.getUserLatitude(), session.getUserLongitude());
        tv_map_postion.setText(mMarkerPosition.toString());

        final ViewGroup rl_location = (ViewGroup) findViewById(R.id.rl_location);

        Switch sw_nearby = (Switch)findViewById(R.id.sw_nearby);
        sw_nearby.setChecked(true);
       /* if (session.getUserNearby())
            rl_location.setVisibility(View.VISIBLE);
        else
            rl_location.setVisibility(View.GONE);
*/
        sw_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Switch) v).isChecked())
                {
                   // session.setUserNearby(true);
                    rl_location.setVisibility(View.VISIBLE);
                }
                else
                {
                   // session.setUserNearby(false);
                    rl_location.setVisibility(View.GONE);
                }
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_location);
        final RadioButton radioButton = (RadioButton)findViewById(R.id.user_radio);

       /* if (!session.getUserLocationMode())
        {
            radioButton.setChecked(true);
            ((RadioButton) findViewById(R.id.selected_radio)).setChecked(false);
        }
        else
        {
            radioButton.setChecked(false);
            ((RadioButton) findViewById(R.id.selected_radio)).setChecked(true);
            findViewById(R.id.map_btn).setVisibility(View.VISIBLE);
        }
*/

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                // Toast.makeText(getApplicationContext(), "11111", Toast.LENGTH_LONG).show();
                if (!radioButton.isChecked()){
                    session.setUserNearby(true);
                    session.setUserLocationMode(true);
                    Toast.makeText(getApplicationContext(), "Please add location points", Toast.LENGTH_LONG).show();
                    findViewById(R.id.map_btn).setVisibility(View.VISIBLE);
                }
                else{
                    session.setUserLocationMode(false);
                    session.setUserNearby(false);
                    Toast.makeText(getApplicationContext(), "33333", Toast.LENGTH_LONG).show();
                    findViewById(R.id.map_btn).setVisibility(View.GONE);
                }

            }
        });

        SeekBar seekBar = (SeekBar)findViewById(R.id.map_seek);

        seekBar.setProgress(session.getUserDistance());

        final TextView perText = (TextView)findViewById(R.id.percent_text);
        perText.setText(Integer.toString(session.getUserDistance()) +"km");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                perText.setText(Integer.toString(progress) +"km");
                session.setUserDistance(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button google_map_btn = (Button)findViewById(R.id.btn_google_map);
        google_map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GoogleMapActivity.class);
                if (mMarkerPosition != null)
                    intent.putExtra("position", mMarkerPosition);
                intent.putExtra("map_setting", "");
                startActivityForResult(intent, 10);
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10)
        {
            String address = data.getStringExtra("MESSAGE");
            if (!address.equals("")) {
                mMarkerPosition = (LatLng) data.getParcelableExtra("position");
                tv_map_postion.setText(mMarkerPosition.toString());

                session.setUserLatitude((float) mMarkerPosition.latitude);
                session.setUserLongitude((float) mMarkerPosition.longitude);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
