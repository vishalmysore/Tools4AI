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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.*;

/**
 * Take actions based on images, compare images, get text from images, get values from images , convert
 * images into Pojo and Json, get values for specific elements in images,
 *
 */
@Slf4j
public class GeminiImageActionProcessor {

    @Getter
    @Setter
    private  GenerativeModel model;

    public GeminiImageActionProcessor() {
        initModel();
    }

    protected void initModel(){
        try (VertexAI vertexAI = new VertexAI(PredictionLoader.getInstance().getProjectId(),PredictionLoader.getInstance().getLocation())) {
             model = new GenerativeModel(PredictionLoader.getInstance().getGeminiVisionModelName(), vertexAI);
        }
    }

    /**
     * Get text from image
     * @param imageNameAndPath
     * @return
     * @throws AIProcessingException
     */
    public String imageToText(String imageNameAndPath) throws AIProcessingException{
         try {
             String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return imageToText(readImageFile(imageNameAndPath),mimeType, "Describe this?");
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }

    /**
     * Compare two images and find differences
     * @param imageNameAndPath
     * @param imageNameAndPath2
     * @return
     * @throws AIProcessingException
     */
    public String compareImages(String imageNameAndPath,String imageNameAndPath2) throws AIProcessingException{
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return compareImages(readImageFile(imageNameAndPath),readImageFile(imageNameAndPath2),mimeType, "List down all the differences between these two images?");
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }

    /**
     * Compare two images and find differences
     * @param imageNameAndPath
     * @param imageNameAndPath2
     * @param prompt
     * @return
     * @throws AIProcessingException
     */
    public String compareImages(String imageNameAndPath,String imageNameAndPath2, String prompt) throws AIProcessingException{
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return compareImages(readImageFile(imageNameAndPath),readImageFile(imageNameAndPath2),mimeType, prompt);
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }

    public String compareImages(URL imageNameAndPathURL,URL imageNameAndPath2URL) throws AIProcessingException{
        try {
            File file = new File(imageNameAndPathURL.toURI());
            File file2 = new File(imageNameAndPath2URL.toURI());
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getPath());
            return compareImages(readImageFile(file.getPath()),readImageFile(file2.getPath()),mimeType, "List down all the differences between these two images?");
        } catch (IOException | URISyntaxException e) {
            throw new AIProcessingException(e);
        }
    }

