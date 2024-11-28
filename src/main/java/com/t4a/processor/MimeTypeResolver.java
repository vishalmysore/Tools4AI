package com.t4a.processor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.activation.MimetypesFileTypeMap;

public class MimeTypeResolver {
    public static String getMimeType(URL url) throws URISyntaxException, IOException {
        return ImageHandler.determineMimeType(url);
    }

    public static String getMimeType(String url) throws IOException, URISyntaxException {
        return getMimeType(URI.create(url).toURL());
    }
}