package com.dermobbbda.gymcoachbob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Exercise implements Serializable {
    /* Name of the exercise */
    private String mName;
    private List<Session> mSessions;

    Exercise(String name) {
        mName = name;
        mSessions = new ArrayList<Session>();
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return "Exercise: " + mName;
    }
}

/** A workout session, i.e. a number of repetitions of an Exercise at a specific date. */
class Session implements Serializable {
    /** The time when the Session was done. */
    private Date mDate;
    /** Number of repetitions per set. */
    private List<Integer> mRepetitions;
    /** Weight used per set. */
    private List<Integer> mWeights;

    Session() {
        mDate = new Date();
        mRepetitions = new ArrayList<Integer>();
        mWeights = new ArrayList<Integer>();
    }

    /** Add a set to the workout session. */
    public void add(int repetitions, int weight) {
        mRepetitions.add(repetitions);
        mWeights.add(weight);
    }
}
