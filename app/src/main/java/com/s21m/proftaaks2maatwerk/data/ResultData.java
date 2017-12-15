package com.s21m.proftaaks2maatwerk.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ResultData implements Parcelable {

    private Uri mPictureUri;
    private int mAge;
    private String mEmotion;

    public ResultData(Uri pictureUri, int age, String emotion)
    {
        this.mPictureUri = pictureUri;
        this.mAge = age;
        this.mEmotion = emotion;
    }

    protected ResultData(Parcel in) {
        mPictureUri = Uri.parse(in.readString());
        mAge = in.readInt();
        mEmotion = in.readString();
    }

    public Uri getPictureUri() {
        return mPictureUri;
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
        parcel.writeString(mPictureUri.toString());
        parcel.writeInt(mAge);
        parcel.writeString(mEmotion);
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
        return this.getClass().getSimpleName() + ":\n" + "\tAge:" + mAge + "\n\tEmotion:" + mEmotion;
    }
}
