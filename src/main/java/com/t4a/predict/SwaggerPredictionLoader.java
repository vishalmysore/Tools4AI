package com.t4a.predict;

import com.t4a.action.http.HttpMethod;
import com.t4a.action.http.HttpPredictedAction;
import com.t4a.action.http.InputParameter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwaggerPredictionLoader {
    public static void main(String[] args) {
        String swaggerFileUrl = "https://example.com/swagger.yaml";

        try {
            //InputStream inputStream = swaggerFileUrl.openStream();
            // Parse Swagger/OpenAPI specification
            OpenAPI openAPI = new OpenAPIV3Parser().readLocation("https://fakerestapi.azurewebsites.net/swagger/v1/swagger.json", null, null).getOpenAPI();
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
                        actionName = method.name().toLowerCase()+resourceName;
                    }
                    httpAction.setActionName(actionName); // Use method name as action name

                    httpAction.setUrl(swaggerFileUrl + path); // Construct URL
                    httpAction.setType(HttpMethod.valueOf(method.name())); // Set HTTP method type

                    // Extract parameters and populate input objects
                    List<InputParameter> inputParameters = new ArrayList<>();
                    if (operation.getParameters() != null) {
                        for (io.swagger.v3.oas.models.parameters.Parameter parameter : operation.getParameters()) {
                            String paraName = parameter.getName();
                            String paraType = parameter.getSchema().getType();
                            InputParameter inputParameter = new InputParameter(paraName,paraType,"");
                            inputParameters.add(inputParameter);

                        }
                    }
                    httpAction.setInputObjects(inputParameters);

                    // Print details or perform further processing
                    System.out.println("Action Name: " + httpAction.getActionName());
                    System.out.println("URL: " + httpAction.getUrl());
                    System.out.println("Type: " + httpAction.getType());
                    System.out.println("Input Parameters: " + inputParameters);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
