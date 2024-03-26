package com.t4a.examples.eat;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class RecipeTasteAndDiet {


    public static void main(String[] args) throws IOException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        String promptText = "whats the taste of paneer butter masala? and is it healthy option for someone who is on diet?";

        whatsTheRecipeTasteAndDiet(projectId, location, modelName, promptText);
    }

    public static String whatsTheRecipeTasteAndDiet(String projectId, String location,
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

            FunctionDeclaration functionTaste = FunctionDeclaration.newBuilder()
                    .setName("getHealthyDiet")
                    .setDescription("provide the taste of recipe based on name")
                    .setParameters(
                            Schema.newBuilder()
                                    .setType(Type.OBJECT)
                                    .putProperties("taste", Schema.newBuilder()
                                            .setType(Type.STRING)
                                            .setDescription("taste")
                                            .build()
                                    )
                                    .addRequired("taste")
                                    .build()
                    )
                    .build();
            log.debug("Function declaration h1:");
            log.debug(""+functionDeclaration);
            log.debug("Function declaration h2:");
            log.debug(""+functionTaste);

            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(functionDeclaration)
                    .addFunctionDeclarations(functionTaste)
                    .build();


            GenerativeModel model =
                   new GenerativeModel.Builder()
                            .setModelName(modelName)
                            .setVertexAi(vertexAI)
                            .setTools(Arrays.asList(tool))
                            .build();
            ChatSession chat = model.startChat();

            log.debug(String.format("Ask the question 1: %s", promptText));
            GenerateContentResponse response = chat.sendMessage(promptText);

            // The model will most likely return a function call to the declared
            // function `getRecipeTaste` with "Paneer Butter Masala" as the value for the
            // argument `recipe`.
            log.debug("\nPrint response 1 : ");
            //log.debug(ResponseHandler.getContent(response).getParts(0).getFunctionCall().getArgs().getFieldsMap().get("recipe").getStringValue());
            log.debug(""+ResponseHandler.getContent(response));


            Content content =
                    ContentMaker.fromMultiModalData(
                            PartMaker.fromFunctionResponse(
                                    "getRecipeTaste",
                                    IndianFoodRecipes.getRecipe()),
                            PartMaker.fromFunctionResponse(
                                    "getHealthyDiet",
                                    IndianFoodRecipes.getHealthy()));
            log.debug("Provide the function response 1: ");
            log.debug(""+content);
            response = chat.sendMessage(content);

            log.debug("Print response content: ");
            log.debug(""+ResponseHandler.getContent(response));
            // See what the model replies now
            log.debug("Print response Text: ");
            String finalAnswer = ResponseHandler.getText(response);
            log.debug(finalAnswer);

            return finalAnswer;
        }
    }
}
