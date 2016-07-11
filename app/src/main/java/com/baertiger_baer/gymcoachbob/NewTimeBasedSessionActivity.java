/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

public class NewTimeBasedSessionActivity extends NewExerciseSessionActivity {
    private int mTime;
    private double mDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_timebased_session);

        initialiseDateText();

        NumberPicker timePicker = (NumberPicker) findViewById(R.id.new_session_time_picker);
        timePicker.setMaxValue(getResources().getInteger(R.integer.new_session_max_time));
        timePicker.setMinValue(getResources().getInteger(R.integer.new_session_min_time));
        timePicker.setValue(getResources().getInteger(R.integer.new_session_min_time));
        /* Allow the picker to wrap around as there is quite a large number of possible values. */
        timePicker.setWrapSelectorWheel(true);
        timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mTime = newValue;
            }
        });

        NumberPicker distancePicker = (NumberPicker) findViewById(R.id.new_session_distance_picker);
        distancePicker.setMaxValue(getResources().getInteger(R.integer.new_session_max_distance));
        distancePicker.setMinValue(getResources().getInteger(R.integer.new_session_min_distance));
        distancePicker.setValue(getResources().getInteger(R.integer.new_session_min_distance));
        /* Allow the picker to wrap around as there is quite a large number of possible values. */
        distancePicker.setWrapSelectorWheel(true);
        distancePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                mDistance = newValue;
            }
        });
    }

    public void addSession(View view) {
        ExerciseSession session = new TimeBasedExerciseSession(mDate, mDistance, mTime);

        Intent returnIntent = createIntentWithSession(session);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
