package com.t4a.processor.spring;

import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AnthropicActionProcessor;
import org.springframework.context.ApplicationContext;

public class SpringAnthropicProcessor extends AnthropicActionProcessor {
    public SpringAnthropicProcessor(ApplicationContext context)  {
        PredictionLoader.getInstance(context);

    }
}
