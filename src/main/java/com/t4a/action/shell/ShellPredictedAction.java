package com.t4a.action.shell;

import com.t4a.api.ActionType;
import com.t4a.api.PredictedAIAction;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Characters cmd.exe interprets even inside an argument list. Arguments come from the LLM, so
     * any of these would let a prompt chain extra commands onto the script.
     */
    private static final String CMD_METACHARACTERS = "&|<>^%!\"()\r\n";

    /**
     * Runs the script with the given arguments and returns its combined stdout/stderr.
     * On Windows the script runs through {@code cmd /c}; elsewhere {@code .sh} scripts run through
     * {@code sh} and anything else is executed directly, so arguments are never shell-interpreted.
     */
    public String executeShell(String[] arguments) throws IOException, InterruptedException {
        String resolvedPath = loadScript(scriptPath);
        if (resolvedPath == null) {
            throw new FileNotFoundException("Script not found on classpath or file system: " + scriptPath);
        }
        List<String> commandList = buildCommand(resolvedPath, arguments, isWindows());
        ProcessBuilder builder = new ProcessBuilder(commandList);
        // Merge stderr into stdout so a script writing to stderr cannot fill the pipe and hang
        builder.redirectErrorStream(true);

        log.debug(String.join(" ", commandList));
        Process process = builder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(line);
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.warn("Script {} exited with code {}", resolvedPath, exitCode);
        } else {
            log.debug("Script exited with code " + exitCode);
        }
        return output.toString();
    }

    static List<String> buildCommand(String resolvedPath, String[] arguments, boolean windows) {
        List<String> commandList = new ArrayList<>();
        if (windows) {
            commandList.add("cmd");
            commandList.add("/c");
        } else if (resolvedPath.endsWith(".sh")) {
            commandList.add("sh");
        }
        commandList.add(resolvedPath);
        if (arguments != null) {
            for (String argument : arguments) {
                if (argument == null) {
                    argument = "";
                }
                if (windows) {
                    checkCmdArgument(argument);
                }
                commandList.add(argument);
            }
        }
        return commandList;
    }

    static void checkCmdArgument(String argument) {
        for (char c : argument.toCharArray()) {
            if (CMD_METACHARACTERS.indexOf(c) >= 0) {
                throw new IllegalArgumentException("Shell argument contains a disallowed character '" + c
                        + "': " + argument);
            }
        }
    }

    static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().startsWith("windows");
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
        return file.getAbsolutePath();
    }

    public  String loadFromClasspath(String fileName) {
        // Load from classpath
        URL resourceUrl = ShellPredictedAction.class.getClassLoader().getResource(fileName);

        if (resourceUrl != null) {
            try {
                return new File(resourceUrl.toURI()).getAbsolutePath();
            } catch (URISyntaxException | IllegalArgumentException e) {
                // e.g. the script is packed inside a jar and has no file-system path
                log.warn("Cannot run script {} from {}: {}", fileName, resourceUrl, e.getMessage());
                return null;
            }
        }
        File relative = new File(fileName);
        if (relative.isFile()) {
            return relative.getAbsolutePath();
        }
        log.warn("File not found in classpath: " + fileName);
        return null;
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
