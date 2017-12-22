package com.s21m.proftaaks2maatwerk.extensions;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.s21m.proftaaks2maatwerk.BuildConfig;

import java.io.File;

public class Application extends android.app.Application {
    public static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileProvider";

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    public void scanFile(File file){
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }
}
