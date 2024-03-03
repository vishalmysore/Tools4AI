package com.t4a.predict;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.api.AIAction;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
        String actionClazzName = predictions.get(actionName).getClazzName();
        try {
            AIAction action = (AIAction)Class.forName(actionClazzName).getDeclaredConstructor().newInstance();
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
        }
        return predictionLoader;
    }

    public static void main(String[] args) {
        //PredictionLoader processor = new PredictionLoader();
        //processor.processCP();

    }

    public void processCP() {
        String basePackage = ""; // Specify the base package for scanning
        Set<Class<?>> annotatedClasses = scanClasspath(basePackage);

        // Process the annotated classes
        for (Class<?> clazz : annotatedClasses) {
            // Do something with the annotated class
            log.info("Found annotated class: " + clazz.getName());
        }
    }

    private  Set<Class<?>> scanClasspath(String basePackage) {
        Set<Class<?>> annotatedClasses = new HashSet<>();
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(File.pathSeparator);

        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                scanDirectory(file, basePackage, annotatedClasses);
            }
        }

        return annotatedClasses;
    }

    private  void scanDirectory(File directory, String packageName, Set<Class<?>> annotatedClasses) {
        File[] files = directory.listFiles(file -> file.isDirectory() || file.getName().endsWith(".class"));

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if(packageName.length() >1)
                    scanDirectory(file, packageName + "." + file.getName(), annotatedClasses);
                    else
                        scanDirectory(file, file.getName(), annotatedClasses);
                } else {
                    String finalFileName = file.getName().replace(".class", "");
                    String className = packageName + "."+ finalFileName;
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(Predict.class)) {
                            annotatedClasses.add(clazz);
                            addAction(clazz);
                        }
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                             InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public  void addAction(Class clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
