package com.s21m.proftaaks2maatwerk.ui;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PictureTakenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taken);

    }

    public void SavePicture(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.imageViewPicture);
        TextView textView = (TextView) findViewById(R.id.textViewTitle);
        imageView.buildDrawingCache();
        Bitmap picture = imageView.getDrawingCache();
        String pictureName = textView.getText().toString();
        PictureToGallery(pictureName, picture);


    }

    public boolean PictureToGallery(String pictureName, Bitmap picture) {

        // TODO Auto-generated method stub


        OutputStream output;

        // Retrieve the image from the res folder

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/Images Classifier/");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, pictureName);

        // Show a toast message on successful save
        Toast.makeText(this, "Image Saved to SD Card",
                Toast.LENGTH_SHORT).show();
        try {

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            picture.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            return true;
        }

        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }




}







