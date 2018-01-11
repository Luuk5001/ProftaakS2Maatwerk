package com.s21m.proftaaks2maatwerk.ui;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
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
import com.s21m.proftaaks2maatwerk.extensions.Application;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends ProgressBarActivity implements ApiListener<String[]> {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    @BindView(R.id.spinnerEmotion)
    Spinner emotionSpinner;
    @BindView(R.id.progressBarFeedback)
    ProgressBar progressBar;

    private PhotoResult photoResult;
    private SparseArray<String> emotionsSparseArray;

    //================================================================================
    // Activity overrides
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        photoResult = intent.getParcelableExtra(PhotoResult.PHOTO_RESULT_DATA_KEY);

        requestAvailableEmotions();

        if(photoResult == null){
            Toast.makeText(getApplicationContext(), R.string.toast_error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Result data is null, closing activity");
            finish();
        }
    }

    //================================================================================
    // ApiListener overrides
    //================================================================================

    @Override
    public void onSuccess(String[] result) {
        toggleProgressBar(progressBar);
        emotionsSparseArray = new SparseArray<>();
        String[] emotionsArray = new String[result.length];
        String detectedEmotion = null;
        for(int i = 0; i < result.length; i++){
            int resId = Application.getStringIdentifier(this, result[i]);
            emotionsSparseArray.put(i, result[i]);
            //Check if the emotion has a corresponding resource, if so use this value
            //as display value in the spinner
            String emotionResource = (resId == 0) ? result[i] : getString(resId);
            if(emotionResource == null) {
                emotionsArray[i] = result[i];
            }
            else{
                emotionsArray[i] = emotionResource;
            }
            //Check if the emotion from the list equals the detected emotion
            //this emotion will be set al the selected one in the spinner.
            if(result[i].equals(photoResult.getEmotion())){
                detectedEmotion = emotionsArray[i];
            }
        }
        updateUI(emotionsArray, detectedEmotion);
    }

    @Override
    public void onFailure(Exception e) {
        toggleProgressBar(progressBar);
        e.printStackTrace();
        Toast.makeText(this, getString(R.string.toast_feedback_unavailable), Toast.LENGTH_LONG).show();
        finish();
    }

    //================================================================================
    // OnClickEvents
    //================================================================================

    @OnClick(R.id.buttonSubmit)
    public void onClickButtonSubmit(View view) {
        String emotion = emotionsSparseArray.get(emotionSpinner.getSelectedItemPosition());

        Feedback feedback = new Feedback(photoResult, 0, emotion);

        Toast.makeText(getApplicationContext(), feedback.toString(), Toast.LENGTH_LONG).show();
        Log.i(TAG, "Feedback data sent:\n" + feedback.toString());

        finish();
    }

    //================================================================================
    // Methods
    //================================================================================

    private void requestAvailableEmotions() {
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

    private void updateUI(String[] emotions, String detectedEmotion) {
        ArrayAdapter<String> emotionsArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, emotions);
        emotionSpinner.setAdapter(emotionsArrayAdapter);
        if (detectedEmotion != null) {
            int spinnerPosition = emotionsArrayAdapter.getPosition(detectedEmotion);
            emotionSpinner.setSelection(spinnerPosition);
        }
    }
}
