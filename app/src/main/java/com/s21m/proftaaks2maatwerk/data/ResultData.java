package com.s21m.proftaaks2maatwerk.data;

import android.graphics.Bitmap;

public class ResultData {
    private Bitmap picture;
    private int age;
    private Emotions emotion;

    public ResultData(Bitmap picture, int age, Emotions emotion)
    {
        this.picture = picture;
        this.age = age;
        this.emotion = emotion;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public int getAge() {
        return age;
    }

    public Emotions getEmotion() {
        return emotion;
    }
}
