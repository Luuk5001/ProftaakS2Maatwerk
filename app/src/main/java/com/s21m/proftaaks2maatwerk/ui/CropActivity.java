package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.SendPhotoToAPI;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.s21m.proftaaks2maatwerk.Utilities.PHOTO_URI_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_RETAKE;
import static com.s21m.proftaaks2maatwerk.Utilities.SHARED_PROVIDER_AUTHORITY;
import static com.s21m.proftaaks2maatwerk.Utilities.createNewTempFile;
import static com.s21m.proftaaks2maatwerk.Utilities.saveBitmapToFile;
import static com.s21m.proftaaks2maatwerk.Utilities.toggleProgressBar;

public class CropActivity extends AppCompatActivity {

    private static String TAG = CropActivity.class.getSimpleName();

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
        mCropImageView.setImageUriAsync(photoUri);
    }

    @OnClick(R.id.buttonCropImage)
    public void onClickButtonCropImage(){
        toggleProgressBar(this, mProgressBar);
        try{
            final File photoFile = createNewTempFile(CropActivity.this, "CROPPED", ".png");
            final Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), SHARED_PROVIDER_AUTHORITY, photoFile);
            mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
                @Override
                public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                    try {
                        saveBitmapToFile(photoFile, result.getBitmap());
                        SendPhotoToAPI.sendPhoto(CropActivity.this, fileUri);
                    } catch (IOException e) {
                        toggleProgressBar(CropActivity.this, mProgressBar);
                        Log.e(TAG, "Failed to save cropped bitmap");
                        e.printStackTrace();
                        finish();
                    }
                }
            });
            mCropImageView.getCroppedImageAsync();
        }
       catch (IOException e){
            toggleProgressBar(this, mProgressBar);
            e.printStackTrace();
       }
    }

    @OnClick(R.id.buttonRetakePhoto)
    public void onClickButtonRetakePhoto(View view){
        setResult(RESULT_RETAKE);
        finish();
    }
}
