package com.t4a.predict;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.t4a.action.ExtendedPredictedAction;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.shell.ShellPredictedAction;
import com.t4a.api.AIAction;
import com.t4a.api.ActionGroup;
import com.t4a.api.ActionList;
import com.t4a.api.ActionType;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
/**
 * The {@code PredictionLoader} class is responsible for managing the prediction process
 * by interacting with various prediction models and loading actions based on predictions.
 * <p>
 * This class dynamically loads actions from clases and provides methods for
 * predicting actions, explaining actions, and building prompts for interaction with users.
 * </p>
 */
@Slf4j
public class PredictionLoader {

    @Getter
    private Map<String,AIAction> predictions = new HashMap<String,AIAction>();
    private StringBuffer actionNameList = new StringBuffer();
    private ActionList actionGroupList = new ActionList();
    private static PredictionLoader predictionLoader =null;

    private final String PREACTIONCMD = "here is my prompt - ";
    private final  String ACTIONCMD = "- what action do you think we should take out of these  - {  ";

    private final String OPEN_AIPRMT = "here is your prompt - prompt_str - here is you action- action_name(params_values) - what parameter should you pass to this function. give comma separated name=values only and nothing else";
    private final  String POSTACTIONCMD = " } - reply back with ";
    private final  String NUMACTION = " action only. Make sure Action matches exactly from this list";

    private final  String NUMACTION_MULTIPROMPT = " actions only, in comma seperated list without any additional special characters";
    private ChatSession chat;
    private ChatSession chatExplain;
    private ChatSession chatMulti;
    private ChatSession chatGroupFinder;
    private ChatSession chatScript;
    private String projectId;
    private String location;
    private String modelName;
    private ChatLanguageModel openAiChatModel;
    private String openAiKey;
    @Getter
    private String serperKey;
    private String actionGroupJson;
    private ApplicationContext springContext;

    /**
     * If the action name returned by AI is not in the list of approved action it will try again this many times
     */
    private final int NUM_OF_RETRIES = 2;
    private PredictionLoader() {
        initProp();
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {


            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            GenerativeModel modelExplain =
                    new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            GenerativeModel multiCommand =
                    new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            GenerativeModel scriptCommand =
                    new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            GenerativeModel chatGroupFinderCommand =
                    new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .build();
            chat = model.startChat();
            chatExplain = modelExplain.startChat();
            chatMulti = multiCommand.startChat();
            chatScript = scriptCommand.startChat();
            chatGroupFinder = chatGroupFinderCommand.startChat();
        }
        if(openAiKey!=null) {
            openAiChatModel = OpenAiChatModel.withApiKey(openAiKey);
        }

    }

