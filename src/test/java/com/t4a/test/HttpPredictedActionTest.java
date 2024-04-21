package com.t4a.test;

import com.t4a.action.http.HttpPredictedAction;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class HttpPredictedActionTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse mockHttpResponse;

    @Mock
    private HttpEntity mockHttpEntity;

    private HttpPredictedAction httpPredictedAction;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        httpPredictedAction = new HttpPredictedAction();
        httpPredictedAction.setUrl("http://test.com");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer your_token");
        httpPredictedAction.setHeaders(headers);
        httpPredictedAction.setClient(mockHttpClient);
    }

    @Test
    public void testExecuteHttpGet() throws IOException {
        String expectedResponse = "Expected response";
        InputStream stream = new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpEntity.getContent()).thenReturn(stream);

        Map<String, Object> params = new HashMap<>();
        params.put("test", "test");

        String actualResponse = httpPredictedAction.executeHttpGet(params);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testExecuteHttpPost() throws IOException {
        String expectedResponse = "Expected response";
        InputStream stream = new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8));
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpEntity.getContent()).thenReturn(stream);

        Map<String, Object> postData = new HashMap<>();
        postData.put("test", "test");

        String actualResponse = httpPredictedAction.executeHttpPost(postData);

        assertEquals(expectedResponse, actualResponse);
    }
}