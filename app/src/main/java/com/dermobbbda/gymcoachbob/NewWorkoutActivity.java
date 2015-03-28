package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class NewWorkoutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_workout, menu);
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

    public void addWorkout(View view) {
        /* Get text of textfield. If no text entered, change it on the button.
         * Prepare return intent with name, set it as result and finish. */
        EditText editText = (EditText) findViewById(R.id.workout_name);
        String workoutName = editText.getText().toString();
        System.out.println("workoutName: " + workoutName);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.EXTRA_WORKOUT_NAME), workoutName);
        setResult(RESULT_OK, returnIntent);
        finish();
     }
}
