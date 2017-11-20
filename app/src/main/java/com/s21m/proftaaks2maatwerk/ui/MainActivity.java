package com.s21m.proftaaks2maatwerk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.BuildConfig;
import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.Utilities;
import com.s21m.proftaaks2maatwerk.data.Emotions;
import com.s21m.proftaaks2maatwerk.data.ResultData;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.s21m.proftaaks2maatwerk.Utilities.CONTEXT_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.PHOTO_URI_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_CAMERA;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_CAMERA_PERMISSION;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_CROP;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_GALLERY;
import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_DATA_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_RETAKE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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
    public void onClickTakePicture(View view) {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED){
            takePhoto();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @OnClick(R.id.buttonOpenGallery)
    public void onClickOpenGallery(View view) {
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
                if(resultCode == RESULT_OK){
                    Uri imageUri = Uri.parse(data.getStringExtra(PHOTO_URI_KEY));
                    cropPhoto(imageUri);
                }
                break;

            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK){
                    Uri imageUri = data.getData();
                    cropPhoto(imageUri);
                }
                break;

            case REQUEST_CROP:
                if(resultCode == RESULT_OK){
                    Uri imageUri = Uri.parse(data.getStringExtra(PHOTO_URI_KEY));
                    sendPhoto(imageUri);
                }
                else if(resultCode == RESULT_RETAKE){
                    takePhoto();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
        }
    }

    private void cropPhoto(Uri imageUri) {
        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra(PHOTO_URI_KEY ,String.valueOf(imageUri));
        startActivityForResult(intent, REQUEST_CROP);
    }

    private void takePhoto(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void sendPhoto(final Uri imageUri){
        String apiKey = "";
        String apiLink = "http://test.nl";

        mResult = new ResultData(imageUri, 24, Emotions.Fear);

        Intent intent = new Intent(getBaseContext(), PictureTakenActivity.class);
        intent.putExtra(RESULT_DATA_KEY, mResult);
        startActivity(intent);

        /*

        if(Utilities.isNetworkAvailable(this)){
            Utilities.toggleProgressBar(mProgressBar);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiLink).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Utilities.toggleProgressBar(mProgressBar);

                    Log.w(TAG, "Connection to API failed");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Utilities.toggleProgressBar(mProgressBar);

                    Log.d(TAG, "API responded with " + response);

                    mResult = new ResultData(imageUri, 24, Emotions.Fear);

                    Intent intent = new Intent(getBaseContext(), PictureTakenActivity.class);
                    intent.putExtra(RESULT_KEY, mResult);
                    startActivity(intent);
                }
            });
        }
        else{
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }
        */
    }
}
