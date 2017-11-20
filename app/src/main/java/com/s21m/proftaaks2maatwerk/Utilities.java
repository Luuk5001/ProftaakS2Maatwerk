package com.s21m.proftaaks2maatwerk;


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
