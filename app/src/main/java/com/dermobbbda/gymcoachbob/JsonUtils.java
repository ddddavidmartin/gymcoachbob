package com.dermobbbda.gymcoachbob;

import android.content.Context;
import java.io.FileOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
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
}
