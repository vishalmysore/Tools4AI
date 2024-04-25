package com.t4a.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.*;
import com.t4a.action.BlankAction;
import com.t4a.api.AIAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This example does not require you to specify the action to take it will automatically detect the action
 * to be taken based on list of prompts and predict accurately what needs to be done
 * 1) java PromptPredictionExample projectId=cookgptserver location=us-central1 modelName=gemini-1.0-pro promptText="Hey I am in Toronto do you think i can go out without jacket"
 * 2) java PromptPredictionExample projectId=cookgptserver location=us-central1 modelName=gemini-1.0-pro promptText="I am an employee my name is Vishal Mysore , I need to save my info"
 * 3) java PromptPredictionExample projectId=cookgptserver location=us-central1 modelName=gemini-1.0-pro promptText="My Name is Vishal I live in Toronto, My friends name is Vinod and He lives in Balaghat , please save this information"
 */
@Slf4j
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

        PromptPredictionExample sample = new PromptPredictionExample(args);
        sample.actionOnPrompt();

    }
    public void actionOnPrompt() throws IOException, InvocationTargetException, IllegalAccessException, AIProcessingException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            AIAction predictedAction = PredictionLoader.getInstance().getPredictedAction(promptText);
            log.debug(predictedAction.getActionName());
            JavaMethodExecutor methodAction = new JavaMethodExecutor();

            FunctionDeclaration weatherFunciton = methodAction.buildFunction(predictedAction);

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

            Object obj = methodAction.action(response, predictedAction);
            log.debug(""+obj);

            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    predictedAction.getActionName(), Collections.singletonMap(predictedAction.getActionName(),obj)));


            response = chat.sendMessage(content);

            log.debug("Print response content: ");
            log.debug(""+ResponseHandler.getContent(response));
            log.debug(ResponseHandler.getText(response));




        }

    }

}
