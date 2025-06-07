package com.t4a.test;

import com.t4a.detect.DetectValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void testPromptWithNull() {
        detectValues.setPrompt(null);
        assertNull(detectValues.getPrompt());
    }

    @Test
    void testPromptWithEmptyString() {
        detectValues.setPrompt("");
        assertEquals("", detectValues.getPrompt());
    }

    @Test
    void testContext() {
        String context = "Test Context";
        detectValues.setContext(context);
        assertEquals(context, detectValues.getContext());
    }

    @Test
    void testContextWithNull() {
        detectValues.setContext(null);
        assertNull(detectValues.getContext());
    }

    @Test
    void testContextWithEmptyString() {
        detectValues.setContext("");
        assertEquals("", detectValues.getContext());
    }

    @Test
    void testResponse() {
        String response = "Test Response";
        detectValues.setResponse(response);
        assertEquals(response, detectValues.getResponse());
    }

    @Test
    void testResponseWithNull() {
        detectValues.setResponse(null);
        assertNull(detectValues.getResponse());
    }

    @Test
    void testResponseWithEmptyString() {
        detectValues.setResponse("");
        assertEquals("", detectValues.getResponse());
    }

    @Test
    void testAdditionalData() {
        String additionalData = "Test Additional Data";
        detectValues.setAdditionalData(additionalData);
        assertEquals(additionalData, detectValues.getAdditionalData());
    }

    @Test
    void testAdditionalDataWithNull() {
        detectValues.setAdditionalData(null);
        assertNull(detectValues.getAdditionalData());
    }

    @Test
    void testAdditionalDataWithEmptyString() {
        detectValues.setAdditionalData("");
        assertEquals("", detectValues.getAdditionalData());
    }

    @Test
    void testEqualsWithSameValues() {
        DetectValues values1 = new DetectValues();
        values1.setPrompt("prompt");
        values1.setContext("context");
        values1.setResponse("response");
        values1.setAdditionalData("data");

        DetectValues values2 = new DetectValues();
        values2.setPrompt("prompt");
        values2.setContext("context");
        values2.setResponse("response");
        values2.setAdditionalData("data");

        assertEquals(values1, values2);
        assertEquals(values1.hashCode(), values2.hashCode());
    }

    @Test
    void testEqualsWithDifferentValues() {
        DetectValues values1 = new DetectValues();
        values1.setPrompt("prompt1");
        values1.setContext("context1");

        DetectValues values2 = new DetectValues();
        values2.setPrompt("prompt2");
        values2.setContext("context2");

        assertNotEquals(values1, values2);
        assertNotEquals(values1.hashCode(), values2.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        DetectValues values = new DetectValues();
        assertNotNull(values);
        assertFalse(values.equals(null));
    }

    @Test
    void testEqualsWithDifferentClass() {
        DetectValues values = new DetectValues();
        assertFalse(values.equals(new Object()));
    }

    @Test
    void testToString() {
        detectValues.setPrompt("Test Prompt");
        detectValues.setContext("Test Context");
        detectValues.setResponse("Test Response");
        detectValues.setAdditionalData("Test Data");

        String toString = detectValues.toString();
        assertTrue(toString.contains("prompt=Test Prompt"));
        assertTrue(toString.contains("context=Test Context"));
        assertTrue(toString.contains("response=Test Response"));
        assertTrue(toString.contains("additionalData=Test Data"));
    }

    @Test
    void testToStringWithNullValues() {
        String toString = detectValues.toString();
        assertTrue(toString.contains("prompt=null"));
        assertTrue(toString.contains("context=null"));
        assertTrue(toString.contains("response=null"));
        assertTrue(toString.contains("additionalData=null"));
    }
}