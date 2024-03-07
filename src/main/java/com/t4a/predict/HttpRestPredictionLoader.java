package com.t4a.predict;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.http.InputParameter;
import com.t4a.api.ActionType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRestPredictionLoader {
    private  String yamlFile = "http_actions.json";
    private URL resourceUrl = null;
    public void load(Map<String,PredictOptions> predictions, StringBuffer actionNameList) throws LoaderException {

        try {
            parseConfig(predictions,actionNameList);
        } catch (IOException e) {
            throw new LoaderException(e);
        }


    }
    public  List<HttpPredictedAction> parseConfig(Map<String,PredictOptions> predictions, StringBuffer actionNameList) throws IOException {
        if(resourceUrl == null)
            resourceUrl = HttpRestPredictionLoader.class.getClassLoader().getResource(yamlFile);

        if (resourceUrl == null) {
            throw new IllegalArgumentException("File not found: " + yamlFile);
        }
        Gson gson = new Gson();
        List<HttpPredictedAction> actions = new ArrayList<>();

        try (InputStream inputStream = resourceUrl.openStream();
             InputStreamReader reader = new InputStreamReader(inputStream)){
            JsonObject rootObject = gson.fromJson(reader, JsonObject.class);
            JsonArray endpoints = rootObject.getAsJsonArray("endpoints");

            for (JsonElement obj : endpoints) {
                JsonObject endpoint = obj.getAsJsonObject();
                String actionName = endpoint.get("actionName").getAsString();
                String url = endpoint.get("url").getAsString();
                String type = endpoint.get("type").getAsString();
                String description = endpoint.get("description").getAsString();
                List<InputParameter> inputObjects = new ArrayList<>();
                JsonArray inputArray = endpoint.getAsJsonArray("input_object");
                for (JsonElement inputElement : inputArray) {
                    JsonObject inputObj = inputElement.getAsJsonObject();
                    String inputname = inputObj.get("name").getAsString();
                    String inputType = inputObj.get("type").getAsString();
                    String inputDescription = inputObj.get("description").getAsString();
                    Object defaultValue = inputObj.get("defaultValue");
                    InputParameter parameter =  new InputParameter(inputname, inputType, inputDescription);
                    if(defaultValue != null) {
                        parameter.setDefaultValue((String)defaultValue);
                    }
                    inputObjects.add(parameter);
                }
                JsonObject outputObject = endpoint.getAsJsonObject("output_object");
                JsonObject authInterface = endpoint.getAsJsonObject("auth_interface");

                PredictOptions option = new PredictOptions(HttpPredictedAction.class.getName(),description,actionName,actionName);
                option.setActionType(ActionType.HTTP);
                option.setHttpUrl(url);
                option.setHttpType(type);
                option.setHttpAuthInterface(authInterface);
                option.setHttpInputObjects(inputObjects);
                option.setHttpOutputObject(outputObject);
                actionNameList.append(actionName+", ");
                predictions.put(actionName,option);
;
            }

            return actions;
        }
    }


}
