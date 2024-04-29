package com.t4a.test;

import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.transform.GeminiV2PromptTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GeminiTransformerTest {

    @Test
    void testTransform() {

        try (MockedStatic<PredictionLoader> responseHandlerMock = Mockito.mockStatic(PredictionLoader.class)) {

            PredictionLoader mockGRP = Mockito.mock(PredictionLoader.class);
            responseHandlerMock.when(() -> PredictionLoader.getInstance()).thenReturn(mockGRP);
            ChatSession mockOpenAiChatModel = Mockito.mock(ChatSession.class);
            GenerateContentResponse responseMockGRP = Mockito.mock(GenerateContentResponse.class);
            when(responseMockGRP.getCandidatesCount()).thenReturn(1);
            // When getText is called on the responseHandlerMock, return a predefined string
            //  when(chatMock.sendMessage(argThat((String argument) -> argument.contains("which group does this prompt belong")))).thenReturn(responseMockGRP);
            String mockGRPSTR = "{'groupName':'No Group','explanation':'mock'}";
            try (MockedStatic<ResponseHandler> geminiMock = Mockito.mockStatic(ResponseHandler.class)) {
                geminiMock.when(() -> ResponseHandler.getText(any(GenerateContentResponse.class))).thenReturn(mockGRPSTR);

                Mockito.when(mockOpenAiChatModel.sendMessage(Mockito.anyString())).thenReturn(responseMockGRP);
                Mockito.when(mockGRP.getChatExplain()).thenReturn(mockOpenAiChatModel);
                GeminiV2PromptTransformer transformer = new GeminiV2PromptTransformer();
                String json = transformer.getJSONResponseFromAI("My name is vishal and I am from India. I love vegetarian food", "{}");
                Assertions.assertEquals("{'groupName':'No Group','explanation':'mock'}", json);
            }
        } catch (AIProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
