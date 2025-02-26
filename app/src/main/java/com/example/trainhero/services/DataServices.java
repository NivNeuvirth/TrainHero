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

    public Exercise getExerciseById(String exerciseId) {
        URL url;
        String sUrl = "https://exercisedb.p.rapidapi.com/exercises/exercise/" + exerciseId;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection request = null;
        Exercise exercise = null;

        try {
            request = (HttpURLConnection) url.openConnection();
            request.setRequestProperty("X-RapidAPI-Key", "4eb84240edmsh338a895e3922f48p1f11a2jsn54678358de5a");
            request.setRequestProperty("X-RapidAPI-Host", "exercisedb.p.rapidapi.com");
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject obj = root.getAsJsonObject();

            // Parsing the fields
            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();
            String bodyPart = obj.get("bodyPart").getAsString();
            String equipment = obj.get("equipment").getAsString();
            String gifUrl = obj.get("gifUrl").getAsString();
            String target = obj.get("target").getAsString();

            // Parsing secondaryMuscles (List of strings)
            JsonElement secondaryMusclesElement = obj.get("secondaryMuscles");
            List<String> secondaryMuscles = new ArrayList<>();
            if (secondaryMusclesElement != null && secondaryMusclesElement.isJsonArray()) {
                JsonArray secondaryArray = secondaryMusclesElement.getAsJsonArray();
                for (JsonElement muscle : secondaryArray) {
                    secondaryMuscles.add(muscle.getAsString());
                }
            }

            // Parsing instructions (List of strings)
            JsonElement instructionsElement = obj.get("instructions");
            List<String> instructions = new ArrayList<>();
            if (instructionsElement != null && instructionsElement.isJsonArray()) {
                JsonArray instructionsArray = instructionsElement.getAsJsonArray();
                for (JsonElement instruction : instructionsArray) {
                    instructions.add(instruction.getAsString());
                }
            }

            // Creating an Exercise object
            exercise = new Exercise(id, name, bodyPart, equipment, gifUrl, target, secondaryMuscles, instructions);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exercise;
    }

    public ArrayList<Exercise> getAllExercises() {
        ArrayList<Exercise> exerciseList = new ArrayList<>();
        String urlString = "https://exercisedb.p.rapidapi.com/exercises?limit=0";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            // Setting headers as in getExerciseById
            request.setRequestProperty("X-RapidAPI-Key", "4eb84240edmsh338a895e3922f48p1f11a2jsn54678358de5a");
            request.setRequestProperty("X-RapidAPI-Host", "exercisedb.p.rapidapi.com");
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray exercisesArray = root.getAsJsonArray();

            for (JsonElement je : exercisesArray) {
                JsonObject obj = je.getAsJsonObject();

                // Extract fields
                String id = obj.get("id").getAsString();
                String name = obj.get("name").getAsString();
                String target = obj.get("target").getAsString();
                String bodyPart = obj.get("bodyPart").getAsString();
                String equipment = obj.get("equipment").getAsString();
                String gifUrl = obj.get("gifUrl").getAsString();

                // Extract List<String> for secondaryMuscles
                List<String> secondaryMuscles = new ArrayList<>();
                JsonArray secondaryMusclesArray = obj.getAsJsonArray("secondaryMuscles");
                if (secondaryMusclesArray != null) {
                    for (JsonElement muscle : secondaryMusclesArray) {
                        secondaryMuscles.add(muscle.getAsString());
                    }
                }

                // Extract List<String> for instructions
                List<String> instructions = new ArrayList<>();
                JsonArray instructionsArray = obj.getAsJsonArray("instructions");
                if (instructionsArray != null) {
                    for (JsonElement step : instructionsArray) {
                        instructions.add(step.getAsString());
                    }
                }

                // Create Exercise object and add it to the list
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

            // Setting headers as in other functions
            request.setRequestProperty("X-RapidAPI-Key", "4eb84240edmsh338a895e3922f48p1f11a2jsn54678358de5a");
            request.setRequestProperty("X-RapidAPI-Host", "exercisedb.p.rapidapi.com");
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray exercisesArray = root.getAsJsonArray();

            for (JsonElement je : exercisesArray) {
                JsonObject obj = je.getAsJsonObject();

                // Extract fields
                String id = obj.get("id").getAsString();
                String name = obj.get("name").getAsString();
                String target = obj.get("target").getAsString();
                String bodyPart = obj.get("bodyPart").getAsString();
                String equipmentValue = obj.get("equipment").getAsString();
                String gifUrl = obj.get("gifUrl").getAsString();

                // Extract List<String> for secondaryMuscles
                List<String> secondaryMuscles = new ArrayList<>();
                JsonArray secondaryMusclesArray = obj.getAsJsonArray("secondaryMuscles");
                if (secondaryMusclesArray != null) {
                    for (JsonElement muscle : secondaryMusclesArray) {
                        secondaryMuscles.add(muscle.getAsString());
                    }
                }

                // Extract List<String> for instructions
                List<String> instructions = new ArrayList<>();
                JsonArray instructionsArray = obj.getAsJsonArray("instructions");
                if (instructionsArray != null) {
                    for (JsonElement step : instructionsArray) {
                        instructions.add(step.getAsString());
                    }
                }

                // Create Exercise object and add it to the list
                Exercise exercise = new Exercise(id, name, bodyPart, equipmentValue, gifUrl, target, secondaryMuscles, instructions);
                exerciseList.add(exercise);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return exerciseList;
    }

}
