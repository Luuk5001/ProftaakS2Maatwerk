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

    /**
     * Checks if network is available
     * @return true if network is available, false if not
     */
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

    /**
     * Updates the android gallery with the specified file
     * @param file file to scan
     */
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

    /**
     * Get a string ID from string resources based on it's name
     * @param context context
     * @param name resource name
     * @return resource ID
     */
    public static  int getStringIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }
}
