package com.s21m.proftaaks2maatwerk;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.WindowManager;
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
    public static final byte REQUEST_STORAGE_PERMISSION = 4;
    public static final byte RESULT_RETAKE = 10;
    public static final byte RESULT_CAMERA_UNAVAILABLE = 11;

    public static void toggleProgressBar(Activity activity, ProgressBar bar) {
        if(bar.getVisibility() == View.INVISIBLE){
            bar.setVisibility(View.VISIBLE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
        else{
            bar.setVisibility(View.INVISIBLE);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    public static File createNewTempFile(Context context, String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix, context.getCacheDir());
    }

    public static void saveBitmapToFile(File file, Bitmap bitmap) throws IOException {
        FileOutputStream fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }

    public static void deleteCache(Context context) {
        File dir = context.getCacheDir();
        deleteDir(dir);
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else
            return dir != null && dir.isFile() && dir.delete();
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
