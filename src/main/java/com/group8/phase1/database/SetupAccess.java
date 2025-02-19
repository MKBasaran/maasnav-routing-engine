package com.group8.phase1.database;

import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.logger.LoggerService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum SetupAccess {
    ;
    private static final LoggerService loggerService = LoggerService.getInstance();

    public static void setupAccess(ProgressPanel progressViewer){
        progressViewer.setStatus("Creating databases...");
        progressViewer.setProgress(0.5);
        createNewDatabase();
        progressViewer.setProgress(0.75);
        createTables();
        progressViewer.setProgress(1.0);
    }
    public static void createNewDatabase(){
        try (Connection conn = ConnectionGrabber.getInstance().connectJSON()) {
            if (conn != null) {
                loggerService.info("OSM Database was created.");
            }

        } catch (SQLException e) {
            loggerService.error("Couldn't create OSM database.");
        }
    }

    public static void createMultiplierTable(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS multiplier(\n" +
                "\tsuperType TEXT PRIMARY KEY,\n" +
                "\tbonus INT NOT NULL\n" +
                ");\n");
    }
    public static void createScoreTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS score (\n" +
                "\ttype TEXT PRIMARY KEY,\n" +
                "\tsuperType TEXT,\n" +
                "\tscore INT NOT NULL,\n" +
                "\tFOREIGN KEY (superType) REFERENCES multiplier(superType)\n" +
                "\tON DELETE CASCADE\n" +
                "\tON UPDATE CASCADE\n" +
                ");\n");
    }
    public static void createAmenitiesTable(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS amenities (\n" +
                "    landmark_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    amenity_type TEXT,\n" +
                "    name TEXT,\n" +
                "    lat REAL NOT NULL,\n" +
                "    long REAL NOT NULL,\n" +
                "    amenityScore INT DEFAULT 0,\n" +
                "    FOREIGN KEY (amenity_type) \n" +
                "    REFERENCES score(type)\n" +
                "    ON UPDATE CASCADE\n" +
                "    ON DELETE CASCADE\n" +
                ");\n");
        Statement rtreeStatement = connection.createStatement();
        rtreeStatement.executeUpdate("CREATE VIRTUAL TABLE IF NOT EXISTS rtree_amenities USING rtree(\n" +
                "    id,\n" +
                "    minX, maxX,\n" +
                "    minY, maxY\n" +
                ");\n");
    }
    public static void createShopTable(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS shops (\n" +
                "    landmark_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    shop_type TEXT,\n" +
                "    name TEXT,\n" +
                "    lat REAL NOT NULL,\n" +
                "    long REAL NOT NULL,\n" +
                "    shopScore INT DEFAULT 0,\n" +
                "    FOREIGN KEY (shop_type) \n" +
                "    REFERENCES score(type)\n" +
                "    ON UPDATE CASCADE\n" +
                "    ON DELETE CASCADE\n" +
                ");\n");
        Statement rtreeShops = connection.createStatement();
        rtreeShops.executeUpdate("CREATE VIRTUAL TABLE IF NOT EXISTS rtree_shops USING rtree(\n" +
                "    id,\n" +
                "    minX, maxX,\n" +
                "    minY, maxY\n" +
                ");\n");
    }
    public static void createTourismTable(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS tourism (\n" +
                "    landmark_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    tourism_type TEXT,\n" +
                "    name TEXT,\n" +
                "    lat REAL NOT NULL,\n" +
                "    long REAL NOT NULL,\n" +
                "    tourismScore INT DEFAULT 0,\n" +
                "    FOREIGN KEY (tourism_type) \n" +
                "    REFERENCES score(type)\n" +
                "    ON UPDATE CASCADE\n" +
                "    ON DELETE CASCADE\n" +
                ");\n");
        Statement rtreeTourism = connection.createStatement();
        rtreeTourism.executeUpdate("CREATE VIRTUAL TABLE IF NOT EXISTS rtree_tourism USING rtree(\n" +
                "    id,\n" +
                "    minX, maxX,\n" +
                "    minY, maxY\n" +
                ");\n");
    }
    public static void createCodeToAgeTable(Connection connection) throws SQLException{
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE CodeToAge (\n" +
                "\tpostalCode CHAR(6) PRIMARY KEY NOT NULL,\n" +
                "\tAgeCategory CHAR(1) NOT NULL\n" +
                ");");
    }
    public static void createScoreTriggers(Connection connection) throws SQLException {

        if (!checkTriggerExistence("handleInsertScore",connection)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TRIGGER handleInsertScore AFTER INSERT ON score\n" +
                    "FOR EACH ROW BEGIN\n" +
                    "\tUPDATE score SET score = (SELECT m.bonus FROM multiplier m WHERE m.superType = NEW.superType)\n" +
                    "\tWHERE type = NEW.type;\n" +
                    "END;\n");
        }

        if (!checkTriggerExistence("handleScoreShops", connection)) {
            Statement statementShops = connection.createStatement();
            statementShops.executeUpdate("CREATE TRIGGER handleScoreShops AFTER INSERT ON shops\n" +
                    "FOR EACH ROW BEGIN\n" +
                    "\tUPDATE shops SET shopScore = (SELECT s.score FROM score s WHERE s.type = NEW.shop_type)\n" +
                    "\tWHERE landmark_id = NEW.landmark_id;\n" +
                    "END;\n");
        }

        if (!checkTriggerExistence("handleScoreAmenity", connection)) {
            Statement statementAmenities = connection.createStatement();
            statementAmenities.executeUpdate("CREATE TRIGGER handleScoreAmenity AFTER INSERT ON amenities\n" +
                    "FOR EACH ROW BEGIN\n" +
                    "\tUPDATE amenities SET amenityScore = (SELECT s.score FROM score s WHERE s.type = NEW.amenity_type)\n" +
                    "\tWHERE landmark_id = NEW.landmark_id;\n" +
                    "END;\n");
        }

        if (!checkTriggerExistence("handleScoreTourism", connection)) {
            Statement statementTourism = connection.createStatement();
            statementTourism.executeUpdate("CREATE TRIGGER handleScoreTourism AFTER INSERT ON tourism\n" +
                    "FOR EACH ROW BEGIN\n" +
                    "\tUPDATE tourism SET tourismScore = (SELECT s.score FROM score s WHERE s.type = NEW.tourism_type)\n" +
                    "\tWHERE landmark_id = NEW.landmark_id;\n" +
                    "END;\n");
        }
    }
    public static boolean checkTriggerExistence(String triggerName, Connection connection){
        String checkTrigger = "SELECT name FROM sqlite_master WHERE type='trigger' AND name='" + triggerName + "';";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(checkTrigger);
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void createTables() {
        try {
            Connection conn = ConnectionGrabber.getInstance().getConnectionJSON();
            createMultiplierTable(conn);
            createScoreTable(conn);
            createAmenitiesTable(conn);
            createShopTable(conn);
            createTourismTable(conn);
            createScoreTriggers(conn);
            createCodeToAgeTable(conn);
        } catch (SQLException e) {
            loggerService.error("Couldn't create OSM tables");
        }
    }
}
