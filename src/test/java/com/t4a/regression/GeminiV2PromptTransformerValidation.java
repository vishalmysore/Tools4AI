package com.t4a.regression;

import com.t4a.examples.pojo.Organization;
import com.t4a.processor.AIProcessingException;
import com.t4a.test.Person;
import com.t4a.transform.GeminiV2PromptTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

 class GeminiV2PromptTransformerValidation {
    private GeminiV2PromptTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new GeminiV2PromptTransformer();
    }

    @Test
    void testTransformIntoJson() {
        String jsonString = "{\"name\":\"John\", \"age\":30}";
        String promptText = "John is 30 years old.";

        try {
            String result = transformer.transformIntoJson(jsonString, promptText);
            assertNotNull(result, "Result should not be null");
            // Add more assertions based on your expected output
        } catch (AIProcessingException e) {
            fail("Exception should not be thrown");
        }
    }
    @Test
    void testTransformIntoPojo() {
        String prompt = "John is 30 years old.";
        String className = Person.class.getName();
        String funName = "createPerson";
        String description = "Create a person object from the prompt";

        try {
            Object result = transformer.transformIntoPojo(prompt, className, funName, description);
            assertNotNull(result, "Result should not be null");
            // Add more assertions based on your expected output
        } catch (AIProcessingException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void testTransformIntoJsonWithValues() {
        String promptText = "John is 30 years old.";
        String[] name = {"name", "age"};

        try {
            String result = transformer.transformIntoJsonWithValues(promptText, name);
            assertNotNull(result, "Result should not be null");
            // Add more assertions based on your expected output
        } catch (AIProcessingException e) {
            fail("Exception should not be thrown");
        }
    }

     @Test
     void testTransformOrganizationIntoPojo() {
         String prompt = "Gulab Movies Inc is a big company and its based out of Mumbai and Bangalore, Amitabh Kapoor and Anil Bacchan are two of its famos employees.";
         String className = Organization.class.getName();
         String funName = "Create Organization Object";
         String description = "Create a Org object from the prompt";

         try {
             Object result = transformer.transformIntoPojo(prompt, className, funName, description);
             assertNotNull(result, "Result should not be null");
             // Add more assertions based on your expected output
         } catch (AIProcessingException e) {
             fail("Exception should not be thrown");
         }
     }
}
