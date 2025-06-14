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
public class SeleniumScriptProcessor extends ScriptProcessor{
    @Getter
    private Gson gson;
    @Getter
    private SeleniumProcessor seleniumProcessor;

    public SeleniumScriptProcessor() {
        gson = new Gson();
        seleniumProcessor = new SeleniumGeminiProcessor();
    }
    public SeleniumScriptProcessor(Gson gson) {
        this.gson = gson;
    }

    public SeleniumScriptProcessor(Gson gson, SeleniumProcessor seleniumProcessor) {
        this.gson = gson;
        this.seleniumProcessor = seleniumProcessor;
    }

    public SeleniumScriptProcessor(SeleniumProcessor seleniumProcessor) {
        gson = new Gson();
        this.seleniumProcessor = seleniumProcessor;
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

    public ScriptResult process(StringBuffer content, SeleniumCallback callback) {
        ScriptResult result = new ScriptResult();
        try {
            BufferedReader reader = new BufferedReader(new StringReader(content.toString()));
            log.debug("Processing content from StringBuffer");
            processCommands(reader, result, callback);
        } catch (IOException e) {
            log.error("Error processing content: " + e.getMessage());
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
    }    public void processCommands(BufferedReader reader, ScriptResult result, SeleniumCallback callback) throws IOException, AIProcessingException {
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                boolean process = callback.beforeWebAction(line, getSeleniumProcessor().getDriver());
                if (process) {
                    try {
                        processWebAction(line, callback, 0);
                        result.addResult(line, "success");
                        callback.afterWebAction(line, getSeleniumProcessor().getDriver());
                    } catch (Exception e) {
                        result.addResult(line, "error: " + e.getMessage());
                        log.error("Failed to process web action: {}", e.getMessage());
                    }
                } else {
                    result.addResult(line, "skipped by callback");
                }
            } catch (Exception e) {
                log.error("Error in web action processing: {}", e.getMessage());
                result.addResult(line, "error: " + e.getMessage());
                throw new AIProcessingException(e.getMessage());
            }
            log.debug("{}", result);
        }
    }@Override
    public void processWebAction(String line, SeleniumCallback callback, int retryCount) {
        try {
            getSeleniumProcessor().processWebAction(line); 
        } catch (Exception e) {
            log.warn("Error executing web action: {} - {}", line, e.getMessage());
            String newLine = callback.handleError(line, e.getMessage(), getSeleniumProcessor().getDriver(), retryCount);
            
            // If callback provided a retry command and we haven't exceeded retry limit, attempt it
            if (newLine != null && retryCount < 3) { // Limit to 3 retries
                retryCount = retryCount + 1;
                log.info("Retrying with modified command: {}", newLine);
                processWebAction(newLine, callback, retryCount);
            }
            // Error wasn't handled by retry, log it
            else {
                log.error("Failed to execute web action after {} retries", retryCount);
            }
        }
    }

    public void processCommands( BufferedReader reader, ScriptResult result, ActionCallback callback) throws IOException, AIProcessingException {
        SeleniumProcessor processor = getSeleniumProcessor();
        String line;


        while ((line = reader.readLine()) != null) {
            callback.sendtStatus("processing "+line, ActionState.WORKING);
            processor.processWebAction(line);
            callback.sendtStatus("processed "+line, ActionState.WORKING);
            log.debug("{}",result);
        }
    }

}
