package com.t4a.examples.enterprise.multi;

import com.t4a.examples.enterprise.fly.BookingHelper;
import com.t4a.examples.enterprise.fly.FlightDetails;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;

/**
 * Flight and Restaurant booking together
 */
public class MultiBot {
        public static void main(String[] args) throws IOException {

            String projectId = "cookgptserver";
            String location = "us-central1";
            String modelName = "gemini-1.0-pro";

            String promptText = "My name is vishal i need to fly from toronto to bangalore on Indian Independence day, then i need to eat paneer butter masala with 4 people at Maharaja restaurant on Canada Independence day";

            String status = bookFlightAndDinner(projectId, location, modelName, promptText);
            System.out.println(promptText+ " : "+status);
        }

        public static String bookFlightAndDinner(String projectId, String location, String modelName,String promptText) {
            try (VertexAI vertexAI = new VertexAI(projectId, location)) {

                Map<String, Type> dinnerProperties = new HashMap<>();
                dinnerProperties.put("restaurantName", Type.STRING);
                dinnerProperties.put("people", Type.STRING);
                dinnerProperties.put("date", Type.STRING);
                dinnerProperties.put("recipe", Type.STRING);
                FunctionDeclaration dinnerPropertiesFunction = FunctionDeclaration.newBuilder()
                        .setName("dinnerReservationDetails")
                        .setDescription("find the details for dinner reservation")
                        .setParameters(
                                getBuild(dinnerProperties)
                        )
                        .build();

                System.out.println("Function declaration h2:");
                System.out.println(dinnerPropertiesFunction);

                Map<String, Type> properties = new HashMap<>();
                properties.put("fromLocation", Type.STRING);
                properties.put("toLocation", Type.STRING);
                properties.put("date", Type.STRING);
                properties.put("name", Type.STRING);
                FunctionDeclaration getDetailsOfFlight = FunctionDeclaration.newBuilder()
                        .setName("getFlightDetails")
                        .setDescription("find the filght booking details")
                        .setParameters(
                                getBuild(properties)
                        )
                        .build();

                System.out.println("Function declaration h1:");
                System.out.println(getDetailsOfFlight);



                Tool tool = Tool.newBuilder()
                        .addFunctionDeclarations(getDetailsOfFlight)
                        .addFunctionDeclarations(dinnerPropertiesFunction)
                        .build();


                GenerativeModel model =
                        new GenerativeModel.Builder()
                                .setModelName(modelName)
                                .setVertexAi(vertexAI)
                                .setTools(Arrays.asList(tool))
                                .build();
                ChatSession chat = model.startChat();

                System.out.println(String.format("Ask the question 1: %s", promptText));
                GenerateContentResponse response = chat.sendMessage(promptText);

                System.out.println("\nPrint response 1 : ");
                System.out.println(ResponseHandler.getContent(response));

                Map<String,String> values =  getPropertyValues(response,(new ArrayList<>(properties.keySet())));
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    String propertyName = entry.getKey();
                    String propertyValue = entry.getValue();
                    System.out.println(propertyName + ": " + propertyValue);
                }

                Gson gson = new Gson();
                String jsonString = gson.toJson(values);

                System.out.println(jsonString);

                FlightDetails flightDetails = gson.fromJson(jsonString, FlightDetails.class);
                Content content =
                        ContentMaker.fromMultiModalData(
                                PartMaker.fromFunctionResponse(
                                        "getFlightDetails",Collections.singletonMap("","")));
                //call the second function
                response = chat.sendMessage(content);

                System.out.println("Print response content: ");
                System.out.println(ResponseHandler.getContent(response));
                System.out.println(BookingHelper.bookFlight(flightDetails));

                //fetch the resuaurent details
                Map<String,String> restaurantValues =  getPropertyValues(response,(new ArrayList<>(dinnerProperties.keySet())));
                for (Map.Entry<String, String> entry : restaurantValues.entrySet()) {
                    String propertyName = entry.getKey();
                    String propertyValue = entry.getValue();
                    System.out.println(propertyName + ": " + propertyValue);
                }

                Gson restaurentGson = new Gson();
                String restaurentJsonString = restaurentGson.toJson(restaurantValues);
                //send this to restaurant booking system
                System.out.println(restaurentJsonString);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return "all done";
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