package com.dermobbbda.gymcoachbob;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class ViewExerciseActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    /** The Sessions of the current Exercise. */
    private List<Session> mSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercise);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.view_exercise_recyclerview);
        /* Improves performance. Only set to true if changes in content do not change the
         * layout size of the RecyclerView. */
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Intent intent = getIntent();
        int position = intent.getIntExtra(getString(R.string.EXTRA_EXERCISE_POSITION), -1);
        /* This should never happen. */
        if (position == -1) {
            finish();
        }

        Exercise exercise = ExerciseWrapper.getInstance(this).get(position);
        setTitle(exercise.getName());

        mSessions = exercise.getSessions();

        /* Fill sessions with some bogus data until we have support for real Sessions in place.
         * This way we can at least work on getting it to look and work right and see the changes
         * in the app. */
        for (int i = 0; i < 5; i++) {
            Session session = new Session();
            session.add(i, i);
            mSessions.add(session);
        }

        mAdapter = new SessionViewAdapter(mSessions);
        mRecyclerView.setAdapter(mAdapter);
    }
}
