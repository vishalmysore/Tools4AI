package com.t4a.processor.spring;

import com.t4a.predict.PredictionLoader;
import com.t4a.processor.GeminiV2ActionProcessor;
import org.springframework.context.ApplicationContext;

/**
 * This will ensure that the action classes are loaded from Spring Applicaiton Context rather than
 * creating the new one , the advantage of that is we can maintain spring dependency injection for all the beans
 * Uses Gemini for processing
 */
public class SpringGeminiProcessor extends GeminiV2ActionProcessor {

    public SpringGeminiProcessor(ApplicationContext context)  {
        PredictionLoader.getInstance(context);

    }
}
