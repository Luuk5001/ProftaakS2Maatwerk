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

    private static final String TAG = CropActivity.class.getSimpleName();
    public static final String KEY_PHOTO_URI = "crop_photo_uri";
    public static final int RESULT_CODE_RETAKE_PHOTO = 201;

    @BindView(R.id.cropImageView)
    CropImageView mCropImageView;
    @BindView(R.id.progressBarCrop)
    ProgressBar mProgressBar;

    //================================================================================
    // Activity overrides
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Uri photoUri = Uri.parse(intent.getStringExtra(KEY_PHOTO_URI));
        //Cropper settings
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setImageUriAsync(photoUri);
    }

    //================================================================================
    // OnClickEvents
    //================================================================================

    @OnClick(R.id.buttonCropImage)
    public void onClickButtonCropImage(){
        mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                try {
                    File photoFile = File.createTempFile("CROPPED", ".jpeg", CropActivity.this.getCacheDir());
                    Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), Application.SHARED_PROVIDER_AUTHORITY, photoFile);
                    Bitmap bitmap = new Bitmap(result.getBitmap());
                    bitmap.resize(850);
                    bitmap.saveToFile(photoFile);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(KEY_PHOTO_URI,fileUri.toString());
                    setResult(RESULT_OK,returnIntent);
                    finish();
                } catch (IOException e) {
                    Toast.makeText(CropActivity.this, R.string.toast_crop_error, Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
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
        setResult(RESULT_CODE_RETAKE_PHOTO);
        finish();
    }
}
