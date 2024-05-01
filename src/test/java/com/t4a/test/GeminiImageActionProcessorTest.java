package com.t4a.test;

import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
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
            throw new RuntimeException(e);
        }
    }
}
