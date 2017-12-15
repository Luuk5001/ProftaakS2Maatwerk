package com.s21m.proftaaks2maatwerk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.data.ResultData;
import com.s21m.proftaaks2maatwerk.ui.CropActivity;
import com.s21m.proftaaks2maatwerk.ui.FeedbackActivity;
import com.s21m.proftaaks2maatwerk.ui.PictureTakenActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import static com.s21m.proftaaks2maatwerk.Utilities.toggleProgressBar;

public final class SendPhotoToAPI {

    public static void sendPhoto(final Context context, final Uri imageUri){
        if(Utilities.isNetworkAvailable(context)){

            String apiUrl = "http://i359079.venus.fhict.nl/api/Classifier";
            File img = null;
            try {
                img = getTempToSendFile(imageUri, context);
            } catch (IOException e) {
                e.printStackTrace();
                ((Activity)context).finish();
            }

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("pic", "pic.png",
                            RequestBody.create(MediaType.parse("image/png"), img))
                    .build();

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
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
                    }
                }
            });
        }
        else{
            Toast.makeText(context, "Network unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private static File getTempToSendFile(Uri imageUri, Context context) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        File tempFile = createNewTempFile(context, "toSend", ".png");
        OutputStream outStream = new FileOutputStream(tempFile);
        outStream.write(buffer);
        return tempFile;
    }

    private static ResultData parseResult(String jsonData, Uri imageUri) throws JSONException, IllegalArgumentException {
        JSONObject data = new JSONObject(jsonData);
        int age = data.getInt("Age");
        String emotion = data.getString("Emotion");
        return new ResultData(imageUri, age, emotion);
    }
}
