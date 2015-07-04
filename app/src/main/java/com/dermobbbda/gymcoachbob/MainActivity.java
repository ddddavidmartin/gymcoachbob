package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {
    public static final int NEW_WORKOUT_REQUEST = 1;
    public static final int NEW_EXERCISE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_quit:
                // TODO: Find good way of exiting the app.
                return true;
            case R.id.action_new_workout:
                newWorkout(getCurrentFocus());
            case R.id.action_new_exercise:
                newExercise(getCurrentFocus());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            System.out.println("Received cancellation of request with code %d " + requestCode + ".");
            return;
        }

        if (requestCode == NEW_WORKOUT_REQUEST && resultCode == RESULT_OK) {
            String res = data.getStringExtra(getString(R.string.EXTRA_WORKOUT_NAME));
            System.out.println("Received workoutName: " + res);
        }

        else if (requestCode == NEW_EXERCISE_REQUEST && resultCode == RESULT_OK) {
            Exercise exercise = (Exercise) data.getSerializableExtra(getString(R.string.EXTRA_EXERCISE));
            System.out.println("Received Exercise: " + exercise);
        }
    }

    public void newWorkout(View view) {
        // Add a new workout.
        Intent intent = new Intent(this, NewWorkoutActivity.class);
        startActivityForResult(intent, NEW_WORKOUT_REQUEST);
    }

    /* Start the respective Activity to add a new exercise. */
    public void newExercise(View view) {
        Intent intent = new Intent(this, NewExerciseActivity.class);
        startActivityForResult(intent, NEW_EXERCISE_REQUEST);
    }
}
