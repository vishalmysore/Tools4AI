package com.enterprise.file;

import com.t4a.bridge.AIAction;
import com.t4a.bridge.ActionType;
import com.t4a.predict.Predict;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Log
@Predict
public class FileWriteAction implements AIAction {

    public Object saveInformationToLocalFile(String args[]) {
        StringBuilder content = new StringBuilder();
        for (String arg : args) {
            content.append(arg).append("\n");
        }

        String fileName = null;
        FileWriter writer = null;
        try {
            // Create a temporary file
            File tempFile = File.createTempFile("temp", ".txt");
            fileName = tempFile.getAbsolutePath();

            // Write content to the temporary file
            writer = new FileWriter(tempFile);
            writer.write(content.toString());
            log.info("Information saved to local file: " + fileName);
        } catch (IOException e) {
            log.severe("Failed to save information to local file: " + e.getMessage());
        } finally {
            // Close the writer
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.severe("Failed to close writer: " + e.getMessage());
                }
            }
        }

        return fileName;
    }

    @Override
    public String getDescription() {
        return "saves the information in local file";
    }

    @Override
    public ActionType getActionType() {
        return ActionType.FILE;
    }

    @Override
    public String getActionName() {
        return "saveInformationToLocalFile";
    }
}
