package com.s21m.proftaaks2maatwerk.api;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.extensions.Application;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class ApiEmotions {

    private static ApiEmotions apiEmotionsInstance;

    private ApiEmotions(){
        if(apiEmotionsInstance != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static ApiEmotions getInstance(){
        return apiEmotionsInstance == null ? new ApiEmotions() : apiEmotionsInstance;
    }

    public<T extends Context & ApiListener<String[]>> void request(T context) throws NetworkErrorException {
        if (((Application) context.getApplicationContext()).isNetworkAvailable()) {

            String apiUrl = context.getString(R.string.api_url);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiUrl).build();
            Call call = client.newCall(request);
            call.enqueue(getCallback(context));
        }
        else{
            throw new NetworkErrorException();
        }
    }

    private String[] parseResult(String jsonData) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonData);
        final String[] emotions = new String[jsonArray.length()];
        for (int i=0;i<jsonArray.length();i++){
            emotions[i] = jsonArray.get(i).toString();
        }
        return emotions;
    }

    private<T extends Context & ApiListener<String[]>> Callback getCallback(final T context){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResponseBody body = response.body();
                            String jsonData = body != null ? body.string() : null;
                            String[] emotions = parseResult(jsonData);
                            context.onSuccess(emotions);
                        }
                        catch (IOException e) {
                            context.onFailure(e);
                        }
                        catch (JSONException e) {
                            context.onFailure(e);
                        }
                    }
                });
            }
        };
    }
}
