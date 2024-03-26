package com.t4a.examples.actions;

import com.t4a.api.JavaMethodAction;
import com.t4a.predict.Predict;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Predict(actionName = "getTemperature", description ="get weather for city" )
public class CustomHttpGetAction implements JavaMethodAction {
    public double getTemperature(String cityName) {
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
                log.debug("Latitude: " + latitude);
                log.debug("Longitude: " + longitude);


            weatherURlStr = weatherURlStr+latitude+"&longitude="+longitude+"&current=temperature_2m";
            response = getResponseFromURl(weatherURlStr);

            log.debug(response.toString());
            jsonObject = new JSONObject(response.toString());
            // Get the "current" object
            JSONObject currentObject = jsonObject.getJSONObject("current");

            // Extract the temperature value
            temperature = currentObject.getDouble("temperature_2m");

            // Print the temperature value
            log.debug("Temperature: " + temperature + " °C");
            } else {
                log.debug("No results found for the longitude and latidue for the city , city is invalid");
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
        log.debug("Response Code: " + responseCode);

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
        CustomHttpGetAction action = new CustomHttpGetAction();
        action.getTemperature("Toronto");

    }


}
