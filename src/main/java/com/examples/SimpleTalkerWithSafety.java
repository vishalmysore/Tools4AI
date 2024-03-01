package com.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SimpleTalkerWithSafety {
    public static void main(String[] args) {
        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerateContentResponse response;
            List<SafetySetting> safetySettings = Arrays.asList(
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build()
            );
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            ChatSession chatSession = new ChatSession(model);

            response = chatSession.sendMessage("what does ***zoo mean.");
            System.out.println(ResponseHandler.getText(response));

          //  response = chatSession.sendMessage("I want to eat really spicy Indian Food Today?");
           // System.out.println(ResponseHandler.getText(response));

          //  response = chatSession.sendMessage("What is the recipe for Paneer Butter Masala?");
          //  System.out.println(ResponseHandler.getText(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
