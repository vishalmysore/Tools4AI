package com.t4a.examples.enterprise.fly;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.*;

@Log
public class AIFlightAssistant {
    public static void main(String[] args) throws IOException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        String promptText = "My name is vishal i need to fly from toronto to bangalore on 25th of june, what a great day it is";

        String status = bookFlight(projectId, location, modelName, promptText);
        log.info(promptText+ " : "+status);
    }

    public static String bookFlight(String projectId, String location, String modelName,String promptText) {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {

            //create the function getDetails , AI will pick the values and populate
            Map<String, Type> properties = new HashMap<>();
            properties.put("fromLocation", Type.STRING);
            properties.put("toLocation", Type.STRING);
            properties.put("date", Type.STRING);
            properties.put("name", Type.STRING);
            FunctionDeclaration getDetailsOfFlight = FunctionDeclaration.newBuilder()
                    .setName("getDetails")
                    .setDescription("find the location and dates")
                    .setParameters(
                            getBuild(properties)
                    )
                    .build();

            log.info("Function declaration h1:");
            log.info(""+getDetailsOfFlight);


            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(getDetailsOfFlight)
                    .build();


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            log.info(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            log.info("\nPrint response 1 : ");
            log.info(""+ResponseHandler.getContent(response));
            Map<String,String> values =  getPropertyValues(response,(new ArrayList<>(properties.keySet())));
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();
                log.info(propertyName + ": " + propertyValue);
            }

            Gson gson = new Gson();
            String jsonString = gson.toJson(values);

            log.info(jsonString);

            FlightDetails flightDetails = gson.fromJson(jsonString, FlightDetails.class);
            return BookingHelper.bookFlight(flightDetails);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static Map<String, String> getPropertyValues(GenerateContentResponse response, List<String> propertyNames) {
        Map<String, String> propertyValues = new HashMap<>();
        for (String propertyName : propertyNames) {
            String propertyValue = getValue(response, propertyName);
            propertyValues.put(propertyName, propertyValue);
        }
        return propertyValues;
    }

    private static String getValue(GenerateContentResponse response, String propertyName) {
        return ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap().get(propertyName).getStringValue();
    }

    private static Schema getBuild(Type type, String property) {
        return Schema.newBuilder()
                .setType(Type.OBJECT)
                .putProperties(property, Schema.newBuilder()
                        .setType(type)
                        .setDescription(property)
                        .build()
                )
                .addRequired(property)
                .build();
    }
    private static Schema getBuild(Map<String, Type> properties) {
        Schema.Builder schemaBuilder = Schema.newBuilder().setType(Type.OBJECT);

        for (Map.Entry<String, Type> entry : properties.entrySet()) {
            String property = entry.getKey();
            Type type = entry.getValue();

            Schema propertySchema = Schema.newBuilder()
                    .setType(type)
                    .setDescription(property)
                    .build();

            schemaBuilder.putProperties(property, propertySchema)
                    .addRequired(property);
        }

        return schemaBuilder.build();
    }

}