    public String compareImages(URL imageNameAndPathURL,URL imageNameAndPath2URL, String prompt) throws AIProcessingException{
        try {
            File file = new File(imageNameAndPathURL.toURI());
            File file2 = new File(imageNameAndPath2URL.toURI());
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getPath());
            return compareImages(readImageFile(file.getPath()),readImageFile(file2.getPath()),mimeType, prompt);
        } catch (IOException | URISyntaxException e) {
            throw new AIProcessingException(e);
        }
    }
    /**
     * Get text from image
     * @param imageNameAndPath
     * @return
     * @throws AIProcessingException
     */
    public String imageToText(URL imageNameAndPath) throws AIProcessingException{
        try {

            String mimeType = getMimeType(imageNameAndPath);
            return imageToText(readImageFile(imageNameAndPath.toURI().toString()),mimeType, "Describe this completely with values and each and every detail?");
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    /**
     * This method will convert image to json, the json will have fields populated with values from the image
     * the name of the fields are passed in names attribute
     * @param imageNameAndPath
     * @param names
     * @return
     * @throws AIProcessingException
     */
    public String imageToJson(URL imageNameAndPath, String... names) throws AIProcessingException{
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.createJson(names);

            String mimeType = getMimeType(imageNameAndPath);
            jsonStr= imageToText(readImageFile(imageNameAndPath.toURI().toString()),mimeType, "look at this image and populate corresponding values for those fields in fieldValue in this json "+jsonStr);
            return utils.extractJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    /**
     * Converts the image into the json object matching with the class. The class passed to this method
     * will be converted to Json and the values populated using AI
     * @param imageNameAndPath
     * @param clazz
     * @return
     * @throws AIProcessingException
     */

    public String imageToJson(URL imageNameAndPath, Class<?> clazz) throws AIProcessingException{
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.convertClassToJSONString(clazz);

            String mimeType = getMimeType(imageNameAndPath);
            jsonStr= imageToText(readImageFile(imageNameAndPath.toURI().toString()),mimeType, "look at this image and populate fieldValue in this json "+jsonStr);
            return utils.extractJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    /**
     * Convert the entire image into the Pojo object
     * @param imageNameAndPath
     * @param clazz
     * @return
     * @throws AIProcessingException
     */
    public Object imageToPojo(URL imageNameAndPath, Class<?> clazz) throws AIProcessingException{
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.convertClassToJSONString(clazz);
            String mimeType = getMimeType(imageNameAndPath);
            jsonStr= imageToText(readImageFile(imageNameAndPath.toURI().toString()),mimeType, "look at this image and populate fieldValue in this json "+jsonStr);
            return utils.populateClassFromJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }
    public String imageToText(URL imageURL, String prompt) throws AIProcessingException{
        try {

            String imageNameAndPath = imageURL.toURI().toString();
            String mimeType = getMimeType(imageURL.toURI().toString());
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


            GenerateContentResponse response ;
            try {
                response = model.generateContent(
                        ContentMaker.fromMultiModalData(
                                prompt,
                                PartMaker.fromMimeTypeAndData(mimeType, imageBytes)));
            } catch (IOException e) {
                throw new AIProcessingException(e);
            }


            String output = ResponseHandler.getText(response);
            log.debug(output);
            return output;

    }

    public String compareImages(byte[] imageBytes1,byte[] imageBytes2, String mimeType, String prompt) throws AIProcessingException{

            GenerateContentResponse response ;
            try {
                response = model.generateContent(
                        ContentMaker.fromMultiModalData(
                                prompt,
                                PartMaker.fromMimeTypeAndData(mimeType, imageBytes1),PartMaker.fromMimeTypeAndData(mimeType, imageBytes2)
                                ));
            } catch (IOException e) {
                throw new AIProcessingException(e);
            }


            String output = ResponseHandler.getText(response);
            log.debug(output);
            return output;

    }

    public InputStream getHttpInputStream(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return connection.getInputStream();
        } else {
            throw new IOException("Error fetching file: " + responseCode);
        }
    }

    public String getMimeType(URL url) throws URISyntaxException, IOException {
        String scheme = url.toURI().getScheme();
        String mimeType;
        if (scheme != null) {
            if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                 mimeType = connection.getHeaderField("Content-Type");
            } else if (scheme.equalsIgnoreCase("file")) {
                log.debug("URL is a local file");
                mimeType = MimeType.PNG.getMimeType();
                File file = new File(url.toURI());
                mimeType = new MimetypesFileTypeMap().getContentType(file.getPath());
            } else {
                log.debug("Unknown URL scheme: " + scheme);
                mimeType = MimeType.PNG.getMimeType();
            }
        } else {
            log.debug("URL scheme is null");
            mimeType = MimeType.PNG.getMimeType();
        }
        return mimeType;
    }

    public String getMimeType(String url) throws IOException, URISyntaxException {
        return getMimeType(URI.create(url).toURL());
    }

    public byte[] readImageFile(String url) throws IOException {
        InputStream inputStream = null;


        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Check if the URL is an HTTP URL
            if (url.startsWith("http://") || url.startsWith("https://")) {
                inputStream = getHttpInputStream(url);
            }
            // If not an HTTP URL, assume it's a local file path
            else {
                // Replace backslashes with forward slashes
                File file = new File(new URI(url));
                inputStream = new FileInputStream(file);
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }



        return outputStream.toByteArray();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            // Close resources
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
