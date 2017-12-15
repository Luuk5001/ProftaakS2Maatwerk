package com.s21m.proftaaks2maatwerk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.Utilities;
import com.s21m.proftaaks2maatwerk.data.ResultData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.s21m.proftaaks2maatwerk.Utilities.PHOTO_URI_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_CAMERA;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_CAMERA_PERMISSION;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_CROP;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_GALLERY;
import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_STORAGE_PERMISSION;
import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_CAMERA_UNAVAILABLE;
import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_DATA_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_RETAKE;
import static com.s21m.proftaaks2maatwerk.Utilities.createNewTempFile;
import static com.s21m.proftaaks2maatwerk.Utilities.toggleProgressBar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ResultData mResult;

    @BindView(R.id.imageViewLastPicture)
    ImageView mImageViewLastPicture;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.buttonTakePicture)
    Button mTakePictureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Ask for external storage read and write permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        File file = new File(getFilesDir(), "lastPicture.png");
        if(file.exists()){
            try {
                Bitmap lastPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(file));
                mImageViewLastPicture.setImageBitmap(lastPicture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.buttonTakePicture)
    public void onClickTakePicture(View view) {
        //Ask for camera permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){
                startPhotoActivity();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        }
        else{
            startPhotoActivity();
        }
    }

    @OnClick(R.id.buttonOpenGallery)
    public void onClickOpenGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    Uri imageUri = Uri.parse(data.getStringExtra(PHOTO_URI_KEY));
                    startCropPhotoActivity(imageUri);
                }
                else if(resultCode == RESULT_CAMERA_UNAVAILABLE){
                    Toast.makeText(this, "The photo camera on this device is either unavailable or unsupported.", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK){
                    Uri imageUri = data.getData();
                    startCropPhotoActivity(imageUri);
                }
                break;

            case REQUEST_CROP:
                if(resultCode == RESULT_OK){
                    Uri imageUri = Uri.parse(data.getStringExtra(PHOTO_URI_KEY));
                    sendPhoto(imageUri);
                }
                else if(resultCode == RESULT_RETAKE){
                    startPhotoActivity();
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPhotoActivity();
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                //Close application if storage permission is not granted
                if (grantResults.length < 1
                        || grantResults[0] == PackageManager.PERMISSION_DENIED
                        || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAndRemoveTask();
                    }
                    else{
                        finishAffinity();
                    }
                }
                break;
        }
        grantResults = null;
    }

    private void startCropPhotoActivity(Uri imageUri) {
        //Start the crop activity
        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra(PHOTO_URI_KEY ,String.valueOf(imageUri));
        startActivityForResult(intent, REQUEST_CROP);
    }

    private void startPhotoActivity(){
        //Start the camera activity
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void sendPhoto(final Uri imageUri){
        if(Utilities.isNetworkAvailable(this)){

            toggleProgressBar(this, mProgressBar);

            String apiUrl = "http://i359079.venus.fhict.nl/api/Classifier";
            File img = null;
            try {
                img = getTempToSendFile(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleProgressBar(MainActivity.this, mProgressBar);
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleProgressBar(MainActivity.this, mProgressBar);
                        }
                    });
                    try{
                        ResponseBody body = response.body();
                        String jsonData = body != null ? body.string() : null;

                        mResult = parseResult(jsonData, imageUri);

                        Intent intent = new Intent(getBaseContext(), PictureTakenActivity.class);
                        intent.putExtra(RESULT_DATA_KEY, mResult);
                        startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private File getTempToSendFile(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        File tempFile = createNewTempFile(this, "toSend", ".png");
        OutputStream outStream = new FileOutputStream(tempFile);
        outStream.write(buffer);
        return tempFile;
    }

    private ResultData parseResult(String jsonData, Uri imageUri) throws JSONException, IllegalArgumentException {
        JSONObject data = new JSONObject(jsonData);
        int age = data.getInt("Age");
        String emotion = data.getString("Emotion");
        return new ResultData(imageUri, age, emotion);
    }
}
