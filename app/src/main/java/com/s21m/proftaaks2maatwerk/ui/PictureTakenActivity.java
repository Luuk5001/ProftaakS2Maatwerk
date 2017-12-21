package com.s21m.proftaaks2maatwerk.ui;

import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.api.ApiListener;
import com.s21m.proftaaks2maatwerk.api.ApiPhoto;
import com.s21m.proftaaks2maatwerk.data.PhotoResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.s21m.proftaaks2maatwerk.utils.Utils.PHOTO_URI_KEY;
import static com.s21m.proftaaks2maatwerk.utils.Utils.RESULT_DATA_KEY;
import static com.s21m.proftaaks2maatwerk.utils.Utils.convertToMutable;
import static com.s21m.proftaaks2maatwerk.utils.Utils.createNewCacheFile;
import static com.s21m.proftaaks2maatwerk.utils.Utils.saveBitmapToFile;
import static com.s21m.proftaaks2maatwerk.utils.Utils.toggleProgressBar;

public class PictureTakenActivity extends AppCompatActivity implements ApiListener<PhotoResult> {

    private static final String SHARED_DIR_NAME = "ProftaakS2Maatwerk";

    private Uri photoUri;
    private PhotoResult result;

    @BindView(R.id.imageViewPicture)
    ImageView imageViewPicture;
    @BindView(R.id.buttonSavePicture)
    Button buttonSavePicture;
    @BindView(R.id.progressBarPictureTaken)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taken);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        photoUri = Uri.parse(intent.getStringExtra(PHOTO_URI_KEY));
        sendPhoto();
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
                shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image_title)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.buttonSavePicture)
    public void onClickButtonSavePicture(View view){
        savePictureToGallery();
    }

    private void savePictureToGallery() {

    }

    @OnClick(R.id.buttonSendFeedback)
    public void onButtonSendFeedbackClick(View view){
        Intent intent = new Intent(this, FeedbackActivity.class);
        intent.putExtra(RESULT_DATA_KEY, result);
        startActivity(intent);
    }

    /*
    @TargetApi(Build.VERSION_CODES.O)
    private void savePictureToGallery() {
        String filename = LocalDateTime.now().toString() + ".png";
        File sd = new File(Environment.getExternalStorageDirectory(), SHARED_DIR_NAME);
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
            buttonSavePicture.setEnabled(false);
            buttonSavePicture.setBackgroundResource(R.drawable.rounded_shape_disabled);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PictureTakenActivity.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    */

    @Override
    public void sendPhotoSuccess(PhotoResult result) {
        toggleProgressBar(this, progressBar);
        this.result = result;
        saveResultPhoto();
        Bitmap bitmap = saveResultPhoto();
        saveToLastPhotoTaken(bitmap);
        imageViewPicture.setImageBitmap(bitmap);
    }

    @Override
    public void sendPhotoFailure(Exception e) {
        toggleProgressBar(this, progressBar);
        e.printStackTrace();
        Toast.makeText(this, R.string.toast_error, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendPhoto(){
        toggleProgressBar(this, progressBar);
        try {
            ApiPhoto.getInstance().send(photoUri, this);
        }
        catch (NetworkErrorException e) {
            toggleProgressBar(this, progressBar);
            Toast.makeText(this, R.string.toast_network_unavailable, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        catch (IOException e) {
            toggleProgressBar(this, progressBar);
            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private Bitmap saveResultPhoto() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            bitmap = drawPhoto(convertToMutable(this, bitmap));
            File cacheFile = createNewCacheFile(this, "DRAWN", ".png");
            saveBitmapToFile(cacheFile, bitmap);
            return bitmap;
        }
        catch (IOException e) {
            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    private void saveToLastPhotoTaken(Bitmap bitmap){
        try {
            File lastPhotoFile = new File(getFilesDir(), MainActivity.LAST_PICTURE_NAME);
            saveBitmapToFile(lastPhotoFile, bitmap);
        }
        catch (IOException e) {
            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private Bitmap drawPhoto(Bitmap bitmap) {
        int h = bitmap.getHeight();
        int w = bitmap.getWidth();

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        canvas.drawBitmap(bitmap, 0, 0, paint);

        //Draw result text
        paint.setColor(Color.WHITE);
        paint.setTextSize(75);
        canvas.drawText(result.getEmotion(), 25, 75, paint);
        canvas.drawText(String.format(Locale.getDefault(),"%d %s",result.getAge(), getString(R.string.result_image_age_suffix)), 25, 175, paint);

        //Draw bottom bar
        Rect r = new Rect(0, h - 50, w, h);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.getResources().getColor(R.color.colorBottomBarResultPicture));
        canvas.drawRect(r, paint);

        //Draw bottom bar color
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        canvas.drawText(getString(R.string.result_image_bottom_bar_text), w - (w - 10), h - 15, paint);

        return bitmap;
    }
}

