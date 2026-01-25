package com.t4a.processor.scripts;

import com.google.gson.Gson;
import com.t4a.api.ActionList;
import com.t4a.api.GroupInfo;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

/**
 * Processes action script line by line , previous result is validated
 */
@Slf4j
public  class ScriptProcessor implements BaseScriptProcessor {

    @Getter
    private Gson gson;
    @Getter
    private AIProcessor actionProcessor;

    public ScriptProcessor() {
        gson = new Gson();
        actionProcessor = new GeminiV2ActionProcessor();
    }
    public ScriptProcessor(Gson gson) {
        this.gson = gson;
    }

    public ScriptProcessor(Gson gson, AIProcessor actionProcessor) {
        this.gson = gson;
        this.actionProcessor = actionProcessor;
    }

    public ScriptProcessor(AIProcessor actionProcessor) {
        gson = new Gson();
        this.actionProcessor = actionProcessor;
    }
    public ScriptResult process(String fileName) {
       return process(fileName,null);
    }

    public ScriptResult process(String fileName, ScriptCallback callback) {
        ScriptResult result = new ScriptResult();
        try (InputStream is = ScriptProcessor.class.getClassLoader().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            processCommands(callback, reader, result);
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (NullPointerException e) {

            log.info("Resource file not found. Make sure the file path is correct.");
        } catch (AIProcessingException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    public void processCommands(ScriptCallback callback, String completeInstructions, ScriptResult result) throws IOException, AIProcessingException {
        AIProcessor processor = getActionProcessor();
        ActionList actionList = PredictionLoader.getInstance().getActionGroupList();
        Map<GroupInfo, String> mapStr = actionList.getGroupActions();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<GroupInfo, String> entry : mapStr.entrySet()) {
            sb.append(entry.getKey() + " -> " + entry.getValue());
        }
        completeInstructions = processor.query("these are the available actions "+sb.toString()+" - now provide me step by step instructions to achieve the goal mentioned here : "+completeInstructions+" - provide each instruction in new line with no other explanations");
        // Assuming completeInstructions is a multi-line string, split into lines and process
        BufferedReader commandsReader = new BufferedReader(new StringReader(completeInstructions));

        processCommands(callback, commandsReader, result);

    }

    public void processCommands(ScriptCallback callback, BufferedReader reader, ScriptResult result) throws IOException, AIProcessingException {
        AIProcessor processor = getActionProcessor();
        String line;

        String resultStr = null;
        while ((line = reader.readLine()) != null) {

            log.info(line);
            String decision = "yes";
            String previousResult = getGson().toJson(result);
            log.info(previousResult);
            if(result.getResults().isEmpty() ) {
                String prompt = "these are the results of previous actions - "+previousResult+" - should we proceed with this step - " +line+" -  Here are  your 5 instructions : 1) provide an answer as yes if the answer is not present in previous results section or 2) no if it has been answered 3) do not say thing else 4) do not add any other explanations 5)answer should be yes or no only";
                decision = getActionProcessor().query(prompt);
            }
            if(decision.toLowerCase().contains("yes")) {
                resultStr =  (String) processor.processSingleAction(line + " - here are previous action results "+previousResult);
                result.addResult(line,resultStr);
            } else {
                result.addResult(line," No action taken due to "+previousResult);
            }

            if(callback != null) {
                resultStr = callback.lineResult(resultStr);
            }
            log.info(resultStr);

        }
    }

    public String summarize(ScriptResult result) {
        try {
            return getActionProcessor().query(" Summarize this - "+getGson().toJson(result));
        } catch (AIProcessingException e) {
            log.error(e.getMessage());
        }
        return "Cannot summarize results";
    }


}
