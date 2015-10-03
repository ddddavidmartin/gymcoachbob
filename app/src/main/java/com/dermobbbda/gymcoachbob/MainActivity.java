package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    public static final int NEW_WORKOUT_REQUEST = 1;
    public static final int NEW_EXERCISE_REQUEST = 2;

    /** All Exercises the app knows about. */
    private List<Exercise> main_exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_exercises = new ArrayList<Exercise>();

        List<Exercise> exercises = JsonUtils.readExercisesFromFile(this);
        if (exercises == null) {
            System.out.println("Parsing failed and exercises are left empty.");
        } else {
            main_exercises = exercises;
        }
        if (main_exercises != null) {
            TextView text = (TextView) findViewById(R.id.main_exercise_name);
            String exercise_names = "Exercises: ";
            for (Exercise e : main_exercises) {
                exercise_names += " " + e.getName();
            }
            text.setText(exercise_names);
        }
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
            main_exercises.add(exercise);
            JsonUtils.toFile(this, main_exercises);
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
