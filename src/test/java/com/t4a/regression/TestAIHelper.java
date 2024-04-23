package com.t4a.regression;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.predict.PredictionLoader;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Will use AI to test AI
 */

@Log
public class TestAIHelper {
    private String projectId;
    private String location;
    private String modelName;
    private ChatLanguageModel openAiChatModel;
    private String openAiKey;
    @Getter
    private ChatSession chat;
    private static TestAIHelper testAIHelper;
    private void initProp() {
        try (InputStream inputStream = PredictionLoader.class.getClassLoader().getResourceAsStream("tools4ai.properties")) {
            if(inputStream == null) {
                log.severe(" tools4ai properties not found ");
                return;
            }
            Properties prop = new Properties();
            prop.load(inputStream);
            // Read properties
            projectId = prop.getProperty("gemini.projectId");
            if(projectId != null)
                projectId = projectId.trim();
            location = prop.getProperty("gemini.location");
            if(location != null)
                location = location.trim();
            modelName = prop.getProperty("gemini.modelName");
            if(modelName != null)
                modelName = modelName.trim();
            openAiKey = prop.getProperty("openAiKey");
            if(openAiKey != null)
                openAiKey = openAiKey.trim();
            try (VertexAI vertexAI = new VertexAI(projectId, location)) {


                GenerativeModel model =
                        new GenerativeModel.Builder()
                                .setModelName(modelName)
                                .setVertexAi(vertexAI)
                                .build();
                GenerativeModel modelExplain =
                        new GenerativeModel.Builder()
                                .setModelName(modelName)
                                .setVertexAi(vertexAI)
                                .build();
                GenerativeModel multiCommand =
                        new GenerativeModel.Builder()
                                .setModelName(modelName)
                                .setVertexAi(vertexAI)
                                .build();

                chat = model.startChat();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TestAIHelper(){
        initProp();
    }
    public static TestAIHelper getInstance() {
        if(testAIHelper == null)
            testAIHelper = new TestAIHelper();
        return testAIHelper;
    }

    public String sendMessage(String msg) {
        try {
            return ResponseHandler.getText(getChat().sendMessage(msg));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
