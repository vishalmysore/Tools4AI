package com.t4a.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.*;
import com.t4a.action.BlankAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.examples.actions.CustomHttpGetAction;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses open weather to get prediction
 */
@Slf4j
public class WeatherSearchExample {
    private String projectId = null;//"cookgptserver";
    private String location = null;//"us-central1";
    private String modelName = null;//"gemini-1.0-pro";

    private String promptText = null;//"Hey I am in Toronto do you think i can go out without jacket,  ";
    public WeatherSearchExample(String[] args) throws Exception {
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
                log.debug("Invalid argument: " + arg);
            }
        }

        // Access values using the keys
        this.projectId = argumentsMap.get("projectId");
        this.location = argumentsMap.get("location");
        this.modelName = argumentsMap.get("modelName");
        this.promptText = argumentsMap.get("promptText");

        // Print the extracted values
        log.debug("projectId: " + projectId);
        log.debug("location: " + location);
        log.debug("modelName: " + modelName);
        log.debug("promptText: " + promptText);
    }
    public static void main(String[] args) throws Exception {

        WeatherSearchExample sample = new WeatherSearchExample(args);
        sample.actionOnPrompt();

    }
    public void actionOnPrompt() throws IOException, InvocationTargetException, IllegalAccessException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            JavaMethodExecutor methodAction = new JavaMethodExecutor();
            CustomHttpGetAction httpAction = new CustomHttpGetAction();

            FunctionDeclaration weatherFunciton = methodAction.buildFunction(httpAction,"getTemperature");

            log.debug("Function declaration h1:");
            log.debug("" + weatherFunciton);

            JavaMethodExecutor additionalQuestion = new JavaMethodExecutor();
            BlankAction blankAction = new BlankAction();
            FunctionDeclaration additionalQuestionFun = additionalQuestion.buildFunction(blankAction);
            log.debug("Function declaration h1:");
            log.debug("" + additionalQuestionFun);
            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(weatherFunciton).addFunctionDeclarations(additionalQuestionFun)
                    .build();


            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            log.debug(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            log.debug("\nPrint response 1 : ");
            log.debug("" + ResponseHandler.getContent(response));
            log.debug(methodAction.getPropertyValuesJsonString(response));

            Object obj = methodAction.action(response, httpAction,"getTemperature");
            log.debug(""+obj);

            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    "getTemprature", Collections.singletonMap("temperature",obj)));


            response = chat.sendMessage(content);

            log.debug("Print response content: ");
            log.debug(""+ResponseHandler.getContent(response));
            log.debug(ResponseHandler.getText(response));


        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
