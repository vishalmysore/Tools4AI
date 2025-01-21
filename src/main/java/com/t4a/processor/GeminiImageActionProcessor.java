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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
public class GeminiImageActionProcessor {
    @Getter
    @Setter
    private GenerativeModel model;

    public GeminiImageActionProcessor() {
        initModel();
    }

    protected void initModel() {
        try (VertexAI vertexAI = new VertexAI(
                PredictionLoader.getInstance().getProjectId(),
                PredictionLoader.getInstance().getLocation())) {
            model = new GenerativeModel(
                    PredictionLoader.getInstance().getGeminiVisionModelName(),
                    vertexAI
            );
        }
    }

    public String imageToText(String imageNameAndPath) throws AIProcessingException {
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return imageToText(
                    ImageHandler.readImageFile(imageNameAndPath),
                    mimeType,
                    "Describe this?"
            );
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }

    public String compareImages(String imageNameAndPath, String imageNameAndPath2) throws AIProcessingException {
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return compareImages(
                    ImageHandler.readImageFile(imageNameAndPath),
                    ImageHandler.readImageFile(imageNameAndPath2),
                    mimeType,
                    "List down all the differences between these two images?"
            );
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToText(URL imageNameAndPath) throws AIProcessingException {
        try {
            String mimeType = MimeTypeResolver.getMimeType(imageNameAndPath);
            return imageToText(
                    ImageHandler.readImageFile(imageNameAndPath.toURI().toString()),
                    mimeType,
                    "Describe this completely with values and each and every detail?"
            );
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToJson(URL imageNameAndPath, String... names) throws AIProcessingException {
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.createJson(names);
            String mimeType = MimeTypeResolver.getMimeType(imageNameAndPath);

            jsonStr = imageToText(
                    ImageHandler.readImageFile(imageNameAndPath.toURI().toString()),
                    mimeType,
                    "Look at this image and populate corresponding values for those fields in fieldValue in this json " + jsonStr
            );
            return utils.extractJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    /**/
    public String compareImages(URL imageNameAndPathURL, URL imageNameAndPath2URL) throws AIProcessingException {
        try {
            File file = new File(imageNameAndPathURL.toURI());
            File file2 = new File(imageNameAndPath2URL.toURI());
            String mimeType = MimeTypeResolver.getMimeType(imageNameAndPathURL);
            return compareImages(
                    ImageHandler.readImageFile(file.getPath()),
                    ImageHandler.readImageFile(file2.getPath()),
                    mimeType,
                    "List down all the differences between these two images?"
            );
        } catch (IOException | URISyntaxException e) {
            throw new AIProcessingException(e);
        }
    }

    public String compareImages(URL imageNameAndPathURL, URL imageNameAndPath2URL, String prompt) throws AIProcessingException {
        try {
            File file = new File(imageNameAndPathURL.toURI());
            File file2 = new File(imageNameAndPath2URL.toURI());
            String mimeType = MimeTypeResolver.getMimeType(imageNameAndPathURL);
            return compareImages(
                    ImageHandler.readImageFile(file.getPath()),
                    ImageHandler.readImageFile(file2.getPath()),
                    mimeType,
                    prompt
            );
        } catch (IOException | URISyntaxException e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToText(URL imageURL, String prompt) throws AIProcessingException {
        try {
            String imageNameAndPath = imageURL.toURI().toString();
            String mimeType = MimeTypeResolver.getMimeType(imageURL);
            return imageToText(
                    ImageHandler.readImageFile(imageNameAndPath),
                    mimeType,
                    prompt
            );
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToText(String imageNameAndPath, String prompt) throws AIProcessingException {
        try {
            String mimeType = new MimetypesFileTypeMap().getContentType(imageNameAndPath);
            return imageToText(
                    ImageHandler.readImageFile(imageNameAndPath),
                    mimeType,
                    prompt
            );
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToText(byte[] imageBytes, String prompt) throws AIProcessingException {
        return imageToText(imageBytes, MimeType.PNG.getMimeType(), prompt);
    }

    public String imageToText(byte[] imageBytes) throws AIProcessingException {
        return imageToText(imageBytes, MimeType.PNG.getMimeType(), "Describe this?");
    }

    public String getValueFor(byte[] imageBytes, String element) throws AIProcessingException {
        return imageToText(
                imageBytes,
                MimeType.PNG.getMimeType(),
                "Get value for " + element
        );
    }

    public String imageToJson(URL imageNameAndPath, Class<?> clazz) throws AIProcessingException {
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.convertClassToJSONString(clazz);
            String mimeType = MimeTypeResolver.getMimeType(imageNameAndPath);

            jsonStr = imageToText(
                    ImageHandler.readImageFile(imageNameAndPath.toURI().toString()),
                    mimeType,
                    "Look at this image and populate fieldValue in this json " + jsonStr
            );
            return utils.extractJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    public Object imageToPojo(URL imageNameAndPath, Class<?> clazz) throws AIProcessingException {
        try {
            JsonUtils utils = new JsonUtils();
            String jsonStr = utils.convertClassToJSONString(clazz);
            String mimeType = MimeTypeResolver.getMimeType(imageNameAndPath);

            jsonStr = imageToText(
                    ImageHandler.readImageFile(imageNameAndPath.toURI().toString()),
                    mimeType,
                    "Look at this image and populate fieldValue in this json " + jsonStr
            );
            return utils.populateClassFromJson(jsonStr);
        } catch (Exception e) {
            throw new AIProcessingException(e);
        }
    }

    public String imageToText(byte[] imageBytes, String mimeType, String prompt) throws AIProcessingException {
        GenerateContentResponse response;
        try {
            response = model.generateContent(
                    ContentMaker.fromMultiModalData(
                            prompt,
                            PartMaker.fromMimeTypeAndData(mimeType, imageBytes)
                    )
            );
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }

        String output = ResponseHandler.getText(response);
        log.debug(output);
        return output;
    }

    public String compareImages(byte[] imageBytes1, byte[] imageBytes2, String mimeType, String prompt) throws AIProcessingException {
        GenerateContentResponse response;
        try {
            response = model.generateContent(
                    ContentMaker.fromMultiModalData(
                            prompt,
                            PartMaker.fromMimeTypeAndData(mimeType, imageBytes1),
                            PartMaker.fromMimeTypeAndData(mimeType, imageBytes2)
                    )
            );
        } catch (IOException e) {
            throw new AIProcessingException(e);
        }

        String output = ResponseHandler.getText(response);
        log.debug(output);
        return output;
    }
}