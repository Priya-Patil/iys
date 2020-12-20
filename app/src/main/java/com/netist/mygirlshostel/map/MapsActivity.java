package com.netist.mygirlshostel.map;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import com.google.android.gms.location.LocationListener;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.hostel.HostelListActivity;
import com.netist.mygirlshostel.utils.Utility;


import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    PrefManager prefManager;
    Button btn_seekbar, btn_hostel_type;
    SeekBar seek_bar;
    TextView perText;
    int typeIndex = 0;
    String type,htype;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        prefManager=new PrefManager(MapsActivity.this);
        btn_seekbar = findViewById(R.id.btn_seekbar);
        btn_hostel_type = findViewById(R.id.btn_hostel_type);
        editText = findViewById(R.id.editText);
        btn_seekbar.setOnClickListener(this);
        btn_hostel_type.setOnClickListener(this);
        seek_bar = findViewById(R.id.seek_bar);

        perText = (TextView)findViewById(R.id.percent_text);
        //   perText.setText(Integer.toString(session.getUserDistance()) +"km");
        perText.setText("20" +"km");
        prefManager.setDistance("20");

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                prefManager.setDistance(String.valueOf(progress));
                perText.setText(Integer.toString(progress) +"km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void searchLocation(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (locationSearch.getText().toString().length() == 0 ) {

            Toast.makeText(this, "Enter area name", Toast.LENGTH_SHORT).show();

        }
        else {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);

            prefManager.setLati(String.valueOf(address.getLatitude()));
            prefManager.setLongi(String.valueOf(address.getLongitude()));

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(), address.getLongitude() ));

            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

            Utility.launchActivity(MapsActivity.this, SearchNearMeActivity.class, false);

         //   Toast.makeText(getApplicationContext(),address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();

        }

          //  Utility.launchActivity(MapsActivity.this, HostelListActivity.class, true);
        }

        public void searchCurrentLocation(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);

            prefManager.setLati(String.valueOf(address.getLatitude()));
            prefManager.setLongi(String.valueOf(address.getLongitude()));

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(), address.getLongitude() ));

            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

           // Utility.launchActivity(MapsActivity.this, SearchNearMeActivity.class, false);

            Toast.makeText(getApplicationContext(),address.getLatitude()+" "+address.getLongitude(),Toast.LENGTH_LONG).show();



          //  Utility.launchActivity(MapsActivity.this, HostelListActivity.class, true);
        }




    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_hostel_type:

                if(editText.getText().toString().length()==0)
                {
                    Toast.makeText(this, "Enter area name", Toast.LENGTH_SHORT).show();
                }
                else {
/*
                    Bundle bundle= new Bundle();
                    bundle.putString("booking","booking");
                    Utility.launchActivity(MapsActivity.this, HostelListActivity.class, true, bundle);
*/
                    Utility.launchActivity(MapsActivity.this, SearchNearMeActivity.class, false);

                }
                break;



        }

    }


    void dialog()
    {
        final CharSequence[] typeList = new CharSequence[] {
                "Girls hostel",
                "Boys hostel",

        };
        final AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setSingleChoiceItems(typeList, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                typeIndex = i;
            }
        });
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                htype = typeList[typeIndex].toString();
                if(htype.equals("Girls hostel"))
                {
                    type="1";
                    prefManager.setType("1");
                    Toast.makeText(MapsActivity.this, "girls hostel", Toast.LENGTH_SHORT).show();
                    Utility.launchActivity(MapsActivity.this, HostelListActivity.class, true);
                   // setHostelList(type);
                }
                else
                {
                    type="2";
                    prefManager.setType("2");
                    Toast.makeText(MapsActivity.this, "boys hostel", Toast.LENGTH_SHORT).show();
                    Utility.launchActivity(MapsActivity.this, HostelListActivity.class, true);
                    //setHostelList(type);
                }
            }
        });
        alert.show();
    }




}
