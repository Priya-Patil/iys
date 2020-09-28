package com.netist.mygirlshostel.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.netist.mygirlshostel.BaseActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.session_handler.SessionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatUsersListActivity extends BaseActivity implements View.OnClickListener{

    ListView usersList;
    TextView noUsersText;
    ArrayList<String> userNameList = new ArrayList<>();
    ArrayList<String> fullNameList = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    SessionHelper session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users_list);

        session = new SessionHelper(getApplicationContext());

        usersList = (ListView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);

        pd = new ProgressDialog(ChatUsersListActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        String url = "https://healtreeapp.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.e("Users Response", s);
                doOnSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatUsersListActivity.this);
        rQueue.add(request);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatUsersListActivity.this, ChatBoxActivity.class);
                intent.putExtra("chatWith",userNameList.get(position));
                intent.putExtra("chatWithFullName",fullNameList.get(position));
                startActivity(intent);
            }
        });
    }

    private void RegisterChatUser(final String name, final String mobile)
    {
        String url = "https://mygirlshostel.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://healtreeapp.firebaseio.com/users");

                if(s.equals("null")) {
                    reference.child(mobile).child("password").setValue(mobile);
                    reference.child(mobile).child("fullName").setValue(name);
                    reference.child(mobile).child("role").setValue("a");

                    Toast.makeText(ChatUsersListActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(mobile)) {
                            reference.child(mobile).child("password").setValue(mobile);
                            reference.child(mobile).child("fullName").setValue(name);
                            reference.child(mobile).child("role").setValue("a");

                            Toast.makeText(ChatUsersListActivity.this, "Chat Enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatUsersListActivity.this, "Chat Disabled", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );

            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ChatUsersListActivity.this);
        rQueue.add(request);



    }

    public void doOnSuccess(String s) {

        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            Iterator j = obj.keys();
            String key = "";
            String role="";
            while (j.hasNext()) {
                key = j.next().toString();
                if (key.equals(session.getUserMobile().trim())) {
                    role = obj.getJSONObject(key).getString("role");
                    break;
                }
            }

            while (i.hasNext()) {
                key = i.next().toString();

                if( !key.equals(session.getUserMobile().trim())  )    {
                    //Toast.makeText(getBaseContext(), " " +obj.getJSONObject(key).getString("password"), Toast.LENGTH_LONG).show();
                    if (!(obj.getJSONObject(key).getString("role").trim().equals(role.trim())))
                    {
                        userNameList.add(key);
                        fullNameList.add(obj.getJSONObject(key).getString("fullName"));
                        totalUsers++;
                    }
                }
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        if (totalUsers <= 0) {
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        } else {
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fullNameList));
        }

        pd.dismiss();
    }

    @Override
    public void onClick(View v) {

    }
}
