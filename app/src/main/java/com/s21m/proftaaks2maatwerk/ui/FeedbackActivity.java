package com.s21m.proftaaks2maatwerk.ui;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.api.ApiEmotions;
import com.s21m.proftaaks2maatwerk.api.ApiListener;
import com.s21m.proftaaks2maatwerk.data.Feedback;
import com.s21m.proftaaks2maatwerk.data.PhotoResult;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends ProgressBarActivity implements ApiListener<String[]> {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    private PhotoResult photoResult;

    @BindView(R.id.spinnerAge)
    Spinner mSpinnerAge;
    @BindView(R.id.spinnerEmotion)
    Spinner mSpinnerEmotion;
    @BindView(R.id.progressBarFeedback)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        requestAvailableEmotions();

        Intent intent = getIntent();
        photoResult = intent.getParcelableExtra(PhotoResult.PHOTO_RESULT_DATA_KEY);

        if(photoResult == null){
            Toast.makeText(getApplicationContext(), R.string.toast_error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Result data is null, closing activity");
            finish();
        }
    }

    @OnClick(R.id.buttonSubmit)
    public void onClickButtonSubmit(View view){
        int age = (int)mSpinnerAge.getSelectedItem();
        String emotion = (String)mSpinnerEmotion.getSelectedItem();

        Feedback feedback = new Feedback(photoResult, age, emotion);

        Toast.makeText(getApplicationContext(), feedback.toString(), Toast.LENGTH_LONG).show();
        Log.i(TAG, "Feedback data sent:\n" + feedback.toString());

        finish();
    }

    @Override
    public void onSuccess(String[] result) {
        toggleProgressBar(progressBar);
        updateUI(getAgeArray(), result);
    }

    @Override
    public void onFailure(Exception e) {
        toggleProgressBar(progressBar);
        e.printStackTrace();
        Toast.makeText(this, getString(R.string.toast_feedback_unavailable), Toast.LENGTH_LONG).show();
        finish();
    }

    private void requestAvailableEmotions(){
        toggleProgressBar(progressBar);
        try {
            ApiEmotions.getInstance().request(this);
        }
        catch (NetworkErrorException e) {
            toggleProgressBar(progressBar);
            Toast.makeText(this, R.string.toast_network_unavailable, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void updateUI(Integer[] ages, String[] emotions) {
        ArrayAdapter<String> emotionsArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, emotions);
        mSpinnerEmotion.setAdapter(emotionsArrayAdapter);
        ArrayAdapter<Integer> ageArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, ages);
        mSpinnerAge.setAdapter(ageArrayAdapter);
        int index = Arrays.asList(ages).indexOf(photoResult.getAge());
        mSpinnerAge.setSelection(index);
        if (photoResult.getEmotion() != null) {
            int spinnerPosition = emotionsArrayAdapter.getPosition(photoResult.getEmotion());
            mSpinnerEmotion.setSelection(spinnerPosition);
        }
    }

    private Integer[] getAgeArray(){
        Integer[] age = new Integer[100];
        for(int i = 0; i < 100; i++){
            age[i] = i + 1;
        }
        return age;
    }
}
