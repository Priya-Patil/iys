/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netist.mygirlshostel;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.netist.mygirlshostel.components.DataParser;
import com.netist.mygirlshostel.components.DoubleArrayEvaluator;
import com.netist.mygirlshostel.components.PermissionUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This activity shows how to save the state of a MapFragment when the activity is recreated, like
 * after rotation of the device.
 */
public class GoogleMapActivity extends BaseActivity implements View.OnClickListener{

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    long timestamp;

    /** Default marker position when the activity is first created. */
   // private static final LatLng DEFAULT_MARKER_POSITION = new LatLng(48.858179, 2.294576);

    private static final LatLng DEFAULT_MARKER_POSITION = new LatLng(19.901054, 75.352478);


    /** List of hues to use for the marker */
    private static final float[] MARKER_HUES = new float[]{
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_state_demo);

        final SaveStateMapFragment mapFragment =
                (SaveStateMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            if (bundle.containsKey("position"))
            {
                LatLng position = bundle.getParcelable("position");
                mapFragment.MarkerPoints.add(position);
                mapFragment. MarkerPointsGet.add(position);
                mapFragment.setMarkerPosition(position);
            }
            if (bundle.containsKey("map_setting"))
            {
                ((ViewGroup) findViewById(R.id.rl_top)).setVisibility(View.VISIBLE);
            }
        }

