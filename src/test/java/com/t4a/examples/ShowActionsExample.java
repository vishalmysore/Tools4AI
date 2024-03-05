package com.t4a.examples;

import com.t4a.predict.PredictionLoader;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
@Log
public class ShowActionsExample {
    private String projectId = null;//"cookgptserver";
    private String location = null;//"us-central1";
    private String modelName = null;//"gemini-1.0-pro";

    private String promptText = null;//"Hey I am in Toronto do you think i can go out without jacket,  ";
    public ShowActionsExample(String[] args) throws Exception {
        if(args.length < 1) {
            throw new Exception("provide args in this format projectId=<> location=<> modelName=<> promptText=<>");
        }
        Map<String, String> argumentsMap = new HashMap<>();
        for (String arg : args) {
            // Split the argument into key and value using '=' as delimiter
            String[] parts = arg.split("=");

            // Ensure that the argument is correctly formatted with key and value
            if (parts.length == 2) {
                // Extract key and value
                String key = parts[0];
                String value = parts[1];

                // Store key-value pair in the map
                argumentsMap.put(key, value);
            } else {
                // Handle invalid arguments
                log.info("Invalid argument: " + arg);
            }
        }

        // Access values using the keys
        this.projectId = argumentsMap.get("projectId");
        this.location = argumentsMap.get("location");
        this.modelName = argumentsMap.get("modelName");
        this.promptText = argumentsMap.get("promptText");

        // Print the extracted values
        log.info("projectId: " + projectId);
        log.info("location: " + location);
        log.info("modelName: " + modelName);
        log.info("promptText: " + promptText);
    }
    public static void main(String[] args) throws Exception {

        ShowActionsExample sample = new ShowActionsExample(args);
        sample.showActionList();

    }

    private void showActionList() {
        log.info(PredictionLoader.getInstance(projectId,location,modelName).getActionNameList().toString());
    }
}
