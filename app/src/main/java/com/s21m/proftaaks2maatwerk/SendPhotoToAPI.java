package com.s21m.proftaaks2maatwerk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.data.ResultData;
import com.s21m.proftaaks2maatwerk.ui.CropActivity;
import com.s21m.proftaaks2maatwerk.ui.FeedbackActivity;
import com.s21m.proftaaks2maatwerk.ui.PictureTakenActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_DATA_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.createNewTempFile;
import static com.s21m.proftaaks2maatwerk.Utilities.deleteCache;
import static com.s21m.proftaaks2maatwerk.Utilities.getResizedBitmap;
import static com.s21m.proftaaks2maatwerk.Utilities.saveBitmapToFile;
import static com.s21m.proftaaks2maatwerk.Utilities.toggleProgressBar;

public final class SendPhotoToAPI {

    private static final String TAG = SendPhotoToAPI.class.getSimpleName();

    public static void sendPhoto(final Context context, final Uri imageUri){
        if(Utilities.isNetworkAvailable(context)){

            String apiUrl = context.getString(R.string.api_url);

            File img = null;

            try {
                Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                imgBitmap = getResizedBitmap(imgBitmap, 64);
                img = createNewTempFile(context, "toSend", ".png");
                saveBitmapToFile(img, imgBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert img != null;
            final RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("pic", "pic.png",
                            RequestBody.create(MediaType.parse("image/png"), img))
                    .build();

            final Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deleteCache(context);
                            Toast.makeText(context, R.string.send_photo_failure, Toast.LENGTH_LONG).show();
                        }
                    });
                    ((Activity)context).finish();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try{
                        ResponseBody body = response.body();
                        String jsonData = body != null ? body.string() : null;

                        ResultData result = parseResult(jsonData, imageUri);

                        Intent intent = new Intent(context, PictureTakenActivity.class);
                        intent.putExtra(RESULT_DATA_KEY, result);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deleteCache(context);
                                Toast.makeText(context, R.string.send_photo_failure, Toast.LENGTH_LONG).show();
                            }
                        });
                        ((Activity)context).finish();
                    }
                }
            });
        }
        else{
            Toast.makeText(context, R.string.toast_network_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    private static ResultData parseResult(String jsonData, Uri imageUri) throws JSONException, IllegalArgumentException {
        JSONObject data = new JSONObject(jsonData);
        int age = data.getInt("Age");
        String emotion = data.getString("Emotion");
        return new ResultData(imageUri, age, emotion);
    }
}
