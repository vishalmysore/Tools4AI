package com.t4a.test;

import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.examples.pojo.Employee;
import com.t4a.examples.pojo.Organization;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiImageActionProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

public class GeminiImageActionProcessorTest {
    private GeminiImageActionProcessor geminiImageActionProcessor;

    @BeforeEach
    public void setUp() {
        geminiImageActionProcessor = new GeminiImageActionProcessor();
    }

    @Test
    void testResponse() throws IOException {
        String mockGRPSTR = "{'groupName':'No Group','explanation':'mock'}";
        GenerativeModel mockGRP = Mockito.mock(GenerativeModel.class);
        GenerateContentResponse mockGRPResponse = Mockito.mock(GenerateContentResponse.class);
        Mockito.when(mockGRPResponse.getCandidatesCount()).thenReturn(1);
        Mockito.when(mockGRP.generateContent(any(Content.class))).thenReturn(mockGRPResponse);
        try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
            geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRPSTR);
            geminiImageActionProcessor.setModel(mockGRP);
            String text =  geminiImageActionProcessor.imageToText(this.getClass().getClassLoader().getResource("fitness.PNG"));
            Assertions.assertEquals(mockGRPSTR, text);
        } catch (AIProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    void testResponseMock() throws IOException {
        String mockGRPSTR = "{'groupName':'No Group','explanation':'mock'}";
        GenerativeModel mockGRP = Mockito.mock(GenerativeModel.class);
        GenerateContentResponse mockGRPResponse = Mockito.mock(GenerateContentResponse.class);
        Mockito.when(mockGRPResponse.getCandidatesCount()).thenReturn(1);
        Mockito.when(mockGRP.generateContent(any(Content.class))).thenReturn(mockGRPResponse);
        try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
            geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRPSTR);
            geminiImageActionProcessor.setModel(mockGRP);
            String text =  geminiImageActionProcessor.imageToJson(this.getClass().getClassLoader().getResource("fitness.PNG"),"fit");
            Assertions.assertEquals(mockGRPSTR, text);
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testResponseClass() throws IOException {
        String mockGRPSTR = "{'groupName':'No Group','explanation':'mock'}";
        GenerativeModel mockGRP = Mockito.mock(GenerativeModel.class);
        GenerateContentResponse mockGRPResponse = Mockito.mock(GenerateContentResponse.class);
        Mockito.when(mockGRPResponse.getCandidatesCount()).thenReturn(1);
        Mockito.when(mockGRP.generateContent(any(Content.class))).thenReturn(mockGRPResponse);
        try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
            geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRPSTR);
            geminiImageActionProcessor.setModel(mockGRP);
            String text =  geminiImageActionProcessor.imageToJson(this.getClass().getClassLoader().getResource("fitness.PNG"), Employee.class);
            Assertions.assertEquals(mockGRPSTR, text);
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    void testResponseClassCompare() throws IOException {
        String mockGRPSTR = "{'groupName':'No Group','explanation':'mock'}";
        GenerativeModel mockGRP = Mockito.mock(GenerativeModel.class);
        GenerateContentResponse mockGRPResponse = Mockito.mock(GenerateContentResponse.class);
        Mockito.when(mockGRPResponse.getCandidatesCount()).thenReturn(1);
        Mockito.when(mockGRP.generateContent(any(Content.class))).thenReturn(mockGRPResponse);
        try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
            geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRPSTR);
            geminiImageActionProcessor.setModel(mockGRP);
            String text =  geminiImageActionProcessor.compareImages(this.getClass().getClassLoader().getResource("fitness.PNG"), this.getClass().getClassLoader().getResource("fitness.PNG"));
            Assertions.assertEquals(mockGRPSTR, text);
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testImageToPojo() throws IOException {
        String mockGRPSTR = "{\n" +
                "    \"className\": \"com.t4a.examples.pojo.Organization\",\n" +
                "    \"fields\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"name\",\n" +
                "            \"fieldType\": \"String\",\n" +
                "            \"fieldValue\": \"Gulab Movies Inc\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"em\",\n" +
                "            \"className\": \"com.t4a.examples.pojo.Employee\",\n" +
                "            \"fieldType\": \"list\",\n" +
                "            \"prompt\": \"If you find more than 1 Employee add it as another object inside fieldValue\",\n" +
                "            \"fieldValue\": [\n" +
                "                {\n" +
                "                    \"fieldName\": \"name\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Amitabh Kapoor\",\n" +
                "                    \"department\": \"Actor\",\n" +
                "                    \"salary\": 1000000,\n" +
                "                    \"location\": \"Mumbai\",\n" +
                "                    \"dateJoined\": \"01-01-2020\",\n" +
                "                    \"tasks\": []\n" +
                "                },\n" +
                "                {\n" +
                "                    \"fieldName\": \"name\",\n" +
                "                    \"fieldType\": \"String\",\n" +
                "                    \"fieldValue\": \"Anil Bacchan\",\n" +
                "                    \"department\": \"Director\",\n" +
                "                    \"salary\": 1500000,\n" +
                "                    \"location\": \"Bangalore\",\n" +
                "                    \"dateJoined\": \"15-01-2021\",\n" +
                "                    \"tasks\": []\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"locations\",\n" +
                "            \"description\": \"there could be multiple String\",\n" +
                "            \"className\": \"java.lang.String\",\n" +
                "            \"fieldType\": \"list\",\n" +
                "            \"fieldValue\": [\"Mumbai\", \"Bangalore\"]\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"customers\",\n" +
                "            \"isArray\": true,\n" +
                "            \"fieldType\": \"Customer[]\",\n" +
                "            \"fieldValue\": []\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        GenerativeModel mockGRP = Mockito.mock(GenerativeModel.class);
        GenerateContentResponse mockGRPResponse = Mockito.mock(GenerateContentResponse.class);
        Mockito.when(mockGRPResponse.getCandidatesCount()).thenReturn(1);
        Mockito.when(mockGRP.generateContent(any(Content.class))).thenReturn(mockGRPResponse);
        try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
            geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRPSTR);
            geminiImageActionProcessor.setModel(mockGRP);
            Organization org = (Organization) geminiImageActionProcessor.imageToPojo(this.getClass().getClassLoader().getResource("fitness.PNG"), Employee.class);
            Assertions.assertEquals("Gulab Movies Inc", org.getName());
            Assertions.assertEquals(2, org.getEm().size());
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
