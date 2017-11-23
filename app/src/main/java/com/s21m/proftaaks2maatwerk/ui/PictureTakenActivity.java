package com.s21m.proftaaks2maatwerk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.data.ResultData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.s21m.proftaaks2maatwerk.Utilities.REQUEST_STORAGE_PERMISSION;
import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_DATA_KEY;

public class PictureTakenActivity extends AppCompatActivity {

    private ResultData mResult;
    private static String SHARED = "ProftaakS2Maatwerk";

    private Bitmap bitmap;

    @BindView(R.id.imageViewPicture)
    ImageView mImageViewPicture;
    @BindView(R.id.buttonSavePicture)
    Button mButtonSavePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taken);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mResult = intent.getParcelableExtra(RESULT_DATA_KEY);

        bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mResult.getPictureUri());
            mImageViewPicture.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_taken_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: Share image
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick(R.id.buttonSavePicture)
    public void onClickButtonSavePicture(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
            else{
                savePictueToGallery();
            }
        }
        else{
            savePictueToGallery();
        }
    }

    @OnClick(R.id.buttonSendFeedback)
    public void onButtonSendFeedbackClick(View view){
        Intent intent = new Intent(this, FeedbackActivity.class);
        intent.putExtra(RESULT_DATA_KEY, mResult);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(PictureTakenActivity.this, "Permission denied to write your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void savePictueToGallery() {
        String filename = LocalDateTime.now().toString() + ".png";
        File sd = new File(Environment.getExternalStorageDirectory(), SHARED);
        if(!sd.exists()){
            if(sd.mkdirs()) {
                Log.d("tag", "success");
            }
        }
        File dest = new File(sd, filename);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(PictureTakenActivity.this, "Photo has been saved in images", Toast.LENGTH_LONG).show();
            mButtonSavePicture.setEnabled(false);
            mButtonSavePicture.setBackgroundResource(R.drawable.rounded_shape_disabled);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PictureTakenActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

