package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.extensions.Application;

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

import static io.fotoapparat.parameter.selector.FlashSelectors.autoFlash;
import static io.fotoapparat.parameter.selector.FlashSelectors.off;
import static io.fotoapparat.parameter.selector.FlashSelectors.on;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.back;
import static io.fotoapparat.parameter.selector.LensPositionSelectors.front;
import static io.fotoapparat.parameter.selector.SizeSelectors.biggestSize;

public class CameraActivity extends AppCompatActivity implements PendingResult.Callback<Void> {

    public static final String TAG = CameraActivity.class.getSimpleName();
    public static final String KEY_PHOTO_URI = "camera_photo_uri";
    public static final int RESULT_CAMERA_UNAVAILABLE = 201;

    private static final int FLASH_ON = 0;
    private static final int FLASH_OFF = 1;
    private static final int FLASH_AUTO = 2;
    private static final int FRONT_LENS = 0;
    private static final int BACK_LENS = 1;

    @BindView(R.id.cameraView)
    CameraView mCameraView;
    @BindView(R.id.imageButtonToggleFlash)
    ImageButton mImageButtonToggleFlash;
    @BindView(R.id.imageButtonSwitchCamera)
    ImageButton mImageButtonSwitchCamera;
    @BindView(R.id.buttonTakePicture)
    Button mButtonTakePicture;
    @BindView(R.id.progressBarCamera)
    ProgressBar mProgressBar;

    private Fotoapparat mFotoapparat;
    private int mCurrentLensPosition = FRONT_LENS;
    private int mCurrentFlashMode = FLASH_AUTO;
    private boolean mFrontLensAvailable = false;
    private boolean mBackLensAvailable = false;
    private Uri photoUri;

    public CameraActivity() {
    }

    //================================================================================
    // Activity overrides
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(!checkLensAvailability()){
            setResult(RESULT_CAMERA_UNAVAILABLE);
            finish();
        }

        configureSwitchCameraButton();

        setFotoapparat();
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

    //================================================================================
    // Fotoapparat overrides
    //================================================================================

    @Override
    public void onResult(Void aVoid) {
        Intent data = new Intent();
        data.putExtra(KEY_PHOTO_URI, String.valueOf(photoUri));
        setResult(RESULT_OK, data);
        finish();
    }

    //================================================================================
    // OnClickEvents
    //================================================================================

    @OnClick(R.id.buttonTakePicture)
    public void onClickButtonTakePicture(View view) {
        try {
            File photoFile = File.createTempFile("noCrop", ".jpeg", this.getCacheDir());
            photoUri = FileProvider.getUriForFile(getApplicationContext(), Application.SHARED_PROVIDER_AUTHORITY, photoFile);
            PhotoResult photoResult = mFotoapparat.takePicture();
            photoResult.saveToFile(photoFile).whenAvailable(this);
        }
        catch (IOException e){
            Log.e(TAG, "Failed to save temp photo taken by camera");
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

    private boolean checkLensAvailability() {
        Fotoapparat front = Fotoapparat.with(this).into(mCameraView).lensPosition(front()).build();
        Fotoapparat back = Fotoapparat.with(this).into(mCameraView).lensPosition(back()).build();
        mFrontLensAvailable = front.isAvailable();
        mBackLensAvailable = back.isAvailable();
        if(!mFrontLensAvailable && !mBackLensAvailable){
            Log.d(TAG, "No lenses detected on device");
            return false;
        }
        else{
            if(mFrontLensAvailable){
                mCurrentLensPosition = FRONT_LENS;
                Log.d(TAG, "Front lens detected");
            }
            else{
                mCurrentLensPosition = BACK_LENS;
                Log.d(TAG, "No front lens detected, configuring rear lens");
            }
            return true;
        }
    }

    //================================================================================
    // Methods
    //================================================================================

    //Go to fullscreen mode
    private void hideSystemUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
    }

    //Toggle the mCurrentLensPosition variable, then set it as the active lens
    private void toggleActiveLens(){
        if(mCurrentLensPosition == FRONT_LENS && mBackLensAvailable){
            mCurrentLensPosition = BACK_LENS;
        }
        else if(mCurrentLensPosition == BACK_LENS && mFrontLensAvailable){
            mCurrentLensPosition = FRONT_LENS;
        }
        setFotoapparat();
    }

    //Change the camera lens in the mFotoapparat variable
    private void setFotoapparat(){
        if(mCurrentLensPosition == FRONT_LENS && mFrontLensAvailable){
            mFotoapparat = Fotoapparat
                    .with(this)
                    .into(mCameraView)
                    .photoSize(biggestSize())
                    .lensPosition(front())
                    .build();
        }
        else if(mCurrentLensPosition == BACK_LENS && mBackLensAvailable){
            mFotoapparat = Fotoapparat
                    .with(this)
                    .into(mCameraView)
                    .photoSize(biggestSize())
                    .lensPosition(back())
                    .build();
        }
    }

    //Toggle the mCurrentFlashMode variable, then set the flash mode
    private void toggleFlashMode() {
        switch (mCurrentFlashMode) {
            case FLASH_OFF:
                mCurrentFlashMode = FLASH_AUTO;
                break;
            case FLASH_AUTO:
                mCurrentFlashMode = FLASH_ON;
                break;
            case FLASH_ON:
                mCurrentFlashMode = FLASH_OFF;
                break;
            default:
                Log.e(TAG, "Unknown flash mode given");
                break;
        }
        setFlashMode();
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
            default:
                Log.e(TAG, "Unknown flash mode given");
                break;
        }
    }

    //Set visibility of switch camera button
    private void configureSwitchCameraButton(){
        if(!mFrontLensAvailable == mBackLensAvailable){
            mImageButtonSwitchCamera.setVisibility(View.GONE);
        }
    }
}
