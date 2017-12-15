package com.s21m.proftaaks2maatwerk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.s21m.proftaaks2maatwerk.R;
import com.s21m.proftaaks2maatwerk.Utilities;
import com.s21m.proftaaks2maatwerk.data.FeedbackData;
import com.s21m.proftaaks2maatwerk.data.ResultData;

import org.json.JSONArray;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.s21m.proftaaks2maatwerk.Utilities.RESULT_DATA_KEY;
import static com.s21m.proftaaks2maatwerk.Utilities.toggleProgressBar;

public class FeedbackActivity extends AppCompatActivity {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    private ResultData mResultData;
    private Integer[] mAge;
    private String[] mEmotions;
    private ArrayAdapter<String> emotionsArrayAdapter;

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

        Intent intent = getIntent();
        mResultData = intent.getParcelableExtra(RESULT_DATA_KEY);

        if(mResultData == null){
            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Result data is null, closing activity");
            finish();
        }

        getEmotions();

        mAge = getAgeArray();

        ArrayAdapter<Integer> ageArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mAge);
        mSpinnerAge.setAdapter(ageArrayAdapter);
    }

    @OnClick(R.id.buttonSubmit)
    public void onClickButtonSubmit(View view){
        int age = (int)mSpinnerAge.getSelectedItem();
        String emotion = (String)mSpinnerEmotion.getSelectedItem();

        FeedbackData feedbackData = new FeedbackData(mResultData, age, emotion);

        Toast.makeText(getApplicationContext(), feedbackData.toString(), Toast.LENGTH_LONG).show();
        Log.i(TAG, "Feedback data sent:\n" + feedbackData.toString());

        finish();
    }

    private void setEmotionSpinnerContent(){
        emotionsArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mEmotions);
        mSpinnerEmotion.setAdapter(emotionsArrayAdapter);
    }

    private void updateUI() {
        int index = Arrays.asList(mAge).indexOf(mResultData.getAge());
        mSpinnerAge.setSelection(index);
        if (mResultData.getEmotion() != null) {
            int spinnerPosition = emotionsArrayAdapter.getPosition(mResultData.getEmotion());
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

    private void getEmotions(){
        if(Utilities.isNetworkAvailable(this)){

            toggleProgressBar(this, progressBar);

            String apiUrl = "http://i359079.venus.fhict.nl/api/Classifier";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiUrl).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleProgressBar(FeedbackActivity.this, progressBar);
                        }
                    });
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Feedback not available at this time", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleProgressBar(FeedbackActivity.this, progressBar);
                        }
                    });
                    try{
                        ResponseBody body = response.body();
                        String jsonData = body != null ? body.string() : null;
                        if (jsonData != null) {
                        JSONArray jsonArray = new JSONArray(jsonData);
                            final String[] emotions = new String[jsonArray.length()];
                            for (int i=0;i<jsonArray.length();i++){
                                emotions[i] = jsonArray.get(i).toString();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEmotions = emotions;
                                    setEmotionSpinnerContent();
                                    updateUI();
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Feedback not available at this time", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Feedback not available at this time", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "Feedback not available at this time", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
