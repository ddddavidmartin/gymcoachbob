package com.dermobbbda.gymcoachbob;

import java.io.Serializable;

public class Exercise implements Serializable {
    /* Name of the exercise */
    private String mName;

    Exercise(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    @Override
    public String toString() {
        return "Exercise: " + this.mName;
    }
}
