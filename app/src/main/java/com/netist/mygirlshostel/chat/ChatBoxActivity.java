package com.netist.mygirlshostel.chat;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.session_handler.SessionHelper;

public class ChatBoxActivity extends BaseActivity implements View.OnClickListener{

    LinearLayout layout;
    ImageView sendMsgButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    SessionHelper session;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        b = intent.getExtras();

        setTitle(b.getString("chatWithFullName"));
        setContentView(R.layout.activity_chat_box);

        session = new SessionHelper(getApplicationContext());

        layout = (LinearLayout)findViewById(R.id.layout1);
        sendMsgButton = (ImageView)findViewById(R.id.sendMsgButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);



        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://healtreeapp.firebaseio.com/messages/" + session.getUserMobile().trim() + "_" + b.getString("chatWith"));
        reference2 = new Firebase("https://healtreeapp.firebaseio.com/messages/" + b.getString("chatWith") + "_" + session.getUserMobile().trim());

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();


                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user",session.getUserMobile().trim());

                    Calendar ct = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    map.put("datetime", simpleDateFormat.format(ct.getTime()));
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String datetime = map.get("datetime").toString();

                if(userName.equals(session.getUserMobile().trim())){
                    addMessageBox("You:-\n" + message+"\n\n"+datetime, 1);
                }
                else{
                    addMessageBox(b.getString("chatWithFullName")+":-\n" + message+"\n\n"+datetime, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                String datetime = map.get("datetime").toString();
                if(userName.equals(session.getUserMobile().trim())){
                    addMessageBox("You:\n\n " +message+"\n\n"+datetime , 1);
                }
                else{
                    addMessageBox(userName+":-\n\n" + message+"\n\n"+datetime, 2);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(ChatBoxActivity.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0,100, 10);

        textView.setLayoutParams(lp);

        if(type == 1) {
            lp.setMargins(100, 0, 0, 0);
            textView.setBackgroundResource(R.drawable.ic_chat_receiver);
            textView.setPadding(30, 20, 50, 20);
        }
        else{
            lp.setMargins(0, 0, 100, 0);
            textView.setBackgroundResource(R.drawable.ic_chat_sender);
            textView.setPadding(50, 20, 10, 20);
        }

        messageArea.setText("");
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onClick(View v) {

    }
}
