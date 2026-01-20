package com.t4a.test;

import com.t4a.action.http.HttpMethod;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.http.InputParameter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpPredictedActionCoverageTest {

    @Test
    public void testExecuteHttpGet() throws Exception {
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse mockResponse = mock(HttpResponse.class);
        HttpEntity mockEntity = mock(HttpEntity.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        String responseBody = "{\"result\":\"success\"}";
        when(mockEntity.getContent())
                .thenReturn(new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8)));
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        HttpPredictedAction action = new HttpPredictedAction();
        action.setUrl("http://example.com/test");
        action.setType(HttpMethod.GET);
        action.setClient(mockClient);

        List<InputParameter> inputs = new ArrayList<>();
        action.setInputObjects(inputs);

        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");

        String result = action.executeHttpRequest(params);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(responseBody, result);
    }

    @Test
    public void testExecuteHttpPost() throws Exception {
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse mockResponse = mock(HttpResponse.class);
        HttpEntity mockEntity = mock(HttpEntity.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        String responseBody = "{\"result\":\"created\"}";
        when(mockEntity.getContent())
                .thenReturn(new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8)));
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        HttpPredictedAction action = new HttpPredictedAction();
        action.setUrl("http://example.com/create");
        action.setType(HttpMethod.POST);
        action.setClient(mockClient);

        List<InputParameter> inputs = new ArrayList<>();
        action.setInputObjects(inputs);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "test");

        String result = action.executeHttpRequest(params);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(responseBody, result);
    }

    @Test
    public void testReplacePlaceholders() throws Exception {
        HttpPredictedAction action = new HttpPredictedAction();
        String url = "http://example.com/users/{userId}/posts/{postId}";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", "123");
        params.put("postId", "456");

        String result = action.replacePlaceholders(url, params);
        Assertions.assertTrue(result.contains("123"));
        Assertions.assertTrue(result.contains("456"));
    }
}
