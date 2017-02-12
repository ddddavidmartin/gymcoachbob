/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.TextView;

public abstract class ViewExerciseActivity extends ActionBarActivity {
    static final int NEW_SESSION_REQUEST = 1;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    /** The current Exercise that is visible. */
    Exercise mExercise;

    /** Set up the Exercise layout for when there are Sessions for this Exercise. */
    abstract void setUpNonEmptyExercise();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_exercise_activity_actions, menu);
        return true;
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
            PositionWithRange posRange = mExercise.add(session, /* update change on file */ true);
            mAdapter.notifyItemInserted(posRange.position());
            if (posRange.rangeStart() != PositionWithRange.UNSET) {
                mAdapter.notifyItemRangeChanged(posRange.rangeStart(), posRange.count());
            }

            /* Scroll the view to the newly added Session so that it is visible. */
            mRecyclerView.scrollToPosition(posRange.position());
        }
    }
}
