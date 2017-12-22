package com.s21m.proftaaks2maatwerk.api;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.data.PhotoResult;
import com.s21m.proftaaks2maatwerk.extensions.Application;
import com.s21m.proftaaks2maatwerk.extensions.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class ApiPhoto {

    private static ApiPhoto apiPhotoInstance;
    private static final String TAG = ApiPhoto.class.getSimpleName();
    private static final int FILE_RESOLUTION = 64;
    private static final String POST_KEY = "photo";
    private static final String POST_FILENAME = "photo.png";

    private ApiPhoto(){
        if(apiPhotoInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ApiPhoto getInstance(){
        return apiPhotoInstance == null ? new ApiPhoto() : apiPhotoInstance;
    }

    public<T extends Context & ApiListener<PhotoResult>> void send(Uri imageUri, T context) throws NetworkErrorException, IOException {
        if (((Application)context.getApplicationContext()).isNetworkAvailable()) {
            File imageFile = getFileToSend(imageUri, context);

            final RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(POST_KEY, POST_FILENAME,
                            RequestBody.create(MediaType.parse("image/png"), imageFile))
                    .build();

            final Request request = new Request.Builder()
                    .url(context.getString(R.string.api_url))
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);
            call.enqueue(getCallback(context));
        }
        else{
            throw new NetworkErrorException();
        }
    }

    private File getFileToSend(Uri imageUri, Context context) throws IOException {
        Bitmap bitmap = new Bitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri));
        bitmap.resize(FILE_RESOLUTION);
        File imageFile = File.createTempFile("toSend", ".png", context.getCacheDir());
        bitmap.saveToFile(imageFile);
        return  imageFile;
    }

    private PhotoResult parseResult(String jsonData) throws JSONException, IllegalArgumentException {
        JSONObject data = new JSONObject(jsonData);
        int age = data.getInt("Age");
        String emotion = data.getString("Emotion");
        return new PhotoResult(age, emotion);
    }

    private<T extends Context & ApiListener<PhotoResult>> Callback getCallback(final T context){
        return new Callback() {
            @Override
            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull final Call call, @NonNull final Response response) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResponseBody body = response.body();
                            String jsonData = body != null ? body.string() : null;
                            PhotoResult result = parseResult(jsonData);
                            context.onSuccess(result);
                        }
                        catch (Exception e) {
                            context.onFailure(e);
                        }
                    }
                });
            }
        };
    }
}
