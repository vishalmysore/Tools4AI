package com.t4a.predict;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.action.http.GenericHttpAction;
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
import java.util.*;

@Log
public class PredictionLoader {

    @Getter
    private Map<String,PredictOptions> predictions = new HashMap<String,PredictOptions>();
    private StringBuffer actionNameList = new StringBuffer();
    private static PredictionLoader predictionLoader =null;

    private final String PREACTIONCMD = "here is my prompt - ";
    private final  String ACTIONCMD = "- what action do you think we should take ";

    private final  String POSTACTIONCMD = " - reply back with ";
    private final  String NUMACTION = " action only";

    private final  String NUMACTION_MULTIPROMPT = " actions only, in comma seperated list without any additional special characters";
    private ChatSession chat;
    private ChatSession chatExplain;
    private PredictionLoader(String projectId, String location, String modelName) {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            GenerativeModel modelExplain =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            chat = model.startChat();
            chatExplain = modelExplain.startChat();
        }

    }

    public List<AIAction> getPredictedAction(String prompt, int num)  {
        GenerateContentResponse response = null;
        List<AIAction> actionList = new ArrayList<AIAction>();
        try {
            response = chat.sendMessage(buildPrompt(prompt,num));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String actionNameListStr = ResponseHandler.getText(response);
        String[] actionNames = actionNameListStr.split(",");
        if(!(actionNames.length >1))
            actionNames = actionNameListStr.split("\n");
        // Iterate over each action name
        for (String actionName : actionNames) {
            // Trim the action name to remove leading/trailing spaces
            actionName = actionName.trim();

            // Get the action and add it to the action list
            AIAction aiAction = getAiAction(actionName);
            actionList.add(aiAction);
        }
        return actionList;

    }


    public AIAction getPredictedAction(String prompt)  {
        GenerateContentResponse response = null;
        try {
            response = chat.sendMessage(buildPrompt(prompt,1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String actionName = ResponseHandler.getText(response);
        return getAiAction(actionName);

    }

    public String explainAction(String prompt, String action)  {
        GenerateContentResponse response = null;
        try {
            response = chatExplain.sendMessage("explain why this action "+action+" is appropriate for this command "+prompt+" out of all these actions "+actionNameList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  ResponseHandler.getText(response);


    }

    private AIAction getAiAction(String actionName) {
        log.info(" Trying to load "+actionName);
        PredictOptions options = predictions.get(actionName);
        String actionClazzName = options.getClazzName();
        try {
            AIAction action = (AIAction)Class.forName(actionClazzName).getDeclaredConstructor().newInstance();
            if(action.getActionType() == ActionType.SHELL) {
                ShellAction shellAction = (ShellAction)action;
                shellAction.setActionName(options.getActionName());
                shellAction.setDescription(options.getDescription());
                shellAction.setScriptPath(options.getScriptPath());
                shellAction.setParameterNames(options.getShellParameterNames());
                return shellAction;
            } else if (action.getActionType() == ActionType.HTTP) {
               if(action instanceof GenericHttpAction) {
                   GenericHttpAction genericHttpAction = (GenericHttpAction)action;
                   genericHttpAction.setType(options.getHttpType());
                   genericHttpAction.setActionName(options.getActionName());
                   genericHttpAction.setDescription(options.getDescription());
                   genericHttpAction.setAuthInterface(options.getHttpAuthInterface());
                   genericHttpAction.setUrl(options.getHttpUrl());
                   genericHttpAction.setInputObjects(options.getHttpInputObjects());
                   genericHttpAction.setOutputObject(options.getHttpOutputObject());
                   return genericHttpAction;

               }
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
            log.warning(e.getMessage());
        }
    }
    private void loadHttpCommands()  {
        HttpRestPredictionLoader httpLoader = new HttpRestPredictionLoader();
        try {
            httpLoader.load(predictions,actionNameList);
        } catch (URISyntaxException e) {
            log.warning(e.getMessage());
        }
    }

    public void processCP() {


        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new AnnotationTypeFilter(Predict.class));
        Set<BeanDefinition> beanDefs = provider
                .findCandidateComponents("*");
        beanDefs.stream().forEach(beanDefinition -> {
            try {
                Class actionCLAZZ = Class.forName(beanDefinition.getBeanClassName());
                if (AIAction.class.isAssignableFrom(actionCLAZZ)) {
                    log.info("Class " + actionCLAZZ + " implements AIAction");
                    addAction(actionCLAZZ);
                }

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
        String append = NUMACTION;
        if(number > 1)
            append = NUMACTION_MULTIPROMPT;
        String query = PREACTIONCMD+prompt+ACTIONCMD+actionNameList.toString()+POSTACTIONCMD+number +append;
        log.info(query);
        return query;
    }

}
