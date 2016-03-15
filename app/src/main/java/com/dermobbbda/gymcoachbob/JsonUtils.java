package com.dermobbbda.gymcoachbob;

import android.content.Context;
import android.util.Log;

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
    public static final String TAG = "GCB";

    /** Write a list of Exercises to file. */
    public static void toFile(Context context, List<Exercise> exercises) {
        FileOutputStream outputStream = null;
        String fileName = context.getString(R.string.file_exercises);

        try {
            JSONObject result = new JSONObject();
            JSONArray exerciseList = new JSONArray();
            for (Exercise e : exercises) {
                JSONObject tmp = new JSONObject();
                tmp.put(context.getString(R.string.json_exercise_name), e.getName());

                JSONArray sessions = new JSONArray();
                for (Session s : e.getSessions()) {
                    JSONObject tmpSession = new JSONObject();
                    /* We store the date as a long as it is the easiest to parse again.
                     * We may consider using an actual String date as that would make the Json
                     * file itself more humanly readable. */
                    tmpSession.put(context.getString(R.string.json_session_date), s.date().getTime());
                    tmpSession.put(context.getString(R.string.json_session_weight), s.weight());
                    tmpSession.put(context.getString(R.string.json_session_repetitions), s.repetitions());
                    sessions.put(tmpSession);
                }
                tmp.put(context.getString(R.string.json_exercise_session_list), sessions);

                exerciseList.put(tmp);
            }
            result.put(context.getString(R.string.json_exercise_list), exerciseList);

            outputStream = context.openFileOutput(fileName, context.MODE_PRIVATE);
            outputStream.write(result.toString().getBytes());
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
                    Log.d(TAG, "Failed to close stream: " + e);
                }
            }
        }
    }

    /* Return exercises read from the given file. */
    public static List<Exercise> readExercisesFromFile(Context context) {
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream inputStream = null;
        String fileName = context.getString(R.string.file_exercises);
        byte[] buffer = new byte[BUFSIZE];
        ArrayList<Exercise> result = new ArrayList<Exercise>();

        try {
            inputStream = context.openFileInput(fileName);
            while (inputStream.read(buffer) != -1) {
                fileContent.append(new String(buffer));
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exercises file not found: " + e);
            return result;
        } catch (IOException e) {
            Log.e(TAG, "IOException during reading: " + e);
            return result;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d(TAG, "Failed to close stream: " + e);
                }
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(fileContent.toString());
            JSONArray exerciseList = jsonObj.getJSONArray(context.getString(R.string.json_exercise_list));
            /* Avoid reallocations of the array by setting the size once in advance. */
            result.ensureCapacity(exerciseList.length());

            for (int i = 0; i < exerciseList.length(); i++){
                JSONObject tmp = exerciseList.getJSONObject(i);
                String exerciseName = tmp.getString(context.getString(R.string.json_exercise_name));
                JSONArray sessionList = tmp.getJSONArray(context.getString(R.string.json_exercise_session_list));

                Exercise exercise = new Exercise(exerciseName, sessionList.length());

                for (int j = 0; j < sessionList.length(); j++) {
                    JSONObject tmpSession = sessionList.getJSONObject(j);
                    String dateString = tmpSession.getString(context.getString(R.string.json_session_date));
                    Date date = new Date(Long.parseLong(dateString));
                    int repetitions = tmpSession.getInt(context.getString(R.string.json_session_repetitions));
                    int weight = tmpSession.getInt(context.getString(R.string.json_session_weight));

                    /* Add the read Session to the Exercise but do not sync the change back to file
                     * again. */
                    exercise.add(new Session(date, weight, repetitions), /* do not sync changes */ false);
                }

                result.add(exercise);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException during parsing: " + e);
            return result;
        }

        return result;
    }
}
