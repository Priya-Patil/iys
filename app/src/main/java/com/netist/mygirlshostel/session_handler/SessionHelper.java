package com.netist.mygirlshostel.session_handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;



public class SessionHelper {
    // LogCat tag
    private static String TAG = SessionHelper.class.getSimpleName();

    // Shared Preferences
    static SharedPreferences mInstance;

    static SessionHelper mSessionHelper;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;


    // Shared preferences file name
    private static final String PREF_NAME = "My Girls Hostel";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_USER_TYPE = "user_type";

    private static final String KEY_USER_ID = "user_id";

    private static final String KEY_USER_LATITUDE = "user_latitude";

    private static final String KEY_USER_LONGITUDE = "user_longitude";

    private static final String KEY_USER_NAME = "user_name";

    private static final String KEY_USER_AGE = "user_age";

    //private static final String KEY_USER_GENDER = "user_gender";

    private static final String KEY_USER_EMAIL = "user_email";

    private static final String KEY_USER_MOBILE = "user_mobile";

    private static final String KEY_USER_IMAGE = "user_image";

    private static final String KEY_USER_DISTANCE = "user_distance";

    private static final String KEY_USER_NEARBY = "user_nearby";

    private static final String KEY_USER_LOCATION_MODE = "user_location_mode";


    private static final String TAG_TOKEN = "mygirlshostel_app_fcm_token";

    private static final String KEY_IS_TOKEN_UPDATED = "isTokenUpdated";

    public SessionHelper(Context context) {
        this._context = context;
        mInstance = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mInstance.edit();

        if(!mInstance.contains(KEY_IS_LOGGEDIN)) {
            editor.putBoolean(KEY_IS_LOGGEDIN, false);
        }
        if(!mInstance.contains(TAG_TOKEN)) {
            editor.putString(TAG_TOKEN, "No Token");
        }
        if(!mInstance.contains(KEY_IS_TOKEN_UPDATED)) {
            editor.putBoolean(KEY_IS_TOKEN_UPDATED, false);
        }
        editor.commit();
    }



    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setLogout() {

        editor.clear();
        editor.commit();

        Log.d(TAG, "User logout from session!");
    }

    public boolean isLoggedIn(){
        return mInstance.getBoolean(KEY_IS_LOGGEDIN, false);
    }


    public void setTokenUpdated(boolean isTokenUpdated) {

        editor.putBoolean(KEY_IS_TOKEN_UPDATED, isTokenUpdated);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isTokenUpdated(){
        return mInstance.getBoolean(KEY_IS_TOKEN_UPDATED, false);
    }


    /********** Set Functions ************/

    public void setUserType(String data_info) {
        editor.putString(KEY_USER_TYPE, data_info);
        editor.commit();
    }
    public void setUserID(String data_info) {
        editor.putString(KEY_USER_ID, data_info);
        editor.commit();
    }
    public void setUserName(String data_info) {
        editor.putString(KEY_USER_NAME, data_info);
        editor.commit();
    }
    public void setUserAge(String data_info) {
        editor.putString(KEY_USER_AGE, data_info);
        editor.commit();
    }

    /*
    public void setUserGender(String data_info) {
        editor.putString(KEY_USER_GENDER, data_info);
        editor.commit();
    }*/
    public void setUserEmail(String data_info) {
        editor.putString(KEY_USER_EMAIL, data_info);
        editor.commit();
    }
    public void setUserMobile(String data_info) {
        editor.putString(KEY_USER_MOBILE, data_info);
        editor.commit();
    }
    public void setUserImage(String data_info) {
        editor.putString(KEY_USER_IMAGE, data_info);
        editor.commit();
    }

    public void setUserLatitude(float data_info) {
        editor.putFloat(KEY_USER_LATITUDE, data_info);
        editor.commit();
    }

    public void setUserLongitude(float data_info) {
        editor.putFloat(KEY_USER_LONGITUDE, data_info);
        editor.commit();
    }

    public void setUserDistance(int data_info) {
        editor.putInt(KEY_USER_DISTANCE, data_info);
        editor.commit();
    }

    public void setUserNearby(boolean data_info) {
        editor.putBoolean(KEY_USER_NEARBY, data_info);
        editor.commit();
    }

    public void setUserLocationMode(boolean data_info) {
        editor.putBoolean(KEY_USER_LOCATION_MODE, data_info);
        editor.commit();
    }

    /********** Get Functions ************/

    public String getUserType() {
        return mInstance.getString(KEY_USER_TYPE,"");
    }

    public float getUserLatitude()
    {
        return mInstance.getFloat(KEY_USER_LATITUDE, 0.0f);
    }

    public float getUserLongitude()
    {
        return mInstance.getFloat(KEY_USER_LONGITUDE, 0.0f);
    }

    public int getUserDistance()
    {
        return mInstance.getInt(KEY_USER_DISTANCE, 0);
    }

    public boolean getUserNearby()
    {
        return mInstance.getBoolean(KEY_USER_NEARBY, false);
    }

    public boolean getUserLocationMode()
    {
        return mInstance.getBoolean(KEY_USER_LOCATION_MODE, false);
    }

    public String getUserID() {
        return mInstance.getString(KEY_USER_ID,"");
    }
    public String getUserName() {
        return mInstance.getString(KEY_USER_NAME,"");
    }
    public String getUserAge() {
        return mInstance.getString(KEY_USER_AGE,"");
    }
    /*public String getUserGender() {
        return mInstance.getString(KEY_USER_GENDER,"");
    }*/
    public String getUserEmail() {
        return mInstance.getString(KEY_USER_EMAIL,"");
    }
    public String getUserMobile() {
        return mInstance.getString(KEY_USER_MOBILE,"");
    }
    public String getUserImage() {
        return mInstance.getString(KEY_USER_IMAGE, "");
    }

    /***********FCM Settings**************/


    public static synchronized SessionHelper getInstance(Context context) {
        if (mSessionHelper == null) {
            mSessionHelper = new SessionHelper(context);
        }
        return mSessionHelper;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, null);
    }


}
