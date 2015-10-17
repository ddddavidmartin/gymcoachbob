package com.dermobbbda.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class ViewExerciseActivity extends Activity {
    public static final String TAG = "GCB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercise);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int position = intent.getIntExtra(getString(R.string.EXTRA_EXERCISE_POSITION), -1);
        /* This should never happen. */
        if (position == -1) {
            finish();
        }
        Log.d(TAG, "Viewing Exercise on Position " + position);
    }

}
