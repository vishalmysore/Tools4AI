package com.t4a.test;

import com.t4a.detect.HallucinationAction;
import com.t4a.detect.HallucinationQA;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.AIProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HallucinationQATest {
    @Test
    void testCalculateTruthPercent() {
        HallucinationQA hallucinationQA = new HallucinationQA();
        hallucinationQA.setTruthPercentage("75%");

        double truthPercent = hallucinationQA.calculateTruthPercent();

        assertEquals(75.0, truthPercent);
    }





        @Test
        void testAskQuestions() throws AIProcessingException {
            // Create a mock AIProcessor
            AIProcessor mockProcessor = Mockito.mock(AIProcessor.class);
            Mockito.when(mockProcessor.query(Mockito.anyString())).thenReturn("Mock answer", "50%");

            // Create a HallucinationAction instance with the mock processor
            HallucinationAction hallucinationAction = new HallucinationAction(mockProcessor, "Test context");

            // Define the questions to ask
            String[] questions = {"Question 1", "Question 2"};

            // Call the askQuestions method
            List<HallucinationQA> result = hallucinationAction.askQuestions(questions);

            // Assert that the result is as expected
            Assertions.assertEquals(2, result.size());
            Assertions.assertEquals("Question 1", result.get(0).getQuestion());
            Assertions.assertEquals("Mock answer", result.get(0).getAnswer());
            Assertions.assertEquals("Test context", result.get(0).getContext());

        }
    }

