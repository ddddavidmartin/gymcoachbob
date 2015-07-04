package com.dermobbbda.gymcoachbob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class NewExerciseActivity extends Activity {

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

    public void addExercise(View view) {
        EditText editText = (EditText) findViewById(R.id.exercise_name);
        String exerciseName = editText.getText().toString();
        System.out.println("exerciseName: " + exerciseName);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.EXTRA_EXERCISE_NAME), exerciseName);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
