package com.dermobbbda.gymcoachbob;

import java.io.Serializable;
import java.util.Date;

public class ExerciseSessionWeightBased extends ExerciseSession implements Serializable {
    /** Number of repetitions of the set. */
    private int mRepetitions;
    /** Weight used for the set. */
    private double mWeight;

    ExerciseSessionWeightBased(Date date, double weight, int repetitions) {
        super(date);
        mWeight = weight;
        mRepetitions = repetitions;
    }

    /** Return the weight used for this ExerciseSession. */
    public double weight() {
        return mWeight;
    }

    /** Return the number of repetitions of this ExerciseSession. */
    public int repetitions() {
        return mRepetitions;
    }
}
