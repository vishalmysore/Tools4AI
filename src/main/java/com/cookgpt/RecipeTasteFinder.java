package com.cookgpt;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Schema;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.api.Type;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.IOException;
import java.util.Arrays;

public class RecipeTasteFinder {
    public static void main(String[] args) throws IOException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        String promptText = "whats the taste of paneer butter masala?";

        whatsTheRecipeTaste(projectId, location, modelName, promptText);
    }

    public static String whatsTheRecipeTaste(String projectId, String location,
                                             String modelName, String promptText)
            throws IOException {

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {

            FunctionDeclaration functionDeclaration = FunctionDeclaration.newBuilder()
                    .setName("getRecipeTaste")
                    .setDescription("provide the taste of recipe based on name")
                    .setParameters(
                            Schema.newBuilder()
                                    .setType(Type.OBJECT)
                                    .putProperties("recipe", Schema.newBuilder()
                                            .setType(Type.STRING)
                                            .setDescription("recipe")
                                            .build()
                                    )
                                    .addRequired("recipe")
                                    .build()
                    )
                    .build();

            System.out.println("Function declaration h1:");
            System.out.println(functionDeclaration);


            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(functionDeclaration)
                    .build();


            GenerativeModel model =
                    GenerativeModel.newBuilder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            System.out.println(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            // The model will most likely return a function call to the declared
            // function `getRecipeTaste` with "Paneer Butter Masala" as the value for the
            // argument `recipe`.
            System.out.println("\nPrint response 1 : ");
            System.out.println(ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap().get("recipe").getStringValue());
            System.out.println(ResponseHandler.getText(response));

            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    "getRecipeTaste",
                                    IndianFoodRecipes.getRecipe()));
            System.out.println("Provide the function response 1: ");
            System.out.println(content);
            response = chat.sendMessage(content);

            // See what the model replies now
            System.out.println("Print response: ");
            String finalAnswer = ResponseHandler.getText(response);
            System.out.println(finalAnswer);

            return finalAnswer;
        }
    }
}
