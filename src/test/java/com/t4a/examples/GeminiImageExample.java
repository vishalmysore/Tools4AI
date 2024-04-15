package com.t4a.examples;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiImageActionProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeminiImageExample {

    public static void main(String[] args) throws AIProcessingException {
        GeminiImageActionProcessor processor = new GeminiImageActionProcessor();
        String imageDisription = processor.imageToText("C:\\temp\\article/veg1.PNG");
        GeminiV2ActionProcessor actionProcessor = new GeminiV2ActionProcessor();
        Object obj = actionProcessor.processSingleAction(imageDisription);
        String str  = actionProcessor.summarize(imageDisription+obj.toString());
        System.out.println(str);
    }
    public static void main1(String[] args) throws IOException {

        String projectId = "cookgptserver";
        String location = "us-central1";
        String modelName = "gemini-1.0-pro-vision";

        multimodalMultiImage(projectId, location, modelName);
    }

    // Generates content from multiple input images.
    public static void multimodalMultiImage(String projectId, String location, String modelName)
            throws IOException {
        // Initialize client that will be used to send requests. This client only needs
        // to be created once, and can be reused for multiple requests.
        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);

            Content content = ContentMaker.fromMultiModalData(
                    PartMaker.fromMimeTypeAndData("image/png", readImageFile(
                            "https://storage.googleapis.com/cloud-samples-data/vertex-ai/llm/prompts/landmark1.png")),
                    "city: Rome, Landmark: the Colosseum",
                    PartMaker.fromMimeTypeAndData("image/png", readImageFile(
                            "https://storage.googleapis.com/cloud-samples-data/vertex-ai/llm/prompts/landmark2.png")),
                    "city: Beijing, Landmark: Forbidden City",
                    PartMaker.fromMimeTypeAndData("image/png", readImageFile(
                            "https://storage.googleapis.com/cloud-samples-data/vertex-ai/llm/prompts/landmark3.png"))
            );

            GenerateContentResponse response = model.generateContent(content);
            response = model.generateContent(
                    ContentMaker.fromMultiModalData(
                            "What is this image?",
                            PartMaker.fromMimeTypeAndData("image/jpg", readImageFile(
                                    "https://storage.googleapis.com/cloud-samples-data/vertex-ai/llm/prompts/landmark3.png"))));



            String output = ResponseHandler.getText(response);
            System.out.println(output);
        }
    }



    // Reads the image data from the given URL.
    public static byte[] readImageFile(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } else {
            throw new RuntimeException("Error fetching file: " + responseCode);
        }
    }
}
