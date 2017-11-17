package com.s21m.proftaaks2maatwerk.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.BuildConfig;
import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.data.Emotions;
import com.s21m.proftaaks2maatwerk.data.ResultData;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

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
    public static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileProvider";
    public static final String RESULT_KEY = "result";
    public static final String CAMERA_RESULT_KEY = "FILE_URI";
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_GALLERY = 1;

    private ResultData mResult;
    private Uri mImageUri;

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
        Intent intent = new Intent(this, CameraActivity.class);
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
                    mImageUri = Uri.parse(data.getStringExtra(CAMERA_RESULT_KEY));
                    startCropActivity();
                }
                break;

            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    mImageUri = data.getData();
                    startCropActivity();
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    File fileDelete = new File(getApplicationInfo().dataDir+mImageUri.getPath());

                    if(fileDelete.delete()) Log.d(TAG, "Temporary not-cropped picture deleted");
                    else Log.d(TAG, "Temporary not-cropped picture NOT deleted");

                    mImageUri = result.getUri();

                    Log.d(TAG, "Cropped file Uri:\n" + mImageUri.toString());

                    sendImage();
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                   Log.e(TAG, result.getError().toString());
                }
                break;
        }
    }

    private void sendImage(){
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

                    mResult = new ResultData(mImageUri, 24, Emotions.Fear);

                    Intent intent = new Intent(getBaseContext(), PictureTakenActivity.class);
                    intent.putExtra(RESULT_KEY, mResult);
                    startActivity(intent);
                }
            });
        }
        else{
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }
    }

    private void startCropActivity() {
        CropImage.activity(mImageUri)
                .setAspectRatio(1,1)
                .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                .setAllowCounterRotation(false)
                .setAllowFlipping(false)
                .setAllowRotation(false)
                .start(this);
    }

    private File createNewImageFile() {
        try{
            return File.createTempFile("NOT_CROPPED", null, getCacheDir());
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
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
