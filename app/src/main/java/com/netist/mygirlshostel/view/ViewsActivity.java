package com.netist.mygirlshostel.view;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewsActivity extends BaseActivity {

    String[] countries = new String[]
            {
                    "Steam Bath",

                    "Electro Nerve Activator",

                    "Massage Therapy",

                    "Bach Flower Remedies",

                    "Air Pressure",

                    "ACS Acupressure Stimulator",

                    "Foot Spa",

                    "Facial",

                    "Hair Spa"
            };

    int[] flags = new int[]
            {
                    R.drawable.view_bath,

                    R.drawable.view_weighing,

                    R.drawable.view_massage,

                    R.drawable.view_flower,

                    R.drawable.view_gun,

                    R.drawable.view_stimulator,

                    R.drawable.view_foot,

                    R.drawable.view_face,

                    R.drawable.view_spahair
            };

    String[] currency = new String[]
            {

                    "Detoxification, skin toning, reduce swelling, pain, weight and burn calories, tan skin rejuvenation Views.",

                    "Acupressure point activate, redusing muscle strain, stiffness, weight, burn calories and joint pain, detoxification.",

                    "Muscle relaxation, improve blood circulation, whole body instrumental view_massage with acupressure point, lactic acid uric acid dispersion.",

                    "Wild rose, cerato, heather, beach, centaury, agrimony.",

                    "Realihnment of vertebral column by air suction",

                    "Redusing muscle pain, dissolving cyst",

                    "Foot tan skin rejuvenation",

                    "Usefull for sensitive skin, all skin problem, reduce fine line and wrinkles, improve skin tone and texture, bridle package.",

                    "Natural conditioning with extract of vanaushadhi + olive oil +aroma oil + homeopathic mother tincture, head view_massage steam, splits"
            };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views);

        setTitle("Our Views");

        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<9;i++)
        {

            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", "View: " + countries[i]);
            hm.put("cur","" + currency[i]);
            hm.put("flag", Integer.toString(flags[i]) );
            aList.add(hm);
        }

        String[] from = { "flag","txt","cur" };
        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt,R.id.cur};

        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.list_layout_view, from, to);
        ListView listView = ( ListView ) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }
}
