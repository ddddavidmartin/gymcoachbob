package com.dermobbbda.gymcoachbob;

import java.util.Date;

public class WeightBasedExercise extends Exercise {
    /* The weight used for the last Session that was added. */
    private double mLastWeight = 0;
    /* The number of repetitions used for the last Session that was added. */
    private int mLastRepetitions = 0;

    WeightBasedExercise(String name) {
        super(name);
        mExerciseType = WEIGHT_BASED;
    }

    /* Return the weight that was used for the last Session. */
    public double lastWeight() {
        return mLastWeight;
    }

    /** Return the number of repetitions that were used for the last Session. */
    public int lastRepetitions() {
        return mLastRepetitions;
    }

    public int add(WeightBasedExerciseSession session, boolean syncExercisesOnFile) {
        int position = super.add(session, syncExercisesOnFile);

        boolean setLastWeightAndRepetitions;
        /* We initialise the 'new Session' dialog with the weight and repetitions of the most recently
         * added Session. For this reason we only set these values if the new Session is at the top of
         * the list (i.e. at position 0) or on the same day as the Session at the top of the list. */
        if (position == 0) {
            setLastWeightAndRepetitions = true;
        } else {
            Date mostRecentDate = mSessions.get(0).date();
            setLastWeightAndRepetitions = Util.onSameDay(session.date(), mostRecentDate);
        }
        if (setLastWeightAndRepetitions) {
            mLastWeight = session.weight();
            mLastRepetitions = session.repetitions();
        }

        return position;
    }
}
