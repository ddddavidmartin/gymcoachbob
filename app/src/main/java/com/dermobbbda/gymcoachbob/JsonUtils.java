package com.dermobbbda.gymcoachbob;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
            JSONArray results_list = new JSONArray();
            for (Exercise e : exercises) {
                JSONObject tmp = new JSONObject();
                tmp.put(context.getString(R.string.json_exercise_name), e.getName());
                results_list.put(tmp);
            }
            outputStream = context.openFileOutput(fileName, context.MODE_PRIVATE);
            outputStream.write(results_list.toString().getBytes());
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

        try {
            inputStream = context.openFileInput(fileName);
            while (inputStream.read(buffer) != -1) {
                fileContent.append(new String(buffer));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Exercises file not found: " + e);
            return null;
        } catch (IOException e) {
            System.out.println("IOException during reading: " + e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d(TAG, "Failed to close stream: " + e);
                }
            }
        }

        List<Exercise> result;
        try {
            JSONArray exercise_list = new JSONArray(fileContent.toString());
            result = new ArrayList<Exercise>(exercise_list.length());
            for (int i = 0; i < exercise_list.length(); i++){
                JSONObject tmp = exercise_list.getJSONObject(i);
                String exercise_name = tmp.getString(context.getString(R.string.json_exercise_name));
                result.add(new Exercise(exercise_name));
            }
        } catch (JSONException e) {
            System.out.println("JSONException during parsing: " + e);
            return null;
        }

        return result;
    }
}
