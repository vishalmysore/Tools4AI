package com.t4a.processor.scripts;

import com.google.gson.Gson;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ActionState;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.selenium.SeleniumGeminiProcessor;
import com.t4a.processor.selenium.SeleniumProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class SeleniumScriptProcessor {
    @Getter
    private Gson gson;
    @Getter
    private SeleniumProcessor actionProcessor;

    public SeleniumScriptProcessor() {
        gson = new Gson();
        actionProcessor = new SeleniumGeminiProcessor();
    }
    public SeleniumScriptProcessor(Gson gson) {
        this.gson = gson;
    }

    public SeleniumScriptProcessor(Gson gson, SeleniumProcessor actionProcessor) {
        this.gson = gson;
        this.actionProcessor = actionProcessor;
    }

    public SeleniumScriptProcessor(SeleniumProcessor actionProcessor) {
        gson = new Gson();
        this.actionProcessor = actionProcessor;
    }

    public ScriptResult process(String fileName) {
        return process(fileName,new LoggingSeleniumCallback());
    }
    public ScriptResult process(String fileName, SeleniumCallback callback) {
        ScriptResult result = new ScriptResult();
        try {
            // Try classpath first
            InputStream is = SeleniumScriptProcessor.class.getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                // If not found in classpath, try filesystem
                is = new FileInputStream(fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                log.debug("Processing script file: " + fileName);
                processCommands(reader, result, callback);
            }
        } catch (IOException e) {
            log.error("Error processing file: " + e.getMessage());
        } catch (AIProcessingException e) {
            log.error("AI Processing error: " + e.getMessage());
        }
        return result;
    }

    public ScriptResult process(String content, ActionCallback callback) {
        ScriptResult result = new ScriptResult();
        try {
            BufferedReader reader = new BufferedReader(new StringReader(content));
            log.debug("Processing content string");
            processCommands(reader, result, callback);
        } catch (IOException e) {
            log.error("Error processing content: " + e.getMessage());
        } catch (AIProcessingException e) {
            log.error("AI Processing error: " + e.getMessage());
        }
        return result;
    }

    public void processCommands( BufferedReader reader, ScriptResult result, SeleniumCallback callback) throws IOException, AIProcessingException {
        SeleniumProcessor processor = getActionProcessor();
        String line;


        while ((line = reader.readLine()) != null) {
             callback.beforeWebAction(processor.getDriver());
             processor.processWebAction(line);
             callback.afterWebAction(processor.getDriver());
             log.debug("{}",result);
        }
    }

    public void processCommands( BufferedReader reader, ScriptResult result, ActionCallback callback) throws IOException, AIProcessingException {
        SeleniumProcessor processor = getActionProcessor();
        String line;


        while ((line = reader.readLine()) != null) {
            callback.sendtStatus("processing "+line, ActionState.WORKING);
            processor.processWebAction(line);
            callback.sendtStatus("processed "+line, ActionState.WORKING);
            log.debug("{}",result);
        }
    }

}
