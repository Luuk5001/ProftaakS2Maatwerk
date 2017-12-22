package com.s21m.proftaaks2maatwerk.ui;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import com.s21m.proftaaks2maatwerk.extensions.Application;
import com.s21m.proftaaks2maatwerk.extensions.Bitmap;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends ProgressBarActivity implements ApiListener<PhotoResult> {

    public static String PHOTO_URI_KEY = "result_photo_uri";

    private Uri photoUri;
    private PhotoResult result;

    @BindView(R.id.imageViewPicture)
    ImageView imageViewPicture;
    @BindView(R.id.progressBarPictureTaken)
    ProgressBar progressBar;
    @BindView(R.id.buttonSavePicture)
    Button buttonSavePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
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
                sharePhoto();
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
        intent.putExtra(PhotoResult.PHOTO_RESULT_DATA_KEY, result);
        startActivity(intent);
    }

    @Override
    public void onSuccess(PhotoResult result) {
        this.result = result;
        toggleProgressBar(progressBar);
        try {
            Bitmap bitmap = new Bitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri));
            bitmap.convertToMutable(File.createTempFile("DRAW", ".png", this.getCacheDir()));
            bitmap = drawOnBitmap(bitmap);
            File lastPhotoFile = new File(getFilesDir(), MainActivity.LAST_PICTURE_NAME);
            bitmap.saveToFile(lastPhotoFile);
            photoUri = FileProvider.getUriForFile(this, Application.SHARED_PROVIDER_AUTHORITY, lastPhotoFile);
            imageViewPicture.setImageBitmap(bitmap.getBitmap());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Exception e) {
        toggleProgressBar(progressBar);
        e.printStackTrace();
        Toast.makeText(this, R.string.toast_error, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendPhoto(){
        toggleProgressBar(progressBar);
        try {
            ApiPhoto.getInstance().send(photoUri, this);
        }
        catch (NetworkErrorException e) {
            toggleProgressBar(progressBar);
            Toast.makeText(this, R.string.toast_network_unavailable, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        catch (IOException e) {
            toggleProgressBar(progressBar);
            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void sharePhoto(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image_title)));
    }

    private void savePictureToGallery() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    this.getString(R.string.shared_directory_name)),
                    new SimpleDateFormat("yyyyMMddHHmmss'.png'", Locale.getDefault()).format(new Date())
            );
            FileUtils.forceMkdirParent(file);
            Bitmap bitmap = new Bitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri));
            bitmap.saveToFile(file);
            bitmap.getBitmap().recycle();
            buttonSavePicture.setEnabled(false);
            buttonSavePicture.setBackgroundResource(R.drawable.rounded_shape_gray);
            ((Application)getApplication()).scanFile(file);
            Toast.makeText(this, R.string.toast_save_photo_to_gallery_success, Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.toast_save_photo_to_gallery_error, Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap drawOnBitmap(Bitmap bitmap) {
        int h = bitmap.getBitmap().getHeight();
        int w = bitmap.getBitmap().getWidth();

        Canvas canvas = new Canvas(bitmap.getBitmap());
        Paint paint = new Paint();

        canvas.drawBitmap(bitmap.getBitmap(), 0, 0, paint);

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

