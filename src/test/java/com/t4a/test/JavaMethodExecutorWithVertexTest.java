package com.t4a.test;

import com.google.cloud.vertexai.api.Candidate;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionCall;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.t4a.api.JavaMethodExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JavaMethodExecutorWithVertexTest {

    @Test
    public void testGetPropertyValuesMapFromResponse() {
        // Build real response objects instead of mocking
        Value stringValue = Value.newBuilder().setStringValue("testValue").build();
        Struct args = Struct.newBuilder()
                .putFields("testParam", stringValue)
                .build();

        FunctionCall functionCall = FunctionCall.newBuilder()
                .setArgs(args)
                .build();

        Part part = Part.newBuilder()
                .setFunctionCall(functionCall)
                .build();

        Content content = Content.newBuilder()
                .addParts(part)
                .build();

        Candidate candidate = Candidate.newBuilder()
                .setContent(content)
                .build();

        GenerateContentResponse response = GenerateContentResponse.newBuilder()
                .addCandidates(candidate)
                .build();

        JavaMethodExecutor executor = new JavaMethodExecutor();
        Map<String, Object> result = executor.getPropertyValuesMapMap(response);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testValue", result.get("testParam"));
    }
}
