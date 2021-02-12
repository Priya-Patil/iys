package com.netist.mygirlshostel.appversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netist.mygirlshostel.adv.ApiStatusCallBack;
import com.netist.mygirlshostel.web_api_handler.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppVersionServices {


    private static final String TAG = AppVersionServices.class.getSimpleName();

    private Context context;

    public AppVersionServices(Context context) {
        this.context = context;
    }

    @SuppressLint("StaticFieldLeak")
    private static AppVersionServices instance;

    public static AppVersionServices getInstance(Context context) {
        if (instance == null) {
            instance = new AppVersionServices(context);
        }
        return instance;
    }

    public void getAppVersion(final ApiStatusCallBack apiStatusCallBack) {
        // {"type":1,"Action":1}
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", 1);
              //  jsonObject.put("Action", 1);

            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post(ApiConfig.urlgetappversion)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.e("responce", "" + response);

                            try {
                                TypeToken<ArrayList<AppVersionModel>> token = new TypeToken<ArrayList<AppVersionModel>>() {
                                };
                                ArrayList<AppVersionModel> subcategorySpinner = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("SubcategorySpinner", "" + subcategorySpinner);
                                apiStatusCallBack.onSuccess(subcategorySpinner);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("NotesPkgs:anError", "" + anError);
                            Log.e("NotesPkgs:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }

                    });
        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }

}