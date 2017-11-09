package com.s21m.proftaaks2maatwerk.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.s21m.proftaaks2maatwerk.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void TakePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    public void OpenGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if(resultCode == RESULT_CANCELED){
                    //action cancelled
                }
                if(resultCode == RESULT_OK) {
                    super.onActivityResult(requestCode, resultCode, data);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Intent intent = new Intent(MainActivity.this, PictureTakenActivity.class);
                    startActivity(intent);

                }
                break;

            case 1:
                if (resultCode == RESULT_CANCELED) {
                    // action cancelled
                }
                if (resultCode == RESULT_OK) {
                    Uri selectedimg = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimg);
                        Intent intent = new Intent(MainActivity.this, PictureTakenActivity.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


        }
    }
}
