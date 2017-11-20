package com.s21m.proftaaks2maatwerk;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class Utilities {

    public static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileProvider";
    public static final String RESULT_DATA_KEY = "RESULT";
    public static final String PHOTO_URI_KEY = "PHOTO_URI";
    public static final String CONTEXT_KEY = "CONTEXT";
    public static final byte REQUEST_CAMERA = 0;
    public static final byte REQUEST_GALLERY = 1;
    public static final byte REQUEST_CROP = 2;
    public static final byte REQUEST_CAMERA_PERMISSION = 3;
    public static final byte RESULT_RETAKE = 0;

    public static void toggleProgressBar(ProgressBar bar) {
        if(bar.getVisibility() == View.INVISIBLE){
            bar.setVisibility(View.VISIBLE);
        }
        else{
            bar.setVisibility(View.INVISIBLE);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    public static File createNewTempFile(Context context, String prefix, String suffix) {
        try{
            return File.createTempFile(prefix, suffix, context.getCacheDir());
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBitmapToFile(File file, Bitmap bitmap) throws IOException {
        FileOutputStream fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }
}