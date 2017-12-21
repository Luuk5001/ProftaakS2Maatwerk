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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.s21m.proftaaks2maatwerk.utils.Utils.PHOTO_URI_KEY;
import static com.s21m.proftaaks2maatwerk.utils.Utils.RESULT_RETAKE;
import static com.s21m.proftaaks2maatwerk.utils.Utils.SHARED_PROVIDER_AUTHORITY;
import static com.s21m.proftaaks2maatwerk.utils.Utils.createNewCacheFile;
import static com.s21m.proftaaks2maatwerk.utils.Utils.getResizedBitmap;
import static com.s21m.proftaaks2maatwerk.utils.Utils.saveBitmapToFile;

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
                    File photoFile = createNewCacheFile(CropActivity.this, "CROPPED", ".png");
                    Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), SHARED_PROVIDER_AUTHORITY, photoFile);
                    saveBitmapToFile(photoFile, getResizedBitmap(result.getBitmap(),850));
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
        setResult(RESULT_RETAKE);
        finish();
    }
}
