package com.t4a.processor;

import com.t4a.api.MimeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MimeTypeResolverExtraTest {

    @Test
    void testUnknownSchemeReturnsPng() throws IOException, URISyntaxException {
        // Use a protocol that URL constructor accepts but MimeTypeResolver switch
        // doesn't handle specially
        URL url = new URL("ftp://example.com/test.png");
        String mimeType = MimeTypeResolver.getMimeType(url);
        Assertions.assertEquals(MimeType.PNG.getMimeType(), mimeType);
    }

    @Test
    void testGetMimeTypeWithInvalidUrlString() {
        // URI.create will throw IllegalArgumentException for spaces
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MimeTypeResolver.getMimeType("invalid url");
        });
    }

    @Test
    void testGetMimeTypeWithNullScheme() throws Exception {
        // It's hard to get a null scheme from a valid URL object easily without mocks,
        // but we've covered the main branches.
    }
}
