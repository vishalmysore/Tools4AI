package com.t4a.action.shell;

import com.t4a.api.ActionType;
import com.t4a.api.PredictedAIAction;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * <pre>
 * Base class to execute shell commands , these configuration are part of shell_action.yaml. Shell commands can be used
 * to trigger any shell or CMD actions in real time based on the prompt
 *
 *
 *  - scriptName: "test_script.cmd"
 *   actionName: saveEmployeeName
 *   parameters: employeeName,employeeLocation
 *   description: This is a command which will save employee information
 *
 *
 * In this case the function and method name is  saveEmployeeName and the parameters are  employeeName and employeeLocation
 * If there is a prompt like this "Hey Vishal joined my company and he is placed in Toronto"
 *
 * then AI will call back script associated with saveEmployeeName (in this case  test_script.cmd) with array of parameters
 * the first value will be Vishal and then second value will be Toronto.
 *
 * You can provide any number of parameters and they will be passed as array in exactly same order
 *</pre>
 * @see com.t4a.predict.ShellPredictionLoader
 */
@NoArgsConstructor

@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class ShellPredictedAction implements PredictedAIAction {

    @NonNull
    private String description;
    @NonNull
    private String scriptPath;
    @NonNull
    private String actionName;

    private String parameterNames;

    private String group;
    private String groupDescription;

    public  void executeShell(String arguments[]) throws IOException, InterruptedException {
        scriptPath = loadScript(scriptPath);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("cmd /c ").append(scriptPath);
        for (String arg : arguments) {
            commandBuilder.append(" ").append(arg);
            log.debug(arg);
        }
        String command = commandBuilder.toString();

        log.debug(command);
        // Execute the command
        Process process = Runtime.getRuntime().exec(command);

        // Read the output of the script (if any)
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.debug(line);
        }

        // Wait for the script to finish execution
        int exitCode = process.waitFor();
        log.debug("Script exited with code " + exitCode);
    }

    @Override
    public String getActionGroup() {
        return group;
    }

    public static String detectPathType(String scriptPath) {
        File file = new File(scriptPath);

        if (file.isAbsolute()) {
            return "Absolute path";
        } else if (file.getParent() != null) {
            return "Relative path";
        } else {
            return "Filename only";
        }
    }

    public  String loadScript(String scriptPath) {
        File file = new File(scriptPath);

        if (file.isAbsolute()) {
            // Load from absolute path
            return loadFromAbsolutePath(scriptPath);
        } else {
            // Load from classpath
            return loadFromClasspath(scriptPath);
        }
    }

    public  String loadFromAbsolutePath(String absolutePath) {
        // Load from absolute path
        File file = new File(absolutePath);
        // Perform file loading operations
        return absolutePath;
    }

    public  String loadFromClasspath(String fileName) {
        // Load from classpath
        URL resourceUrl = ShellPredictedAction.class.getClassLoader().getResource(fileName);

        if (resourceUrl != null) {
            File f = null;
            try {
                f = new File(resourceUrl.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            return f.getAbsolutePath();
        } else {
            return "File not found in classpath: " + fileName;
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.SHELL;
    }
}
