package com.dermobbbda.gymcoachbob;

import android.os.Bundle;
import android.app.Activity;

public class ViewExerciseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercise);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
