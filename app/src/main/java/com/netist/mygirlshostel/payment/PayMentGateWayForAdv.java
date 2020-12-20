package com.netist.mygirlshostel.payment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.netist.mygirlshostel.adv.AdvertisementListActivity;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;

import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by varun Kumar on 23/1/16.
 */
public class PayMentGateWayForAdv extends Activity {

    private static final String TAG = "tag";
    private ArrayList<String> post_val = new ArrayList<String>();
    private String post_Data="";
    WebView webView ;
    final Activity activity = this;
    private String tag = "PayMentGateWay";
    private String hash,hashSequence;
    ProgressDialog progressDialog ;

   //  String merchant_key="kYz2vV"; // test
    String merchant_key="10PD3CTF";
    // String salt="zhoXe53j"; // test
    String salt="TsTOxPK2YD";
    String action1 ="";
    //String base_url="https://test.payu.in";
    String base_url="https://secure.payu.in/_payment";

    int error=0;
    String hashString="";
    Map<String,String> params;
    String txnid ="";

    String SUCCESS_URL = "https://www.payumoney.com/mobileapp/payumoney/success.php" ; // failed
    String FAILED_URL = "https://www.payumoney.com/mobileapp/payumoney/failure.php" ;

    Handler mHandler = new Handler();


    static String getFirstName, getNumber, getEmailAddress, getRechargeAmt, getcDate;


    PrefManager prefManager;
    SessionHelper session;
    File path;

    String fullpath,title,details, Userid,expiryDate,amount;

