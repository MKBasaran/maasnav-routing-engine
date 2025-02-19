package com.group8.phase1.logger;

public class LoggerService {
    private static LoggerService instance;
    private Logger logger;

    private LoggerService() {
        this.logger = LoggerFactory.getLogger("file");
    }

    public static synchronized LoggerService getInstance() {
        if (instance == null) {
            instance = new LoggerService();
        }
        return instance;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }
}
