package com.external;

import com.t4a.bridge.AIAction;
import com.t4a.bridge.ActionType;
import com.t4a.predict.Predict;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This uses https://open-meteo.com/en/docs/geocoding-api/ free non commercial purpose only
 * if you are planning to ue it for commercial purpose please visit https://open-meteo.com/en/pricing/
 */

@Log
@Predict
public class HttpGetAction implements AIAction {
    public double getTemprature(String cityName) {
        double temperature = 0;
        String urlStr = "https://geocoding-api.open-meteo.com/v1/search?name="+cityName+"&count=1&language=en&format=json";
        String weatherURlStr = "https://api.open-meteo.com/v1/forecast?latitude=";
        try {
            StringBuilder response = getResponseFromURl(urlStr);

            // Parse JSON response
            JSONObject jsonObject  = new JSONObject(response.toString());

            // Extract latitude and longitude from the JSON response
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            // Extract latitude and longitude from the first element of the "results" array
            if (resultsArray.length() > 0) {
                JSONObject result = resultsArray.getJSONObject(0);
                double latitude = result.getDouble("latitude");
                double longitude = result.getDouble("longitude");

                // Print latitude and longitude
                log.info("Latitude: " + latitude);
                log.info("Longitude: " + longitude);


            weatherURlStr = weatherURlStr+latitude+"&longitude="+longitude+"&current=temperature_2m";
            response = getResponseFromURl(weatherURlStr);

            log.info(response.toString());
            jsonObject = new JSONObject(response.toString());
            // Get the "current" object
            JSONObject currentObject = jsonObject.getJSONObject("current");

            // Extract the temperature value
            temperature = currentObject.getDouble("temperature_2m");

            // Print the temperature value
            log.info("Temperature: " + temperature + " °C");
            } else {
                log.info("No results found for the longitude and latidue for the city , city is invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temperature;
    }

    private static StringBuilder getResponseFromURl(String urlStr) throws IOException {
        // Create a URL object
        URL url = new URL(urlStr);

        // Open a connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method
        connection.setRequestMethod("GET");

        // Get the response code
        int responseCode = connection.getResponseCode();
        log.info("Response Code: " + responseCode);

        // Read the response body
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // Disconnect the connection
        connection.disconnect();
        return response;
    }

    public static void main(String[] args) {
        HttpGetAction action = new HttpGetAction();
        action.getTemprature("Toronto");

    }

    @Override
    public String getActionName() {
        return "getTemprature";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.JAVAMETHOD;
    }

    @Override
    public String getDescription() {
        return "get weather for city";
    }
}
