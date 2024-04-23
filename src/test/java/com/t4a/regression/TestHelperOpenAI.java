package com.t4a.regression;

import com.t4a.predict.PredictionLoader;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHelperOpenAI {


    private ChatLanguageModel openAiChatModel;
    private static TestHelperOpenAI testAIHelper;

    private void initProp() {
        openAiChatModel = PredictionLoader.getInstance().getOpenAiChatModel();
    }

    private TestHelperOpenAI() {
        initProp();
    }

    public static TestHelperOpenAI getInstance() {
        if (testAIHelper == null)
            testAIHelper = new TestHelperOpenAI();
        return testAIHelper;
    }

    public String sendMessage(String msg) {
        return openAiChatModel.generate(msg);
    }
}