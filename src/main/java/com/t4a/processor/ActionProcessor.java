package com.t4a.processor;

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
import lombok.extern.java.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log
public class ActionProcessor {
    public Object processSingleAction(String projectId, String location, String modelName, String promptText) throws IOException, InvocationTargetException, IllegalAccessException {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            AIAction predictedAction = PredictionLoader.getInstance(projectId, location, modelName).getPredictedAction(promptText);
            log.info(predictedAction.getActionName());
            JavaMethodExecutor methodAction = new JavaMethodExecutor();

             methodAction.buildFunciton(predictedAction);

            log.info("Function declaration h1:");
            log.info("" + methodAction.getGeneratedFunction());

            JavaMethodExecutor additionalQuestion = new JavaMethodExecutor();
            BlankAction blankAction = new BlankAction();
            FunctionDeclaration additionalQuestionFun = additionalQuestion.buildFunciton(blankAction);
            log.info("Function declaration h1:");
            log.info("" + additionalQuestionFun);
            //add the function to the tool
            Tool.Builder toolBuilder = Tool.newBuilder();
            toolBuilder.addFunctionDeclarations(methodAction.getGeneratedFunction());
            toolBuilder.addFunctionDeclarations(additionalQuestionFun);
            Tool tool = toolBuilder.build();


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
            log.info("" + obj);

            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    predictedAction.getActionName(), Collections.singletonMap(predictedAction.getActionName(), obj)));


            response = chat.sendMessage(content);

            log.info("Print response content: ");
            log.info("" + ResponseHandler.getContent(response));
            String result = ResponseHandler.getText(response);
            return result;


        }
    }

    public Object processSingleAction(String projectId, String location, String modelName, String promptText, HumanInLoop humanValidate) throws IOException, InvocationTargetException, IllegalAccessException {
        return processSingleAction(projectId,location,modelName,promptText);
    }

    public Object processSingleAction(String projectId, String location, String modelName, String promptText, HumanInLoop humanValidate, ExplainDecision explain) throws IOException, InvocationTargetException, IllegalAccessException {
        return processSingleAction(projectId,location,modelName,promptText);
    }

    public List<Object> processMultipleAction(String projectId, String location, String modelName, String promptText, int num) throws IOException, InvocationTargetException, IllegalAccessException {
        List<Object> restulList = new ArrayList<Object>();
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            List<AIAction> predictedActionList = PredictionLoader.getInstance(projectId, location, modelName).getPredictedAction(promptText, num);
            List<JavaMethodExecutor> javaMethodExecutorList = new ArrayList<>();
            Tool.Builder toolBuilder = Tool.newBuilder();

            for (AIAction predictedAction:predictedActionList
                 ) {
                log.info(predictedAction.getActionName());
                JavaMethodExecutor methodAction = new JavaMethodExecutor();

                methodAction.buildFunciton(predictedAction);

                log.info("Function declaration h1:");
                log.info("" + methodAction.getGeneratedFunction());



                //add the function to the tool

                toolBuilder.addFunctionDeclarations(methodAction.getGeneratedFunction());
            }
            JavaMethodExecutor additionalQuestion = new JavaMethodExecutor();
            BlankAction blankAction = new BlankAction();
            FunctionDeclaration additionalQuestionFun = additionalQuestion.buildFunciton(blankAction);
            log.info("Function declaration h1:");
            log.info("" + additionalQuestionFun);
            toolBuilder.addFunctionDeclarations(additionalQuestionFun);
            Tool tool = toolBuilder.build();


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();

            ChatSession chat = model.startChat();

            for (JavaMethodExecutor methodExecutor:javaMethodExecutorList
                 ) {
                log.info(String.format("Ask the question 1: %s", promptText));
                GenerateContentResponse response = chat.sendMessage(promptText);

                log.info("\nPrint response 1 : ");
                log.info("" + ResponseHandler.getContent(response));

                log.info(methodExecutor.getPropertyValuesJsonString(response));

                Object obj = methodExecutor.action(response, methodExecutor.getAction());
                log.info("" + obj);

                Content content =
                        ContentMaker.fromMultiModalData(
                                PartMaker.fromFunctionResponse(
                                        methodExecutor.getAction().getActionName(), Collections.singletonMap(methodExecutor.getAction().getActionName(), obj)));


                response = chat.sendMessage(content);

                log.info("Print response content: ");
                log.info("" + ResponseHandler.getContent(response));
                String result = ResponseHandler.getText(response);
                restulList.add(result);

            }


            return restulList;


        }
    }
}
