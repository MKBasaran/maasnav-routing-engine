package com.group8.phase1.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements Logger {
    private final String fileName;
    private FileWriter fileWriter;

    public FileLogger(String fileName) throws IOException {
        this.fileName = fileName;
        initializeFileWriter();
    }

    private void initializeFileWriter() throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            // Clear the log file so it starts logging on a fresh one
            fileWriter = new FileWriter(fileName, false);
        } else {
            fileWriter = new FileWriter(fileName);
        }
    }

    @Override
    public void info(String message) {
        log("(INFO) " + message);
    }

    @Override
    public void warn(String message) {
        log("(WARN) " + message);
    }

    @Override
    public void error(String message) {
        log("(ERROR) " + message);
    }

    private void log(String message) {
        try {
            fileWriter.write(message + "\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
