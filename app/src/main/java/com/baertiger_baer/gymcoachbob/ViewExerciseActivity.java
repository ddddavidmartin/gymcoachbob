/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewExerciseActivity extends ActionBarActivity {
    private static final int NEW_SESSION_REQUEST = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    /** The current Exercise that is visible. */
    private Exercise mExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int position = intent.getIntExtra(getString(R.string.EXTRA_EXERCISE_POSITION), -1);
        /* This should never happen. It can happen though if a 'up button' press instantiates a new
         * ViewExerciseActivity instance, and the Exercise position is not provided. We avoid it by
         * setting the launchMode of this Activity to 'singleTop'. */
        if (position == -1) {
            Log.e("Not starting activity as exercise position is missing.");
            finish();
            return;
        }

        Exercise exercise = ExerciseWrapper.getInstance(getApplicationContext()).get(position);
        setTitle(exercise.name());

        mExercise = exercise;

        if (mExercise.sessions().size() > 0) {
            setUpNonEmptyExercise();
        } else {
            setUpEmptyExercise();
        }
    }

    /** Set up the Exercise layout for when no Sessions have been added yet. */
    protected void setUpEmptyExercise() {
        setContentView(R.layout.activity_view_empty);
        TextView textView = (TextView) findViewById(R.id.activity_view_empty_textview);
        textView.setText(R.string.activity_view_empty_exercise_description);
    }

    /** Set up the Exercise layout for when there are Sessions for this Exercise. */
    protected void setUpNonEmptyExercise() {
        setContentView(R.layout.activity_view_exercise);
        mRecyclerView = (RecyclerView) findViewById(R.id.view_exercise_recyclerview);
        /* Improves performance. Only set to true if changes in content do not change the
         * layout size of the RecyclerView. */
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if (mExercise.type() == Exercise.TYPE_WEIGHT_BASED) {
            mAdapter = new WeightBasedSessionViewAdapter(getApplicationContext(), mExercise);
        } else if (mExercise.type() == Exercise.TYPE_TIME_BASED) {
            mAdapter = new TimeBasedSessionViewAdapter(getApplicationContext(), mExercise);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_exercise_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_new_session:
                if (mExercise.type() == Exercise.TYPE_WEIGHT_BASED) {
                    Intent intent = new Intent(this, NewWeightBasedSessionActivity.class);
                    /* Provide the last added Session details so that the Activity can be initialised
                     * with good default values. */
                    intent.putExtra(getString(R.string.EXTRA_LAST_WEIGHT),
                                    ((WeightBasedExercise) mExercise).lastWeight());
                    intent.putExtra(getString(R.string.EXTRA_LAST_REPETITIONS),
                                    ((WeightBasedExercise) mExercise).lastRepetitions());
                    startActivityForResult(intent, NEW_SESSION_REQUEST);
                } else if (mExercise.type() == Exercise.TYPE_TIME_BASED) {
                    Intent intent = new Intent(this, NewTimeBasedSessionActivity.class);
                    startActivityForResult(intent, NEW_SESSION_REQUEST);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_SESSION_REQUEST && resultCode == RESULT_OK) {
            /* If we added our first Session, we have to show the list of Sessions now. */
            if (mExercise.sessions().size() == 0) {
                setUpNonEmptyExercise();
            }
            ExerciseSession session = (ExerciseSession) data.getSerializableExtra(getString(R.string.EXTRA_SESSION));
            Log.d("Received Session: " + session);
            int position = mExercise.add(session, /* update change on file */ true);
            mAdapter.notifyItemInserted(position);
            if (mExercise.nextSessionNeedsUpdate(position)) {
                mAdapter.notifyItemChanged(position + 1);
            }
            /* Scroll the view to the newly added Session so that it is visible. */
            mRecyclerView.scrollToPosition(position);
        }
    }
}
