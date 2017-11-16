package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.data.ResultData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PictureTakenActivity extends AppCompatActivity {

    private ResultData mResult;
    private static String SHARED = "myFolder";

    private Bitmap bitmap;

    @BindView(R.id.imageViewPicture)
    ImageView mImageViewPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taken);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mResult = intent.getParcelableExtra(MainActivity.RESULT_KEY);

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

    @OnClick(R.id.buttonSavePicture)
    public void onClickButtonSavePicture(View view){
//        File imagePath = new File(Environment.getExternalStorageDirectory(), SHARED);
//        File newFile = new File(imagePath, "default_image.jpg");
//        Uri contentUri = FileProvider.getUriForFile(this, MainActivity.SHARED_PROVIDER_AUTHORITY, newFile);

        String filename = "pippo.png";
        File sd = new File(Environment.getExternalStorageDirectory(), SHARED);
        if(!sd.exists()){
            if(sd.mkdirs()) {
                Log.d("tag", "success");
            }
        }
        File dest = new File(sd, filename);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO: Save picture in external storage and add to Gallery
    }

    @OnClick(R.id.buttonSendFeedback)
    public void onButtonSendFeedbackClick(View view){
        Intent intent = new Intent(this, FeedbackActivity.class);
        intent.putExtra(MainActivity.RESULT_KEY, mResult);
        startActivity(intent);
    }
}

