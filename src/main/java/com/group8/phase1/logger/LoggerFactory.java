package com.group8.phase1.logger;

import java.io.IOException;

public enum LoggerFactory {
    ;

    public static Logger getLogger(String type) {
        if ("console".equalsIgnoreCase(type)) {
            return new ConsoleLogger();
        } else if ("file".equalsIgnoreCase(type)) {
            try {
                return new FileLogger("app.log");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Unknown logger type: " + type);
    }
}
