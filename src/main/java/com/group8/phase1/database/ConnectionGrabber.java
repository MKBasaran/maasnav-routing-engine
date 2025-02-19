package com.group8.phase1.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.group8.phase1.logger.LoggerService;

public class ConnectionGrabber {


    private final String databaseOSM = "./Database/graph_data.db";
    private final String databaseGTFS = "./Database/GTFS.db";
    private final String databaseJSON = "./Database/JSON.db";


    private static ConnectionGrabber instance;
    private Connection connectionGTFS;
    private Connection connectionOSM;
    LoggerService loggerService;
    

    private ConnectionGrabber() {
        this.loggerService = LoggerService.getInstance();
    }

    public static ConnectionGrabber getInstance() {
        if (instance == null) {
            instance = new ConnectionGrabber();
        }
        return instance;
    }

    public Connection connectGTFS() {
        try {
            loggerService.info("Created connection with GTFS database!");
            return DriverManager.getConnection("jdbc:sqlite:" + databaseGTFS);
        } catch (SQLException e) {
            loggerService.error("Couldn't create connection with GTFS database!");
            System.out.println(e.getMessage());
            return null;
        }
    }
    public Connection connectJSON(){
        try{
            loggerService.info("Created connection with JSON files");
            return DriverManager.getConnection("jdbc:sqlite:" + databaseJSON);
        } catch (SQLException e) {
            loggerService.error("Couldn't create connection with JSON database!");
            System.out.println(e.getMessage());
            return null;
        }
    }


    public Connection connectOSM() {
        try {
            loggerService.info("Created connection with OSM database!");
            return DriverManager.getConnection("jdbc:sqlite:" + databaseOSM);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            loggerService.error("Couldn't create connection with OSM database!");
            return null;
        }
    }

    public Connection getConnectionGTFS() {
        return connectGTFS();
    }

    public Connection getConnectionOSM() {
        return connectOSM();
    }
    public Connection getConnectionJSON(){
        return connectJSON();
    }
}
