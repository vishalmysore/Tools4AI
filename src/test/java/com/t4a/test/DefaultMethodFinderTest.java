package com.t4a.test;

import com.t4a.api.DefaultMethodFinder;
import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultMethodFinderTest {

    private DefaultMethodFinder finder;

    @BeforeEach
    void setUp() {
        finder = new DefaultMethodFinder();
    }

    static class SampleClass {
        public String hello() { return "hello"; }
        public int add(int a, int b) { return a + b; }
    }

    @Test
    void testFindMethod_success() throws AIProcessingException {
        Method m = finder.findMethod(SampleClass.class, "hello");
        assertNotNull(m);
        assertEquals("hello", m.getName());
    }

    @Test
    void testFindMethod_withParams() throws AIProcessingException {
        Method m = finder.findMethod(SampleClass.class, "add");
        assertNotNull(m);
        assertEquals("add", m.getName());
        assertEquals(2, m.getParameterCount());
    }

    @Test
    void testFindMethod_notFound_throwsException() {
        AIProcessingException ex = assertThrows(AIProcessingException.class,
                () -> finder.findMethod(SampleClass.class, "nonExistent"));
        assertTrue(ex.getMessage().contains("nonExistent"));
        assertTrue(ex.getMessage().contains(SampleClass.class.getName()));
    }

    @Test
    void testFindMethod_inheritedMethod() throws AIProcessingException {
        // getMethods() includes inherited public methods (e.g. toString from Object)
        Method m = finder.findMethod(SampleClass.class, "toString");
        assertNotNull(m);
        assertEquals("toString", m.getName());
    }
}
