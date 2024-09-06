package com.t4a.processor.scripts;

import com.google.gson.Gson;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.selenium.SeleniumGeminiProcessor;
import com.t4a.processor.selenium.SeleniumProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    public ScriptResult process(String fileName, SeleniumCallback callback ) {
        ScriptResult result = new ScriptResult();
        try (InputStream is = SeleniumScriptProcessor.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
             log.debug("Processing script file: " + fileName);
            processCommands(reader, result,callback);
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (NullPointerException e) {

            log.info("Resource file not found. Make sure the file path is correct.");
        } catch (AIProcessingException e) {
            log.error(e.getMessage());
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



}
