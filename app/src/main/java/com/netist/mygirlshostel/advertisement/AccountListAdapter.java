package com.netist.mygirlshostel.advertisement;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.netist.mygirlshostel.R;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;


public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<AttachmentModel> attachmentModels;
    private  ItemClickListener itemClickListener;
    private Activity activity;
    PrefManager prefManager;
    ProgressDialog progressDialog;
    private SessionHelper session;

    public AccountListAdapter(Activity activity, ArrayList<AttachmentModel> attachmentModels, ItemClickListener itemClickListener){

        this.activity = activity;
        this.attachmentModels =   attachmentModels;
        this.itemClickListener=itemClickListener;
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.item_account_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        prefManager = new PrefManager(activity);
        progressDialog = new ProgressDialog(activity);
        session = new SessionHelper(activity);

        return new MyViewHolder(view);
        
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AttachmentModel item=attachmentModels.get(position);

        holder.tv_topicName.setText(item.getName());
        holder.tv_details.setText(item.getTitle());
        holder.tv_date.setText("Date: "+item.getCreated());
        holder.tv_amount.setText("Amount: "+item.getAmount());
        holder.tv_mobile.setText("Mobile: "+item.getMobile());

        String profile_path;
        Log.e( "onBindViewHolder: ", item.toString());

        profile_path = "http://iysonline.club/iys/api/attachments/sliderimages/" + item.getImages();

        Picasso.with(activity).load(profile_path).into(holder.img_title);

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Bundle bundle= new Bundle();
                bundle.putParcelable("AttachementItem",item);*/

                Intent intent = new Intent(activity,EditAdvertisementActivity.class);
                intent.putExtra("id",String.valueOf(item.getId()));
                intent.putExtra("Userid",String.valueOf(item.getUserid()));
                intent.putExtra("title",item.getTitle());
                intent.putExtra("details",item.getDetails());
                intent.putExtra("imageName",item.getImages());
                intent.putExtra("expiryDate",item.getExpirydate());
                intent.putExtra("profile_path",profile_path);
                activity.startActivity(intent);

            }
        });

                if(item.getIsapprove()==1)
                {
                    holder.tv_approve.setText("Approved");
                }
                else
                {


                    if(session.getUserType().equals("admin"))
                    {
                        holder.tv_approve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.e("Attachment", " "+item.getId());
                                //Change parameter
                                //String imgpath,String Title ,String Details , int Userid,String Expirydate
                                Log.e("AttachAdapter "," "+item.getId()+" "+profile_path+" "+item.getTitle()+" "+item.getDetails()+" "+item.getUserid()+" "+item.getExpirydate());
                                dialogForApproveImage(item.getId(),item.getImages(),item.getTitle(),
                                        item.getDetails(),item.getUserid(),item.getExpirydate(),
                                        item.getIsapprove(), String.valueOf(item.getAmount()));
                            }
                        });
                    }


                }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Attachment", " "+item.getId());
                    //Change parameter
                    //String imgpath,String Title ,String Details , int Userid,String Expirydate
                    Log.e("AttachAdapter "," "+item.getId()+" "+profile_path+" "+item.getTitle()+" "+item.getDetails()+" "+item.getUserid()+" "+item.getExpirydate());
                    dialogForDeleteImage(item.getId(),profile_path,item.getTitle(),item.getDetails(),item.getUserid(),item.getExpirydate());
                }
            });



    }

    @Override
    public int getItemCount() {
        return   attachmentModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_details,tv_topicName,tv_date, tv_approve, tv_amount, tv_mobile;
        ImageView img_title,iv_delete,iv_edit;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_topicName = (TextView) itemView.findViewById(R.id.tv_topicName);
            tv_details = (TextView) itemView.findViewById(R.id.tv_details);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            img_title =  itemView.findViewById(R.id.img_title);
            iv_edit =  itemView.findViewById(R.id.iv_edit);
            iv_delete =  itemView.findViewById(R.id.iv_delete);
            tv_approve =  itemView.findViewById(R.id.tv_approve);
            tv_amount =  itemView.findViewById(R.id.tv_amount);
            tv_mobile =  itemView.findViewById(R.id.tv_mobile);

            itemView.setOnClickListener(this); // bind the listener
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onClick(view, getAdapterPosition());
        }
    }

    //region Search Filter (setFilter Code)
    public void setFilter(ArrayList<AttachmentModel> newList) {
          attachmentModels = new ArrayList<>();
          attachmentModels.addAll(newList);
        notifyDataSetChanged();
    }

    private void dialogForDeleteImage(int id,String imgpath,String Title ,String Details , int Userid,String Expirydate ) {
        // this.correct = correct;
        final Dialog resultbox = new Dialog(activity);
        resultbox.setContentView(R.layout.delete_subject_dialog);
        // resultbox.setCanceledOnTouchOutside(false);
        Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);
        Button btn_cancel = (Button) resultbox.findViewById(R.id.btn_resume);
        TextView text_title =  resultbox.findViewById(R.id.text_title);
        text_title.setText(" Are you sure you want to delete this Attachment ? ");
        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Utility.isNetworkAvailable(activity)) {

                    /**********Delete Code****************/

                    AndroidNetworking.upload("http://iysonline.club/iys/api/processes/images.php/")
                            // .addFileToUpload("", "certificate") //Adding file
                            .addMultipartParameter("type", "1")
                            .addMultipartParameter("Action", "0")
                            .addMultipartParameter("ImagesId", String.valueOf(id))
                            .addMultipartParameter("Title", Title)
                            .addMultipartParameter("Details",Details)
                            .addMultipartParameter("images",imgpath)
                            .addMultipartParameter("Userid", String.valueOf(Userid))
                            .addMultipartParameter("Expirydate", Expirydate)
                            .addMultipartParameter("Isapprove", "0")
                            .addMultipartParameter("Amount", "0")
                            .addMultipartParameter("LogedinUserId", "1")
                            .setTag("uploadTest")
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                    resultbox.cancel();
                                    Log.e("adapter1", "uploadImage: totalBytes: " + totalBytes);
                                    Log.e("adapter1", "uploadImage: bytesUploaded: " + bytesUploaded);
                                    progressDialog.setMessage("Deleting File, Please wait...");
                                    progressDialog.show();
                                    progressDialog.setCancelable(false);
                                }
                            })
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressDialog.hide();
                                    Log.e("adapter1", "FileonRes: " + response.toString());
                                    //Toast.makeText(activity,"Deleted Succesfully ",Toast.LENGTH_LONG).show();
                                    //Intent intent= new Intent(activity,AdvertisementListActivity.class);
                                    //startActivity(intent);
                                    //rl_img_delete.setVisibility(View.GONE);
                                    resultbox.cancel();
                                    activity.finish();
                                    activity.overridePendingTransition( 0, 0);
                                    activity.startActivity(activity.getIntent());
                                    activity.overridePendingTransition( 0, 0);
                                }

                                @Override
                                public void onError(ANError error) {
                                    resultbox.cancel();
                                    Log.e("adapter1", "FileonError: ", error);
                                    progressDialog.hide();
                                }
                            });
                } else {
                    resultbox.cancel();
                    Toast.makeText(activity, "No Internet Connection",Toast.LENGTH_SHORT).show();                }
            }

            ;

            /**************************************/
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();
            }
        });

        resultbox.show();
    }
    private void dialogForApproveImage(int id,String imgpath,String Title ,String Details, int Userid,
                                       String Expirydate, int Isapprove, String Amount ) {
        // this.correct = correct;
        final Dialog resultbox = new Dialog(activity);
        resultbox.setContentView(R.layout.delete_subject_dialog);
        // resultbox.setCanceledOnTouchOutside(false);
        Button btn_finish = (Button) resultbox.findViewById(R.id.btn_finish);
        Button btn_cancel = (Button) resultbox.findViewById(R.id.btn_resume);
        btn_finish.setText("Approve");

        TextView text_title =  resultbox.findViewById(R.id.text_title);
        text_title.setText(" Are you sure you want to Approve this ? ");
        btn_finish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Utility.isNetworkAvailable(activity)) {

                    /**********Delete Code****************/

                    AndroidNetworking.upload("http://iysonline.club/iys/api/processes/images.php/")
                            // .addFileToUpload("", "certificate") //Adding file
                            .addMultipartParameter("type", "1")
                            .addMultipartParameter("Action", "1")
                            .addMultipartParameter("ImagesId", String.valueOf(id))
                            .addMultipartParameter("Title", Title)
                            .addMultipartParameter("Details",Details)
                            .addMultipartParameter("images",imgpath)
                            .addMultipartParameter("Userid", String.valueOf(Userid))
                            .addMultipartParameter("Expirydate", Expirydate)
                            .addMultipartParameter("Isapprove", "1")
                            .addMultipartParameter("Amount", Amount)
                            .addMultipartParameter("LogedinUserId", "1")
                            .setTag("uploadTest")
                            .setPriority(Priority.HIGH)
                            .build()
                            .setUploadProgressListener(new UploadProgressListener() {
                                @Override
                                public void onProgress(long bytesUploaded, long totalBytes) {
                                    resultbox.cancel();
                                    Log.e("adapter1", "uploadImage: totalBytes: " + totalBytes);
                                    Log.e("adapter1", "uploadImage: bytesUploaded: " + bytesUploaded);
                                    progressDialog.setMessage("Deleting File, Please wait...");
                                    progressDialog.show();
                                    progressDialog.setCancelable(false);
                                }
                            })
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressDialog.hide();
                                    Log.e("adapter1", "FileonRes: " + response.toString());
                                    //Toast.makeText(activity,"Deleted Succesfully ",Toast.LENGTH_LONG).show();
                                    //Intent intent= new Intent(activity,AdvertisementListActivity.class);
                                    //startActivity(intent);
                                    //rl_img_delete.setVisibility(View.GONE);
                                    resultbox.cancel();
                                    activity.finish();
                                    activity.overridePendingTransition( 0, 0);
                                    activity.startActivity(activity.getIntent());
                                    activity.overridePendingTransition( 0, 0);
                                }

                                @Override
                                public void onError(ANError error) {
                                    resultbox.cancel();
                                    Log.e("adapter1", "FileonError: ", error);
                                    progressDialog.hide();
                                }
                            });
                } else {
                    resultbox.cancel();
                    Toast.makeText(activity, "No Internet Connection",Toast.LENGTH_SHORT).show();                }
            }

            ;

            /**************************************/
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                resultbox.cancel();
            }
        });

        resultbox.show();
    }

}
