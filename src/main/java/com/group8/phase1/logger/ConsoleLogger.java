package com.group8.phase1.logger;

public class ConsoleLogger implements Logger {
    @Override
    public void info(String message) {
        System.out.println("(INFO) " + message);
    }

    @Override
    public void warn(String message) {
        System.out.println("(WARN) " + message);
    }

    @Override
    public void error(String message) {
        System.err.println("(ERROR) " + message);
    }
}
