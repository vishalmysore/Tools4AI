package com.t4a.test;

import com.t4a.detect.DetectValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class DetectValuesTest {

    private DetectValues detectValues;

    @BeforeEach
    public void setup() {
        detectValues = new DetectValues();
    }

    @Test
     void testPrompt() {
        String prompt = "Test Prompt";
        detectValues.setPrompt(prompt);
        assertEquals(prompt, detectValues.getPrompt());
    }

    @Test
     void testContext() {
        String context = "Test Context";
        detectValues.setContext(context);
        assertEquals(context, detectValues.getContext());
    }

    @Test
     void testResponse() {
        String response = "Test Response";
        detectValues.setResponse(response);
        assertEquals(response, detectValues.getResponse());
    }

    @Test
     void testAdditionalData() {
        String additionalData = "Test Additional Data";
        detectValues.setAdditionalData(additionalData);
        assertEquals(additionalData, detectValues.getAdditionalData());
    }
}