    @SuppressLint({"JavascriptInterface", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(PayMentGateWayForAdv.this);
        prefManager =new PrefManager(PayMentGateWayForAdv.this);
        this.session = new SessionHelper(getApplicationContext());
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        webView = new WebView(this);
        setContentView(webView);
        Intent oIntent  = getIntent();

        getFirstName    = oIntent.getExtras().getString("FIRST_NAME");
        getNumber       = oIntent.getExtras().getString("PHONE_NUMBER");
        getEmailAddress = oIntent.getExtras().getString("EMAIL_ADDRESS");
        getRechargeAmt  = oIntent.getExtras().getString("RECHARGE_AMT");
        getcDate  = oIntent.getExtras().getString("C_DATE");

        fullpath  = oIntent.getExtras().getString("fullpath");
        title  = oIntent.getExtras().getString("title");
        details  = oIntent.getExtras().getString("details");
        Userid  = oIntent.getExtras().getString("Userid");
        expiryDate  = oIntent.getExtras().getString("expiryDate");
        amount  = oIntent.getExtras().getString("amount");



        Log.e( "onCreate: ",fullpath+" "+title+" " +details+" "+Userid+ expiryDate+amount);
        Log.e( "onCreate: ",amount+" ");


        //post_val = getIntent().getStringArrayListExtra("post_val");
        //Log.d(tag, "post_val: "+post_val);
        params= new HashMap<String,String>();
        params.put("key", merchant_key);

        params.put("amount", getRechargeAmt);
        params.put("firstname", getFirstName);
        params.put("email", getEmailAddress);
        params.put("phone", getNumber);
        params.put("productinfo", "Recharge Wallet");
        params.put("surl", SUCCESS_URL);
        params.put("furl", FAILED_URL);
        params.put("service_provider", "payu_paisa");
        params.put("lastname", "");
        params.put("address1", "");
        params.put("address2", "");
        params.put("city", "");
        params.put("state", "");
        params.put("country", "");
        params.put("zipcode", "");
        params.put("udf1", "");
        params.put("udf2", "");
        params.put("udf3", "");
        params.put("udf4", "");
        params.put("udf5", "");
        params.put("pg", "");

		/*for(int i = 0;i<post_val.size();){
			params.put(post_val.get(i), post_val.get(i+1));

			i+=2;
		}*/


        if(empty(params.get("txnid"))){
            Random rand = new Random();
            String rndm = Integer.toString(rand.nextInt())+(System.currentTimeMillis() / 1000L);
            txnid=hashCal("SHA-256",rndm).substring(0,20);
            params.put("txnid", txnid);
        }
        else
            txnid=params.get("txnid");
        //String udf2 = txnid;
        String txn="abcd";
        hash="";
        String hashSequence = "key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|udf6|udf7|udf8|udf9|udf10";
        if(empty(params.get("hash")) && params.size()>0)
        {
            if( empty(params.get("key"))
                    || empty(params.get("txnid"))
                    || empty(params.get("amount"))
                    || empty(params.get("firstname"))
                    || empty(params.get("email"))
                    || empty(params.get("phone"))
                    || empty(params.get("productinfo"))
                    || empty(params.get("surl"))
                    || empty(params.get("furl"))
                    || empty(params.get("service_provider"))

            ){
                error=1;
            }
            else{
                String[] hashVarSeq=hashSequence.split("\\|");

                for(String part : hashVarSeq)
                {
                    hashString= (empty(params.get(part)))?hashString.concat(""):hashString.concat(params.get(part));
                    hashString=hashString.concat("|");
                }
                hashString=hashString.concat(salt);


                hash=hashCal("SHA-512",hashString);
                action1=base_url.concat("/_payment");
            }
        }
        else if(!empty(params.get("hash")))
        {
            hash=params.get("hash");
            action1=base_url.concat("/_payment");
        }

        webView.setWebViewClient(new MyWebViewClient(){

            public void onPageFinished(WebView view, final String url) {
                progressDialog.dismiss();
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //make sure dialog is showing
                if(! progressDialog.isShowing()){
                    progressDialog.show();
                }
            }


			/*@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "SslError! " +  error, Toast.LENGTH_SHORT).show();
				 handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Page Started! " + url, Toast.LENGTH_SHORT).show();
				if(url.startsWith(SUCCESS_URL)){
					Toast

					.makeText(activity, "Payment Successful! " + url, Toast.LENGTH_SHORT).show();
					 Intent intent = new Intent(PayMentGateWay.this, MainActivity.class);
					    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
					    startActivity(intent);
					    finish();
					    return false;
				}else if(url.startsWith(FAILED_URL)){
					Toast.makeText(activity, "Payment Failed! " + url, Toast.LENGTH_SHORT).show();
				    return false;
				}else if(url.startsWith("http")){
					return true;
				}
				//return super.shouldOverrideUrlLoading(view, url);
				return false;
			}*/


        });

        webView.setVisibility(0);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(2);
        webView.getSettings().setDomStorageEnabled(true);
        webView.clearHistory();
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(false);

        //webView.addJavascriptInterface(new PayUJavaScriptInterface(getApplicationContext()), "PayUMoney");
        webView.addJavascriptInterface(new PayUJavaScriptInterface(), "PayUMoney");
        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put("key",merchant_key);
        mapParams.put("hash", PayMentGateWayForAdv.this.hash);
        mapParams.put("txnid",(empty(PayMentGateWayForAdv.this.params.get("txnid"))) ? "" : PayMentGateWayForAdv.this.params.get("txnid"));
        Log.d(tag, "txnid: "+ PayMentGateWayForAdv.this.params.get("txnid"));
        mapParams.put("service_provider","payu_paisa");

        mapParams.put("amount",(empty(PayMentGateWayForAdv.this.params.get("amount"))) ? "" : PayMentGateWayForAdv.this.params.get("amount"));
        mapParams.put("firstname",(empty(PayMentGateWayForAdv.this.params.get("firstname"))) ? "" : PayMentGateWayForAdv.this.params.get("firstname"));
        mapParams.put("email",(empty(PayMentGateWayForAdv.this.params.get("email"))) ? "" : PayMentGateWayForAdv.this.params.get("email"));
        mapParams.put("phone",(empty(PayMentGateWayForAdv.this.params.get("phone"))) ? "" : PayMentGateWayForAdv.this.params.get("phone"));

        mapParams.put("productinfo",(empty(PayMentGateWayForAdv.this.params.get("productinfo"))) ? "" : PayMentGateWayForAdv.this.params.get("productinfo"));
        mapParams.put("surl",(empty(PayMentGateWayForAdv.this.params.get("surl"))) ? "" : PayMentGateWayForAdv.this.params.get("surl"));
        mapParams.put("furl",(empty(PayMentGateWayForAdv.this.params.get("furl"))) ? "" : PayMentGateWayForAdv.this.params.get("furl"));
        mapParams.put("lastname",(empty(PayMentGateWayForAdv.this.params.get("lastname"))) ? "" : PayMentGateWayForAdv.this.params.get("lastname"));

        mapParams.put("address1",(empty(PayMentGateWayForAdv.this.params.get("address1"))) ? "" : PayMentGateWayForAdv.this.params.get("address1"));
        mapParams.put("address2",(empty(PayMentGateWayForAdv.this.params.get("address2"))) ? "" : PayMentGateWayForAdv.this.params.get("address2"));
        mapParams.put("city",(empty(PayMentGateWayForAdv.this.params.get("city"))) ? "" : PayMentGateWayForAdv.this.params.get("city"));
        mapParams.put("state",(empty(PayMentGateWayForAdv.this.params.get("state"))) ? "" : PayMentGateWayForAdv.this.params.get("state"));

        mapParams.put("country",(empty(PayMentGateWayForAdv.this.params.get("country"))) ? "" : PayMentGateWayForAdv.this.params.get("country"));
        mapParams.put("zipcode",(empty(PayMentGateWayForAdv.this.params.get("zipcode"))) ? "" : PayMentGateWayForAdv.this.params.get("zipcode"));
        mapParams.put("udf1",(empty(PayMentGateWayForAdv.this.params.get("udf1"))) ? "" : PayMentGateWayForAdv.this.params.get("udf1"));
        mapParams.put("udf2",(empty(PayMentGateWayForAdv.this.params.get("udf2"))) ? "" : PayMentGateWayForAdv.this.params.get("udf2"));

        mapParams.put("udf3",(empty(PayMentGateWayForAdv.this.params.get("udf3"))) ? "" : PayMentGateWayForAdv.this.params.get("udf3"));
        mapParams.put("udf4",(empty(PayMentGateWayForAdv.this.params.get("udf4"))) ? "" : PayMentGateWayForAdv.this.params.get("udf4"));
        mapParams.put("udf5",(empty(PayMentGateWayForAdv.this.params.get("udf5"))) ? "" : PayMentGateWayForAdv.this.params.get("udf5"));
        mapParams.put("pg",(empty(PayMentGateWayForAdv.this.params.get("pg"))) ? "" : PayMentGateWayForAdv.this.params.get("pg"));
        webview_ClientPost(webView, action1, mapParams.entrySet());

    }

	/*public class PayUJavaScriptInterface {

		@JavascriptInterface
        public void success(long id, final String paymentId) {
            mHandler.post(new Runnable() {
                public void run() {
                    mHandler = null;

                    new PostRechargeData().execute();

            		Toast.makeText(getApplicationContext(),"Successfully payment\n redirect from PayUJavaScriptInterface" ,Toast.LENGTH_LONG).show();

                }
            });
        }
	}*/


    private final class PayUJavaScriptInterface {

        PayUJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void success(long id, final String paymentId) {
            mHandler.post(new Runnable() {
                public void run() {
                    mHandler = null;

	                    /*Intent intent = new Intent();
	                    intent.putExtra(Constants.RESULT, "success");
	                    intent.putExtra(Constants.PAYMENT_ID, paymentId);
	                    setResult(RESULT_OK, intent);
	                    finish();*/
                    // new PostRechargeData().execute();
                   /* Intent intent=new Intent(PayMentGateWay.this,MainActivity.class);
                    intent.putExtra("test",getFirstName);
                    startActivity(intent);

                    Toast.makeText(getApplicationContext(), "Successfully payment", Toast.LENGTH_LONG).show();
*/

                    // imp
                    Toast.makeText(getApplicationContext(), "Successfully payment", Toast.LENGTH_LONG).show();

                    //addPayment(session.getUserID(),prefManager.getTYPEID(),getRechargeAmt);

                    InsertMessageWithFile(fullpath,title,details, Integer.parseInt(Userid),expiryDate,amount);
                }
            });
        }

        @JavascriptInterface
        public void failure(final String id, String error) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //cancelPayment();
                    Toast.makeText(getApplicationContext(),"Cancel payment" ,Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void failure() {
            failure("");
        }

        @JavascriptInterface
        public void failure(final String params) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

	                    /*Intent intent = new Intent();
	                    intent.putExtra(Constants.RESULT, params);
	                    setResult(RESULT_CANCELED, intent);
	                    finish();*/
                    Toast.makeText(getApplicationContext(),"Failed payment" ,Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    public void webview_ClientPost(WebView webView, String url, Collection< Map.Entry<String, String>> postData){
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));
        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");
        Log.d(tag, "webview_ClientPost called");

        //setup and load the progress bar
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading. Please wait...");
        webView.loadData(sb.toString(), "text/html", "utf-8");
    }


    public void success(long id, final String paymentId) {

        mHandler.post(new Runnable() {
            public void run() {
                mHandler = null;

                //  new PostRechargeData().execute();

                Toast.makeText(getApplicationContext(),"Successfully payment\n redirect from Success Function" ,Toast.LENGTH_LONG).show();

            }
        });
    }


    public boolean empty(String s)
    {
        if(s== null || s.trim().equals(""))
            return true;
        else
            return false;
    }

    public String hashCal(String type,String str){
        byte[] hashseq=str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try{
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();



            for (int i=0;i<messageDigest.length;i++) {
                String hex=Integer.toHexString(0xFF & messageDigest[i]);
                if(hex.length()==1) hexString.append("0");
                hexString.append(hex);
            }

        }catch(NoSuchAlgorithmException nsae){ }

        return hexString.toString();


    }

    //String SUCCESS_URL = "https://pay.in/sccussful" ; // failed
    //String FAILED_URL = "https://pay.in/failed" ;
    //override the override loading method for the webview client
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

        	/*if(url.contains("response.php") || url.equalsIgnoreCase(SUCCESS_URL)){

        		new PostRechargeData().execute();

        		Toast.makeText(getApplicationContext(),"Successfully payment\n redirect from webview" ,Toast.LENGTH_LONG).show();

                return false;
        	}else  */if(url.startsWith("http")){
                //Toast.makeText(getApplicationContext(),url ,Toast.LENGTH_LONG).show();
                progressDialog.show();
                view.loadUrl(url);
                System.out.println("myresult "+url);
                //return true;
            } else {
                return false;
            }

            return true;
        }
    }


