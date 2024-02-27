package com.simple;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.*;

import java.io.IOException;

/**
 * Just basic example demonstrating chatting with Gemini
 */
public class SimpleTalker {
    public static void main(String[] args) {
        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerateContentResponse response;

            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            ChatSession chatSession = new ChatSession(model);

            response = chatSession.sendMessage("Hello CookGPT.");
            System.out.println(ResponseHandler.getText(response));

            response = chatSession.sendMessage("I want to eat really spicy Indian Food Today?");
            System.out.println(ResponseHandler.getText(response));

            response = chatSession.sendMessage("What is the recipe for Paneer Butter Masala?");
            System.out.println(ResponseHandler.getText(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}