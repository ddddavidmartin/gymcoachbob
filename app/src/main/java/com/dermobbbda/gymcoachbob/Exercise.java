package com.dermobbbda.gymcoachbob;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Exercise implements Serializable {
    /* Name of the exercise */
    private String mName;
    private List<Session> mSessions;
    /* The default number of Sessions to allocate for an Exercise. */
    private static final int DEFAULT_CAPACITY = 0;

    Exercise(String name) {
        this(name, DEFAULT_CAPACITY);
    }

    /** Create an Exercise with the given name and enough space allocated to save capacity Sessions. */
    Exercise(String name, int capacity) {
        mName = name;
        ArrayList<Session> sessions = new ArrayList<Session>();
        if (capacity > DEFAULT_CAPACITY) {
            /* Setting the size up front avoids reallocations when Sessions are added. */
            sessions.ensureCapacity(capacity);
        }
        mSessions = sessions;
    }

    public String getName() {
        return mName;
    }

    public List<Session> getSessions() {
        return mSessions;
    }

    /** Add the given Session to the Exercise. */
    public void add(Session session) {
        mSessions.add(session);
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
    /** Number of repetitions of the set. */
    private int mRepetitions;
    /** Weight used for the set. */
    private int mWeight;

    Session(int weight, int repetitions) {
        this(new Date(), weight, repetitions);
    }

    Session(Date date, int weight, int repetitions) {
        mDate = date;
        mWeight = weight;
        mRepetitions = repetitions;
    }

    /** Return the time at which the Session took place. */
    public Date date() {
        return mDate;
    }

    /** Return the weight used for this Session. */
    public int weight() {
        return mWeight;
    }

    /** Return the number of repetitions of this Session. */
    public int repetitions() {
        return mRepetitions;
    }
}
