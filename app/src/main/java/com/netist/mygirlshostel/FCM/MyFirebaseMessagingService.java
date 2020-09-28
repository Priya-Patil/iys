package com.netist.mygirlshostel.FCM;

/**
 * Created by Ganesh on 9/27/2017.
 */
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.netist.mygirlshostel.facilities.FacilityListActivity;
import com.netist.mygirlshostel.MainActivity;
import com.netist.mygirlshostel.NoticeListActivity;
import com.netist.mygirlshostel.view.ViewListActivity;
import com.netist.mygirlshostel.hostel.HostelBookingListActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");


            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());


            String[] titles = (data.getString("title").trim()).split(":");

            Intent intent;
            //creating an intent for the notification
            if(titles[0].trim().equals("Facility"))
                intent= new Intent(getApplicationContext(), FacilityListActivity.class);
            else if(titles[0].trim().equals("Notice"))
                intent = new Intent(getApplicationContext(), NoticeListActivity.class);
            else if(titles[0].trim().equals("View"))
                intent = new Intent(getApplicationContext(), ViewListActivity.class);
            else if(titles[0].trim().equals("Booking")) {
                intent = new Intent(getApplicationContext(), HostelBookingListActivity.class);
                SessionHelper session = new SessionHelper(getApplicationContext());
                if(session.getUserType().equals("user"))
                    intent.putExtra("userId",session.getUserID());
                else
                    intent.putExtra("hostelId", session.getUserID());
            }
            else
                intent = new Intent(getApplicationContext(), MainActivity.class);

            //if there is no image
            if(imageUrl.equals("null")){
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent);
            }else{
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}