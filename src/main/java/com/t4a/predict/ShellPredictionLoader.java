package com.t4a.predict;

import com.t4a.action.shell.ShellPredictedAction;
import com.t4a.api.AIAction;
import com.t4a.api.ActionList;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * The {@code ShellPredictionLoader} class is responsible for loading shell actions from a YAML file
 * and populating a map of predictions within the application.
 * <p>
 * This class utilizes the SnakeYAML library for parsing YAML files and provides methods for loading
 * shell actions, parsing their details, and populating a predictions map for further use in the
 * application's predictive capabilities. It handles potential exceptions such as URISyntaxException
 * and IOException during the loading process and utilizes URL resources and input/output streams
 * for seamless access to the YAML file. With its no-args constructor and logging capabilities provided
 * by Lombok annotations, the class ensures robustness and reliability in predicting shell actions
 * within the application.
 * </p>
 */
@Log
@NoArgsConstructor
public class ShellPredictionLoader {

    private  String yamlFile = "shell_actions.yaml";
    private URL resourceUrl = null;



    public void load(Map<String, AIAction> predictions, StringBuffer actionNameList, ActionList listOfActions) throws LoaderException {

        try {
            loadYamlFile(predictions,actionNameList,listOfActions);
        } catch (URISyntaxException e) {
            throw new LoaderException(e);
        }


    }

    public  void loadYamlFile(Map<String,AIAction> predictions,StringBuffer actionNameList, ActionList listOfActions) throws URISyntaxException {
        if(resourceUrl == null)
        resourceUrl = ShellPredictionLoader.class.getClassLoader().getResource(yamlFile);

        if (resourceUrl == null) {
            log.warning("File not found: " + yamlFile);
            return;
        }

        try (InputStream inputStream = resourceUrl.openStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            Yaml yaml = new Yaml();
            Map<String, List<Map<String, Object>>> data = yaml.load(reader);

            for (Map.Entry<String, List<Map<String, Object>>> groupEntry : data.entrySet()) {

                List<Map<String, Object>> groupInfo = groupEntry.getValue();


                for (Map<String, Object> group : groupInfo) {
                    String groupDescription = (String) group.get("description");
                    String groupName = (String) group.get("name");
                    log.info("Group Name: " + groupName);
                    log.info("Description: " + groupDescription);

                    List<Map<String, String>> scripts = (List<Map<String, String>>) group.get("scripts");
                    for (Map<String, String> scriptInfo : scripts) {
                        String scriptName = scriptInfo.get("scriptName");
                        String actionName = scriptInfo.get("actionName");
                        String parameterNames = scriptInfo.get("parameters");
                        String description = scriptInfo.get("description");


                        ShellPredictedAction shellAction = new ShellPredictedAction();
                        shellAction.setActionName(actionName);
                        shellAction.setScriptPath(scriptName);
                        shellAction.setParameterNames(parameterNames);
                        shellAction.setDescription(description);

                        shellAction.setGroup(groupName);
                        shellAction.setGroupDescription(groupDescription);
                        actionNameList.append(actionName + ",");
                        predictions.put(actionName, shellAction);
                        listOfActions.addAction(shellAction);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
