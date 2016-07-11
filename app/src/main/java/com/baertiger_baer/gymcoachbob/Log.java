/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

/** A basic wrapper around Android's logging class that only logs for debug builds. */
public class Log {
    private static final boolean LOG = BuildConfig.DEBUG;

    public static void i(String tag, String string) {
        if (LOG) {
            android.util.Log.i(tag, string);
        }
    }

    public static void e(String tag, String string) {
        /* We always log errors as they do not happen often and give insight into things going
         * fatally wrong. */
        android.util.Log.e(tag, string);
    }
    public static void d(String tag, String string) {
        if (LOG) {
            android.util.Log.d(tag, string);
        }
    }
    public static void v(String tag, String string) {
        if (LOG) {
            android.util.Log.v(tag, string);
        }
    }
    public static void w(String tag, String string) {
        if (LOG) {
            android.util.Log.w(tag, string);
        }
    }
}
