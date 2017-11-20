package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.Utilities;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;

public class CameraActivity extends AppCompatActivity {

    private Fotoapparat mFotoapparat;

    @BindView(R.id.cameraView)
    CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

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
}
