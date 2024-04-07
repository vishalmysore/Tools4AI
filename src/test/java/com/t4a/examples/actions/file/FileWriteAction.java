package com.t4a.examples.actions.file;

import com.t4a.api.JavaMethodAction;
import com.t4a.predict.Predict;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Predict(actionName = "saveInformationToLocalFile",description = "saves the information in local file",groupName = "personal", groupDescription = "all personal stuff")
public class FileWriteAction implements JavaMethodAction {

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
            log.debug("Information saved to local file: " + fileName);
        } catch (IOException e) {
            log.error("Failed to save information to local file: " + e.getMessage());
        } finally {
            // Close the writer
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.error("Failed to close writer: " + e.getMessage());
                }
            }
        }

        return fileName;
    }


}
