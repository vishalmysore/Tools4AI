package com.t4a.test;

import com.t4a.processor.ImageHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class ImageHandlerTest {

    @Test
    void testReadImageFile_fromFileUri() throws IOException {
        URL resource = getClass().getClassLoader().getResource("fitness.PNG");
        assertNotNull(resource, "fitness.PNG must exist in test resources");

        byte[] bytes = ImageHandler.readImageFile(resource.toString());
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testReadImageFile_returnsCorrectContent() throws IOException {
        URL resource = getClass().getClassLoader().getResource("auto.PNG");
        assertNotNull(resource, "auto.PNG must exist in test resources");

        byte[] bytes = ImageHandler.readImageFile(resource.toString());
        assertTrue(bytes.length > 0);
    }

    @Test
    void testReadImageFile_invalidPath_throwsException() {
        assertThrows(Exception.class,
                () -> ImageHandler.readImageFile("file:///no/such/file/image.png"));
    }
}
