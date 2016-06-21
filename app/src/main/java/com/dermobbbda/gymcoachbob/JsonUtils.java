/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.dermobbbda.gymcoachbob;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    public static final int BUFSIZE = 1024;

    /** Place the versionCode of the app in the given Context in the given JSONObject.
     *  The caller is responsible to handle any JSONExceptions thrown. */
    private static void writeVersionCode(Context context, JSONObject dest) throws JSONException {
        PackageManager pm = context.getPackageManager();
        int versionCode;
        try {
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Util.TAG, "Failed to find package info: " + e);
            /* Not being able to access the versionCode should never happen. If it does we set
             * it to 0 which is never going to be valid as the first release is versionCode 1. */
            versionCode = 0;
        }
        dest.put(context.getString(R.string.json_version_code), versionCode);
    }

    /** Place the list of exercises in the given JSONObject.
     *  The caller is responsible to handle any JSONExceptions thrown. */
    private static void writeExercises(Context context, List<Exercise> exercises, JSONObject dest)
            throws JSONException {

        JSONArray exerciseList = new JSONArray();
        for (Exercise e : exercises) {
            JSONObject tmp = new JSONObject();
            tmp.put(context.getString(R.string.json_exercise_name), e.name());
            tmp.put(context.getString(R.string.json_exercise_type), e.type());

            JSONArray sessions = new JSONArray();
            if (e.type() == Exercise.TYPE_WEIGHT_BASED) {
                for (ExerciseSession s : e.sessions()) {
                    JSONObject tmpSession = new JSONObject();
                    /* We store the date as a long as it is the easiest to parse again.
                     * We may consider using an actual String date as that would make the Json
                     * file itself more humanly readable. */
                    tmpSession.put(context.getString(R.string.json_session_date), s.date().getTime());
                    if (e.type() == Exercise.TYPE_WEIGHT_BASED) {
                        WeightBasedExerciseSession currentSession = (WeightBasedExerciseSession) s;
                        tmpSession.put(context.getString(R.string.json_session_weight), currentSession.weight());
                        tmpSession.put(context.getString(R.string.json_session_repetitions), currentSession.repetitions());
                    }
                    sessions.put(tmpSession);
                }
            }
            tmp.put(context.getString(R.string.json_exercise_session_list), sessions);

            exerciseList.put(tmp);
        }
        dest.put(context.getString(R.string.json_exercise_list), exerciseList);
    }

    /** Return a File descriptor for the External storage.
     *  Returns null if external storage can not be written to. */
    private static File getExternalStorage() {
        File storage = Environment.getExternalStorageDirectory();
        String state = Environment.getExternalStorageState();

        String path = storage.getAbsolutePath();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return storage;
        } else {
            Log.e(Util.TAG, "External media '" + path + "' not available: '" + state + "'");
            return null;
        }
    }

    /** Return a File descriptor for the Exercise file.
     *  Returns null if access is not possible. */
    private static File getExerciseFile(Context context) {
        File storage = getExternalStorage();
        if (storage == null) {
            return null;
        }

        File dir = new File(storage.getAbsolutePath() + "/" + context.getString(R.string.app_directory_name));
        dir.mkdir();
        return new File(dir, context.getString(R.string.file_exercises));
    }

    /** Write a list of Exercises to file. */
    public static void toFile(Context context, List<Exercise> exercises) {
        File outputFile = getExerciseFile(context);
        if (outputFile == null) {
            Log.d(Util.TAG, "Not writing Exercises as media is not available.");
        }
        FileOutputStream outputStream = null;
        try {
            JSONObject result = new JSONObject();
            writeVersionCode(context, result);
            writeExercises(context, exercises, result);

            outputStream = new FileOutputStream(outputFile);
            int spaces = context.getResources().getInteger(R.integer.exercise_file_spaces);
            outputStream.write(result.toString(spaces).getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.d(Util.TAG, "Failed to close stream: " + e);
                }
            }
        }
    }

    /* Return exercises read from the given file. */
    public static List<Exercise> readExercisesFromFile(Context context) {
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream inputStream = null;
        ArrayList<Exercise> result = new ArrayList<Exercise>();

        File inputFile = getExerciseFile(context);
        if (inputFile == null) {
            Log.e(Util.TAG, "Not reading Exercises as media is not available.");
            return result;
        }
        byte[] buffer = new byte[BUFSIZE];

        try {
            inputStream = new FileInputStream(inputFile);
            while (inputStream.read(buffer) != -1) {
                fileContent.append(new String(buffer));
            }
        } catch (FileNotFoundException e) {
            Log.e(Util.TAG, "Exercises file not found: " + e);
            return result;
        } catch (IOException e) {
            Log.e(Util.TAG, "IOException during reading: " + e);
            return result;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d(Util.TAG, "Failed to close stream: " + e);
                }
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(fileContent.toString());

            int versionCode = jsonObj.getInt(context.getString(R.string.json_version_code));
            Log.d(Util.TAG, "Loading data with versionCode " + versionCode + ".");

            JSONArray exerciseList = jsonObj.getJSONArray(context.getString(R.string.json_exercise_list));
            /* Avoid reallocations of the array by setting the size once in advance. */
            result.ensureCapacity(exerciseList.length());

            for (int i = 0; i < exerciseList.length(); i++) {
                JSONObject tmp = exerciseList.getJSONObject(i);
                String exerciseName = tmp.getString(context.getString(R.string.json_exercise_name));
                int exerciseType = tmp.getInt(context.getString(R.string.json_exercise_type));
                JSONArray sessionList = tmp.getJSONArray(context.getString(R.string.json_exercise_session_list));

                Exercise exercise = new WeightBasedExercise(exerciseName, sessionList.length());

                for (int j = 0; j < sessionList.length(); j++) {
                    JSONObject tmpSession = sessionList.getJSONObject(j);
                    String dateString = tmpSession.getString(context.getString(R.string.json_session_date));
                    Date date = new Date(Long.parseLong(dateString));
                    if (exerciseType == Exercise.TYPE_WEIGHT_BASED) {
                        int repetitions = tmpSession.getInt(context.getString(R.string.json_session_repetitions));
                        double weight = tmpSession.getDouble(context.getString(R.string.json_session_weight));

                        /* Add the read Session to the Exercise but do not sync the change back to file
                         * again. */
                        ExerciseSession s = new WeightBasedExerciseSession(date, weight, repetitions);
                        exercise.add(s, /* do not sync changes */ false);
                    }
                }

                result.add(exercise);
            }
        } catch (JSONException e) {
            Log.e(Util.TAG, "JSONException during parsing: " + e);
            return result;
        }

        return result;
    }
}
