/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class JsonUtils {
    private static final int BUFSIZE = 1024;

    /** Place the versionCode of the app in the given Context in the given JSONObject.
     *  The caller is responsible to handle any JSONExceptions thrown. */
    private static void writeVersionCode(Context context, JSONObject dest) throws JSONException {
        PackageManager pm = context.getPackageManager();
        int versionCode;
        try {
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Failed to find package info: " + e);
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
                } else if (e.type() == Exercise.TYPE_TIME_BASED) {
                    TimeBasedExerciseSession currentSession = (TimeBasedExerciseSession) s;
                    tmpSession.put(context.getString(R.string.json_session_time), currentSession.duration());
                }
                sessions.put(tmpSession);
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
            Log.e("External media '" + path + "' not available: '" + state + "'");
            return null;
        }
    }

    /** Return a File descriptor for the Exercise file on internal storage. */
    private static File getExerciseFilesFromInternalStorage(Context context) {
        File storage = context.getFilesDir();

        return new File(storage, context.getString(R.string.file_exercises));
    }

    /** Return a File descriptor for the Exercise file on external storage.
     *  Returns null if access is not possible. */
    private static File getExerciseFileFromExternalStorage(Context context) {
        File storage = getExternalStorage();
        if (storage == null) {
            return null;
        }

        File dir = new File(storage.getAbsolutePath() + "/" + context.getString(R.string.app_directory_name));
        dir.mkdir();
        return new File(dir, context.getString(R.string.file_exercises));
    }

    /* Export the given Exercises to external storage so that they can be backed up by hand for
     * example with adb. */
    static void exportExercises(Context context, List<Exercise> exercises) {
        File destination = getExerciseFileFromExternalStorage(context);
        if (destination == null) {
            Log.d("Not exporting Exercises as media is not available.");
        } else {
            toFile(context, exercises, destination);
            Toast toast = Toast.makeText(context, "Exported sets to file " + destination + ".", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /* Store the given Exercises to file. */
    static void storeExercises(Context context, List<Exercise> exercises) {
        File destination = getExerciseFilesFromInternalStorage(context);
        if (destination == null) {
            Log.d("Not writing Exercises as media is not available.");
        } else {
            toFile(context, exercises, destination);
        }
    }

    /** Write the given list of Exercises to the given file. */
    private static void toFile(Context context, List<Exercise> exercises, File file) {
        FileOutputStream outputStream = null;
        try {
            JSONObject result = new JSONObject();
            writeVersionCode(context, result);
            writeExercises(context, exercises, result);

            outputStream = new FileOutputStream(file);
            int spaces = context.getResources().getInteger(R.integer.exercise_file_spaces);
            outputStream.write(result.toString(spaces).getBytes());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.d("Failed to close stream: " + e);
                }
            }
        }
    }

    /** Return a new WeightBasedExerciseObject with the given name and Sessions. */
    private static Exercise readWeightBasedExercise(Context context, String name, JSONArray sessionList)
    throws JSONException {

        Exercise exercise = new WeightBasedExercise(name, sessionList.length());

        for (int j = 0; j < sessionList.length(); j++) {
            JSONObject tmpSession = sessionList.getJSONObject(j);
            String dateString = tmpSession.getString(context.getString(R.string.json_session_date));
            Date date = new Date(Long.parseLong(dateString));
            int repetitions = tmpSession.getInt(context.getString(R.string.json_session_repetitions));
            double weight = tmpSession.getDouble(context.getString(R.string.json_session_weight));

            ExerciseSession s = new WeightBasedExerciseSession(date, weight, repetitions);
            /* Add the read Session to the Exercise but do not sync the change back to file
             * again. */
            exercise.add(context, s, /* do not sync changes */ false);
        }

        return exercise;
    }

    private static Exercise readTimeBasedExercise(Context context, String name, JSONArray sessionList)
    throws JSONException {
        Exercise exercise = new TimeBasedExercise(name);

        for (int j = 0; j < sessionList.length(); j++) {
            JSONObject tmpSession = sessionList.getJSONObject(j);
            String dateString = tmpSession.getString(context.getString(R.string.json_session_date));
            Date date = new Date(Long.parseLong(dateString));
            int time = tmpSession.getInt(context.getString(R.string.json_session_time));

            ExerciseSession s = new TimeBasedExerciseSession(date, time);
            exercise.add(context, s, /* do not sync changes */ false);
        }

        return exercise;
    }

    static List<Exercise> readExercisesFromInternalMemory(Context context) {
        File file = getExerciseFilesFromInternalStorage(context);
        return readExercisesFromFile(context, file);
    }

    /* Return exercises read from the given file. */
    private static List<Exercise> readExercisesFromFile(Context context, File file) {
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream inputStream = null;
        ArrayList<Exercise> result = new ArrayList<Exercise>();

        if (file == null) {
            Log.e("Not reading Exercises as media is not available.");
            return result;
        }
        byte[] buffer = new byte[BUFSIZE];

        try {
            inputStream = new FileInputStream(file);
            while (inputStream.read(buffer) != -1) {
                fileContent.append(new String(buffer));
            }
        } catch (FileNotFoundException e) {
            Log.e("Exercises file not found: " + e);
            return result;
        } catch (IOException e) {
            Log.e("IOException during reading: " + e);
            return result;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d("Failed to close stream: " + e);
                }
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(fileContent.toString());

            int versionCode = jsonObj.getInt(context.getString(R.string.json_version_code));
            Log.d("Loading data with versionCode " + versionCode + ".");

            JSONArray exerciseList = jsonObj.getJSONArray(context.getString(R.string.json_exercise_list));
            /* Avoid reallocations of the array by setting the size once in advance. */
            result.ensureCapacity(exerciseList.length());

            for (int i = 0; i < exerciseList.length(); i++) {
                JSONObject tmp = exerciseList.getJSONObject(i);
                String exerciseName = tmp.getString(context.getString(R.string.json_exercise_name));
                int exerciseType = tmp.getInt(context.getString(R.string.json_exercise_type));
                JSONArray sessionList = tmp.getJSONArray(context.getString(R.string.json_exercise_session_list));

                Exercise exercise;
                if (exerciseType == Exercise.TYPE_WEIGHT_BASED) {
                    exercise = readWeightBasedExercise(context, exerciseName, sessionList);
                } else if (exerciseType == Exercise.TYPE_TIME_BASED) {
                    exercise = readTimeBasedExercise(context, exerciseName, sessionList);
                } else {
                    throw new RuntimeException("Trying to write Exercise of unhandled type " + exerciseType + ".");
                }
                result.add(exercise);
            }
        } catch (JSONException e) {
            Log.e("JSONException during parsing: " + e);
            return result;
        }

        return result;
    }
}
