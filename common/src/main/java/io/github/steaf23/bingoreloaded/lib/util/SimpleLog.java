package io.github.steaf23.bingoreloaded.lib.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class SimpleLog
{
    private BufferedWriter writer;

    public SimpleLog(File location) {
        // Create the log file in the plugin's data folder
        try {
            // Create the log file if it doesn't exist
            if (!location.exists()) {
                location.getParentFile().mkdirs();
                location.createNewFile();
            }
            // Create a BufferedWriter to write to the file
            writer = new BufferedWriter(new FileWriter(location, true)); // true for append mode
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        try {
            // Write the message to the file, followed by a newline
            writer.write(Instant.now() + ": " + message);
            writer.newLine();
            writer.flush(); // Ensure the message is written to the file immediately
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close(); // Close the BufferedWriter when done
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
