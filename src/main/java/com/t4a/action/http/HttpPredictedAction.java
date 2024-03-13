package com.t4a.action.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.t4a.api.ActionType;
import com.t4a.api.PredictedAIAction;
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
import java.util.Objects;

/**
 * <pre>
 * This is a Generic Http Action Class , it will be built from the config file. executeHttpRequest is the main functionality
 * which will be automatically called by the processor. This method will decide whether its post or get and based on the the method
 * type the specific method will be executed
 *
 * <code>
 *  "endpoints": [
 *     {
 *       "actionName": "getUserDetails",
 *       "description" : " this will fetch User details from the user inventory corporate application",
 *       "url": "https://api.example.com/users/",
 *       "type": "GET",
 *       "input_object": [
 *       {
 *         "name": "userId",
 *         "type": "path_parameter",
 *         "description": "User ID"
 *       }
 *       ],
 *
 *       "output_object": {
 *         "type": "json",
 *         "description": "User object"
 *       },
 *       "auth_interface": {
 *         "type": "Bearer Token",
 *         "description": "Authentication token required"
 *       }
 *     },
 *     </code>
 *  </pre>
 *@see com.t4a.predict.HttpRestPredictionLoader
 */

@Getter
@Setter
@Log
@NoArgsConstructor
public final class HttpPredictedAction implements PredictedAIAction {
    private String actionName;
    private String url;
    private HttpMethod type;
    List<InputParameter> inputObjects;
    private JsonObject outputObject;
    private JsonObject authInterface;
    private String description;
    private  final HttpClient client = HttpClientBuilder.create().build();
    private String requestBodyJson;
    private final Gson gson = new Gson();
    public HttpPredictedAction(String actionName, String url, HttpMethod type, List<InputParameter> inputObjects, JsonObject outputObject, JsonObject authInterface, String description) {
        this.actionName = actionName;
        this.url = url;
        this.type = type;
        this.inputObjects = inputObjects;
        this.outputObject = outputObject;
        this.authInterface = authInterface;
        this.description = description;
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
                log.info("Response: " + jsonResponse);
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

    /**
     * <pre>
     * THis method will be automatically called by the processor , the params are populated by AI directly
     * for example if the prompt was "hey hows the weather in Toronto on Sunday , 8th Sep" and your inputParams are
     * <code>
     *     "input_object":[
     *         {
     *           "name": "city",
     *           "type": "query_parameter",
     *           "description": "City Name"
     *       },
     *       {
     *                 "name": "date",
     *                 "type": "query_parameter",
     *                 "description": "date"
     *       }
     *
     * </code>
     * Then those params will get automatically mapped
     *
     *</pre>
     * @param params
     * @return
     * @throws IOException
     */
    public  String executeHttpRequest(Map<String, Object> params) throws IOException {
        for (InputParameter parameter : inputObjects) {
            if(parameter.hasDefaultValue())
                params.put(parameter.getName(), parameter.getDefaultValue());
        }
        if (HttpMethod.GET == getType()) {
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
        return "HttpPredictedAction{" +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpPredictedAction that = (HttpPredictedAction) o;
        return Objects.equals(actionName, that.actionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionName);
    }
}


