/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

/** A basic wrapper around Android's logging class that only logs for debug builds. */
public class Log {
    private static final boolean LOG = BuildConfig.DEBUG;

    public static void i(String string) {
        if (LOG) {
            android.util.Log.i(Util.TAG, string);
        }
    }

    public static void e(String string) {
        /* We always log errors as they do not happen often and give insight into things going
         * fatally wrong. */
        android.util.Log.e(Util.TAG, string);
    }
    public static void d(String string) {
        if (LOG) {
            android.util.Log.d(Util.TAG, string);
        }
    }
    public static void v(String string) {
        if (LOG) {
            android.util.Log.v(Util.TAG, string);
        }
    }
    public static void w(String string) {
        if (LOG) {
            android.util.Log.w(Util.TAG, string);
        }
    }
}
