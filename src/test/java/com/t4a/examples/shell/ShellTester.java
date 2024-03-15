package com.t4a.examples.shell;

import com.t4a.action.shell.ShellPredictedAction;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ShellTester {
    public static void main(String[] args) throws IOException, InterruptedException {

        URL resourceUrl = ShellTester.class.getClassLoader().getResource("test_script.cmd");
        if (resourceUrl != null) {
            // Convert URL to file path
            String filePath = resourceUrl.getFile();

            // Get the absolute path of the file
            File file = new File(filePath);
            String absolutePath = file.getAbsolutePath();
            ShellPredictedAction action = new ShellPredictedAction("This is action to run shelll command", absolutePath,"testMyScript");
            action.executeShell(args);
            // Output the absolute path
            System.out.println("Absolute path of the file: " + absolutePath);
        } else {
            System.out.println("File not found.");
        }

    }
}
