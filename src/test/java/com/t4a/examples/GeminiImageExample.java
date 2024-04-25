package com.t4a.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.*;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiImageActionProcessor;
import lombok.extern.java.Log;

import java.io.IOException;

@Log
public class GeminiImageExample {

    public static void main1(String[] args) {
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(), PredictionLoader.getInstance().getLocation())) {
            // Update the values for your query.
            String firstTextPrompt = "What is this image";
            String imageUri = "gs://generativeai-downloads/images/scones.jpg";
            String secondTextPrompt = "what did I just show you";

            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(2048)
                            .setTemperature(0.4F)
                            .setTopK(32)
                            .setTopP(1)
                            .build();

            GenerativeModel model = new GenerativeModel(PredictionLoader.getInstance().getGeminiVisionModelName(), vertexAI);
            // For multi-turn responses, start a chat session.
            ChatSession chatSession = model.startChat();

            GenerateContentResponse response;
            // First message with multimodal input
            response = chatSession.sendMessage(ContentMaker.fromMultiModalData(
                    "are both images the same",
                    PartMaker.fromMimeTypeAndData(
                            // Update Mime type according to your image.
                            "image/jpeg",
                            imageUri), PartMaker.fromMimeTypeAndData(
                            // Update Mime type according to your image.
                            "image/jpeg",
                            imageUri)
            ));
            System.out.println(ResponseHandler.getText(response));

            // Second message with text input
           // response = chatSession.sendMessage(secondTextPrompt);
           // System.out.println(ResponseHandler.getText(response));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws AIProcessingException {
        GeminiImageActionProcessor processor = new GeminiImageActionProcessor();
        String text = processor.compareImages("C:\\work\\simpleLAM\\src\\main\\resources\\images\\wolf1.PNG","C:\\work\\simpleLAM\\src\\main\\resources\\images\\wolf2.PNG");
        System.out.println(text);
       // GeminiV2ActionProcessor v2 = new GeminiV2ActionProcessor();
       // System.out.println(v2.processSingleAction(text));
        /*
        String jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"),"Full Inspection");

        log.info(jsonStr);
        jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"),"Full Inspection","Tire Rotation","Oil Change");
        log.info(jsonStr);
        jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"), AutoRepairScreen.class);
        log.info(jsonStr);
        jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("fitness.PNG"), MyGymSchedule.class);
        log.info(jsonStr);
        Object pojo = processor.imageToPojo(GeminiImageExample.class.getClassLoader().getResource("fitness.PNG"), MyGymSchedule.class);
        log.info(pojo.toString());
        pojo = processor.imageToPojo(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"), AutoRepairScreen.class);
        log.info(pojo.toString());
        */

    }

}
