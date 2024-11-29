package com.t4a.processor;

import java.io.*;
import java.net.*;
import javax.activation.MimetypesFileTypeMap;
import com.t4a.api.MimeType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageHandler {
    public static byte[] readImageFile(String url) throws IOException {
        try (InputStream inputStream = createInputStream(url);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream createInputStream(String url) throws IOException, URISyntaxException {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return createHttpInputStream(url);
        } else {
            File file = new File(new URI(url));
            return new FileInputStream(file);
        }
    }

    private static InputStream createHttpInputStream(String url) throws IOException {
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


}