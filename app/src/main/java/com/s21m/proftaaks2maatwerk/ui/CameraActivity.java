package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.s21m.proftaaks2maatwerk.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.update.UpdateRequest;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static com.s21m.proftaaks2maatwerk.Utilities.PHOTO_URI_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.SHARED_PROVIDER_AUTHORITY;
import static com.s21m.proftaaks2maatwerk.Utilities.createNewTempFile;
import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.on;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.front;

public class CameraActivity extends AppCompatActivity{

    private static final byte FLASH_ON = 0;
    private static final byte FLASH_OFF = 1;
    private static final byte FLASH_AUTO = 2;
    private static final byte FRONT_LENS = 0;
    private static final byte BACK_LENS = 1;

    private Fotoapparat mFotoapparat;
    private int mCurrentLensPosition = FRONT_LENS;
    private byte mCurrentFlashMode = FLASH_AUTO;

    @BindView(R.id.cameraView)
    CameraView mCameraView;
    @BindView(R.id.imageButtonToggleFlash)
    ImageButton mImageButtonToggleFlash;
    @BindView(R.id.buttonTakePicture)
    Button mButtonTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setActiveLens();
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
        try {
            final File photoFile = createNewTempFile(this, "NOCROP", null);
            final Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), SHARED_PROVIDER_AUTHORITY, photoFile);
            PhotoResult photoResult = mFotoapparat.takePicture();
            photoResult.saveToFile(photoFile).whenAvailable(new PendingResult.Callback<Void>() {
                @Override
                public void onResult(Void aVoid) {
                    Intent data = new Intent();
                    data.putExtra(PHOTO_URI_KEY, String.valueOf(fileUri));
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.imageButtonSwitchCamera)
    public void onClickImageButtonSwitchCamera(View view){
        //Stop camera before changing lens
        mFotoapparat.stop();
        toggleActiveLens();
        mFotoapparat.start();
        //Recover flash mode set before lens toggle
        setFlashMode();
    }

    @OnClick(R.id.imageButtonToggleFlash)
    public void onClickImageButtonToggleFlash(View view){
        toggleFlashMode();
    }

    //Change the camera lens in the mFotoapparat variable
    private void setActiveLens(){
        if(mCurrentLensPosition == FRONT_LENS){
            mFotoapparat = Fotoapparat
                    .with(this)
                    .into(mCameraView)
                    .lensPosition(front())
                    .build();
        }
        else if(mCurrentLensPosition == BACK_LENS){
            mFotoapparat = Fotoapparat
                    .with(this)
                    .into(mCameraView)
                    .lensPosition(back())
                    .build();
        }
    }

    //Update camera parameters with desired flash mode
    private void setFlashMode(){
        switch (mCurrentFlashMode){
            case FLASH_OFF:
                mFotoapparat.updateParameters(
                        UpdateRequest.builder()
                                .flash(off())
                                .build()
                );
                mImageButtonToggleFlash.setImageResource(R.drawable.ic_flash_off_white_36dp);
                break;
            case FLASH_AUTO:
                mFotoapparat.updateParameters(
                        UpdateRequest.builder()
                                .flash(autoFlash())
                                .build()
                );
                mImageButtonToggleFlash.setImageResource(R.drawable.ic_flash_auto_white_36dp);
                break;
            case  FLASH_ON:
                mFotoapparat.updateParameters(
                        UpdateRequest.builder()
                                .flash(on())
                                .build()
                );
                mImageButtonToggleFlash.setImageResource(R.drawable.ic_flash_on_white_36dp);
                break;
        }
    }

    //Toggle the mCurrentLensPosition variable, then set it as the active lens
    private void toggleActiveLens(){
        if(mCurrentLensPosition == FRONT_LENS){
            mCurrentLensPosition = BACK_LENS;
        }
        else if(mCurrentLensPosition == BACK_LENS){
            mCurrentLensPosition = FRONT_LENS;
        }
        setActiveLens();
    }

    //Toggle the mCurrentFlashMode variable, then set the flash mode
    private void toggleFlashMode(){
        switch (mCurrentFlashMode){
            case FLASH_OFF:
                mCurrentFlashMode = FLASH_AUTO;
                break;
            case FLASH_AUTO:
                mCurrentFlashMode = FLASH_ON;
                break;
            case  FLASH_ON:
                mCurrentFlashMode = FLASH_OFF;
                break;
        }
        setFlashMode();
    }

    private void hideSystemUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
    }
}
