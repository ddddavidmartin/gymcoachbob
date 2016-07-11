/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import java.util.Date;

public class TimeBasedExerciseSession extends ExerciseSession {
    /** The distance of the respective Session. */
    private double mDistance;
    /** The time in seconds of the respective Session. */
    private int mDuration;

    TimeBasedExerciseSession(Date date, double distance, int time) {
        super(date);
        mDistance = distance;
        mDuration = time;
    }

    public double distance() {
        return mDistance;
    }

    public int duration() {
        return mDuration;
    }
}
