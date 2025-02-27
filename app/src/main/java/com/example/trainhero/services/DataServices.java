package com.example.trainhero.services;

import android.os.StrictMode;

import com.example.trainhero.models.Exercise;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataServices {

    public ArrayList<Exercise> getAllExercises() {
        ArrayList<Exercise> exerciseList = new ArrayList<>();
        String urlString = "https://exercisedb.p.rapidapi.com/exercises?limit=0";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            request.setRequestProperty("X-RapidAPI-Key", "4eb84240edmsh338a895e3922f48p1f11a2jsn54678358de5a");
            request.setRequestProperty("X-RapidAPI-Host", "exercisedb.p.rapidapi.com");
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
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

                Exercise exercise = new Exercise(id, name, bodyPart, equipment, gifUrl, target, secondaryMuscles, instructions);
                exerciseList.add(exercise);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exerciseList;
    }

    public ArrayList<Exercise> getExercisesByEquipment(String equipment) {
        ArrayList<Exercise> exerciseList = new ArrayList<>();
        String urlString = "https://exercisedb.p.rapidapi.com/exercises/equipment/" + equipment + "?limit=0";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            request.setRequestProperty("X-RapidAPI-Key", "4eb84240edmsh338a895e3922f48p1f11a2jsn54678358de5a");
            request.setRequestProperty("X-RapidAPI-Host", "exercisedb.p.rapidapi.com");
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray exercisesArray = root.getAsJsonArray();

            for (JsonElement je : exercisesArray) {
                JsonObject obj = je.getAsJsonObject();

                String id = obj.get("id").getAsString();
                String name = obj.get("name").getAsString();
                String target = obj.get("target").getAsString();
                String bodyPart = obj.get("bodyPart").getAsString();
                String equipmentValue = obj.get("equipment").getAsString();
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

                Exercise exercise = new Exercise(id, name, bodyPart, equipmentValue, gifUrl, target, secondaryMuscles, instructions);
                exerciseList.add(exercise);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exerciseList;
    }

}
