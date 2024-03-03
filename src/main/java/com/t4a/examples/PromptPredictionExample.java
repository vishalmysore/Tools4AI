package com.t4a.examples;

import com.t4a.action.BlankAction;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.*;
import com.t4a.api.AIAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.predict.PredictionLoader;
import lombok.extern.java.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This example does not require you to specify the action to take it will automatically detect the action
 * to be taken based on list of prompts and predict accurately what needs to be done
 */
@Log
public class PromptPredictionExample {
    private String projectId = null;//"cookgptserver";
    private String location = null;//"us-central1";
    private String modelName = null;//"gemini-1.0-pro";

    private String promptText = null;//"Hey I am in Toronto do you think i can go out without jacket,  ";
    public PromptPredictionExample(String[] args) throws Exception {
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

        PromptPredictionExample sample = new PromptPredictionExample(args);
        sample.actionOnPrompt();

    }
    public void actionOnPrompt() throws IOException, InvocationTargetException, IllegalAccessException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            AIAction predictedAction = PredictionLoader.getInstance(projectId, location,modelName).getPredictedAction(promptText);
            log.info(predictedAction.getActionName());
            JavaMethodExecutor methodAction = new JavaMethodExecutor();

            FunctionDeclaration weatherFunciton = methodAction.buildFunciton(predictedAction);

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

            Object obj = methodAction.action(response, predictedAction);
            log.info(""+obj);

            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    predictedAction.getActionName(), Collections.singletonMap(predictedAction.getActionName(),obj)));


            response = chat.sendMessage(content);

            log.info("Print response content: ");
            log.info(""+ResponseHandler.getContent(response));
            log.info(ResponseHandler.getText(response));




        }

    }

}
