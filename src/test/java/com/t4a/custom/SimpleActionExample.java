package com.t4a.custom;

import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import lombok.extern.java.Log;

/**
 * A simple example demonstrating how to use AIProcessor to process a single action.
 * Make sure to set the "tools4ai.properties.path" system property to point to your configuration file.
 * You can run this class directly to see the output.
 * In our classpath there is only one Action SimpleAction hence it will process queries related to that action.
 * in the tools4ai.properties file we action.packages.to.scan=io.github.vishalmysore.simple to scan for actions.
 *## if you do not provide it will scan all actions available in classpath
 */
@Log
public class SimpleActionExample {
    public static void main(String[] args) {
        System.setProperty("tools4ai.properties.path", "io/github/vishalmysore/simple/tools4ai.properties");
        AIProcessor processor = PredictionLoader.getInstance().createOrGetAIProcessor();
        log.info("AIProcessor initialized: " + (processor != null));
        try {
            log.info(processor.query("Hello, world!"));
            log.info((String) processor.processSingleAction("what does Vishal like to eat he needs to eat 2 plates, he will take 5 hours to eat and he is vegetarian , he needs to eat somethign which takes 30 mins to cook"));
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}