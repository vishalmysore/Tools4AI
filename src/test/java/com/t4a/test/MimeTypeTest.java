package com.t4a.test;

import com.t4a.api.MimeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MimeTypeTest {
    @Test
    void testGetMimeType() {
        assertEquals("image/jpeg", MimeType.JPEG.getMimeType());
        assertEquals("image/png", MimeType.PNG.getMimeType());
        assertEquals("image/gif", MimeType.GIF.getMimeType());
        assertEquals("text/html", MimeType.HTML.getMimeType());
        assertEquals("text/plain", MimeType.TEXT.getMimeType());
        assertEquals("application/pdf", MimeType.PDF.getMimeType());
        assertEquals("application/msword", MimeType.MS_WORD.getMimeType());
        assertEquals("application/vnd.oasis.opendocument.text", MimeType.OPEN_DOCUMENT_TEXT.getMimeType());
    }
}
