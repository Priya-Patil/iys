package com.netist.mygirlshostel.adv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netist.mygirlshostel.adv.model.SliderImageModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdvertisementServices {

    private static final String TAG = AdvertisementServices.class.getSimpleName();

    private Context context;

    public AdvertisementServices(Context context) {
        this.context = context;
    }

    @SuppressLint("StaticFieldLeak")
    private static AdvertisementServices instance;

    public static AdvertisementServices getInstance(Context context) {
        if (instance == null) {
            instance = new AdvertisementServices(context);
        }
        return instance;
    }

    public void fetchAdvertise( final ApiStatusCallBack apiStatusCallBack) {

        //{"type":2,"Action":3,"Classid":3}
        //{"type":2,"Action":4,"Classid":15} Added className & boardName
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", 2);
                jsonObject.put("Action", 1);
                //jsonObject.put("Classid", Classid);
               // jsonObject.put("Subjectid", Subjectid);
            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post("http://iysonline.club/iys/api/processes/images.php/")
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                TypeToken<ArrayList<AttachmentModel>> token = new TypeToken<ArrayList<AttachmentModel>>() {
                                };
                                ArrayList<AttachmentModel> notesPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("HWModel", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("HWerror", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("HW:anError", "" + anError);
                            Log.e("HW:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }

    public void fetchAdvertiseForUser( int Userid, final ApiStatusCallBack apiStatusCallBack) {

        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", 2);
                jsonObject.put("Action", 2);
                jsonObject.put("Userid", Userid);
               // jsonObject.put("Subjectid", Subjectid);
            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post("http://iysonline.club/iys/api/processes/images.php/")
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                TypeToken<ArrayList<AttachmentModel>> token = new TypeToken<ArrayList<AttachmentModel>>() {
                                };
                                ArrayList<AttachmentModel> notesPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("HWModel", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("HWerror", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("HW:anError", "" + anError);
                            Log.e("HW:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }

    public void fetchSliderImages( final ApiStatusCallBack apiStatusCallBack) {

        //{"type":2,"Action":3, "Productid":5}
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", 2);
                jsonObject.put("Action", 3);

                // jsonObject.put("Subjectid", Subjectid);
            } catch (Exception e) {
                Log.e("JSONOBJECTerr", "" + e);
                apiStatusCallBack.onUnknownError(e);
            }

            AndroidNetworking.post("http://iysonline.club/iys/api/processes/images.php/")
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                TypeToken<ArrayList<SliderImageModel>> token = new TypeToken<ArrayList<SliderImageModel>>() {
                                };
                                ArrayList<SliderImageModel> notesPackages = new Gson().fromJson(response.toString(), token.getType());
                                Log.e("productImages", "" + notesPackages);
                                apiStatusCallBack.onSuccess(notesPackages);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("productImages", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("productImages:anError", "" + anError);
                            Log.e("productImages:anError", "" + anError.getErrorBody());
                            apiStatusCallBack.onError(anError);
                        }
                    });

        } catch (Exception ex) {
            Log.e("onUnknownError", "" + ex);
            apiStatusCallBack.onUnknownError(ex);
        }
    }



}
