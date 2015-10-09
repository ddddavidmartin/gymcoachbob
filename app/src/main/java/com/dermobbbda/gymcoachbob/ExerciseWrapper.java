package com.dermobbbda.gymcoachbob;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Wrapper to handle the state of all Exercises.
 */
public class ExerciseWrapper {
    private static final String TAG = "GCB";
    private static List<Exercise> mMainExercises;
    private static Context mContext;

    ExerciseWrapper(Context context) {
        if (mMainExercises != null) {
            return;
        }

        mContext = context;

        List<Exercise> exercises = JsonUtils.readExercisesFromFile(mContext);
        if (exercises == null) {
            Log.d(TAG, "Parsing failed and exercises are left empty.");
        } else {
            mMainExercises = exercises;
        }
    }

    /** Add an Exercise to the list of existing ones. */
    public void add(Exercise exercise) {
        mMainExercises.add(exercise);
        JsonUtils.toFile(mContext, mMainExercises);
    }

    /** Return the Exercise at the given position. */
    public Exercise get(int position) {
        return mMainExercises.get(position);
    }

    /** Return the overall number of existing Exercises. */
    public int size() {
        return mMainExercises.size();
    }

    /** Remove the Exercise at the given position. */
    public void remove(int position) {
        mMainExercises.remove(position);
        JsonUtils.toFile(mContext, mMainExercises);
    }
}
