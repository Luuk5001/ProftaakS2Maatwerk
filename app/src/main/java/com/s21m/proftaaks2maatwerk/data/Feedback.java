package com.s21m.proftaaks2maatwerk.data;


public class Feedback {
    private PhotoResult mOriginalResult;
    private int mCorrectedAge;
    private String mCorrectedEmotion;

    public Feedback(PhotoResult originalResult, int correctedAge, String correctedEmotion){
        mOriginalResult = originalResult;
        mCorrectedAge = correctedAge;
        mCorrectedEmotion = correctedEmotion;
    }

    public PhotoResult getmOriginalResult() {
        return mOriginalResult;
    }

    public int getmCorrectedAge() {
        return mCorrectedAge;
    }

    public String getmCorrectedEmotion() {
        return mCorrectedEmotion;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":\n" + "\tAge:" + mCorrectedAge + "\n\tEmotion:"
                + mCorrectedEmotion + "\n" + mOriginalResult.toString();
    }
}
