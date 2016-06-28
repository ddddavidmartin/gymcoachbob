/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import java.util.Date;

public class TimeBasedExerciseSession extends ExerciseSession {
    /** The distance of the respective Session. */
    private double mDistance;
    /** The time in seconds of the respective Session. */
    private int mTime;

    TimeBasedExerciseSession(Date date, double distance, int time) {
        super(date);
        mDistance = distance;
        mTime = time;
    }
}
