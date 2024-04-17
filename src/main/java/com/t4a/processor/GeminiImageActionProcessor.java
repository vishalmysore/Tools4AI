package com.t4a.processor;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.t4a.JsonUtils;
import com.t4a.api.MimeType;
import com.t4a.predict.PredictionLoader;
import lombok.extern.slf4j.Slf4j;


import javax.activation.MimetypesFileTypeMap;
import java.io.*;
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
             String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return imageToText(readImageFile(imageNameAndPath),mimeType, "Describe this?");
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToText(URL imageNameAndPath) throws AIProcessingException{
        try {
            File file = new File(imageNameAndPath.toURI());
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getPath());
            return imageToText(readImageFile(file.getPath()),mimeType, "Describe this completely with values and each and every detail?");
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToJson(URL imageNameAndPath, String... names) throws AIProcessingException{
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.createJson(names);
            File file = new File(imageNameAndPath.toURI());
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getPath());
            jsonStr= imageToText(readImageFile(file.getPath()),mimeType, "look at this image and populate corresponding values for those fields in fieldValue in this json "+jsonStr);
            return utils.extractJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToJson(URL imageNameAndPath, Class clazz) throws AIProcessingException{
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.convertClassToJSONString(clazz);
            File file = new File(imageNameAndPath.toURI());
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getPath());
            jsonStr= imageToText(readImageFile(file.getPath()),mimeType, "look at this image and populate fieldValue in this json "+jsonStr);
            return utils.extractJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }
    public Object imageToPojo(URL imageNameAndPath, Class clazz) throws AIProcessingException{
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.convertClassToJSONString(clazz);
            File file = new File(imageNameAndPath.toURI());
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getPath());
            jsonStr= imageToText(readImageFile(file.getPath()),mimeType, "look at this image and populate fieldValue in this json "+jsonStr);
            return utils.populateClassFromJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }
    public String imageToText(URL imageURL, String prompt) throws AIProcessingException{
        try {
            File file = new File(imageURL.toURI());
            String imageNameAndPath = file.getPath();
            String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return imageToText(readImageFile(imageNameAndPath),mimeType, prompt);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }
    public String imageToText(String imageNameAndPath, String prompt) throws AIProcessingException{
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return imageToText(readImageFile(imageNameAndPath),mimeType, prompt);
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }
    public String imageToText(byte[] imageBytes,  String prompt) throws AIProcessingException{
        return imageToText(imageBytes, MimeType.PNG.getMimeType(),prompt);

    }

    public String imageToText(byte[] imageBytes) throws AIProcessingException{
        return imageToText(imageBytes, MimeType.PNG.getMimeType(),"Describe this ?");

    }

    public String getValueFor(byte[] imageBytes, String element) throws AIProcessingException{
        return imageToText(imageBytes, MimeType.PNG.getMimeType(),"get value for "+element);

    }

    public String imageToText(byte[] imageBytes, String mimeType, String prompt) throws AIProcessingException{
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(),PredictionLoader.getInstance().getLocation())) {
            GenerativeModel model = new GenerativeModel(PredictionLoader.getInstance().getGeminiVisionModelName(), vertexAI);
            GenerateContentResponse response = null;
            try {
                response = model.generateContent(
                        ContentMaker.fromMultiModalData(
                                prompt,
                                PartMaker.fromMimeTypeAndData(mimeType, imageBytes)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            String output = ResponseHandler.getText(response);
            log.debug(output);
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
