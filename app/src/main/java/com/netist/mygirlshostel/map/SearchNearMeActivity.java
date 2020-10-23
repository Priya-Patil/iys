package com.netist.mygirlshostel.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.hostel.HostelDetailActivity;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;
import com.netist.mygirlshostel.web_api_handler.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchNearMeActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener{
    private GoogleMap mMap;
    PrefManager prefManager;
    private ArrayList<HashMap<String, String>> hostelList;

    private static final int Request_User_Location_Code=99;
    private GoogleApiClient googleApiClient;

    LatLng latLng1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_near_me);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        prefManager=new PrefManager(SearchNearMeActivity.this);
        setHostelListUsingLatLong(prefManager.getType(), prefManager.getLati(), prefManager.getLongi(),
                prefManager.getDistance());

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    protected synchronized void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      /*  mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        /*Log.e( "list: ", hostelList.toString() );
        for (int i=0; i<hostelList.size(); i++)
        {
            hostelList.get(i).get("latitude");
            Log.e( "onMapReady: ", hostelList.get(i).get("latitude"));
        }*/
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e( "onLocationChanged: ", String.valueOf(location));
        Toast.makeText(this, "this", Toast.LENGTH_SHORT).show();

    }


    private void setHostelListUsingLatLong(String type, String lati, String longi, String range) {
     String  tag_string_req = "HostelListRequest";

        hostelList = new ArrayList<HashMap<String, String>>();

        final ProgressDialog loading = ProgressDialog.show(this, null, "Loading List, Please wait...", false, false);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ApiConfig.urlHostelList, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Hostel Response", response);
                //Disimissing the progress dialog
                loading.dismiss();
                try {
                    JSONArray responseArr = new JSONArray(response);

                    Log.e( "chk: ", String.valueOf(responseArr.length()));

                    if(responseArr.length()==0)
                    {
                        dialogForHostelNotAvailable();                    }
                    else {


                        hostelList.clear();
                        // Parsing json
                        for (int i = 0; i < responseArr.length(); i++) {

                            JSONObject obj = responseArr.getJSONObject(i);
                            HashMap<String, String> listItem = new HashMap<String, String>();

                            listItem.put("hostelId", obj.getString("hostelId"));
                            listItem.put("name", obj.getString("name"));
                            listItem.put("picture", ApiConfig.urlHostelsImage + obj.getString("picture"));
                            listItem.put("location", obj.getString("location"));
                            listItem.put("room_count", obj.getString("room_count"));
                            listItem.put("total", obj.getString("total"));
                            listItem.put("availability", obj.getString("availability"));
                            listItem.put("occupancy", obj.getString("occupancy"));
                            listItem.put("chargesDateTime", obj.getString("chargesDateTime"));
                            listItem.put("charges", obj.getString("charges"));
                            listItem.put("latitude", obj.getString("latitude"));
                            listItem.put("longitude", obj.getString("longitude"));

                            createMarker(Double.parseDouble(obj.getString("latitude")),
                                    Double.parseDouble(obj.getString("longitude")),
                                    obj.getString("name"), obj.getString("name"), R.drawable.ic_add_black_24dp);

                            latLng1  = new LatLng(Double.parseDouble(obj.getString("latitude")),
                                    Double.parseDouble(obj.getString("longitude")));

                            //moveToCurrentLocation(latLng1);


                            //drawMarker(latLng1,obj.getString("name") );

                            hostelList.add(listItem);
                        }

                        latLng1  = new LatLng(Double.parseDouble(prefManager.getLati()), Double.parseDouble(prefManager.getLongi()));

                        moveToCurrentLocation(latLng1);
                        //  CameraUpdateFactory.zoomIn();

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                for (int i=0; i< hostelList.size(); i++)
                                {
                                    if(hostelList.get(i).get("name").equals(marker.getTitle()))
                                    {
                                        Bundle bundle=new Bundle();
                                        bundle.putString("hostelId",hostelList.get(i).get("hostelId") );
                                        bundle.putString("name",hostelList.get(i).get("name") );
                                        bundle.putString("picture",hostelList.get(i).get("picture") );
                                        bundle.putString("availability",hostelList.get(i).get("availability") );
                                        Utility.launchActivity(SearchNearMeActivity.this, HostelDetailActivity.class,
                                                false, bundle);
                                        //  Toast.makeText(SearchNearMeActivity.this, "mm"+hostelList.get(i).get("hostelId"), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                return false;
                            }
                        });
                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                 //   Log.e(tag_string_req, e.getMessage());
                }
                // notifying list adapter about data changes
                // so that it renders the list view with updated data


                loading.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Registration Error: ",  error.getMessage());
                //Dismissing the progress dialog
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "onErrorResponse" + error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();


                if (prefManager.getAREA_SELECTED().equals("area1")) {
                    params.put("id", "3");
                    params.put("type", type);
                    params.put("lati", lati);
                    params.put("longi", longi);
                    params.put("range", range);

                    //  params.put("id",session.getUserID());
                } else if (prefManager.getAREA_SELECTED().equals("details")) {
                    params.put("id", "4");
                    params.put("type", type);
                    params.put("mobile", prefManager.getMOBILE_SELECTED());
                    params.put("lati", lati);
                    params.put("longi", longi);
                    params.put("range", range);

                }


                Log.e("Register Params: ", params.toString());

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        Log.e( "createMarker: ", String.valueOf(latitude));
        Log.e( "nnn: ", title);
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluemarker)));
    }

  /*  protected void createMarker(double latitude, double longitude, String title, String snippet, String path) {

        try {
            Bitmap bmImg = null;
          //  if (path.length()>0) {

                bmImg = Ion.with(this)
                       // .load(Constants.URL_USER_PROFILE_PIC + path).asBitmap().get();
                        .load("http://iysonline.club/iys/api/attachments/sliderimages/25463-Gandhinagar%20to%20Site.png").asBitmap().get();


                int height = 100;
                int width = 100;

                Bitmap smallMarker = Bitmap.createScaledBitmap(bmImg, width, height, false);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            *//*}else {
                int height = 100;
                int width = 100;
                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.male_avatar);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet))
                        //.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_circle));
                        .setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            }*//*


        } catch (ExecutionException e) {
            e.printStackTrace();
          //  Log.e(TAG, "createMarker: error" + e.getMessage());

        } catch (InterruptedException e) {
            e.printStackTrace();
           // Log.e(TAG, "createMarker: error" + e.getMessage());

        }
    }
*/

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,12));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

    }

    private void drawMarker(LatLng point, String text) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point).title(text).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        mMap.addMarker(markerOptions);
        builder.include(markerOptions.getPosition());

    }


    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
                int position = (int)(marker.getTag());
                Toast.makeText(SearchNearMeActivity.this, "nnn ", Toast.LENGTH_SHORT).show();
                //Using position get Value from arraylist
                return false;

    }

    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }

    private void dialogForHostelNotAvailable( ) {
        // this.correct = correct;
        final Dialog resultbox = new Dialog(SearchNearMeActivity.this);
        resultbox.setContentView(R.layout.delete_subject_dialog);
        // resultbox.setCanceledOnTouchOutside(false);
        Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);
        Button btn_cancel = (Button) resultbox.findViewById(R.id.btn_resume);
        btn_finish.setText("Ok");

        TextView text_title =  resultbox.findViewById(R.id.text_title);
        text_title.setText("Hostels not available in this area...");
        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();

            }

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
                //resultbox.cancel();
            }
        });

        resultbox.show();
    }


}
