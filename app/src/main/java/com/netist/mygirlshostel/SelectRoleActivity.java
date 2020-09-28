package com.netist.mygirlshostel;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.utils.Utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.SEND_SMS;

public class SelectRoleActivity extends AppCompatActivity implements View.OnClickListener {


    private CardView cvPrincipal;
    private CardView cvTeacher;
    private CardView cv_adv, cv_Trust;
    PrefManager prefManager;

    int typeIndex = 0;
    String role_type;

    CircleImageView ib_twitter;
    CircleImageView ib_facebook;
    CircleImageView ib_instagram;
    CircleImageView ib_whatsapp;
    CircleImageView ib_linkedin;
    CircleImageView ib_telegram;
    CircleImageView ib_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);
        initView();
        requestPermission();

    }

    private void initView() {
        cvPrincipal = (CardView) findViewById(R.id.cv_Principal);
        cvPrincipal.setOnClickListener(this);
        cvTeacher = (CardView) findViewById(R.id.cv_teacher);
        cvTeacher.setOnClickListener(this);
        cv_adv = (CardView) findViewById(R.id.cv_adv);
        cv_Trust = (CardView) findViewById(R.id.cv_Trust);
        cv_adv.setOnClickListener(this);
        cv_Trust.setOnClickListener(this);
        prefManager=new PrefManager(SelectRoleActivity.this);
        ib_twitter = findViewById(R.id.ib_twitter);
        ib_facebook = findViewById(R.id.ib_facebook);
        ib_instagram = findViewById(R.id.ib_instagram);
        ib_whatsapp =   findViewById(R.id.ib_whatsapp);
        ib_linkedin =findViewById(R.id.ib_linkedin);
        ib_telegram =findViewById(R.id.ib_telegram);
        ib_email =findViewById(R.id.ib_email);

        ib_twitter.setOnClickListener(this);
        ib_facebook.setOnClickListener(this);
        ib_instagram.setOnClickListener(this);
        ib_whatsapp.setOnClickListener(this);
        ib_linkedin.setOnClickListener(this);
        ib_telegram.setOnClickListener(this);
        ib_email.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.cv_Trust:

                prefManager.setROLE("Admin");
                Utility.launchActivity(SelectRoleActivity.this, WelcomeActivity.class, true);
                break;

            case R.id.cv_Principal:
                // prefManager.setROLE("Service Provider");

                final CharSequence[] typeList = new CharSequence[] {
                        "Hostel",
                        "Mess",
                        "Classes",
                        "Library"
                };

                final AlertDialog.Builder alert = new AlertDialog.Builder(SelectRoleActivity.this);
                alert.setSingleChoiceItems(typeList, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        typeIndex = i;
                    }
                });

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        role_type = typeList[typeIndex].toString();
                        prefManager.setROLE(role_type);

                        Log.d("role_type", role_type);
                        Utility.launchActivity(SelectRoleActivity.this, WelcomeActivity.class, true);
                    }
                });
                alert.show();

                break;

            case R.id.cv_teacher:
                prefManager.setROLE("Common User");
                Utility.launchActivity(SelectRoleActivity.this, WelcomeActivity.class, true);
                break;

            case R.id.cv_adv:
                prefManager.setROLE("adv");
                Utility.launchActivity(SelectRoleActivity.this, WelcomeActivity.class, true);
                break;

            case R.id.ib_twitter:
               /* Intent intentTwitter = null;
                intentTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/@teammandroid"));
                this.startActivity(intentTwitter);
               */

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:9423780567"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                startActivity(callIntent);

                break;

            case R.id.ib_facebook:
                /*Intent intentFacebook = null;
                try {
                    // intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
                    intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/balasahebbodkhepage/"));
                } catch (Exception e) {
                    // intentFacebook =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
                    intentFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/balasahebbodkhepage/"));
                }
                startActivity(intentFacebook);*/
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ib_instagram:
                // Uri uri2 = Uri.parse("http://instagram.com/_u/teammandroid");
                /*Uri uri2 = Uri.parse("https://www.instagram.com/balasaheb.bodkhe.7/");
                Intent intentInstagram = new Intent(Intent.ACTION_VIEW, uri2);

                intentInstagram.setPackage("com.instagram.android");

                try {
                    startActivity(intentInstagram);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            // Uri.parse("http://instagram.com/teammandroid")));
                            Uri.parse("https://www.instagram.com/balasaheb.bodkhe.7/")));
                }
*/
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();

                break;

            case R.id.ib_whatsapp:
                String url = "https://api.whatsapp.com/send?phone=" + "+91 94237 80567";
                Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW);
                intentWhatsapp.setData(Uri.parse(url));
                startActivity(intentWhatsapp);
                break;

            case R.id.ib_linkedin:
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=you"));
                startActivity(intent);*/
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ib_email:
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=you"));
                startActivity(intent);*/
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"devendramahadik@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SelectRoleActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.ib_telegram:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, ("https://play.google.com/store/apps/details?id=com.iys.inyourservice"));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    //  Runtime permission
    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        CALL_PHONE,
                        SEND_SMS,
                        ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            // showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! "+error, Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


}
