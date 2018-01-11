package com.s21m.proftaaks2maatwerk.data;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoResult implements Parcelable {

    public static final String PHOTO_RESULT_DATA_KEY = "photo_result";

    private final int mAge;
    private final String mEmotion;

    public PhotoResult(int age, String emotion)
    {
        this.mAge = age;
        this.mEmotion = emotion;
    }

    private PhotoResult(Parcel in) {
        mAge = in.readInt();
        mEmotion = in.readString();
    }

    public int getAge() {
        return mAge;
    }

    public String getEmotion() {
        return mEmotion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mAge);
        parcel.writeString(mEmotion);
    }

    public static final Creator<PhotoResult> CREATOR = new Creator<PhotoResult>() {
        @Override
        public PhotoResult createFromParcel(Parcel in) {
            return new PhotoResult(in);
        }

        @Override
        public PhotoResult[] newArray(int size) {
            return new PhotoResult[size];
        }
    };

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":\n" + "\tAge:" + mAge + "\n\tEmotion:" + mEmotion;
    }
}
