package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.data.Emotions;
import com.s21m.proftaaks2maatwerk.data.FeedbackData;
import com.s21m.proftaaks2maatwerk.data.ResultData;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends AppCompatActivity {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    private ResultData mResultData;
    private Integer[] mAge;

    @BindView(R.id.spinnerAge)
    Spinner mSpinnerAge;
    @BindView(R.id.spinnerEmotion)
    Spinner mSpinnerEmotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mResultData = intent.getParcelableExtra(PictureTakenActivity.RESULTDATA_KEY);

        if(mResultData == null){
            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Result data is null, closing activity");
            finish();
        }

        mAge = getAgeArray();

        ArrayAdapter<Integer> ageArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mAge);
        mSpinnerAge.setAdapter(ageArrayAdapter);

        ArrayAdapter<Emotions> emotionsArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, Emotions.values());
        mSpinnerEmotion.setAdapter(emotionsArrayAdapter);

        updateUI();
    }

    @OnClick(R.id.buttonSubmit)
    public void onClickButtonSubmit(View view){
        int age = (int)mSpinnerAge.getSelectedItem();
        Emotions emotion = (Emotions)mSpinnerEmotion.getSelectedItem();

        FeedbackData feedbackData = new FeedbackData(mResultData, age, emotion);

        Toast.makeText(getApplicationContext(), feedbackData.toString(), Toast.LENGTH_LONG).show();
        Log.i(TAG, "Feedback data sent:\n" + feedbackData.toString());

        finish();
    }

    private void updateUI() {
        int index = Arrays.asList(mAge).indexOf(mResultData.getAge());
        mSpinnerAge.setSelection(index);
        mSpinnerEmotion.setSelection(mResultData.getEmotion().ordinal());
    }

    private Integer[] getAgeArray(){
        Integer[] age = new Integer[100];
        for(int i = 0; i < 100; i++){
            age[i] = i + 1;
        }
        return age;
    }
}
