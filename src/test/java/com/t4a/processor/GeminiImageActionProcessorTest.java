package com.t4a.processor;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.predict.PredictionLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeminiImageActionProcessorTest {

    private GeminiImageActionProcessor processor;
    private MockedStatic<PredictionLoader> predictionLoaderStatic;
    private PredictionLoader predictionLoaderMock;
    private MockedStatic<ResponseHandler> responseHandlerStatic;
    private GenerativeModel generativeModelMock;

    @BeforeEach
    public void setUp() {
        predictionLoaderMock = mock(PredictionLoader.class);
        predictionLoaderStatic = mockStatic(PredictionLoader.class);
        predictionLoaderStatic.when(PredictionLoader::getInstance).thenReturn(predictionLoaderMock);
        when(predictionLoaderMock.getProjectId()).thenReturn("test-project");
        when(predictionLoaderMock.getLocation()).thenReturn("us-central1");
        when(predictionLoaderMock.getGeminiVisionModelName()).thenReturn("gemini-pro-vision");

        responseHandlerStatic = mockStatic(ResponseHandler.class);
        generativeModelMock = mock(GenerativeModel.class);

        try (MockedConstruction<VertexAI> vertexAiMock = mockConstruction(VertexAI.class)) {
            processor = new GeminiImageActionProcessor();
            processor.setModel(generativeModelMock);
        }
    }

    @AfterEach
    public void tearDown() {
        predictionLoaderStatic.close();
        responseHandlerStatic.close();
    }

    @Test
    public void testImageToText_Bytes() throws IOException, AIProcessingException {
        byte[] bytes = new byte[] { 1, 2, 3 };
        GenerateContentResponse responseMock = mock(GenerateContentResponse.class);
        when(generativeModelMock.generateContent(any(Content.class))).thenReturn(responseMock);
        responseHandlerStatic.when(() -> ResponseHandler.getText(responseMock)).thenReturn("description");

        String result = processor.imageToText(bytes);
        assertEquals("description", result);
    }

    @Test
    public void testCompareImages_Bytes() throws IOException, AIProcessingException {
        byte[] b1 = new byte[] { 1 };
        byte[] b2 = new byte[] { 2 };
        GenerateContentResponse responseMock = mock(GenerateContentResponse.class);
        when(generativeModelMock.generateContent(any(Content.class))).thenReturn(responseMock);
        responseHandlerStatic.when(() -> ResponseHandler.getText(responseMock)).thenReturn("differences");

        String result = processor.compareImages(b1, b2, "image/png", "compare");
        assertEquals("differences", result);
    }

    @Test
    public void testGetValueFor() throws AIProcessingException, IOException {
        byte[] bytes = new byte[] { 1 };
        GenerateContentResponse responseMock = mock(GenerateContentResponse.class);
        when(generativeModelMock.generateContent(any(Content.class))).thenReturn(responseMock);
        responseHandlerStatic.when(() -> ResponseHandler.getText(responseMock)).thenReturn("value");

        String result = processor.getValueFor(bytes, "element");
        assertEquals("value", result);
    }

    @Test
    public void testImageToText_URL_Exception() {
        assertThrows(AIProcessingException.class, () -> {
            processor.imageToText(new URL("http://example.com/img.png"));
        });
    }
}
