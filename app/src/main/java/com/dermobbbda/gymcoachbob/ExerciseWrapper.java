package com.dermobbbda.gymcoachbob;

import android.content.Context;

import java.util.List;

/**
 * Wrapper to handle the state of all Exercises.
 */
public class ExerciseWrapper {
    private static final String TAG = "GCB";
    private static List<Exercise> mMainExercises;
    private static Context mContext;
    private static ExerciseWrapper mExerciseWrapper = null;

    private ExerciseWrapper(Context context) {
        if (mMainExercises != null) {
            return;
        }

        mContext = context;

        List<Exercise> exercises = JsonUtils.readExercisesFromFile(mContext);
        mMainExercises = exercises;
    }

    /** Return the existing instance of ExerciseWrapper or, if it does not exist, a new one. */
    public static ExerciseWrapper getInstance(Context context) {
        if (mExerciseWrapper == null) {
            mExerciseWrapper = new ExerciseWrapper(context);
        }
        return mExerciseWrapper;
    }

    /** Add an Exercise to the list of existing ones.
     *  Returns the position at which the Exercise was inserted. */
    public int add(Exercise exercise) {
        mMainExercises.add(exercise);
        JsonUtils.toFile(mContext, mMainExercises);
        return mMainExercises.size() - 1;
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
