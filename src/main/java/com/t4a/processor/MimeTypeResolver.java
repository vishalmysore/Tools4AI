package com.t4a.processor;

import com.t4a.api.MimeType;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MimeTypeResolver {
    public static String getMimeType(URL url) throws URISyntaxException, IOException {
        String scheme = url.toURI().getScheme();
        if (scheme == null) {
            log.debug("URL scheme is null");
            return MimeType.PNG.getMimeType();
        }

        switch (scheme.toLowerCase()) {
            case "http":
            case "https":
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                return connection.getHeaderField("Content-Type");
            case "file":
                log.debug("URL is a local file");
                File file = new File(url.toURI());
                return new MimetypesFileTypeMap().getContentType(file.getPath());
            default:
                log.debug("Unknown URL scheme: " + scheme);
                return MimeType.PNG.getMimeType();
        }
    }

    public static String getMimeType(String url) throws IOException, URISyntaxException {
        return getMimeType(URI.create(url).toURL());
    }
}