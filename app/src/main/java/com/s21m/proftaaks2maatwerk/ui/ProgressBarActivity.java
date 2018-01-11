package com.s21m.proftaaks2maatwerk.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

public abstract class ProgressBarActivity extends AppCompatActivity {
    /**
     * Toggles the given progressbar's visibility
     * @param bar the progressbar view
     */
    public void toggleProgressBar(ProgressBar bar) {
        if(bar.getVisibility() == View.INVISIBLE){
            bar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
        else{
            bar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
