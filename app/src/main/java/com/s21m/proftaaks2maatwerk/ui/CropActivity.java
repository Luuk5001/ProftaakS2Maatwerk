package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.extensions.Application;
import com.s21m.proftaaks2maatwerk.extensions.Bitmap;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CropActivity extends AppCompatActivity {

    public static final byte REQUEST_CROP = 42;
    public static final String PHOTO_URI_KEY = "crop_photo_uri";

    private static final String TAG = CropActivity.class.getSimpleName();

    @BindView(R.id.cropImageView)
    CropImageView mCropImageView;
    @BindView(R.id.progressBarCrop)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Uri photoUri = Uri.parse(intent.getStringExtra(PHOTO_URI_KEY));
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setImageUriAsync(photoUri);
    }

    @OnClick(R.id.buttonCropImage)
    public void onClickButtonCropImage(){
        mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                try {
                    File photoFile = File.createTempFile("CROPPED", ".png", CropActivity.this.getCacheDir());
                    Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), Application.SHARED_PROVIDER_AUTHORITY, photoFile);
                    Bitmap bitmap = new Bitmap(result.getBitmap());
                    bitmap.resize(850);
                    bitmap.saveToFile(photoFile);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(PHOTO_URI_KEY,fileUri.toString());
                    setResult(RESULT_OK,returnIntent);
                    finish();
                } catch (IOException e) {
                    Toast.makeText(CropActivity.this, R.string.toast_crop_error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to save cropped bitmap");
                    e.printStackTrace();
                    finish();
                }
            }
        });
        mCropImageView.getCroppedImageAsync();
    }

    @OnClick(R.id.buttonRetakePhoto)
    public void onClickButtonRetakePhoto(View view){
        setResult(CameraActivity.RESULT_RETAKE);
        finish();
    }
}
