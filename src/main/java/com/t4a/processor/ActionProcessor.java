package com.t4a.processor;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.*;
import com.google.gson.Gson;
import com.t4a.action.BlankAction;
import com.t4a.api.AIAction;
import com.t4a.api.JavaMethodExecutor;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.chain.Prompt;
import com.t4a.processor.chain.SubPrompt;
import lombok.extern.java.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * The main processor class, can execute single action or multiple action in sequence
 * - based on prompt can predict and trigger action
 * - based on prompt can predict and trigger multiple actions sequentially
 * - based on prompt can predict and trigger multiple actions parallely
 * - Can take HumanInLoop object and wait for Human Validation
 * - Can take ExplainDecision Object and provide why the decision was taken by AI
 *
 * </pre>
 */
@Log
public class ActionProcessor implements AIProcessor{

    /**
     * Process single action based on prediction
     * @param promptText prompt
     * @param humanVerification
     * @param explain
     * @return
     */
    public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain)  {
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            AIAction predictedAction = PredictionLoader.getInstance().getPredictedAction(promptText);
            log.info(predictedAction.getActionName());
            explain.explain(promptText, predictedAction.getActionName(), PredictionLoader.getInstance().explainAction(promptText,predictedAction.getActionName()));
            JavaMethodExecutor methodAction = new JavaMethodExecutor();

             methodAction.buildFunction(predictedAction);

            log.info("Function declaration h1:");
            log.info("" + methodAction.getGeneratedFunction());

            JavaMethodExecutor additionalQuestion = new JavaMethodExecutor();
            BlankAction blankAction = new BlankAction();
            FunctionDeclaration additionalQuestionFun = additionalQuestion.buildFunction(blankAction);
            log.info("Function declaration h1:");
            log.info("" + additionalQuestionFun);
            //add the function to the tool
            Tool.Builder toolBuilder = Tool.newBuilder();
            toolBuilder.addFunctionDeclarations(methodAction.getGeneratedFunction());
            toolBuilder.addFunctionDeclarations(additionalQuestionFun);
            Tool tool = toolBuilder.build();


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(PredictionLoader.getInstance().getModelName())
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();

            ChatSession chat = model.startChat();

            log.info(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            log.info("\nPrint response 1 : ");
            log.info("" + ResponseHandler.getContent(response));
            log.info(" Human Validation Proceed " +" funciton "+predictedAction.getActionName()+" params "+methodAction.getPropertyValuesJsonString(response));

            Object obj = null;
            FeedbackLoop feedbackFromHuman = humanVerification.allow(promptText,predictedAction.getActionName(),methodAction.getPropertyValuesMap(response));
            if(feedbackFromHuman.isAIResponseValid())
                obj = methodAction.action(response, predictedAction);
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


        } catch (IOException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Process Multiple actions sequentially based on prediction
     *
     * @param promptText
     * @return
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object processSingleAction(String promptText) throws IOException, InvocationTargetException, IllegalAccessException {
        return processSingleAction(promptText, new LoggingHumanDecision(), new LogginggExplainDecision());
    }

    public List<Object> processMultipleAction(String promptText, int num) throws IOException, InvocationTargetException, IllegalAccessException {
        return processMultipleAction(promptText, num,new LoggingHumanDecision(), new LogginggExplainDecision());
    }

    public List<Object> processMultipleAction(String promptText, int num,HumanInLoop humanVerification, ExplainDecision explain) throws IOException, InvocationTargetException, IllegalAccessException {
        List<Object> restulList = new ArrayList<Object>();
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            List<AIAction> predictedActionList = PredictionLoader.getInstance().getPredictedAction(promptText, num);
            List<JavaMethodExecutor> javaMethodExecutorList = new ArrayList<>();
            Tool.Builder toolBuilder = Tool.newBuilder();

            for (AIAction predictedAction:predictedActionList
                 ) {
                log.info(predictedAction.getActionName());
                JavaMethodExecutor methodAction = new JavaMethodExecutor();

                methodAction.buildFunction(predictedAction);

                log.info("Function declaration h1:");
                log.info("" + methodAction.getGeneratedFunction());



                //add the function to the tool

                toolBuilder.addFunctionDeclarations(methodAction.getGeneratedFunction());
                javaMethodExecutorList.add(methodAction);
            }

            Tool tool = toolBuilder.build();


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(PredictionLoader.getInstance().getModelName())
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();

            ChatSession chat = model.startChat();

            log.info(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            for (JavaMethodExecutor methodExecutor:javaMethodExecutorList
                 ) {


                log.info("Print response content: ");
                log.info("" + ResponseHandler.getContent(response));
                String result = ResponseHandler.getText(response);
                restulList.add(result);

                log.info(methodExecutor.getPropertyValuesJsonString(response));

                Object obj = methodExecutor.action(response, methodExecutor.getAction());
                log.info("" + obj);

                Content content =
                        ContentMaker.fromMultiModalData(
                                PartMaker.fromFunctionResponse(
                                        methodExecutor.getAction().getActionName(), Collections.singletonMap(methodExecutor.getAction().getActionName(), obj)));


                response = chat.sendMessage(content);




            }


            return restulList;


        }
    }

    public String processMultipleActionDynamically(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws IOException, InvocationTargetException, IllegalAccessException {
        String jsonPrompts = PredictionLoader.getInstance().getPredictedActionMultiStep(promptText);
        log.info(jsonPrompts);
        int startIndex = jsonPrompts.indexOf("{");
        int endIndex = jsonPrompts.lastIndexOf("}");
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            // Extract the JSON substring
            jsonPrompts = jsonPrompts.substring(startIndex, endIndex + 1);
            System.out.println("Extracted JSON substring:");
            System.out.println(jsonPrompts);
        } else {
            System.out.println("Error: Unable to find valid JSON substring.");
        }
        Gson gson = new Gson();
        Prompt prompt = gson.fromJson(jsonPrompts, Prompt.class);

        // Process subprompts
        for (SubPrompt subprompt : prompt.getPrmpt()) {
            recurrProcessPrompt(subprompt, prompt);
        }

        String json =  gson.toJson(prompt);
        return PredictionLoader.getInstance().getMultiStepResult(json);
    }

    private  SubPrompt recurrProcessPrompt(SubPrompt subprompt, Prompt prompt) {
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

    private SubPrompt getDependent(SubPrompt subprompt, Prompt prompt, String dependid) {
        SubPrompt tempsubPrompt = new SubPrompt();
        tempsubPrompt.setId(dependid);
        tempsubPrompt= prompt.getPrmpt().get(prompt.getPrmpt().indexOf(tempsubPrompt));
        if(!tempsubPrompt.isProcessed())
           return recurrProcessPrompt(tempsubPrompt, prompt);
        return tempsubPrompt;
    }


    private  SubPrompt processSubprompt(SubPrompt sub,String depdendentResult) {

        if(!sub.isProcessed()){
            sub.setProcessed(true);
        }
        try {
            sub.setResult((String)processSingleAction(sub.getSubprompt()+" here is additional information "+depdendentResult));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        log.info("processing "+sub);
        // Perform processing logic here
        return sub;
    }

    private static boolean hasProcessed(String id) {
        // Dummy implementation to simulate processing
        return true;
    }
}
