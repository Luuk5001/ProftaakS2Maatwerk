package com.s21m.proftaaks2maatwerk.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ResultData implements Parcelable {

    private Bitmap mPicture;
    private int mAge;
    private Emotions mEmotion;

    public ResultData(Bitmap picture, int age, Emotions emotion)
    {
        this.mPicture = picture;
        this.mAge = age;
        this.mEmotion = emotion;
    }

    protected ResultData(Parcel in) {
        mPicture = in.readParcelable(Bitmap.class.getClassLoader());
        mAge = in.readInt();
        mEmotion = Emotions.valueOf(in.readString());
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public int getAge() {
        return mAge;
    }

    public Emotions getEmotion() {
        return mEmotion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mPicture, i);
        parcel.writeInt(mAge);
        parcel.writeString(this.mEmotion.name());
    }

    public static final Creator<ResultData> CREATOR = new Creator<ResultData>() {
        @Override
        public ResultData createFromParcel(Parcel in) {
            return new ResultData(in);
        }

        @Override
        public ResultData[] newArray(int size) {
            return new ResultData[size];
        }
    };

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":\n" + "\tAge:" + mAge + "\n\tEmotion:" + mEmotion.name();
    }
}
