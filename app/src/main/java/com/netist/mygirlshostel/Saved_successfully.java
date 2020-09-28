package com.netist.mygirlshostel;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.netist.mygirlshostel.hostel.HotelListChargesActivity;

public class Saved_successfully extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_successfully);
    }

    public void openfile(View v)
    {
         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + HotelListChargesActivity.path);
                intent.setDataAndType(uri, "text/csv");
                startActivity(Intent.createChooser(intent, "Open folder"));

        Toast.makeText(getApplicationContext(), "Please go to File Manager/Documents" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }
}
