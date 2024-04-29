package com.t4a.test;

import com.t4a.predict.PredictionLoader;
import com.t4a.transform.AnthropicTransformer;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class AnthropicTransformerTest {
    @Test
    void testTransform() {

        try (MockedStatic<PredictionLoader> responseHandlerMock = Mockito.mockStatic(PredictionLoader.class)) {

            PredictionLoader mockGRP = Mockito.mock(PredictionLoader.class);
            responseHandlerMock.when(() -> PredictionLoader.getInstance()).thenReturn(mockGRP);
            AnthropicChatModel mockOpenAiChatModel = Mockito.mock(AnthropicChatModel.class);
            Mockito.when(mockOpenAiChatModel.generate(Mockito.anyString())).thenReturn("{'response':'success'}");
            Mockito.when(mockGRP.getAnthropicChatModel()).thenReturn(mockOpenAiChatModel);
            AnthropicTransformer transformer = new AnthropicTransformer();
            String json = transformer.getJSONResponseFromAI("My name is vishal and I am from India. I love vegetarian food","{}");
            Assertions.assertEquals("{'response':'success'}", json);
        }
    }
}
