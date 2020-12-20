package com.netist.mygirlshostel.adv;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.netist.mygirlshostel.MainActivity;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;

import java.util.ArrayList;

public class AdvertisementListActivity extends AppCompatActivity {

    private static final String TAG = AdvertisementListActivity.class.getSimpleName() ;
    ProgressDialog progressDialog;
    RecyclerView rv_attachment, rv_account;

    ImageView iv_backprofile, iv_home;


    private ArrayList<AttachmentModel> mList = new ArrayList<>();
    AttachmentListAdapter attachmentListAdapter;
    AccountListAdapter accountListAdapter;
    private SessionHelper session;
    Button btn_request,btn_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_list);
        progressDialog=new ProgressDialog(AdvertisementListActivity.this);
        rv_attachment = findViewById(R.id.rv_attachment);
        iv_backprofile = findViewById(R.id.iv_backprofile);
        iv_home = findViewById(R.id.iv_home);
        rv_account = findViewById(R.id.rv_account);
        btn_request = findViewById(R.id.btn_request);
        btn_account = findViewById(R.id.btn_account);

        session = new SessionHelper(this);
        Log.e( "usertypechk: ",session.getUserType() );
        Log.e( "chk: ",session.getUserID() );

        iv_backprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(session.getUserType().equals("admin"))
                {
                    Utility.launchActivity(AdvertisementListActivity.this, MainActivity.class, true);

                }
                else {
                    Utility.launchActivity(AdvertisementListActivity.this, AdvHomeActivity.class, true);

                }
            }
        });
        if(session.getUserType().equals("adv"))
        {
            AttachmentListForUser(Integer.parseInt(session.getUserID()));
            AccountListForUser(Integer.parseInt(session.getUserID()));

        }
        else
        {
            AttachmentListForAdmin();
            AccountListForAdmin();

        }

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rv_attachment.setVisibility(View.VISIBLE);
                rv_account.setVisibility(View.GONE);
                btn_request.setVisibility(View.GONE);
                btn_account.setVisibility(View.VISIBLE);

            }
        });
          btn_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        rv_attachment.setVisibility(View.GONE);
                        rv_account.setVisibility(View.VISIBLE);

                        btn_request.setVisibility(View.VISIBLE);
                        btn_account.setVisibility(View.GONE);


                    }
                });


    }

    private void AttachmentListForAdmin() {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                // progressDialog.setProgressStyle(R.id.abbreviationsBar);
                progressDialog.show();

                Log.e(TAG, "LoginUser: ");

                AdvertisementServices.getInstance(getApplicationContext()).
                        fetchAdvertise( new ApiStatusCallBack<ArrayList<AttachmentModel>>() {
                            @Override
                            public void onSuccess(ArrayList<AttachmentModel> classModels) {
                                progressDialog.dismiss();
                                Log.e( "onSuccess: ", classModels.toString() );
                                BindList(classModels);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "ANError " + anError.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(AdvertisementListActivity.this,"No Attachment Found",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onUnknownError(Exception e) {
                                progressDialog.dismiss();
                                Log.e(TAG, "exc " + e.getMessage());
                                //Utility.showErrorMessage(HomeworkDetailActivity.this, e.getMessage(), Snackbar.LENGTH_LONG);

                            }

                        });
            } else {
                Toast.makeText(AdvertisementListActivity.this,"Could not connect to the internet",Toast.LENGTH_SHORT).show();

                //Utility.showErrorMessage(HomeworkDetailActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //  lyt_progress_reg.setVisibility(View.GONE);
            progressDialog.dismiss();
            Toast.makeText(AdvertisementListActivity.this, "Something went wrong !! "+ex.getMessage(),Toast.LENGTH_SHORT).show();

            //Utility.showErrorMessage(HomeworkDetailActivity.this, ex.getMessage());
        }
    }
    private void AccountListForAdmin() {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                // progressDialog.setProgressStyle(R.id.abbreviationsBar);
                progressDialog.show();

                Log.e(TAG, "LoginUser: ");

                AdvertisementServices.getInstance(getApplicationContext()).
                        fetchAdvertise( new ApiStatusCallBack<ArrayList<AttachmentModel>>() {
                            @Override
                            public void onSuccess(ArrayList<AttachmentModel> classModels) {
                                progressDialog.dismiss();
                                Log.e( "onSuccess: ", classModels.toString() );
                                AccountList(classModels);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "ANError " + anError.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(AdvertisementListActivity.this,"No Attachment Found",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onUnknownError(Exception e) {
                                progressDialog.dismiss();
                                Log.e(TAG, "exc " + e.getMessage());
                                //Utility.showErrorMessage(HomeworkDetailActivity.this, e.getMessage(), Snackbar.LENGTH_LONG);

                            }

                        });
            } else {
                Toast.makeText(AdvertisementListActivity.this,"Could not connect to the internet",Toast.LENGTH_SHORT).show();

                //Utility.showErrorMessage(HomeworkDetailActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //  lyt_progress_reg.setVisibility(View.GONE);
            progressDialog.dismiss();
            Toast.makeText(AdvertisementListActivity.this, "Something went wrong !! "+ex.getMessage(),Toast.LENGTH_SHORT).show();

            //Utility.showErrorMessage(HomeworkDetailActivity.this, ex.getMessage());
        }
    }
    private void AttachmentListForUser(int Userid) {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                // progressDialog.setProgressStyle(R.id.abbreviationsBar);
                progressDialog.show();

                Log.e(TAG, "LoginUser: ");

                AdvertisementServices.getInstance(getApplicationContext()).
                        fetchAdvertiseForUser(Userid, new ApiStatusCallBack<ArrayList<AttachmentModel>>() {
                            @Override
                            public void onSuccess(ArrayList<AttachmentModel> classModels) {
                                Log.e( "user: ",classModels.toString() );
                                progressDialog.dismiss();
                                BindList(classModels);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "ANError " + anError.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(AdvertisementListActivity.this,"No Attachment Found",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onUnknownError(Exception e) {
                                progressDialog.dismiss();
                                Log.e(TAG, "exc " + e.getMessage());
                                //Utility.showErrorMessage(HomeworkDetailActivity.this, e.getMessage(), Snackbar.LENGTH_LONG);

                            }

                        });
            } else {
                Toast.makeText(AdvertisementListActivity.this,"Could not connect to the internet",Toast.LENGTH_SHORT).show();

                //Utility.showErrorMessage(HomeworkDetailActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //  lyt_progress_reg.setVisibility(View.GONE);
            progressDialog.dismiss();
            Toast.makeText(AdvertisementListActivity.this, "Something went wrong !! "+ex.getMessage(),Toast.LENGTH_SHORT).show();

            //Utility.showErrorMessage(HomeworkDetailActivity.this, ex.getMessage());
        }
    }
    private void AccountListForUser(int Userid) {
        try {
            if (Utility.isNetworkAvailable(getApplicationContext())) {

                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                // progressDialog.setProgressStyle(R.id.abbreviationsBar);
                progressDialog.show();

                Log.e(TAG, "LoginUser: ");

                AdvertisementServices.getInstance(getApplicationContext()).
                        fetchAdvertiseForUser(Userid, new ApiStatusCallBack<ArrayList<AttachmentModel>>() {
                            @Override
                            public void onSuccess(ArrayList<AttachmentModel> classModels) {
                                Log.e( "user: ",classModels.toString() );
                                progressDialog.dismiss();
                                AccountList(classModels);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "ANError " + anError.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(AdvertisementListActivity.this,"No Attachment Found",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onUnknownError(Exception e) {
                                progressDialog.dismiss();
                                Log.e(TAG, "exc " + e.getMessage());
                                //Utility.showErrorMessage(HomeworkDetailActivity.this, e.getMessage(), Snackbar.LENGTH_LONG);

                            }

                        });
            } else {
                Toast.makeText(AdvertisementListActivity.this,"Could not connect to the internet",Toast.LENGTH_SHORT).show();

                //Utility.showErrorMessage(HomeworkDetailActivity.this, "Could not connect to the internet");
            }
        } catch (Exception ex) {
            //  lyt_progress_reg.setVisibility(View.GONE);
            progressDialog.dismiss();
            Toast.makeText(AdvertisementListActivity.this, "Something went wrong !! "+ex.getMessage(),Toast.LENGTH_SHORT).show();

            //Utility.showErrorMessage(HomeworkDetailActivity.this, ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void BindList(final ArrayList<AttachmentModel> mUserList) {
        try {
            progressDialog.dismiss();
            //rv_attachment.setVisibility(View.VISIBLE);

            setTitle("NotesPackages (" + mUserList.size() + ")");

            //Utility.showErrorMessage(this, "Bind list", Snackbar.LENGTH_LONG);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdvertisementListActivity.this);
            //rv_attachment.setLayoutManager(mLayoutManager);
            rv_attachment.setLayoutManager(new LinearLayoutManager(AdvertisementListActivity.this,
                    LinearLayoutManager.VERTICAL, false));
            rv_attachment.setItemAnimator(new DefaultItemAnimator());
            rv_attachment.setHasFixedSize(true);

            attachmentListAdapter = new AttachmentListAdapter(AdvertisementListActivity.this, mUserList,
                    new AttachmentListAdapter.ItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                         /*   Bundle bundle=new Bundle();
                            bundle.putInt("Classid", mUserList.get(position).getClassid());
                            bundle.putString("Classname", mUserList.get(position).getClassname());
                            Utility.launchActivity(HomeworkListActivity.this, AdminHomeActivity.class, false, bundle);
                       */   //  Log.e("item", String.valueOf(mNotesPackages.get(position).getArrayList().get(position).getUsername()));

                        }
                    });
            rv_attachment.setAdapter(attachmentListAdapter);
            attachmentListAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            //Utility.showErrorMessage(this, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }
    private void AccountList(final ArrayList<AttachmentModel> accountlist) {
        try {
            progressDialog.dismiss();
            //rv_attachment.setVisibility(View.VISIBLE);

            setTitle("NotesPackages (" + accountlist.size() + ")");

            //Utility.showErrorMessage(this, "Bind list", Snackbar.LENGTH_LONG);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdvertisementListActivity.this);
            //rv_attachment.setLayoutManager(mLayoutManager);
            rv_account.setLayoutManager(new LinearLayoutManager(AdvertisementListActivity.this,
                    LinearLayoutManager.VERTICAL, false));
            rv_account.setItemAnimator(new DefaultItemAnimator());
            rv_account.setHasFixedSize(true);

            accountListAdapter = new AccountListAdapter(AdvertisementListActivity.this, accountlist,
                    new AccountListAdapter.ItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                         /*   Bundle bundle=new Bundle();
                            bundle.putInt("Classid", mUserList.get(position).getClassid());
                            bundle.putString("Classname", mUserList.get(position).getClassname());
                            Utility.launchActivity(HomeworkListActivity.this, AdminHomeActivity.class, false, bundle);
                       */   //  Log.e("item", String.valueOf(mNotesPackages.get(position).getArrayList().get(position).getUsername()));

                        }
                    });
            rv_account.setAdapter(accountListAdapter);
            accountListAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            //Utility.showErrorMessage(this, ex.getMessage(), Snackbar.LENGTH_LONG);
        }
    }

}
