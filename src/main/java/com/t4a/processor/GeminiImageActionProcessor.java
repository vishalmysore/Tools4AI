package com.t4a.processor;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.predict.PredictionLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Take actions based on images
 *
 */
@Slf4j
public class GeminiImageActionProcessor {
    public String imageToText(String imageNameAndPath) throws AIProcessingException{
         try {
            return imageToText(readImageFile(imageNameAndPath));
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }
    public String imageToText(byte[] imageBytes) throws AIProcessingException{
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(),PredictionLoader.getInstance().getLocation())) {
            GenerativeModel model = new GenerativeModel(PredictionLoader.getInstance().getGeminiVisionModelName(), vertexAI);
            GenerateContentResponse response = null;
            try {
                response = model.generateContent(
                        ContentMaker.fromMultiModalData(
                                "Describe this?",
                                PartMaker.fromMimeTypeAndData("image/jpg", imageBytes)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            String output = ResponseHandler.getText(response);
            log.info(output);
            return output;
        }
    }
    public  byte[] readImageFileDeprecated(String url) throws IOException {
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

    public byte[] readImageFile(String url) throws IOException {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Check if the URL is an HTTP URL
            if (url.startsWith("http://") || url.startsWith("https://")) {
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                } else {
                    throw new IOException("Error fetching file: " + responseCode);
                }
            }
            // If not an HTTP URL, assume it's a local file path
            else {
                // Replace backslashes with forward slashes
                url = url.replace("\\", "/");
                inputStream = new FileInputStream(url);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } finally {
            // Close resources
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return outputStream.toByteArray();
    }
}
