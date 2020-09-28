package com.netist.mygirlshostel.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";


    public static void launchActivity(Activity activity, Class<?> mClass, boolean shouldFinishParent, Bundle bundle) {
        Intent intent = new Intent(activity, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        if (shouldFinishParent) {
            activity.finish();
        }
    }

    public static void launchActivityForResult(Activity activity, Class<?> mClass, int requestCode) {
        Intent intent = new Intent(activity, mClass);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launchActivityWithContext(Context context, Class<?> mClass) {
        Intent intent = new Intent(context, mClass);
        context.startActivity(intent);
    }

    public static void launchActivityForResult(Activity activity, Class<?> mClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launchActivityForResultFromFragment(Fragment fragment, Activity activity, Class<?> mClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, mClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void launchActivity(Activity activity, Class<?> mClass, boolean shouldFinishParent) {
        Intent intent = new Intent(activity, mClass);
        activity.startActivity(intent);
        if (shouldFinishParent) {
            activity.finish();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void finishWithResult(Activity activity, Bundle bundle, int result) {
        Intent i = new Intent();
        if (bundle != null) {
            i.putExtras(bundle);
        }
        activity.setResult(result, i);
        activity.finish();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the cu rrently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view = null;
    }

    public static String getDeviceToken(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean emailValidate(String email) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean numberValidation(String number) {
        return Pattern.compile("^[0-9]+$").matcher(number).matches();
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static String getDeviceName() {
        return Build.MANUFACTURER + " - " + Build.MODEL;
    }


    public static String getCurrentDateTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-4:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("KK:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT-4:00"));
        String localTime = date.format(currentLocalTime);
        return localTime;
    }


    public static void decrypt(String key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    static void doCrypto(int cipherMode, String key, File inputFile,
                         File outputFile) throws Exception {
        Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(cipherMode, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }


    public static String getFilenameWithoutExtension(String fileNameWithExtension) {
        int pos = fileNameWithExtension.lastIndexOf(".");
        if (pos > 0) {
            fileNameWithExtension = fileNameWithExtension.substring(0, pos);
        }
        return fileNameWithExtension;
    }




//    public static void sendIntent(Context context, String action) {
//        final Intent intent = new Intent(context, MusicService.class);
//        intent.setAction(action);
//        context.startService(intent);
//    }
//
//    public static void sendIntent(Context context, String action, Bundle b) {
//        final Intent intent = new Intent(context, MusicService.class);
//        intent.putExtras(b);
//        intent.setAction(action);
//        context.startService(intent);
//    }
}