    private void InsertMessageWithFile(String imgpath,String Title ,String Details , int Userid,
                                       String Expirydate, String Amount ) {
        try {
            progressDialog.show();
            //  uriData = media.getUri();
            path = new File(imgpath);
           // Log.e(TAG, "uploadWithFilePath: " + path.toString()+Userid);

            //{"type":3,"Action":1,"Attachmentid":0,"Homeworkid":3,"Typeid":1,"Filename":"Filename","LogedinUserId":1}
            int UserId = 1;
            AndroidNetworking.upload("http://iysonline.club/iys/api/processes/images.php/")
                    // .addFileToUpload("", "certificate") //Adding file
                    .addMultipartParameter("type", "1")
                    .addMultipartParameter("Action", "1")
                    .addMultipartParameter("ImagesId", "0")
                    .addMultipartParameter("Title", Title)
                    .addMultipartParameter("Details",Details)
                    .addMultipartFile("images", path)
                    .addMultipartParameter("Userid", String.valueOf(Userid))
                    .addMultipartParameter("Expirydate", Expirydate)
                    .addMultipartParameter("Isapprove", "0")
                    .addMultipartParameter("Amount", Amount)
                    .addMultipartParameter("LogedinUserId", "1")
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            Log.e(TAG, "uploadImage: totalBytes: " + totalBytes);
                            Log.e(TAG, "uploadImage: bytesUploaded: " + bytesUploaded);
                            progressDialog.setMessage("Attaching File, Please wait...");
                            progressDialog.show();
                            progressDialog.setCancelable(false);
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.hide();
                            Log.e(TAG, "FileonRes: " + response.toString());
                            Toast.makeText(PayMentGateWayForAdv.this,"Created Succesfully ",Toast.LENGTH_LONG).show();
                          /*  Intent intent= new Intent(AddAdvertisementActivity.this,AdvertisementListActivity.class);
                            startActivity(intent);*/
                            Utility.launchActivity(PayMentGateWayForAdv.this, AdvertisementListActivity.class,true);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e(TAG, "FileonError: ", error);
                            progressDialog.hide();
                        }
                    });
        } catch (Exception exc) {
            Log.e(TAG, "InsertMessageWithPdf: " + exc.getMessage());
            Toast.makeText(PayMentGateWayForAdv.this, "Please select Image", Toast.LENGTH_SHORT).show();
        }
    }

}