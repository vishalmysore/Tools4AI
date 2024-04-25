package com.t4a.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.*;
import com.t4a.action.BlankAction;
import com.t4a.action.http.HttpMethod;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.http.InputParameter;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class GenericHtppTest {



    private String promptText = "Hey I am in Toronto do you think i can go out without jacket,  ";
    public GenericHtppTest(String[] args) throws Exception {

    }
    public static void main(String[] args) throws Exception {

        GenericHtppTest sample = new GenericHtppTest(args);
        sample.actionOnPrompt(args);

    }

    public void actionOnPrompt(String[] args) throws IOException, AIProcessingException {
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            JavaMethodExecutor methodAction = new JavaMethodExecutor();
            HttpPredictedAction httpAction = new HttpPredictedAction();
            httpAction.setActionName("getTemperature");
            httpAction.setUrl("https://geocoding-api.open-meteo.com/v1/search");
            httpAction.setType(HttpMethod.GET);
            InputParameter cityParameter = new InputParameter("name","String","name of the city");
            InputParameter countparameter = new InputParameter("count","String","count");
            countparameter.setDefaultValue("1");
            List<InputParameter> parameters = new ArrayList<InputParameter>();

            InputParameter language = new InputParameter("language","String","name of the city");
            InputParameter format = new InputParameter("format","String","name of the city");
            parameters.add(cityParameter);
            parameters.add(countparameter);
            parameters.add(language);
            parameters.add(format);
            httpAction.setInputObjects(parameters);
            httpAction.setDescription("get temperature in real time");
            FunctionDeclaration weatherFunciton = methodAction.buildFunction(httpAction);

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
                            .setModelName(PredictionLoader.getInstance().getModelName())
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            log.debug(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            log.debug("\nPrint response 1 : ");
            log.debug("" + ResponseHandler.getContent(response));
            log.debug(methodAction.getPropertyValuesJsonString(response));

            Object obj = methodAction.action(response, httpAction);
            log.debug(""+obj);

            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    httpAction.getActionName(), Collections.singletonMap(httpAction.getActionName(),obj)));


            response = chat.sendMessage(content);

            log.debug("Print response content: ");
            log.debug(""+ResponseHandler.getContent(response));
            log.debug(ResponseHandler.getText(response));


        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
