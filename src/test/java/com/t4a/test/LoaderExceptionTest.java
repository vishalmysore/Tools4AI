package com.t4a.test;

import com.t4a.predict.LoaderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoaderExceptionTest {


    @Test
    void testLoaderExceptionMessage() {
        String message = "Test message";
        LoaderException exception = new LoaderException(message);
        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    void testLoaderExceptionCause() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");
        LoaderException exception = new LoaderException(message, cause);
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

    @Test
    void testLoaderExceptionCauseOnly() {
        Throwable cause = new RuntimeException("Test cause");
        LoaderException exception = new LoaderException(cause);
        Assertions.assertEquals(cause, exception.getCause());
    }

    @Test
    void testLoaderExceptionAllParameters() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");
        boolean enableSuppression = true;
        boolean writableStackTrace = false;
        LoaderException exception = new LoaderException(message, cause, enableSuppression, writableStackTrace);
        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());

        Assertions.assertEquals(writableStackTrace, exception.getStackTrace().length > 0);
    }
}