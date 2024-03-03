package com.t4a.predict;

import com.t4a.action.shell.ShellAction;
import com.t4a.examples.shell.ShellTest;
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

@Log
@NoArgsConstructor
public class ShellPredictionLoader {

    private  String yamlFile = "shell.yaml";
    private URL resourceUrl = null;

    public ShellPredictionLoader(URL resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void load(Map<String,PredictOptions> predictions,StringBuffer actionNameList) throws URISyntaxException {

        loadYamlFile(predictions,actionNameList);


    }

    public  void loadYamlFile(Map<String,PredictOptions> predictions,StringBuffer actionNameList) throws URISyntaxException {
        if(resourceUrl == null)
        resourceUrl = ShellTest.class.getClassLoader().getResource(yamlFile);

        if (resourceUrl == null) {
            throw new IllegalArgumentException("File not found: " + yamlFile);
        }

        try (InputStream inputStream = resourceUrl.openStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            Yaml yaml = new Yaml();
            List<Map<String, String>> data = yaml.load(reader);


            for (Map<String, String> scriptInfo : data) {
                String scriptName = scriptInfo.get("scriptName");
                String actionName = scriptInfo.get("actionName");
                String description = scriptInfo.get("description");
                PredictOptions options = new PredictOptions(ShellAction.class.getName(),description,actionName,actionName);
                options.setScriptPath(scriptName);
                actionNameList.append(actionName+",");
                predictions.put(actionName,options);
            }


        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
