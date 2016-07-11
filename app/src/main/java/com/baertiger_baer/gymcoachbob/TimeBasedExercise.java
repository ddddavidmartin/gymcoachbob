/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

/* Exercises that are measured in time and distance. */
public class TimeBasedExercise extends Exercise {
    TimeBasedExercise(String name) {
        super(name);

        mExerciseType = TYPE_TIME_BASED;
    }
}
