package com.s21m.proftaaks2maatwerk.data;


public class FeedbackData {
    private ResultData mOriginalResult;
    private int mCorrectedAge;
    private Emotions mCorrectedEmotion;

    public FeedbackData(ResultData originalResult, int correctedAge, Emotions correctedEmotion){
        mOriginalResult = originalResult;
        mCorrectedAge = correctedAge;
        mCorrectedEmotion = correctedEmotion;
    }

    public ResultData getmOriginalResult() {
        return mOriginalResult;
    }

    public int getmCorrectedAge() {
        return mCorrectedAge;
    }

    public Emotions getmCorrectedEmotion() {
        return mCorrectedEmotion;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":\n" + "\tAge:" + mCorrectedAge + "\n\tEmotion:"
                + mCorrectedEmotion.name() + "\n" + mOriginalResult.toString();
    }
}
