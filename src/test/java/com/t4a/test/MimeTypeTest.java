package com.t4a.test;

import com.t4a.api.MimeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MimeTypeTest {
    @Test
    void testGetMimeType() {
        Assertions.assertEquals("image/jpeg", MimeType.JPEG.getMimeType());
        Assertions.assertEquals("image/png", MimeType.PNG.getMimeType());
        Assertions.assertEquals("image/gif", MimeType.GIF.getMimeType());
        Assertions.assertEquals("text/html", MimeType.HTML.getMimeType());
        Assertions.assertEquals("text/plain", MimeType.TEXT.getMimeType());
        Assertions.assertEquals("application/pdf", MimeType.PDF.getMimeType());
        Assertions.assertEquals("application/msword", MimeType.MS_WORD.getMimeType());
        Assertions.assertEquals("application/vnd.oasis.opendocument.text", MimeType.OPEN_DOCUMENT_TEXT.getMimeType());
    }
}
