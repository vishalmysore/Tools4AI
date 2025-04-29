package com.t4a.test;

import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.detect.ActionCallback;
import com.t4a.detect.ExplainDecision;
import com.t4a.detect.HumanInLoop;
import com.t4a.examples.actions.CookingAction;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AIProcessorTest {
    AIProcessor mockProcessor = new AIProcessor() {
        @Override
        public Object processSingleAction(String promptText, AIAction action, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
            return action;
        }

        @Override
        public Object processSingleAction(String promptText, HumanInLoop humanVerification, ExplainDecision explain) throws AIProcessingException {
            return promptText;
        }

        @Override
        public Object processSingleAction(String promptText) throws AIProcessingException {
            return promptText;
        }

        @Override
        public Object processSingleAction(String promptText, ActionCallback callback) throws AIProcessingException {
            return null;
        }

        @Override
        public String query(String promptText) throws AIProcessingException {
            return promptText;
        }

        @Override
        public Object processSingleAction(String prompt, AIAction action, HumanInLoop humanVerification, ExplainDecision explain, ActionCallback callback) throws AIProcessingException {
            return null;
        }
    };

    @Test
     void testQueryWithTwoParameters() throws AIProcessingException {
        String question = "What is the meaning of life?";
        String answer = "42";
        String expected = " this was my question { "+ question+"} context - "+answer;
        String result = mockProcessor.query(question, answer);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testSummarize() throws AIProcessingException {
        String prompt = "This is a test.";
        String expected = " Summarize this { "+ prompt+"}";
        String result = mockProcessor.summarize(prompt);
        Assertions.assertEquals(expected, result);
    }


    @Test
    public void testProcessSingleActionWithThreeParameters() throws AIProcessingException {
        String promptText = "This is a test.";
        CookingAction actionInstance = new CookingAction();
        String actionName = "cookThisForLunch";
        Object result = mockProcessor.processSingleAction(promptText, actionInstance, actionName);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result instanceof GenericJavaMethodAction);
    }
}
