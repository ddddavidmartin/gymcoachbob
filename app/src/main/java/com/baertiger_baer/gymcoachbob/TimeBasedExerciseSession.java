/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import java.util.Date;

class TimeBasedExerciseSession extends ExerciseSession {
    /** The time in seconds of the respective Session. */
    private int mDuration;

    TimeBasedExerciseSession(Date date, int time) {
        super(date);
        mDuration = time;
    }

    int duration() {
        return mDuration;
    }
}
