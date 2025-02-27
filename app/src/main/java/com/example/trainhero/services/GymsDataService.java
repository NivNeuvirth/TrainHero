package com.example.trainhero.services;

import android.os.StrictMode;

import com.example.trainhero.Constants;
import com.example.trainhero.models.Gym;
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

public class GymsDataService {

    public List<Gym> getNearbyGyms(double latitude, double longitude) {
        URL url;
        String sUrl = Constants.GOOGLE_MAPS_NEARBY_BASE_URL + latitude + "," + longitude + "&radius=5000&type=gym&key=" + Constants.GOOGLE_API_KEY;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<Gym> gyms = new ArrayList<>();

        try {
            url = new URL(sUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection request = null;

        try {
            request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject obj = root.getAsJsonObject();

            JsonArray results = obj.getAsJsonArray("results");

            for (JsonElement result : results) {
                JsonObject gym = result.getAsJsonObject();
                String name = gym.get("name").getAsString();
                double lat = gym.getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble();
                double lng = gym.getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble();

                gyms.add(new Gym(name, lat, lng));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return gyms;
    }
}
