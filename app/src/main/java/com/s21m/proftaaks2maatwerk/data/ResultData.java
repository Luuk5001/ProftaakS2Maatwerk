package com.s21m.proftaaks2maatwerk.data;

import android.graphics.Bitmap;

public class ResultData {
    private Bitmap picture;
    private int age;
    private String emotion;

    public ResultData(Bitmap picture, int age, String emotion)
    {
        this.picture = picture;
        this.age = age;
        this.emotion = emotion;
    }

}
