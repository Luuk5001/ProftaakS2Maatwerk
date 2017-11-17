package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.s21m.proftaaks2maatwerk.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

import static com.s21m.proftaaks2maatwerk.ui.MainActivity.SHARED_PROVIDER_AUTHORITY;

public class CameraActivity extends AppCompatActivity {

    private Fotoapparat mFotoapparat;

    @BindView(R.id.cameraView)
    CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        mFotoapparat = Fotoapparat
                .with(this)
                .into(mCameraView)
                .build();
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
        final File photoFile = createNewImageFile();
        final Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), SHARED_PROVIDER_AUTHORITY, photoFile);
        PhotoResult photoResult = mFotoapparat.takePicture();
        photoResult.saveToFile(photoFile).whenAvailable(new PendingResult.Callback<Void>() {
            @Override
            public void onResult(Void aVoid) {
                Intent data = new Intent();
                data.putExtra(MainActivity.CAMERA_RESULT_KEY, String.valueOf(fileUri));
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private File createNewImageFile() {
        try{
            return File.createTempFile("NOT_CROPPED", null, getCacheDir());
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
