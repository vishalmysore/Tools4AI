package com.t4a.predict;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.t4a.JsonUtils;
import com.t4a.action.http.HttpMethod;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.http.InputParameter;
import com.t4a.action.http.ParamLocation;
import com.t4a.api.AIAction;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log
public class SwaggerPredictionLoader {

    private  String yamlFile = "swagger_actions.json";
    private URL resourceUrl = null;
    public void load(Map<String, AIAction> predictions, StringBuffer actionNameList) throws LoaderException {

        try {
            parseConfig(predictions,actionNameList);
        } catch ( IOException e) {
            throw new LoaderException(e);
        }


    }
    public  void parseConfig(Map<String,AIAction> predictions, StringBuffer actionNameList) throws IOException {
        if (resourceUrl == null)
            resourceUrl = SwaggerPredictionLoader.class.getClassLoader().getResource(yamlFile);

        if (resourceUrl == null) {
            log.warning("File not found: " + yamlFile);
            return;
        }
        Gson gson = new Gson();
        //List<HttpPredictedAction> actions = new ArrayList<>();

        try (InputStream inputStream = resourceUrl.openStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            JsonObject rootObject = gson.fromJson(reader, JsonObject.class);
            JsonArray endpoints = rootObject.getAsJsonArray("endpoints");

            for (JsonElement obj : endpoints) {
                JsonObject endpoint = obj.getAsJsonObject();
                String swaggerurl = endpoint.get("swaggerurl").getAsString();
                String id = endpoint.get("id").getAsString();
                String baseurl = endpoint.get("baseurl").getAsString();
                loadURL(swaggerurl, predictions,  actionNameList,baseurl);

            }
        }
    }


    public  void loadURL(String jsonURL,Map<String,AIAction> predictions, StringBuffer actionNameList, String baseURL ) {


        try {

            OpenAPI openAPI = new OpenAPIV3Parser().readLocation(jsonURL, null, null).getOpenAPI();
            Map<String, PathItem> paths = openAPI.getPaths();

            // Iterate over each endpoint
            for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
                String path = entry.getKey();
                PathItem pathItem = entry.getValue();

                // Iterate over HTTP methods for the endpoint
                for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry : pathItem.readOperationsMap().entrySet()) {
                    PathItem.HttpMethod method = operationEntry.getKey();
                    Operation operation = operationEntry.getValue();

                    // Extract relevant information and create HttpPredictedAction object
                    HttpPredictedAction httpAction = new HttpPredictedAction();
                    String actionName = operation.getOperationId();
                    if((actionName == null) || (actionName.trim().equals(""))) {
                        String[] segments = path.split("/");
                        String resourceNameQ = segments[segments.length - 1];
                        String resourceName = resourceNameQ.split("[{?]")[0];
                        if((resourceName == null)||(resourceName.trim().equals(""))) {
                            String[] tryAgainSeg = path.split("/");

                              // Find the last segment that does not contain curly braces (path parameter)
                            int index = segments.length - 1;
                            while (index >= 0 && tryAgainSeg[index].contains("{")) {
                                index--;
                            }

                             resourceName = tryAgainSeg[index];
                        }
                        actionName = method.name().toLowerCase()+resourceName;
                    }
                    httpAction.setActionName(actionName); // Use method name as action name

                    httpAction.setUrl(baseURL + path); // Construct URL
                    httpAction.setType(HttpMethod.valueOf(method.name())); // Set HTTP method type

                    // Extract parameters and populate input objects
                    List<InputParameter> inputParameters = new ArrayList<>();
                    InputParameter inputParameter = null;
                    if (operation.getParameters() != null) {
                        for (io.swagger.v3.oas.models.parameters.Parameter parameter : operation.getParameters()) {
                            String paraName = parameter.getName();
                            String paraType = parameter.getSchema().getType();
                            inputParameter = new InputParameter(paraName,paraType,"");
                            if ("query".equals(parameter.getIn())) {
                                inputParameter.setLocation(ParamLocation.QUERY);
                            } else {
                                if(path.contains("{" + paraName + "}")) {
                                    inputParameter.setLocation(ParamLocation.PATH);
                                }
                            }


                        }
                    } else if((operation.getRequestBody()!=null)&&(operation.getRequestBody().getContent() != null)){ if (operation != null) {
                        inputParameter = new InputParameter();
                        RequestBody requestBody = operation.getRequestBody();
                        if (requestBody != null && requestBody.getContent() != null) {
                            for (MediaType mediaType : requestBody.getContent().values()) {
                                if (mediaType.getSchema() != null) {
                                    // Check if the schema is a reference
                                    if (mediaType.getSchema().get$ref() != null) {
                                        // Resolve $ref if present
                                        String jsonString = JsonUtils.convertClassObjectToJsonString(resolveSchema(openAPI, mediaType.getSchema().get$ref()));
                                      //  log.info(jsonString + " for "+ actionName);
                                        ObjectMapper objectMapper = new ObjectMapper();
                                        Map<String, Object> map = objectMapper.readValue(jsonString, Map.class);
                                        httpAction.setRequestBodyJson(jsonString);
                                        httpAction.setJsonMap(map);
                                        httpAction.setHasJson(true);

                                    } else {
                                        // Return the schema directly
                                        log.info("Schema not found for "+actionName+" "+jsonURL);
                                    }
                                }
                            }
                        }

                    }
                    } else {
                        log.info("nothing found for "+actionName+" it could be direct call without parameters");
                    }
                    inputParameters.add(inputParameter);
                    httpAction.setInputObjects(inputParameters);

                    if(predictions.containsKey(actionName)) {
                        log.info(actionName+" is present");
                        actionName = getModifiedActionName(httpAction);
                        log.info(actionName+" is new the name ");
                    }
                    if(predictions.containsKey(actionName)) {
                        log.warning("cannot put action as its already there "+actionName);
                    }else {
                        predictions.put(actionName, httpAction);
                        actionNameList.append("," + actionName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getModifiedActionName(HttpPredictedAction httpAction) {
        String actionName = httpAction.getActionName();
        List<InputParameter> params =  httpAction.getInputObjects();
        actionName = actionName +"_With";
        for (InputParameter param:params
             ) {
            actionName = actionName+"_"+param.getName();

        }
        return actionName;
    }
    private static Schema<?> resolveSchema(OpenAPI openAPI, String ref) {
        // Extract the reference key
        String refKey = ref.substring(ref.lastIndexOf("/") + 1);

        // Retrieve the schema definition from the components section
        return openAPI.getComponents().getSchemas().get(refKey);
    }
    private static MediaType findJsonMediaType(RequestBody requestBody) {
        // Iterate over the content map and find the JSON media type by key
        for (String key : requestBody.getContent().keySet()) {
            if (key.startsWith("application/json")) {
                return requestBody.getContent().get(key);
            }
        }
        return null;
    }
}
