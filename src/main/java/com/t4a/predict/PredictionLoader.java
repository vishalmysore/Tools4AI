package com.t4a.predict;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.action.shell.ShellAction;
import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log
public class PredictionLoader {

    @Getter
    private Map<String,PredictOptions> predictions = new HashMap<String,PredictOptions>();
    private StringBuffer actionNameList = new StringBuffer();
    private static PredictionLoader predictionLoader =null;

    private String PREACTIONCMD = "here is my prompt - ";
    private String ACTIONCMD = "- what action do you think we should take ";

    private String POSTACTIONCMD = " - reply back with ";
    private String NUMACTION = " action only";
    private ChatSession chat;

    private PredictionLoader(String projectId, String location, String modelName) {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            chat = model.startChat();
        }

    }

    public AIAction getPredictedAction(String prompt)  {
        GenerateContentResponse response = null;
        try {
            response = chat.sendMessage(buildPrompt(prompt,1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String actionName = ResponseHandler.getText(response);
        PredictOptions options = predictions.get(actionName);
        String actionClazzName = options.getClazzName();
        try {
            AIAction action = (AIAction)Class.forName(actionClazzName).getDeclaredConstructor().newInstance();
            if(action.getActionType() == ActionType.SHELL) {
                ShellAction shellAction = (ShellAction)action;
                shellAction.setActionName(options.getActionName());
                shellAction.setDescription(options.getDescription());
                shellAction.setScriptPath(options.getScriptPath());
                return shellAction;
            }
            return action;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static PredictionLoader getInstance(String projectId, String location, String modelName) {
        if(predictionLoader == null) {
            predictionLoader = new PredictionLoader(projectId,location, modelName);
            predictionLoader.processCP();
            predictionLoader.loadShellCommands();
            predictionLoader.loadHttpCommands();
        }
        return predictionLoader;
    }




    private void loadShellCommands()  {
        ShellPredictionLoader shellLoader = new ShellPredictionLoader();
        try {
            shellLoader.load(predictions,actionNameList);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadHttpCommands()  {
        HttpRestPredictionLoader httpLoader = new HttpRestPredictionLoader();
        try {
            httpLoader.load(predictions,actionNameList);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void processCP() {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Predict.class));
        Set<BeanDefinition> beanDefs = provider
                .findCandidateComponents("com");
        beanDefs.stream().forEach(beanDefinition -> {
            try {
                addAction(Class.forName(beanDefinition.getBeanClassName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }






    private  void addAction(Class clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AIAction instance = (AIAction)clazz.getDeclaredConstructor().newInstance();
        String actionName = instance.getActionName();
        PredictOptions options = new PredictOptions(clazz.getName(),instance.getDescription(),instance.getActionName(),instance.getActionName());
        actionNameList.append(actionName+",");
        predictions.put(actionName,options);
    }

    public Map<String, PredictOptions> getPredictions() {
        return predictions;
    }

    public StringBuffer getActionNameList() {
        return actionNameList;
    }

    private String buildPrompt(String prompt, int number) {
        String query = PREACTIONCMD+prompt+ACTIONCMD+actionNameList.toString()+POSTACTIONCMD+number +NUMACTION;
        log.info(query);
        return query;
    }
}
