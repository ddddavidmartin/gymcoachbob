/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

/**
 * Wrapper to handle the state of all Exercises.
 */
class ExerciseWrapper {
    /** Value to show that no Exercise was last accessed. */
    static final int NO_LAST_EXERCISE = -1;
    private static List<Exercise> mMainExercises;
    private static ExerciseWrapper mExerciseWrapper = null;
    /** The position of the Exercise that was last accessed. */
    private static int mLastSelected = NO_LAST_EXERCISE;

    private ExerciseWrapper(Context context) {
        if (mMainExercises != null) {
            return;
        }

        mMainExercises = JsonUtils.readExercisesFromInternalMemory(context);
    }

    /** Return the existing instance of ExerciseWrapper or, if it does not exist, a new one. */
    static ExerciseWrapper getInstance(Context context) {
        if (mExerciseWrapper == null) {
            mExerciseWrapper = new ExerciseWrapper(context);
        }
        return mExerciseWrapper;
    }

    /** Add an Exercise to the list of existing ones.
     *  Updates the persistent Exercises on file.
     *  Returns the position at which the Exercise was inserted. */
    public int add(Context context, Exercise exercise) {
        mMainExercises.add(exercise);
        JsonUtils.storeExercises(context, mMainExercises);
        return mMainExercises.size() - 1;
    }

    /** Return the Exercise at the given position. */
    Exercise get(int position) {
        mLastSelected = position;
        return mMainExercises.get(position);
    }

    /** Return the overall number of existing Exercises. */
    public int size() {
        return mMainExercises.size();
    }

    /** Remove the Exercise at the given position.
     *  Updates the persistent Exercises on file. */
    void remove(Context context, int position) {
        /* If we remove the item that was last selected, then it should not be accessed anymore. */
        if (mLastSelected == position)
        {
            mLastSelected = NO_LAST_EXERCISE;
        }
        mMainExercises.remove(position);
        JsonUtils.storeExercises(context, mMainExercises);
    }

    /** Return the position of the last selected Exercise.
     *  Returns ExerciseWrapper.NO_LAST_EXERCISE if no Exercise was selected yet. */
    int last() {
        return mLastSelected;
    }

    /** Notify the ExerciseWrapper that the existing Exercises have changed.
     *  Use this method to notify the ExerciseWrapper about changes on the Exercises that
     *  were applied without using the ExerciseWrapper methods (eg. without using ExerciseWrapper's
     *  add or remove).
     *  Updates the persistent Exercises on file. */
    static void notifyExercisesChanged(Context context) {
        if (mMainExercises == null) {
            Log.d("Skipping the writing of Exercises to file as they are not initialised yet.");
            return;
        }
        JsonUtils.storeExercises(context, mMainExercises);
    }

    static void importExercises(Context context) {
        List<Exercise> exercises = JsonUtils.importExercises(context);
        if (exercises.size() > 0) {
            mMainExercises = exercises;
            JsonUtils.storeExercises(context, mMainExercises);
            Toast toast = Toast.makeText(context, "Imported " + mMainExercises.size() + " exercise(s).", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /* Export the current Exercises to external storage. */
    static void exportExercises(Context context) {
        if (mMainExercises == null) {
            Log.d("Skipping export of exercises as there are none.");
        } else {
            JsonUtils.exportExercises(context, mMainExercises);
        }
    }
}
