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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String LAST_PICTURE_NAME = "lastPicture.jpeg";
    public static final byte RC_GALLERY = 101;
    public static final int RC_CAMERA = 102;
    public static final int RC_CAMERA_PERMISSION = 103;
    public static final byte RC_CROP = 104;
    public static final byte RC_STORAGE_PERMISSION = 105;

    @BindView(R.id.imageViewLastPicture)
    ImageView imageViewLastPicture;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //================================================================================
    // Activity overrides
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Ask for external storage read and write permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, RC_STORAGE_PERMISSION);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get last picture taken and show in imageView
        File file = new File(getFilesDir(), LAST_PICTURE_NAME);
        if(file.exists()){
            try {
                Bitmap lastPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(file));
                imageViewLastPicture.setImageBitmap(lastPicture);
            } catch (IOException e) {
                Log.e(TAG, "Failed to load last scanned image");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_CAMERA:
                if(resultCode == RESULT_OK){
                    startCropPhotoActivity(Uri.parse(data.getStringExtra(CameraActivity.KEY_PHOTO_URI)));
                }
                else if(resultCode == CameraActivity.RESULT_CAMERA_UNAVAILABLE){
                    Toast.makeText(this, R.string.toast_camera_unavailable, Toast.LENGTH_LONG).show();
                }
                break;
            case RC_GALLERY:
                if(resultCode == RESULT_OK){
                    startCropPhotoActivity(data.getData());
                }
                break;
            case RC_CROP:
                if(resultCode == RESULT_OK){
                    startResultActivity(Uri.parse(data.getStringExtra(CropActivity.KEY_PHOTO_URI)));
                }
                else if(resultCode == CropActivity.RESULT_CODE_RETAKE_PHOTO){
                    startCameraActivity();
                }
                break;
            default:
                Log.e(TAG, "Result with unknown request code");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RC_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity();
                }
                break;
            case RC_STORAGE_PERMISSION:
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
    }

    //================================================================================
    // OnClickEvents
    //================================================================================

    @OnClick(R.id.buttonOpenGallery)
    public void onClickOpenGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_GALLERY);
    }

    @OnClick(R.id.buttonTakePicture)
    public void onClickTakePicture(View view) {
        //Ask for camera permission, start camera if already acquired
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){
                startCameraActivity();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, RC_CAMERA_PERMISSION);
            }
        }
        else{
            startCameraActivity();
        }
    }

    //================================================================================
    // Methods
    //================================================================================

    private void startCameraActivity(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, RC_CAMERA);
    }

    private void startCropPhotoActivity(Uri imageUri) {
        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra(CropActivity.KEY_PHOTO_URI,String.valueOf(imageUri));
        startActivityForResult(intent, RC_CROP);
    }

    private void startResultActivity(Uri imageUri){
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.PHOTO_URI_KEY ,String.valueOf(imageUri));
        startActivity(intent);
    }
}
