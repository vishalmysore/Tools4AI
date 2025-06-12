package com.t4a.predict;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.gson.Gson;
import com.t4a.JsonUtils;
import com.t4a.action.ExtendedPredictedAction;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.annotations.Action;
import com.t4a.annotations.ActivateLoader;
import com.t4a.annotations.Agent;
import com.t4a.api.*;
import com.t4a.processor.*;
import com.t4a.transform.AnthropicTransformer;
import com.t4a.transform.GeminiV2PromptTransformer;
import com.t4a.transform.OpenAIPromptTransformer;
import com.t4a.transform.PromptTransformer;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public static final String PRMPT = " This is the prompt - {";
    public static final String GRP = "} - which group does it belong - {";
    public static final String LOOK_FOR_ACTION_IN_THE_GROUP = " will look for action in the group ";
    public static final String OUT_OF = " out of ";


    private final Map<String,AIAction> predictions = new HashMap<>();
    private final StringBuilder actionNameList = new StringBuilder();
    @Getter
    private final ActionList actionGroupList = new ActionList();
    private static PredictionLoader predictionLoader =null;

    private final String PREACTIONCMD = "here is my prompt - ";
    private String PREACTIONCMDOI = "here is my prompt - ";
    private String ACTIONCMDOI = "- what action do you think we should take out of these  - {  ";

    private   String POSTACTIONCMDOI = " } - reply back with ";

    public  String METHODTOJSONOI = "- if you find the field named fieldValue, populate the field else and return the json as is";
    private final  String ACTIONCMD = "- what action do you think we should take out of these  - {  ";

    private final  String POSTACTIONCMD = " } - reply back with ";
    private final  String NUMACTION = " action only. Make sure Action matches exactly from this list";
    private   String NUMACTIONOI = " action only. Make sure Action matches exactly from this list";
    private Map<Object, Object> tools4AIPropertiesImmutable;
    private Properties tools4AIProperties = new Properties();
    private final  String NUMACTION_MULTIPROMPT = " actions only, in comma seperated list without any additional special characters";
    @Getter
    @Setter
    private ChatSession chat;
    @Getter
    @Setter
    private ChatSession chatExplain;
    private ChatSession chatMulti;
    @Setter
    @Getter
    private ChatSession chatGroupFinder;
    @Getter
    private String projectId;
    @Getter
    private String location;
    @Getter
    private String modelName;
    @Getter
    private String geminiVisionModelName;
    @Getter
    @Setter
    private ChatLanguageModel anthropicChatModel;
    private String anthropicModelName;
    private boolean anthropicLogReqFlag;
    private boolean anthropicLogResFlag;

    @Setter
    @Getter
    private ChatLanguageModel openAiChatModel;
    private String openAiKey;
    private String openAiBaseURL;

    private String openAiModelName;
    private String claudeKey;
    @Getter
    private String serperKey;
    private String actionGroupJson;
    private ApplicationContext springContext;

    private final ConfigManager configManager = new ConfigManager();


    private PromptTransformer promptTransformer;

    private AIProcessor aiProcessor;

    @Getter
    private String actionPackagesToScan ="";


    private PredictionLoader() {
        final int MAX_TOKENS = 4000; // Maximum tokens for Anthropic model
        initPromptProp();
        initModelProp();
        if((modelName!=null)&&(projectId!=null)&&(location!=null)) {
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

                GenerativeModel chatGroupFinderCommand =
                        new GenerativeModel.Builder()
                                .setModelName(modelName)
                                .setVertexAi(vertexAI)
                                .build();
                chat = model.startChat();
                chatExplain = modelExplain.startChat();
                chatMulti = multiCommand.startChat();
                chatGroupFinder = chatGroupFinderCommand.startChat();

            }
        }
        if(openAiKey!=null) {
            if(openAiBaseURL!=null) {
                log.info("baseurl "+openAiBaseURL);
                openAiChatModel = OpenAiChatModel.builder().apiKey(openAiKey).baseUrl(openAiBaseURL).modelName(openAiModelName).build();
            } else {
                openAiChatModel = OpenAiChatModel.withApiKey(openAiKey);
            }


        }

        if(claudeKey!=null) {
            anthropicChatModel = AnthropicChatModel.builder()
                    .apiKey(claudeKey)
                    .modelName(anthropicModelName)
                    .logRequests(anthropicLogReqFlag)
                    .logResponses(anthropicLogResFlag)
                    .maxTokens(MAX_TOKENS)
                    .build();

        }
    }

    public Map<Object, Object> getTools4AIProperties() {
        return tools4AIPropertiesImmutable;
    }

    public AIProcessor createOrGetAIProcessor() {
        if(this.aiProcessor == null) {
            if(openAiChatModel != null) {
                this.aiProcessor = new OpenAiActionProcessor();
            } else if(anthropicChatModel != null) {
                this.aiProcessor = new AnthropicActionProcessor();
            } else {
                this.aiProcessor = new GeminiV2ActionProcessor();
            }
        }
        return this.aiProcessor;
    }
    public PromptTransformer createOrGetPromptTransformer() {
        if(promptTransformer == null) {
            if(openAiChatModel != null) {
                this.promptTransformer = new OpenAIPromptTransformer();
            } else if(anthropicChatModel != null) {
                this.promptTransformer = new AnthropicTransformer();
            } else {
                this.promptTransformer = new GeminiV2PromptTransformer();
            }
        }
        return this.promptTransformer;
    }

    private void initPromptProp() {
        PREACTIONCMDOI = configManager.getProperty("openai.pre_action", "here is my prompt - ");
        ACTIONCMDOI = configManager.getProperty("openai.action", "- what action do you think we should take out of these  - {  ");
        POSTACTIONCMDOI = configManager.getProperty("openai.post_action", " } - reply back with ");
        NUMACTIONOI = configManager.getProperty("openai.num_action", " action only. Make sure Action matches exactly from this list");
        METHODTOJSONOI = configManager.getProperty("openai.method_to_json", "- if you find the field named fieldValue, populate the field else and return the json as is");
    }

    private void initModelProp() {
        String propertiesPath = System.getProperty("tools4ai.properties.path", "tools4ai.properties");
        log.info("Loading properties from path: " + propertiesPath);
        try (InputStream inputStream = PredictionLoader.class.getClassLoader().getResourceAsStream(propertiesPath)) {
            if(inputStream == null) {
                log.error(" tools4ai properties not found ");
                return;
            }

            tools4AIProperties.load(inputStream);
            tools4AIPropertiesImmutable = Collections.unmodifiableMap(new HashMap<>(tools4AIProperties));
            String packagesToScan = tools4AIProperties.getProperty("action.packages.to.scan");
            if(packagesToScan != null) {
                actionPackagesToScan = packagesToScan.trim();
                log.info(" will scan these packages for actions "+actionPackagesToScan );
            } else {
                actionPackagesToScan = "";
                log.info(" will scan all packages for actions " );
            }

            log.info("will scan for actions in packages: " + actionPackagesToScan);
            // Read properties
            projectId = tools4AIProperties.getProperty("gemini.projectId");
            if(projectId != null)
                projectId = projectId.trim();
            location = tools4AIProperties.getProperty("gemini.location");
            if(location != null)
                location = location.trim();
            modelName = tools4AIProperties.getProperty("gemini.modelName");
            if(modelName != null)
                modelName = modelName.trim();
            geminiVisionModelName =   tools4AIProperties.getProperty("gemini.vision.modelName");
            if(geminiVisionModelName != null)
                geminiVisionModelName = geminiVisionModelName.trim();
            anthropicModelName = tools4AIProperties.getProperty("anthropic.modelName");
            anthropicLogReqFlag = Boolean.parseBoolean(tools4AIProperties.getProperty("anthropic.logRequests", "false"));
            anthropicLogResFlag = Boolean.parseBoolean(tools4AIProperties.getProperty("anthropic.logResponse", "false"));
            if(anthropicModelName != null)
                anthropicModelName = anthropicModelName.trim();
            openAiKey = tools4AIProperties.getProperty("openAiKey");
            if(openAiKey != null)
                openAiKey = openAiKey.trim();
            if(openAiKey == null || openAiKey.trim().isEmpty()) {
                openAiKey = System.getProperty("openAiKey");
            }

            openAiBaseURL = tools4AIProperties.getProperty("openAiBaseURL");
            if(openAiBaseURL != null)
                openAiBaseURL = openAiBaseURL.trim();
            if(openAiBaseURL == null || openAiBaseURL.trim().isEmpty()) {
                openAiBaseURL = System.getProperty("openAiBaseURL");
            }

            openAiModelName = tools4AIProperties.getProperty("openAiModelName");
            if(openAiModelName != null)
                openAiModelName = openAiModelName.trim();
            if(openAiModelName == null || openAiModelName.trim().isEmpty()) {
                openAiModelName = System.getProperty("openAiModelName");
            }


            if(claudeKey == null || claudeKey.trim().isEmpty()) {
                claudeKey = System.getProperty("claudeKey");
            }
            serperKey = tools4AIProperties.getProperty("serperKey");
            if(serperKey == null || serperKey.trim().isEmpty()) {
                serperKey = System.getProperty("serperKey");
            }
            if(serperKey != null)
                serperKey = serperKey.trim();
            // Use the properties
            log.debug("projectId: " + projectId);
            log.debug("location: " + location);
            log.debug("modelName: " + modelName);
            log.debug("serperKey: " + serperKey);
            log.debug("openAiKey: " + openAiKey);
            log.debug("claudeKey: " + claudeKey);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public List<AIAction> getPredictedAction(String prompt, int num) throws AIProcessingException {
        GenerateContentResponse response;
        List<AIAction> actionList = new ArrayList<>();
        try {
            String groupName = ResponseHandler.getText(chatGroupFinder.sendMessage(" This is the prompt -"+prompt+" - which group does it belong "+actionGroupJson+" - just provide the group name and nothing else"));
            log.info(" will look in group "+groupName);
            String actionNameListTemp = getActionGroupList().getGroupActions().get((new ActionGroup(groupName)).getGroupInfo());
            log.info(" list of actions "+actionNameListTemp);
            response = chat.sendMessage(buildPrompt(prompt,num,actionNameListTemp));
        } catch (IOException e) {
            throw new AIProcessingException(e);
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
    public String getActionParams(AIAction action, String prompt, AIPlatform aiProvider, Map<String, Object> params) throws AIProcessingException {
        String OPEN_AIPRMT = "here is your prompt - prompt_str - here is you action- action_name(params_values) - what parameter should you pass to this function. give comma separated name=values only and nothing else";
        String prmpt = OPEN_AIPRMT.replace("prompt_str",prompt);
        prmpt = prmpt.replace("action_name",action.getActionName());
        String realParms = getCommaSeparatedKeys(params);
        prmpt = prmpt.replace("params_values",realParms);
        log.debug(prmpt);
        if(aiProvider == AIPlatform.OPENAI) {
            return openAiChatModel.generate(prmpt);
        }
        if(aiProvider == AIPlatform.ANTHROPIC) {
            return anthropicChatModel.generate(prmpt);
        } else {
            try {
                return ResponseHandler.getText(chatExplain.sendMessage(prmpt));
            } catch (IOException e) {
                throw new AIProcessingException(e);
            }
        }
    }
    public Object[] getComplexActionParams(String prompt, Map<String, Object> params, Gson gson)  {
        Object[] paramsRet = new Object[params.keySet().size()];
        int i  = 0 ;
        for (String key: params.keySet()
        ) {
            String json;
            try {
                Object value = params.get(key);
                json = classToJson((Class<?>)value);

                String response = openAiChatModel.generate(" here is you prompt { "+prompt+"} and here is your json "+json+" - fill the json with values and return");
                Object ret = gson.fromJson(response,((Class<?>)value) )   ;
                paramsRet[i] = ret;
                i++;

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return paramsRet;
    }

    private String classToJson(Class<?> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(clazz, visitor);
        JsonSchema jsonSchema = visitor.finalSchema();
        JsonNode propertiesNode = mapper.valueToTree(jsonSchema).path("properties");

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(propertiesNode);
    }
    public String postActionProcessing(String prompt, String result)  {

        return openAiChatModel.generate(prompt+" result "+result);
    }
    public AIAction getPredictedAction(String prompt,AIPlatform aiProvider)  {
        int numRetries = 0;
        GenerateContentResponse response;
        String actionName;
        AIAction action = null;
        try {
            if(AIPlatform.GEMINI == aiProvider) {
                JsonUtils utils = new JsonUtils();

                String groupName = ResponseHandler.getText(chatGroupFinder.sendMessage(PRMPT +prompt+ GRP +actionGroupJson+"} - which group does this prompt belong to? response back in this format with only one set {'groupName':'','explanation':''}"));
                log.info(groupName);
                groupName= utils.fetchGroupName(groupName);
                log.info(LOOK_FOR_ACTION_IN_THE_GROUP +groupName+ OUT_OF +actionGroupJson);
                String actionNameListTemp = getActionGroupList().getGroupActions().get((new ActionGroup(groupName.trim())).getGroupInfo()).trim();
                log.info(" list of actions "+actionNameListTemp);
                response = chat.sendMessage(buildPromptWithJsonResponse(prompt, 1,actionNameListTemp));

                actionName = ResponseHandler.getText(response).trim();
                actionName = utils.fetchActionName(actionName);
                actionNameListTemp = ","+actionNameListTemp+",";
                if(!actionNameListTemp.contains(","+actionName+",")) {
                    response = chat.sendMessage("give me just the action name from this query { "+actionName+"}");
                    actionName = ResponseHandler.getText(response).trim();
                    if(actionNameListTemp.contains(","+actionName+",")) {
                        log.info(" Got the name correct "+actionName);
                    } else {
                        int NUM_OF_RETRIES = 2;
                        while (numRetries++ < NUM_OF_RETRIES) {
                            log.debug(" got " + actionName + " Trying again " + numRetries);
                            response = chat.sendMessage(buildPrompt(prompt, 1, actionNameListTemp));
                            actionName = ResponseHandler.getText(response);
                            if (actionNameListTemp.contains("," + actionName + ",")) {
                                break;
                            }
                        }
                    }


                }
                log.debug(" Predicted action by AI is "+actionName);
                action = getAiAction(actionName);
            }else if (AIPlatform.OPENAI == aiProvider) {
                actionName = getOpenAiActionName(prompt);
                action = getAiAction(actionName);
                if(action == null) {
                    actionName = openAiChatModel.generate("provide action name from this and nothing else - "+actionName);
                    action = getAiAction(actionName);
                }
                if(action == null) {
                    log.debug("action not since it was null found , trying again");
                    actionName = fetchActionNameFromList(actionName);
                    log.debug("Predicted action by AI is "+actionName);
                    action = getAiAction(actionName);
                }
            }else if (AIPlatform.ANTHROPIC == aiProvider) {
                actionName = getAnthrpicActionName(prompt);
                action = getAiAction(actionName);
                if(action == null) {
                    actionName = anthropicChatModel.generate("provide action name from this and nothing else - "+actionName);
                    action = getAiAction(actionName);
                }
                if(action == null) {
                    log.debug("action not again found , trying again");
                    actionName = fetchActionNameFromList(actionName);
                    log.debug("Predicted action by AI is "+actionName);
                    action = getAiAction(actionName);
                }
            }
        }  catch (Exception e) {
            log.error(" Please make sure actions are configured {} ",e.getMessage());
            log.error(" Details ",e);
            return null;
        }

        return action;

    }

    @NotNull
    private String getOpenAiActionName(String prompt) {
        String actionName;
        String groupName = openAiChatModel.generate(PRMPT+ prompt +GRP+actionGroupJson+"} - just provide the group name and nothing else");
        log.info(LOOK_FOR_ACTION_IN_THE_GROUP +groupName+ OUT_OF +actionGroupJson);
        String actionNameListTemp = getActionGroupList().getGroupActions().get((new ActionGroup(groupName)).getGroupInfo());
        actionName = openAiChatModel.generate(buildPromptForOpenAI(prompt, 1, new StringBuilder(actionNameListTemp)));
        actionName = actionName.replace("()","");
        return actionName;
    }

    @NotNull
    private String getAnthrpicActionName(String prompt) {
        String actionName;
        String groupName = anthropicChatModel.generate(PRMPT+ prompt +GRP+actionGroupJson+"} - just provide the group name and nothing else");
        log.info(LOOK_FOR_ACTION_IN_THE_GROUP+groupName+ OUT_OF+actionGroupJson);
        String actionNameList = getActionGroupList().getGroupActions().get((new ActionGroup(groupName)).getGroupInfo());
        actionName = anthropicChatModel.generate(buildPromptForOpenAI(prompt, 1, new StringBuilder(actionNameList)));
        actionName = actionName.replace("()","");
        return actionName;
    }

    public String fetchActionNameFromList(String actionName) {
        String[] namesArray = actionNameList.toString().split(",");
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

    public String getMultiStepResult(String json) throws AIProcessingException {
        GenerateContentResponse response;
        try {
            response = chatMulti.sendMessage("look at the json string - "+json+" -  provide the information inside json in plain english language which is understandable");
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
        String nlpans = ResponseHandler.getText(response);
        log.debug(nlpans);
        return nlpans;
    }
    public String getPredictedActionMultiStep(String prompt) throws AIProcessingException {
        GenerateContentResponse response;
        try {
            response = chatMulti.sendMessage(buildPromptMultiStep(prompt));
        } catch (IOException e) {
            throw new AIProcessingException(e);
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
            log.warn(e.getMessage());
        }
        assert response != null;
        return  ResponseHandler.getText(response);


    }

    public AIAction getAiAction(String actionName) {
        log.debug(" Trying to load "+actionName);
        AIAction action = predictions.get(actionName);
        if(action != null) {

            if (action.getActionType() == ActionType.SHELL) {
                return action;
            } else if (action.getActionType() == ActionType.HTTP) {
                if (action instanceof HttpPredictedAction) {
                    return action;

                }
                if (action.getActionType() == ActionType.EXTEND) {
                    return action;
                }
            }
        }
        return action;

    }

    public static PredictionLoader getInstance() {
        return getInstance(null);
    }
    public static PredictionLoader getInstance(ApplicationContext springContext) {
        if (springContext!=null) {
            Environment env = springContext.getEnvironment();
            String tools4aiPath = env.getProperty("tools4ai.properties.path");

            if (tools4aiPath != null && !tools4aiPath.isEmpty()) {
                System.setProperty("tools4ai.properties.path", tools4aiPath);
                log.info("Loaded tools4ai path from Spring env: " + tools4aiPath);
            } else {
                log.info("tools4ai.properties.path not defined in Spring environment; will load default tools4ai.properties");
            }
        }
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
        Reflections reflections = new Reflections(actionPackagesToScan,
                new SubTypesScanner(),
                new TypeAnnotationsScanner());
        Set<Class<?>> predict = reflections.getTypesAnnotatedWith(Agent.class);
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
                log.warn(e.getMessage());
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
                    List<Method> annoatatedMethods =   getAnnotatedMethods(actionCLAZZ);
                    for (Method method : annoatatedMethods) {
                        annotated(actionCLAZZ, method);
                    }

                }

            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }

    private void annotated(Class<?> actionCLAZZ, Method method) throws AIProcessingException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        GenericJavaMethodAction action = new GenericJavaMethodAction(actionCLAZZ, method);
        addGenericJavaMethodAction(action);
    }

    private  void loadFromLoader(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

    public  List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> annotatedMethods = new ArrayList<>();

        // Get all methods in the class
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Action.class)) {  // Check for Action annotation
                annotatedMethods.add(method);  // Add to list if annotated with Action
            }
        }

        return annotatedMethods;  // Return the list of annotated methods
    }

    private void addGenericJavaMethodAction(GenericJavaMethodAction action) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object instance = null;
        if(springContext != null) {
            try {
                instance = springContext.getBean(action.getActionClass());
                log.debug(" instance from Spring " + instance);
            } catch (NoSuchBeanDefinitionException e) {
                instance = null;
            }

        }
        if(instance == null) {
            instance =  action.getActionClass().getDeclaredConstructor().newInstance();
        }
        action.setActionInstance(instance);
        String actionName = action.getActionName();

        actionNameList.append(actionName).append(",");
        actionGroupList.addAction(action);
        predictions.put(actionName,action);
    }

    private  void addAction(Class<?> clazz) throws  AIProcessingException {
        AIAction instance = null;
        if(springContext != null) {
            instance = (AIAction) springContext.getBean(clazz);
            log.debug("got instance from Spring " + instance);
        }
        if(instance == null) {
            try {
                instance = (AIAction) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new AIProcessingException(e);
            } catch (IllegalAccessException e) {
                throw new AIProcessingException(e);
            } catch (InvocationTargetException e) {
                throw new AIProcessingException(e);
            } catch (NoSuchMethodException e) {
                throw new AIProcessingException(e);
            }
        }

        String actionName = instance.getActionName();

        actionNameList.append(actionName).append(",");
        actionGroupList.addAction(instance);
        predictions.put(actionName,instance);
    }

    public Map<String, AIAction> getPredictions() {
        return predictions;
    }

    public StringBuilder getActionNameList() {
        return actionNameList;
    }

    private String buildPrompt(String prompt, int number, String actionNameList) {
        String append = NUMACTION;
        if(number > 1)
            append = NUMACTION_MULTIPROMPT;
        String query = PREACTIONCMD+ "{ "+prompt+" }"+ACTIONCMD+actionNameList +POSTACTIONCMD+number +append;
        log.debug(query);
        return query;
    }
    private String buildPromptWithJsonResponse(String prompt, int number, String actionNameList) {
        String query = " this is your prompt {"+prompt+"} and these are your actionNames {"+actionNameList+"}"+" reply back with just one action name in json format {'actionName':'','reasoning':''}";
        log.debug(query);
        log.debug(number+"");
        return query;
    }
    private String getModifiedActionName(StringBuilder stringBuffer) {
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
    private String buildPromptForOpenAI(String prompt, int number, StringBuilder actionNameList) {
        String append = NUMACTIONOI;
        if(number > 1)
            append = NUMACTION_MULTIPROMPT;
        String query = PREACTIONCMDOI+prompt+ACTIONCMDOI+getModifiedActionName(actionNameList)+" "+POSTACTIONCMDOI+number +" "+append;
        log.debug(query);
        return query;
    }

    private String buildPromptMultiStep(String prompt) {
        return "break down this prompt into multiple logical prompts in this json format { prompt : [ { id:unique_id,subprompt:'',depend_on=id} ] }- "+prompt+" - just give JSON and dont put any characters ";
    }

}
