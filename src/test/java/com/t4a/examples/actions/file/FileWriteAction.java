package com.t4a.examples.actions.file;

import com.t4a.annotations.Action;
import com.t4a.annotations.Predict;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Predict(groupName = "File", groupDescription = "File related actions")
@Slf4j
public class FileWriteAction  {

    @Action(description = "Save information to a local file")
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
