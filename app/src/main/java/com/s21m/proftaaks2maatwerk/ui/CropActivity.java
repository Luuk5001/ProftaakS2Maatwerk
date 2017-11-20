package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.Utilities;
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

public class CropActivity extends AppCompatActivity {

    @BindView(R.id.cropImageView)
    CropImageView mCropImageView;

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
        final File photoFile = createNewTempFile(CropActivity.this, "CROPPED", null);
        final Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), SHARED_PROVIDER_AUTHORITY, photoFile);
        mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                try {
                    saveBitmapToFile(photoFile, result.getBitmap());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                data.putExtra(PHOTO_URI_KEY, String.valueOf(fileUri));
                setResult(RESULT_OK, data);
                finish();
            }
        });
        mCropImageView.getCroppedImageAsync();
    }

    @OnClick(R.id.buttonRetakePhoto)
    public void onClickButtonRetakePhoto(View view){
        setResult(RESULT_RETAKE);
        finish();
    }
}