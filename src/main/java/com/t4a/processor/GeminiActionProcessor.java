package com.t4a.processor;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.*;
import com.google.gson.Gson;
import com.t4a.api.AIAction;
import com.t4a.api.ActionRisk;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.FeedbackLoop;
import com.t4a.detect.HumanInLoop;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.chain.Prompt;
import com.t4a.processor.chain.SubPrompt;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * The main processor class, can execute single action or multiple action in sequence Uses Gemini Function calling
 * - based on prompt can predict and trigger action
 * - based on prompt can predict and trigger multiple actions sequentially
 * - based on prompt can predict and trigger multiple actions parallely
 * - Can take HumanInLoop object and wait for Human Validation
 * - Can take ExplainDecision Object and provide why the decision was taken by AI
 *
 * </pre>
 */
@Deprecated // This class is deprecated and will be removed in future versions please use GeminiV2ActionProcessor instead
@Slf4j
public class GeminiActionProcessor implements AIProcessor{
    private Gson gson;
    public GeminiActionProcessor() {
    }
    public GeminiActionProcessor(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String query(String promptText) throws AIProcessingException {
        GenerateContentResponse response ;
        try {
            response = PredictionLoader.getInstance().getChatExplain().sendMessage(promptText);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  ResponseHandler.getText(response);
    }

    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException  {
       return processSingleAction( promptText, null,  humanVerification,  explain) ;
    }
    /**
     * Process single action based on prediction
     * @param promptText prompt
     * @param humanVerification
     * @param explain
     * @return
     */
    public Object processSingleAction(String promptText,AIAction predictedAction, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException  {
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {

            if(predictedAction == null) {
                predictedAction = PredictionLoader.getInstance().getPredictedAction(promptText);
                if(predictedAction.getActionRisk() == ActionRisk.HIGH) {
                    log.warn(" This is a high risk action needs to be explicitly provided by human operator cannot be predicted by AI "+predictedAction.getActionName());
                    return "This is a high risk action will not proceed "+predictedAction.getActionName();
                }
            }


            log.debug((predictedAction).getActionName());

            JavaMethodExecutor methodAction = new JavaMethodExecutor(gson);

             methodAction.buildFunction(predictedAction);

            log.debug("Function declaration h1:");
            log.debug("" + methodAction.getGeneratedFunction());

            JavaMethodExecutor additionalQuestion = new JavaMethodExecutor();
         //   BlankAction blankAction = new BlankAction();
         //   FunctionDeclaration additionalQuestionFun = additionalQuestion.buildFunction(blankAction);
         //   log.debug("Function declaration h1:");
        //    log.debug("" + additionalQuestionFun);
            //add the function to the tool
            Tool.Builder toolBuilder = Tool.newBuilder();
            toolBuilder.addFunctionDeclarations(methodAction.getGeneratedFunction());
       //     toolBuilder.addFunctionDeclarations(additionalQuestionFun);
            Tool tool = toolBuilder.build();


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
            log.debug(" Human Validation Proceed " +" funciton "+predictedAction.getActionName()+" params "+methodAction.getPropertyValuesJsonString(response));
            Object actionResponse = null;
            if(humanVerification != null) {
                FeedbackLoop feedbackFromHuman = humanVerification.allow(promptText, predictedAction.getActionName(), methodAction.getPropertyValuesMap(response));
                if (feedbackFromHuman.isAIResponseValid()) {
                    actionResponse = methodAction.action(response, predictedAction);
                }

            } else {
                actionResponse = methodAction.action(response, predictedAction);
            }
            log.debug("" + actionResponse);
           /* Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    predictedAction.getActionName(), Collections.singletonMap(predictedAction.getActionName(), actionResponse)));


            response = chat.sendMessage(content);

            log.debug("Print response content: ");
            log.debug("" + ResponseHandler.getContent(response));
            String result = ResponseHandler.getText(response);

            */
            return actionResponse;


        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AIProcessingException(e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage());
            throw new AIProcessingException(e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
            throw new AIProcessingException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            return "failed";
        }

    }

    /**
     * Process Multiple actions sequentially based on prediction
     *
     * @param promptText
     * @return

     */
    @Override
    public Object processSingleAction(String promptText)  throws AIProcessingException {
        return processSingleAction(promptText, null, new LoggingHumanDecision(), new LogginggExplainDecision());
    }

    /**
     * Directly pass the action object with the prompt , this is useful in case of spring beans as the actions can be
     * initialized as spring boot as beans with other dependency
     * @param promptText
     * @param action
     * @return
     * @throws AIProcessingException
     */
    public Object processSingleAction(String promptText, AIAction action)  throws AIProcessingException {
        return processSingleAction(promptText, action, null,null);
    }

    /**
     * Trigger the action by specifically passing the name, prediction wont be called . This action will be executed
     * Parameters for the action will be taken from the prompt
     * @param promptText
     * @param actionName
     * @return
     * @throws AIProcessingException
     */
    public Object processSingleAction(String promptText, String actionName)  throws AIProcessingException {
        AIAction action = PredictionLoader.getInstance().getAiAction(actionName);
        if(action == null) {
            throw new AIProcessingException(" action not found "+actionName);
        }
        return processSingleAction(promptText, action, null,null);
    }
    public List<Object> processMultipleAction(String promptText, int num) throws AIProcessingException  {
        return processMultipleAction(promptText, num,new LoggingHumanDecision(), new LogginggExplainDecision());
    }

    public List<Object> processMultipleAction(String promptText, int num,HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException{
        List<Object> restulList = new ArrayList<Object>();
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            List<AIAction> predictedActionList = PredictionLoader.getInstance().getPredictedAction(promptText, num);
            List<JavaMethodExecutor> javaMethodExecutorList = new ArrayList<>();
            Tool.Builder toolBuilder = Tool.newBuilder();

            for (AIAction predictedAction:predictedActionList
                 ) {
                log.debug(predictedAction.getActionName());
                JavaMethodExecutor methodAction = new JavaMethodExecutor();

                methodAction.buildFunction(predictedAction);

                log.debug("Function declaration h1:");
                log.debug("" + methodAction.getGeneratedFunction());



                //add the function to the tool

                toolBuilder.addFunctionDeclarations(methodAction.getGeneratedFunction());
                javaMethodExecutorList.add(methodAction);
            }

            Tool tool = toolBuilder.build();


            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName(PredictionLoader.getInstance().getModelName())
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();

            ChatSession chat = model.startChat();

            log.debug(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response ;
            try {
                response = chat.sendMessage(promptText);
            } catch (IOException e) {
                throw new AIProcessingException(e);
            }

            for (JavaMethodExecutor methodExecutor:javaMethodExecutorList
                 ) {


                log.debug("Print response content: ");
                log.debug("" + ResponseHandler.getContent(response));
                String result = ResponseHandler.getText(response);
                restulList.add(result);

                log.debug(methodExecutor.getPropertyValuesJsonString(response));

                Object obj;
                try {
                    obj = methodExecutor.action(response, methodExecutor.getAction());
                } catch (InvocationTargetException e) {
                    throw new AIProcessingException(e);
                } catch (IllegalAccessException e) {
                    throw new AIProcessingException(e);
                }
                log.debug("" + obj);

                Content content =
                        ContentMaker.fromMultiModalData(
                                PartMaker.fromFunctionResponse(
                                        methodExecutor.getAction().getActionName(), Collections.singletonMap(methodExecutor.getAction().getActionName(), obj)));


                try {
                    response = chat.sendMessage(content);
                } catch (IOException e) {
                    throw new AIProcessingException(e);
                }


            }


            return restulList;


        }
    }

    /**
     * Converts one big prompt into JSON and process them as single prompts one by one or parallely based on dependency order
     * @param promptText
     * @param humanVerification
     * @param explain
     * @return
     * @throws AIProcessingException
     */

    public String processMultipleActionDynamically(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException{
        String jsonPrompts = PredictionLoader.getInstance().getPredictedActionMultiStep(promptText);
        log.debug(jsonPrompts);
        int startIndex = jsonPrompts.indexOf("{");
        int endIndex = jsonPrompts.lastIndexOf("}");
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            // Extract the JSON substring
            jsonPrompts = jsonPrompts.substring(startIndex, endIndex + 1);
            log.debug("Extracted JSON substring:");
            log.debug(jsonPrompts);
        } else {
            log.debug("Error: Unable to find valid JSON substring.");
        }
        Gson gson = new Gson();
        Prompt prompt = gson.fromJson(jsonPrompts, Prompt.class);

        // Process subprompts
        for (SubPrompt subprompt : prompt.getPrmpt()) {
            recurrProcessPrompt(subprompt, prompt);
        }

        String json =  gson.toJson(prompt);
        log.debug(json);
        return PredictionLoader.getInstance().getMultiStepResult(json);
    }

    private  SubPrompt recurrProcessPrompt(SubPrompt subprompt, Prompt prompt) throws AIProcessingException{
        String depdendentResult = null;
        if(subprompt.canBeExecutedParallely()){
            return processSubprompt(subprompt,depdendentResult);
        } else {
            String dependid = subprompt.getDepend_on().trim();
            if(dependid.contains(",")) {
              String[] depedsplit = dependid.split(",");
                for (String depedentID:depedsplit
                     ) {
                    getDependent(subprompt, prompt, depedentID);
                }

            } else {
                depdendentResult =  getDependent(subprompt, prompt, dependid).getResult();
            }
            return processSubprompt(subprompt,depdendentResult);
        }
    }

    private SubPrompt getDependent(SubPrompt subprompt, Prompt prompt, String dependid) throws AIProcessingException{
        SubPrompt tempsubPrompt = new SubPrompt();
        tempsubPrompt.setId(dependid);
        tempsubPrompt= prompt.getPrmpt().get(prompt.getPrmpt().indexOf(tempsubPrompt));
        if(!tempsubPrompt.isProcessed())
           return recurrProcessPrompt(tempsubPrompt, prompt);
        return tempsubPrompt;
    }


    private  SubPrompt processSubprompt(SubPrompt sub,String depdendentResult) throws AIProcessingException{

        if(!sub.isProcessed()){
            sub.setProcessed(true);
        }
      
            sub.setResult((String)processSingleAction(sub.getSubprompt()+" here is additional information "+depdendentResult));
        
        log.debug("processing "+sub);
        // Perform processing logic here
        return sub;
    }

    private static boolean hasProcessed(String id) {
        // Dummy implementation to simulate processing
        return true;
    }

    /**
     * Return all the actions comma seperated
     * @return
     */
    public String getActionList() {
        return PredictionLoader.getInstance().getActionNameList().toString();
    }
}