        ((Button) findViewById(R.id.btn_map_set)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((System.currentTimeMillis() - timestamp) / 1000 <= 1)
                    return;

                String address = mapFragment.getMarkerPosition().toString();//getMapAddress();
                Intent intent = new Intent();
                intent.putExtra("MESSAGE",address);
                intent.putExtra("position",mapFragment.getMarkerPosition());
                setResult(10, intent);
                finish();//finishing activity
            }
        });

        ((Button) findViewById(R.id.btn_marker_set)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.moveMarkerToMyLocation();
                timestamp = System.currentTimeMillis();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Extra info about a marker.
     */
    static class MarkerInfo implements Parcelable {

        public static final Parcelable.Creator<MarkerInfo> CREATOR =
                new Parcelable.Creator<MarkerInfo>() {
                    @Override
                    public MarkerInfo createFromParcel(Parcel in) {
                        return new MarkerInfo(in);
                    }

                    @Override
                    public MarkerInfo[] newArray(int size) {
                        return new MarkerInfo[size];
                    }
                };

        float mHue;

        public MarkerInfo(float color) {
            mHue = color;
        }

        private MarkerInfo(Parcel in) {
            mHue = in.readFloat();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(mHue);
        }
    }


    /**
     * Example of a custom {@code MapFragment} showing how the position of a marker and other
     * custom
     * {@link Parcelable}s objects can be saved after rotation of the device.
     * <p>
     * Storing custom {@link Parcelable} objects directly in the {@link Bundle} provided by the
     * {@link #onActivityCreated(Bundle)} method will throw a {@code ClassNotFoundException}. This
     * is due to the fact that this Bundle is parceled (thus losing its ClassLoader attribute at
     * this moment) and unparceled later in a different ClassLoader.
     * <br>
     * A workaround to store these objects is to wrap the custom {@link Parcelable} objects in a
     * new
     * {@link Bundle} object.
     * <p>
     * However, note that it is safe to store {@link Parcelable} objects from the Maps API (eg.
     * MarkerOptions, LatLng, etc.) directly in the Bundle provided by the
     * {@link #onActivityCreated(Bundle)} method.
     */
    public static class SaveStateMapFragment extends SupportMapFragment
            implements OnMarkerClickListener, OnMarkerDragListener, OnMapReadyCallback, GoogleMap.OnMapLoadedCallback,
            ActivityCompat.OnRequestPermissionsResultCallback
    {

        // Bundle keys.
        private static final String OTHER_OPTIONS = "options";

        private static final String MARKER_POSITION = "markerPosition";

        private static final String MARKER_INFO = "markerInfo";

        private LatLng mMarkerPosition = null;
        private GoogleMap mMap = null;
        private Marker marker = null;

        private MarkerInfo mMarkerInfo;

        private boolean mMoveCameraToMarker;

        ArrayList<LatLng> MarkerPoints;
        ArrayList<LatLng> MarkerPointsGet;

        public LatLng getMarkerPosition()
        {
            return mMarkerPosition;
        }

        public void setMarkerPosition(LatLng position)
        {
            mMarkerPosition = position;
        }

        private ProgressDialog loading;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Initializing
            MarkerPoints = new ArrayList<>();
            MarkerPointsGet= new ArrayList<>();
            if (savedInstanceState == null) {
                // Activity created for the first time.
                if (mMarkerPosition == null)
                    mMarkerPosition = DEFAULT_MARKER_POSITION;
                mMarkerInfo = new MarkerInfo(BitmapDescriptorFactory.HUE_RED);
                mMoveCameraToMarker = true;
            } else {
                // Extract the state of the MapFragment:
                // - Objects from the API (eg. LatLng, MarkerOptions, etc.) were stored directly in
                //   the savedInsanceState Bundle.
                // - Custom Parcelable objects were wrapped in another Bundle.

                mMarkerPosition = savedInstanceState.getParcelable(MARKER_POSITION);
                Bundle bundle = savedInstanceState.getBundle(OTHER_OPTIONS);
                mMarkerInfo = bundle.getParcelable(MARKER_INFO);

                mMoveCameraToMarker = false;
            }

            getMapAsync(this);
        }


        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            // All Parcelable objects of the API  (eg. LatLng, MarkerOptions, etc.) can be set
            // directly in the given Bundle.
            outState.putParcelable(MARKER_POSITION, mMarkerPosition);

            // All other custom Parcelable objects must be wrapped in another Bundle. Indeed,
            // failing to do so would throw a ClassNotFoundException. This is due to the fact that
            // this Bundle is being parceled (losing its ClassLoader at this time) and unparceled
            // later in a different ClassLoader.
            Bundle bundle = new Bundle();
            bundle.putParcelable(MARKER_INFO, mMarkerInfo);
            outState.putBundle(OTHER_OPTIONS, bundle);
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            float newHue = MARKER_HUES[new Random().nextInt(MARKER_HUES.length)];
            mMarkerInfo.mHue = newHue;
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(newHue));
            return true;
        }

        @Override
        public void onMapReady(GoogleMap map) {

            loading = ProgressDialog.show(getContext(),"Loading Map","Please wait...",false,false);

            mMap = map;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(mMarkerPosition)
                    .icon(BitmapDescriptorFactory.defaultMarker(mMarkerInfo.mHue))
                    .draggable(true);
            marker = map.addMarker(markerOptions);

            mMap.getUiSettings().setZoomControlsEnabled(true);

            map.setOnMarkerDragListener(this);
            map.setOnMarkerClickListener(this);
            map.setOnMapLoadedCallback(this);

                enableMyLocation();

            if (mMoveCameraToMarker) {
                map.animateCamera(CameraUpdateFactory.newLatLng(mMarkerPosition));
            }
        }

        @Override
        public void onMapLoaded() {
            if (loading != null)
                loading.dismiss();
            Location location = mMap.getMyLocation();
            if (location != null)
                MarkerPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));

            if (MarkerPointsGet.size() > 0){
               Log.e("mmmm",""+MarkerPointsGet.get(0).latitude+" Lo "+MarkerPointsGet.get(0).longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MarkerPointsGet.get(0).latitude, MarkerPointsGet.get(0).longitude), 17.0f));

            }else
            {
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
                }

                catch (Exception e)
                {

                }

            }

            if (MarkerPoints.size() != 2)
                return;
            LatLng origin = MarkerPoints.get(0);
            LatLng dest = MarkerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getUrl(origin, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
                return;
            }

            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted.
                enableMyLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
            }
        }

        /**
         * Enables the My Location layer if the fine location permission has been granted.
         */
        private void enableMyLocation() {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
            } else if (mMap != null) {
                // Access to the location has been granted to the app.
                mMap.setMyLocationEnabled(true);
            }
        }


        @Override
        public void onMarkerDragStart(Marker marker) {
        }

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            mMarkerPosition = marker.getPosition();
        }

        private void moveMarkerToMyLocation()
        {
            Location location = mMap.getMyLocation();
            mMarkerPosition = new LatLng(location.getLatitude(), location.getLongitude());
            double[] startValues = new double[]{mMarkerPosition.latitude, mMarkerPosition.longitude};
            final double[] endValues = new double[]{location.getLatitude(), location.getLongitude()};
            ValueAnimator latLngAnimator = ValueAnimator.ofObject(new DoubleArrayEvaluator(), startValues, endValues);
            latLngAnimator.setDuration(1000);
            latLngAnimator.setInterpolator(new DecelerateInterpolator());
            latLngAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    double[] animatedValue = (double[]) animation.getAnimatedValue();
                    marker.setPosition(new LatLng(animatedValue[0], animatedValue[1]));
                }
            });
            latLngAnimator.start();
        }

        private String getUrl(LatLng origin, LatLng dest) {

            // Origin of route
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest + "&" + sensor;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


            return url;
        }

        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        // Fetches data from url passed
        private class FetchUrl extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... url) {

                // For storing data from web service
                String data = "";

                try {
                    // Fetching the data from web service
                    data = downloadUrl(url[0]);
                    Log.d("Background Task data", data.toString());
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                ParserTask parserTask = new ParserTask();

                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);

            }
        }

        /**
         * A class to parse the Google Places in JSON format
         */
        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    Log.d("ParserTask",jsonData[0].toString());
                    DataParser parser = new DataParser();
                    Log.d("ParserTask", parser.toString());

                    // Starts parsing data
                    routes = parser.parse(jObject);
                    Log.d("ParserTask","Executing routes");
                    Log.d("ParserTask",routes.toString());

                } catch (Exception e) {
                    Log.d("ParserTask",e.toString());
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                if (result == null || result.size() <= 0)
                    return;

                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);

                    Log.d("onPostExecute","onPostExecute lineoptions decoded");

                }

                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions != null) {
                    mMap.addPolyline(lineOptions);
                }
                else {
                    Log.d("onPostExecute","without Polylines drawn");
                }
            }
        }

    }

    /*
    private String getMapAddress()
    {
        try {
            Geocoder geo = new Geocoder(SaveStateDemoActivity.this, Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(SaveStateMapFragment.mMarkerPosition.latitude,
                    SaveStateMapFragment.mMarkerPosition.longitude, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Waiting for Location", Toast.LENGTH_LONG).show();
            }
            else {
                if (addresses.size() > 0) {
                    return addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }

        return "";
    }*/
}