    public String getProjectId() {
        return projectId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getLocation() {
        return location;
    }

    private void initProp() {
        try (InputStream inputStream = PredictionLoader.class.getClassLoader().getResourceAsStream("tools4ai.properties")) {
            if(inputStream == null) {
                log.error(" tools4ai properties not found ");
                return;
            }
            Properties prop = new Properties();
            prop.load(inputStream);
            // Read properties
            projectId = prop.getProperty("projectId");
            if(projectId != null)
                projectId = projectId.trim();
            location = prop.getProperty("location");
            if(location != null)
                location = location.trim();
            modelName = prop.getProperty("modelName");
            if(modelName != null)
                    modelName = modelName.trim();
            openAiKey = prop.getProperty("openAiKey");
            if(openAiKey != null)
                openAiKey = openAiKey.trim();
            serperKey = prop.getProperty("serperKey");
            if(serperKey == null || serperKey.trim().length() <1) {
                serperKey = System.getProperty("serperKey");
            }
            if(serperKey != null)
                serperKey = serperKey.trim();
            // Use the properties
            log.debug("projectId: " + projectId);
            log.debug("location: " + location);
            log.debug("modelName: " + modelName);
            log.debug("serperKey: " + serperKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ActionList getActionGroupList() {
        return actionGroupList;
    }

    public List<AIAction> getPredictedAction(String prompt, int num)  {
        GenerateContentResponse response = null;
        List<AIAction> actionList = new ArrayList<AIAction>();
        try {
            String groupName = ResponseHandler.getText(chatGroupFinder.sendMessage(" This is the prompt -"+prompt+" - which group does it belong "+actionGroupJson+" - just provide the group name and nothing else"));
            log.info(" will look in group "+groupName);
            String actionNameList = getActionGroupList().getGroupActions().get((new ActionGroup(groupName)).getGroupInfo());
            log.info(" list of actions "+actionNameList);
            response = chat.sendMessage(buildPrompt(prompt,num,actionNameList));
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
    public  String getCommaSeparatedKeys(Map<String, ?> map) {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(key);
        }
        return builder.toString();
    }
    public String getActionParams(AIAction action, String prompt, AIPlatform aiProvider, Map<String, Object> params)  {
        String prmpt = OPEN_AIPRMT.replace("prompt_str",prompt);
        prmpt = prmpt.replace("action_name",action.getActionName());
        String realParms = getCommaSeparatedKeys(params);
        prmpt = prmpt.replace("params_values",realParms);
        log.debug(prmpt);
        return openAiChatModel.generate(prmpt);
    }

    public String postActionProcessing(AIAction action, String prompt, AIPlatform aiProvider, Map<String, Object> params,String result)  {

        return openAiChatModel.generate(prompt+" result "+result);
    }
    public AIAction getPredictedAction(String prompt,AIPlatform aiProvider)  {
        int numRetries = 0;
        GenerateContentResponse response = null;
        String actionName = null;
        AIAction action = null;
        try {
            if(AIPlatform.GEMINI == aiProvider) {
                String groupName = ResponseHandler.getText(chatGroupFinder.sendMessage(" This is the prompt - {"+prompt+"} - which group does it belong - {"+actionGroupJson+"} - just provide the group name and nothing else"));
                log.info(" will look in group "+groupName);
                String actionNameList = getActionGroupList().getGroupActions().get((new ActionGroup(groupName)).getGroupInfo());
                log.info(" list of actions "+actionNameList);
                response = chat.sendMessage(buildPrompt(prompt, 1,actionNameList));
                actionName = ResponseHandler.getText(response);
                if(!actionNameList.toString().contains(","+actionName+",")) {
                    while(numRetries++<NUM_OF_RETRIES) {
                        log.debug(" got "+actionName+" Trying again "+numRetries);
                        response = chat.sendMessage(buildPrompt(prompt, 1,actionNameList));
                        actionName = ResponseHandler.getText(response);
                        if(actionNameList.toString().contains(","+actionName+",")) {
                            break;
                        }
                    }


                }
                log.debug(" Predicted action by AI is "+actionName);
                action = getAiAction(actionName);
            }else if (AIPlatform.OPENAI == aiProvider) {

                actionName = openAiChatModel.generate(buildPromptForOpenAI(prompt, 1));
                actionName = actionName.replace("()","");
                action = getAiAction(actionName);
                if(action == null) {
                    log.debug("action not found , trying again");
                    actionName = fetchActionNameFromList(actionName);
                    log.debug("Predicted action by AI is "+actionName);
                    action = getAiAction(actionName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(" Please make sure actions are configured");
        }

        return action;

    }

    public String fetchActionNameFromList(String actionName) {
        String namesArray[] = actionNameList.toString().split(",");
        String realName = null;
        for (String name:namesArray
             ) {
            if(name.equalsIgnoreCase(actionName))
            {
                 realName = name;
            }
        }
        return realName;
    }

    public AIAction getPredictedAction(String prompt)  {
       return getPredictedAction(prompt,AIPlatform.GEMINI);
    }

    public String getMultiStepResult(String json) {
        GenerateContentResponse response = null;
        try {
            response = chatMulti.sendMessage("look at the json string - "+json+" -  provide the information inside json in plain english language which is understandable");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String nlpans = ResponseHandler.getText(response);
        log.debug(nlpans);
        return nlpans;
    }
    public String getPredictedActionMultiStep(String prompt)  {
        GenerateContentResponse response = null;
        try {
            response = chatMulti.sendMessage(buildPromptMultiStep(prompt));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String subprompts = ResponseHandler.getText(response);
        log.debug(subprompts);
        return subprompts;

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

    public String scriptDecision(String prompt, String context)  {
        GenerateContentResponse response = null;
        try {
            response = chatExplain.sendMessage("these are the results of previous actions - "+context+" - should we proceed with this step - " +prompt+" - provide an answer as yes or no only");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  ResponseHandler.getText(response);


    }

    public String summarize(String prompt)  {
        GenerateContentResponse response = null;
        try {
            response = chatExplain.sendMessage("summarize this - "+prompt);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  ResponseHandler.getText(response);


    }
    public AIAction getAiAction(String actionName) {
        log.debug(" Trying to load "+actionName);
        AIAction action = predictions.get(actionName);
        if(action != null) {

            if (action.getActionType() == ActionType.SHELL) {
                ShellPredictedAction shellAction = (ShellPredictedAction) action;

                return shellAction;
            } else if (action.getActionType() == ActionType.HTTP) {
                if (action instanceof HttpPredictedAction) {
                    HttpPredictedAction genericHttpAction = (HttpPredictedAction) action;

                    return genericHttpAction;

                }
                if (action.getActionType() == ActionType.EXTEND) {
                    ExtendedPredictedAction shellAction = (ExtendedPredictedAction) action;

                    return shellAction;
                }
            }
        }
            return action;

    }

    public static PredictionLoader getInstance() {
        return getInstance(null);
    }
    public static PredictionLoader getInstance(ApplicationContext springContext) {
        if(predictionLoader == null) {
            predictionLoader = new PredictionLoader();
            predictionLoader.setSpringContext(springContext);
            predictionLoader.processCP();
            predictionLoader.loadShellCommands();
            predictionLoader.loadHttpCommands();
            predictionLoader.loadSwaggerHttpActions();
            predictionLoader.buildGroupInfo();

        }
        return predictionLoader;
    }

    private  void buildGroupInfo(){
        Gson gson = new Gson();
        actionGroupJson = gson.toJson(getActionGroupList().getGroupInfo());
    }

    public void setSpringContext(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    private void loadShellCommands()  {
        ShellPredictionLoader shellLoader = new ShellPredictionLoader();
        try {
            shellLoader.load(predictions,actionNameList,actionGroupList);
        } catch (LoaderException e) {
            log.error(e.getMessage());
        }
    }
    private void loadSwaggerHttpActions() {
        SwaggerPredictionLoader httpLoader = new SwaggerPredictionLoader();
        try {
            httpLoader.load(predictions,actionNameList,actionGroupList);
        } catch (LoaderException e) {
            log.error(e.getMessage());
        }
    }
    private void loadHttpCommands()  {
        HttpRestPredictionLoader httpLoader = new HttpRestPredictionLoader();
        try {
            httpLoader.load(predictions,actionNameList);
        } catch (LoaderException e) {
            log.error(e.getMessage());
        }
    }

    public void processCP() {
        Reflections reflections = new Reflections("",
                new SubTypesScanner(),
                new TypeAnnotationsScanner());
        Set<Class<?>> predict = reflections.getTypesAnnotatedWith(Predict.class);
        Set<Class<?>> activateLoader = reflections.getTypesAnnotatedWith(ActivateLoader.class);
        loaderPredict(predict);
        loaderExtended(activateLoader);
    }

    private void loaderExtended(Set<Class<?>> loaderClasses) {
        loaderClasses.forEach(actionCLAZZ->{
            try {
                if (ExtendedPredictionLoader.class.isAssignableFrom(actionCLAZZ)) {
                    log.debug("Class " + actionCLAZZ + " implements Loader");
                    loadFromLoader(actionCLAZZ);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void loaderPredict(Set<Class<?>> loaderClasses) {
        loaderClasses.forEach(actionCLAZZ->{
            try {
                if (AIAction.class.isAssignableFrom(actionCLAZZ)) {
                    log.debug("Class " + actionCLAZZ + " implements AIAction");
                    if (ExtendedPredictedAction.class.isAssignableFrom(actionCLAZZ))
                        log.error("You cannot predict extended option implement AIAction instead"+actionCLAZZ);
                    else
                        addAction(actionCLAZZ);
                } else {
                    log.error(" You have predict annotation but the class does not implement AIAction interface "+actionCLAZZ.getName());
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private  void loadFromLoader(Class clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ExtendedPredictionLoader instance = (ExtendedPredictionLoader)clazz.getDeclaredConstructor().newInstance();
        try {
            Map<String, ExtendedPredictedAction> extendedPredictOptionsMap = instance.getExtendedActions();
            for (String key : extendedPredictOptionsMap.keySet()) {
                log.debug(" names "+actionNameList);
                actionNameList.append(key).append(",");

                predictions.put(key,extendedPredictOptionsMap.get(key));
            }

        } catch (LoaderException e) {
            log.error(e.getMessage()+" for "+clazz.getName());

        }
    }



    private  void addAction(Class clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AIAction instance = null;
        if(springContext != null) {
            instance = (AIAction) springContext.getBean(clazz);
            log.debug(" instance from Spring " + instance);
        } if(instance == null) {
            instance = (AIAction) clazz.getDeclaredConstructor().newInstance();
        }

        String actionName = instance.getActionName();

        actionNameList.append(actionName+",");
        actionGroupList.addAction(instance);
        predictions.put(actionName,instance);
    }

    public Map<String, AIAction> getPredictions() {
        return predictions;
    }

    public StringBuffer getActionNameList() {
        return actionNameList;
    }

    private String buildPrompt(String prompt, int number, String actionNameList) {
        String append = NUMACTION;
        if(number > 1)
            append = NUMACTION_MULTIPROMPT;
        String query = PREACTIONCMD+ "{ "+prompt+" }"+ACTIONCMD+"{ " +actionNameList.toString() +" }"+POSTACTIONCMD+number +append;
        log.debug(query);
        return query;
    }
    private String getModifiedActionName(StringBuffer stringBuffer) {
        String[] functionNames = stringBuffer.toString().split(",");

        // StringBuilder to build the modified string
        StringBuilder modifiedString = new StringBuilder();

        // Append '()' to each function name and append them to the modifiedString
        for (String functionName : functionNames) {
            modifiedString.append(functionName).append("(),");
        }
        if (modifiedString.length() > 0) {
            modifiedString.setLength(modifiedString.length() - 1);
        }
        return modifiedString.toString();
    }
    private String buildPromptForOpenAI(String prompt, int number) {
        String append = NUMACTION;
        if(number > 1)
            append = NUMACTION_MULTIPROMPT;
        String query = PREACTIONCMD+prompt+ACTIONCMD+getModifiedActionName(actionNameList)+POSTACTIONCMD+number +append;
        log.debug(query);
        return query;
    }

    private String buildPromptMultiStep(String prompt) {
        return "break down this prompt into multiple logical prompts in this json format { prmpt : [ { id:unique_id,subprompt:'',depend_on=id} ] }- "+prompt+" - just give JSON and dont put any characters ";
    }

}
