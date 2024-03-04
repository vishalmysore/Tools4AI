package com.t4a.action.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@Log
@NoArgsConstructor
public class GenericHttpAction implements AIAction {
    private String actionName;
    private String url;
    private String type;
    List<InputParameter> inputObjects;
    private JsonObject outputObject;
    private JsonObject authInterface;
    private String description;
    private  final HttpClient client = HttpClientBuilder.create().build();
    private final Gson gson = new Gson();
    public GenericHttpAction(String actionName, String url, String type,   List<InputParameter> inputObjects, JsonObject outputObject, JsonObject authInterface, String description) {
        this.actionName = actionName;
        this.url = url;
        this.type = type;
        this.inputObjects = inputObjects;
        this.outputObject = outputObject;
        this.authInterface = authInterface;
        this.description = description;
    }
    public String getDefaultExecutorMethodName() {
        return "executeHttpRequest";
    }
    private  String executeHttpGet(Map<String, Object> parameters) {
        // Construct the query string from parameters
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // Append the query string to the URL
        if (queryString.length() > 0) {
            url += "?" + queryString.toString();
        }

        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // Convert response entity to JSON string
                String jsonResponse = EntityUtils.toString(entity);
                System.out.println("Response: " + jsonResponse);
                // Further processing of jsonResponse...
                return jsonResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception...
        }
        return null;
    }

    private String executeHttpPost(Map<String, Object> postData) throws IOException {
        // Convert postData to JSON
        JsonObject json = new JsonObject();
        for (Map.Entry<String, Object> entry : postData.entrySet()) {
            json.addProperty(entry.getKey(), (String) entry.getValue());
        }
        String jsonPayload = gson.toJson(json);

        // Execute HTTP POST request using the provided URL and JSON payload
        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(jsonPayload));

        HttpResponse response = client.execute(request);
        // Handle response...
        return null;
    }
    public  String executeHttpRequest(Map<String, Object> params) throws IOException {
        for (InputParameter parameter : inputObjects) {
            if(parameter.hasDefaultValue())
                params.put(parameter.getName(), parameter.getDefaultValue());
        }
        if ("GET".equals(getType())) {
            return executeHttpGet(params);
        } else if ("POST".equals(getType())) {
            return executeHttpPost(params);
        } else {
            return null;
        }


    }
    @Override
    public ActionType getActionType() {
        return ActionType.HTTP;
    }



    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "GenericHttpAction{" +
                "actionName='" + actionName + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", inputObjects=" + inputObjects +
                ", outputObject=" + outputObject +
                ", authInterface=" + authInterface +
                ", description='" + description + '\'' +
                ", client=" + client +
                ", gson=" + gson +
                '}';
    }
}


