package com.t4a.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.*;
import com.t4a.action.BlankAction;
import com.t4a.action.http.GenericHttpAction;
import com.t4a.action.http.InputParameter;
import com.t4a.api.JavaMethodExecutor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Log
public class GenericHtppTest {
    private String projectId = null;//"cookgptserver";
    private String location = null;//"us-central1";
    private String modelName = null;//"gemini-1.0-pro";



    private String promptText = null;//"Hey I am in Toronto do you think i can go out without jacket,  ";
    public GenericHtppTest(String[] args) throws Exception {
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

        GenericHtppTest sample = new GenericHtppTest(args);
        sample.actionOnPrompt(args);

    }

    public void actionOnPrompt(String[] args) throws IOException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            JavaMethodExecutor methodAction = new JavaMethodExecutor();
            GenericHttpAction httpAction = new GenericHttpAction();
            httpAction.setActionName("getTemperature");
            httpAction.setUrl("https://geocoding-api.open-meteo.com/v1/search");
            httpAction.setType("GET");
            InputParameter cityParameter = new InputParameter("name","String","name of the city");
            InputParameter countparameter = new InputParameter("count","String","count");
            List<InputParameter> parameters = new ArrayList<InputParameter>();

            InputParameter language = new InputParameter("language","String","language","en");
            InputParameter format = new InputParameter("format","String","language","json");
            parameters.add(cityParameter);
            parameters.add(countparameter);
            parameters.add(language);
            parameters.add(format);
            httpAction.setInputObjects(parameters);
            httpAction.setDescription("get temperature in real time");
            FunctionDeclaration weatherFunciton = methodAction.buildFunciton(httpAction);

            log.info("Function declaration h1:");
            log.info("" + weatherFunciton);

            JavaMethodExecutor additionalQuestion = new JavaMethodExecutor();
            BlankAction blankAction = new BlankAction();
            FunctionDeclaration additionalQuestionFun = additionalQuestion.buildFunciton(blankAction);
            log.info("Function declaration h1:");
            log.info("" + additionalQuestionFun);
            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(weatherFunciton).addFunctionDeclarations(additionalQuestionFun)
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
            log.info("" + ResponseHandler.getContent(response));
            log.info(methodAction.getPropertyValuesJsonString(response));

            Object obj = methodAction.action(response, httpAction);
            log.info(""+obj);

            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    httpAction.getActionName(), Collections.singletonMap(httpAction.getActionName(),obj)));


            response = chat.sendMessage(content);

            log.info("Print response content: ");
            log.info(""+ResponseHandler.getContent(response));
            log.info(ResponseHandler.getText(response));


        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
