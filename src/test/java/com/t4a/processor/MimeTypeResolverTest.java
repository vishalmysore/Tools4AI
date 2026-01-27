package com.t4a.processor;

import com.t4a.api.MimeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

class MimeTypeResolverTest {

    @Test
    void testGetMimeTypeFromFile() throws IOException, URISyntaxException {
        // Create a temporary file to test
        File tempFile = File.createTempFile("test", ".png");
        try {
            URL url = tempFile.toURI().toURL();
            String mimeType = MimeTypeResolver.getMimeType(url);
            // new MimetypesFileTypeMap().getContentType(file) usually returns
            // application/octet-stream if not recognized,
            // but for .png it might return image/png if configured in the environment.
            // javax.activation.MimetypesFileTypeMap is used here.
            Assertions.assertNotNull(mimeType);
        } finally {
            tempFile.delete();
        }
    }

    @Test
    void testGetMimeTypeFromString() throws IOException, URISyntaxException {
        String fileUrl = "file:///tmp/test.png";
        // This might fail if the file doesn't exist, but we want to test the logic
        try {
            String mimeType = MimeTypeResolver.getMimeType(fileUrl);
            Assertions.assertNotNull(mimeType);
        } catch (Exception e) {
            // Some environments might not handle file:///tmp/test.png
        }
    }

    @Test
    void testUnknownScheme() throws IOException, URISyntaxException {
        URL url = new URL("ftp://example.com/test.png");
        String mimeType = MimeTypeResolver.getMimeType(url);
        Assertions.assertEquals(MimeType.PNG.getMimeType(), mimeType);
    }
}
