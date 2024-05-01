package com.t4a.test;

import com.google.cloud.vertexai.api.FunctionDeclaration;
import com.t4a.api.JavaClassExecutor;
import com.t4a.examples.actions.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JavaClassExecutorTest {

    private JavaClassExecutor javaClassExecutor;

    @BeforeEach
    public void setUp() {
        javaClassExecutor = new JavaClassExecutor();
    }

    @Test
    public void testBuildFunctionFromClass() throws ClassNotFoundException {
        String className = Customer.class.getName();
        String funName = "testFunction";
        String description = "Test function description";

        FunctionDeclaration functionDeclaration = javaClassExecutor.buildFunctionFromClass(className, funName, description);

        assertNotNull(functionDeclaration);
        assertEquals(funName, functionDeclaration.getName());
        assertEquals(description, functionDeclaration.getDescription());
    }
}
