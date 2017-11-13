package com.s21m.proftaaks2maatwerk.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ResultData implements Parcelable {

    private Uri mImageUri;
    private int mAge;
    private Emotions mEmotion;

    public ResultData(Uri imageUri, int age, Emotions emotion)
    {
        this.mImageUri = imageUri;
        this.mAge = age;
        this.mEmotion = emotion;
    }

    protected ResultData(Parcel in) {
        mImageUri = Uri.parse(in.readString());
        mAge = in.readInt();
        mEmotion = Emotions.valueOf(in.readString());
    }

    public Uri getPicturePath() {
        return mImageUri;
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
        parcel.writeString(mImageUri.toString());
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
