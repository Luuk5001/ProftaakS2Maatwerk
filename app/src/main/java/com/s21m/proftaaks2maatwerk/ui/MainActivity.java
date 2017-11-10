package com.s21m.proftaaks2maatwerk.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.data.Emotions;
import com.s21m.proftaaks2maatwerk.data.ResultData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_GALLERY = 1;
    public static final String RESULTDATA_KEY = "result";

    private ResultData mResult;

    @BindView(R.id.imageViewLastPicture)
    ImageView mImageViewLastPicture;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.buttonTakePicture)
    public void TakePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @OnClick(R.id.buttonOpenGallery)
    public void OpenGallery(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    sendImage(imageToByteArray(image));
                }
                break;

            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri selectedImg = data.getData();
                    try {
                        Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                        sendImage(imageToByteArray(image));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void sendImage(final byte[] imageByteArray){
        String imageBase64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        String apiKey = "";
        String apiLink = "http://test.nl";

        if(isNetworkAvailable()){
            toggleProgressBar();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiLink).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    toggleProgressBar();

                    Log.w(TAG, "Connection to API failed");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    toggleProgressBar();

                    Log.d(TAG, "API responded with " + response);

                    mResult = new ResultData(writeImageToInternalStorage(imageByteArray), 24, Emotions.Fear);

                    Intent intent = new Intent(getBaseContext(), PictureTakenActivity.class);
                    intent.putExtra(RESULTDATA_KEY, mResult);
                    startActivity(intent);
                }
            });
        }
        else{
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }
    }

    private String writeImageToInternalStorage(byte[] imageByteArray) throws IOException {
        File fileDirectory = getApplicationContext().getFilesDir();
        File fileToWrite = new File(fileDirectory, UUID.randomUUID().toString());

        FileOutputStream out = new FileOutputStream(fileToWrite);

        out.write(imageByteArray);
        out.flush();
        out.close();

        return fileToWrite.getAbsolutePath();
    }

    private byte[] imageToByteArray(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream .toByteArray();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }

        return isAvailable;
    }

    private void toggleProgressBar() {
        if(mProgressBar.getVisibility() == View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
