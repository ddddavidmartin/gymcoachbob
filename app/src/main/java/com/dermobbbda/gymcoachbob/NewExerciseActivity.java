/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;


public class NewExerciseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Callback for Radio Button presses. */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch (view.getId()) {
                case R.id.radio_button_new_exercise_weight_based:
                    Log.d(Util.TAG, "Weight-based exercise selected");
                    break;
                case R.id.radio_button_new_exercise_time_based:
                    Log.d(Util.TAG, "Time-based exercise selected");
            }
        }
    }

    public void addExercise(View view) {
        EditText editText = (EditText) findViewById(R.id.exercise_name);
        String exerciseName = editText.getText().toString();

        if (exerciseName.isEmpty()) {
            Log.d(Util.TAG, "Ignoring button press for empty exercise name.");
            return;
        }

        Log.d(Util.TAG, "new exerciseName: " + exerciseName);

        WeightBasedExercise exercise = new WeightBasedExercise(exerciseName);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.EXTRA_EXERCISE), exercise);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
