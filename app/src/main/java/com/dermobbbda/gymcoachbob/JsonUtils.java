package com.dermobbbda.gymcoachbob;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    public static final int BUFSIZE = 1024;

    public static String toJson(Context context, Exercise exercise) {
        try {
            JSONObject res = new JSONObject();
            res.put(context.getString(R.string.json_exercise_name), exercise.getName());
            return res.toString();
        } catch(JSONException e) {
            System.out.println("Caught exception: " + e);
        }

        return null;
    }

    public static void toFile(Context context, Exercise exercise) {
        FileOutputStream outputStream;
        String fileName = context.getString(R.string.file_exercises);

        try {
            outputStream = context.openFileOutput(fileName, context.MODE_PRIVATE);
            outputStream.write(toJson(context, exercise).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Return an exercise read from the given file. */
    public static Exercise readExerciseFromFile(Context context) {
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream inputStream;
        String fileName = context.getString(R.string.file_exercises);
        byte[] buffer = new byte[BUFSIZE];
        String exerciseName;

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
        }

        try {
            JSONObject jObj = new JSONObject(fileContent.toString());
            exerciseName = jObj.getString(context.getString(R.string.json_exercise_name));
        } catch (JSONException e) {
            System.out.println("JSONException during parsing: " + e);
            return null;
        }

        return new Exercise(exerciseName);
    }
}
