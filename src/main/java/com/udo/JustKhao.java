package com.udo;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.mysore.bridge.JavaMethodBridge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class JustKhao {
    public JustKhao(){

    }
    public static void main(String[] args) throws IOException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";

        String promptText = "can you book a dinner reseration for Vishal and 4 other people at Maharaj on 15th august and make sure its cancellable";

        String status = khao(projectId, location, modelName, promptText);
        System.out.println(promptText+ " : "+status);
    }

    public static String khao(String projectId, String location, String modelName,String promptText) {
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            JavaMethodBridge generator = new JavaMethodBridge();
            FunctionDeclaration bookMyReservation = generator.buildFunction("com.udo.JustKhao", "bookMyReservation","bookMyReservation","Booking reservation Function");

            System.out.println("Function declaration h1:");
            System.out.println(bookMyReservation);


            //add the function to the tool
            Tool tool = Tool.newBuilder()
                    .addFunctionDeclarations(bookMyReservation)
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

            System.out.println("\nPrint response 1 : ");
            System.out.println(ResponseHandler.getContent(response));
            System.out.println(generator.getPropertyValuesJsonString(response));

            generator.invoke(response,new JustKhao());


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public String bookMyReservation(String name , int numberOfPeople, String restaurantName, boolean cancel, String reserveDate) {
        System.out.println(name +":"+numberOfPeople+":"+restaurantName+":"+cancel);
        return "reserved";
    }
}
