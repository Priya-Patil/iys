package com.netist.mygirlshostel.FCM;

/**
 * Created by Ganesh on 9/27/2017.
 */
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.netist.mygirlshostel.session_handler.SessionHelper;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "HealTreeFirebaseIID";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        //SessionHelper.getInstance(getApplicationContext()).saveDeviceToken(refreshedToken);
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //saving the token on shared preferences
        SessionHelper.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}