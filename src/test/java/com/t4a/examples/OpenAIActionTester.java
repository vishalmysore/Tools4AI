package com.t4a.examples;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.LoggingHumanDecision;
import com.t4a.processor.LogginggExplainDecision;
import com.t4a.processor.OpenAiActionProcessor;

public class OpenAIActionTester  {
    public static void main(String[] args) throws AIProcessingException {
        String prompt = "My friends name is Vishal ,I dont know what to cook for him today.";
        OpenAiActionProcessor processor = new OpenAiActionProcessor();
        String result = (String)processor.processSingleAction(prompt,new LoggingHumanDecision(),new LogginggExplainDecision());
        System.out.println(result);
    }

}
