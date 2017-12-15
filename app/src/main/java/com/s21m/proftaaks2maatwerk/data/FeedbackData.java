package com.s21m.proftaaks2maatwerk.data;


public class FeedbackData {
    private ResultData mOriginalResult;
    private int mCorrectedAge;
    private String mCorrectedEmotion;

    public FeedbackData(ResultData originalResult, int correctedAge, String correctedEmotion){
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

    public String getmCorrectedEmotion() {
        return mCorrectedEmotion;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":\n" + "\tAge:" + mCorrectedAge + "\n\tEmotion:"
                + mCorrectedEmotion + "\n" + mOriginalResult.toString();
    }
}
