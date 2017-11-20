package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.Utilities;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.update.UpdateRequest;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.on;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.front;

public class CameraActivity extends AppCompatActivity {

    private static final byte FLASH_ON = 0;
    private static final byte FLASH_OFF = 1;
    private static final byte FLASH_AUTO = 2;

    private Fotoapparat mFotoapparat;
    private int mCurrentLensPosition;
    private byte mCurrentFlashMode = FLASH_AUTO;

    @BindView(R.id.cameraView)
    CameraView mCameraView;
    @BindView(R.id.imageButtonToggleFlash)
    ImageButton mImageButtonToggleFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentLensPosition = intent.getIntExtra(MainActivity.LENS_POSITION_KEY, 0);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if(mCurrentLensPosition == MainActivity.FRONT_LENS){
            mCurrentLensPosition = MainActivity.FRONT_LENS;
            setFrontLens();
        }
        else{
            mCurrentLensPosition = MainActivity.BACK_LENS;
            setBackLens();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFotoapparat.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFotoapparat.stop();
    }

    @OnClick(R.id.buttonTakePicture)
    public void onClickButtonTakePicture(View view) {
        final File photoFile = Utilities.createNewTempFile(this, "NOCROP", null);
        final Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), MainActivity.SHARED_PROVIDER_AUTHORITY, photoFile);
        PhotoResult photoResult = mFotoapparat.takePicture();
        photoResult.saveToFile(photoFile).whenAvailable(new PendingResult.Callback<Void>() {
            @Override
            public void onResult(Void aVoid) {
                Intent data = new Intent();
                data.putExtra(MainActivity.PHOTO_URI_KEY, String.valueOf(fileUri));
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @OnClick(R.id.imageButtonSwitchCamera)
    public void onClickImageButtonSwitchCamera(View view){
        toggleCurrentLensPosition();
        Intent intent = getIntent();
        intent.putExtra(MainActivity.LENS_POSITION_KEY, mCurrentLensPosition);
        finish();
        startActivity(intent);
    }

    private void setFrontLens(){
        mFotoapparat = Fotoapparat
                .with(this)
                .into(mCameraView)
                .lensPosition(front())
                .build();
    }

    private void setBackLens(){
        mFotoapparat = Fotoapparat
                .with(this)
                .into(mCameraView)
                .lensPosition(back())
                .build();
    }

    private void toggleCurrentLensPosition(){
        if(mCurrentLensPosition == MainActivity.FRONT_LENS){
            mCurrentLensPosition = MainActivity.BACK_LENS;
        }
        else if(mCurrentLensPosition == MainActivity.BACK_LENS){
            mCurrentLensPosition = MainActivity.FRONT_LENS;
        }
    }

    private void toggleFlash(){
        switch (mCurrentFlashMode){
            case FLASH_OFF:
                mFotoapparat.updateParameters(
                        UpdateRequest.builder()
                                .flash(autoFlash())
                        .build()
                );
               // mImageButtonToggleFlash.setImageDrawable(R.drawable.ic_flash_auto_white_36dp);
                mCurrentFlashMode = FLASH_AUTO;
            case FLASH_AUTO:
                mFotoapparat.updateParameters(
                        UpdateRequest.builder()
                                .flash(on())
                                .build()
                );
                mCurrentFlashMode = FLASH_ON;
            case  FLASH_ON:
                mFotoapparat.updateParameters(
                        UpdateRequest.builder()
                                .flash(off())
                                .build()
                );
                mCurrentFlashMode = FLASH_OFF;
        }
    }
}
