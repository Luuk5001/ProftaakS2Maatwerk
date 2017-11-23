package com.s21m.proftaaks2maatwerk.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_DATA_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.saveBitmapToFile;

public class PictureTakenActivity extends AppCompatActivity {

    private ResultData mResult;
    private static String SHARED = "ProftaakS2Maatwerk";

    private Bitmap mPictureBitmap;

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

        File file = new File(getFilesDir(), "lastPicture.png");

        try {
            mPictureBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mResult.getPictureUri());
            saveBitmapToFile(file, mPictureBitmap);
            mImageViewPicture.setImageBitmap(mPictureBitmap);
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
        switch (item.getItemId()){
            case R.id.itemShare:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, mResult.getPictureUri());
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, "Share image to..."));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.buttonSavePicture)
    public void onClickButtonSavePicture(View view){
        savePictureToGallery();
    }

    @OnClick(R.id.buttonSendFeedback)
    public void onButtonSendFeedbackClick(View view){
        Intent intent = new Intent(this, FeedbackActivity.class);
        intent.putExtra(RESULT_DATA_KEY, mResult);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void savePictureToGallery() {
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
            mPictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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

