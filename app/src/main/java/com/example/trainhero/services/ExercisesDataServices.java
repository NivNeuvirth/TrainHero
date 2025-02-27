package com.example.trainhero.services;

import android.os.StrictMode;

import com.example.trainhero.utils.Constants;
import com.example.trainhero.models.Exercise;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExercisesDataServices {
    public ArrayList<Exercise> getAllExercises() {
        return fetchExercises(Constants.EXERCISE_BASE_URL + "?limit=0");
    }

    public ArrayList<Exercise> getExercisesByEquipment(String equipment) {
        return fetchExercises(Constants.EXERCISE_BASE_URL + "/equipment/" + equipment + "?limit=0");
    }

    private ArrayList<Exercise> fetchExercises(String urlString) {
        ArrayList<Exercise> exerciseList = new ArrayList<>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestProperty("X-RapidAPI-Key", Constants.EXERCISE_API_KEY);
            request.setRequestProperty("X-RapidAPI-Host", Constants.EXERCISE_API_HOST);
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(request.getInputStream()));
            JsonArray exercisesArray = root.getAsJsonArray();

            for (JsonElement je : exercisesArray) {
                JsonObject obj = je.getAsJsonObject();

                String id = obj.get("id").getAsString();
                String name = obj.get("name").getAsString();
                String target = obj.get("target").getAsString();
                String bodyPart = obj.get("bodyPart").getAsString();
                String equipment = obj.get("equipment").getAsString();
                String gifUrl = obj.get("gifUrl").getAsString();

                List<String> secondaryMuscles = new ArrayList<>();
                JsonArray secondaryMusclesArray = obj.getAsJsonArray("secondaryMuscles");
                if (secondaryMusclesArray != null) {
                    for (JsonElement muscle : secondaryMusclesArray) {
                        secondaryMuscles.add(muscle.getAsString());
                    }
                }

                List<String> instructions = new ArrayList<>();
                JsonArray instructionsArray = obj.getAsJsonArray("instructions");
                if (instructionsArray != null) {
                    for (JsonElement step : instructionsArray) {
                        instructions.add(step.getAsString());
                    }
                }

                exerciseList.add(new Exercise(id, name, bodyPart, equipment, gifUrl, target, secondaryMuscles, instructions));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exerciseList;
    }
}