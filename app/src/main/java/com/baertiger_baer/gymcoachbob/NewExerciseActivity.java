/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class NewExerciseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise);
    }

    /** Callback for Radio Button presses. */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch (view.getId()) {
                case R.id.radio_button_new_exercise_weight_based:
                    Log.d("Weight-based exercise selected");
                    break;
                case R.id.radio_button_new_exercise_time_based:
                    Log.d("Time-based exercise selected");
            }
        }
    }

    /** Return the selected type for the new Exercise. */
    private int selectedExerciseType() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_new_exercise);
        final int selectedButtonId = radioGroup.getCheckedRadioButtonId();
        if (selectedButtonId == R.id.radio_button_new_exercise_weight_based) {
            return Exercise.TYPE_WEIGHT_BASED;
        } else if (selectedButtonId == R.id.radio_button_new_exercise_time_based) {
            return Exercise.TYPE_TIME_BASED;
        } else {
            throw new RuntimeException("Unknown Exercise type button used.");
        }
    }

    public void addExercise(View view) {
        EditText editText = (EditText) findViewById(R.id.exercise_name);
        String exerciseName = editText.getText().toString();

        if (exerciseName.isEmpty()) {
            Log.d("Ignoring button press for empty exercise name.");
            return;
        }

        Log.d("new exerciseName: " + exerciseName);

        Exercise exercise;
        int exerciseType = selectedExerciseType();
        if (exerciseType == Exercise.TYPE_WEIGHT_BASED) {
            exercise = new WeightBasedExercise(exerciseName);
        } else if (exerciseType == Exercise.TYPE_TIME_BASED) {
            exercise = new TimeBasedExercise(exerciseName);
        } else {
            throw new RuntimeException("Trying to add Exercise of unknown type.");
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.EXTRA_EXERCISE), exercise);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
