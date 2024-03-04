package com.t4a.action.shell;

import com.t4a.api.AIAction;
import com.t4a.api.ActionType;
import lombok.*;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * base class to execute shell commands
 */
@NoArgsConstructor

@Getter
@Setter
@Log
@RequiredArgsConstructor
public class ShellAction implements AIAction {

    @NonNull
    private String description;
    @NonNull
    private String scriptPath;
    @NonNull
    private String actionName;


    public  void executeShell(String arguments[]) throws IOException, InterruptedException {
        scriptPath = loadScript(scriptPath);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("cmd /c ").append(scriptPath);
        for (String arg : arguments) {
            commandBuilder.append(" ").append(arg);
            log.info(arg);
        }
        String command = commandBuilder.toString();

        log.info(command);
        // Execute the command
        Process process = Runtime.getRuntime().exec(command);

        // Read the output of the script (if any)
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Wait for the script to finish execution
        int exitCode = process.waitFor();
        System.out.println("Script exited with code " + exitCode);
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
        URL resourceUrl = ShellAction.class.getClassLoader().getResource(fileName);

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
    public String getDefaultExecutorMethodName() {
        return "executeShell";
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
