package com.group8.phase1.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.logger.LoggerService;

/**
 * The {@code setup} enum provides methods for setting up databases and creating tables.
 */
public enum Setup {
    ;

    private static final LoggerService loggerService = LoggerService.getInstance();

    /**
     * Sets up the databases and creates tables.
     *
     * @param progressViewer a {@link ProgressPanel} to show the progress of setup
     */
    public static void setup(ProgressPanel progressViewer) {


        progressViewer.setStatus("Creating databases...");
        progressViewer.setProgress(0.00);
        createNewDatabase();
        progressViewer.setProgress(0.25);
        createTables();
        progressViewer.setProgress(0.5);
    }

    /**
     * Creates a new database (graph_data.db).
     */
    public static void createNewDatabase() {
        try (Connection conn = ConnectionGrabber.getInstance().getConnectionOSM()) {
            if (conn != null) {
                loggerService.info("OSM Database was created.");
            }

        } catch (SQLException e) {
            loggerService.error("Couldn't create OSM database.");
        }
    }

    /**
     * Creates the graph table in the database.
     *
     * @param conn the database connection
     * @throws SQLException if a SQL error occurs
     */
    public static void createGraphTable(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("CREATE TABLE graph (\n" +
                "                       start_node_id INTEGER,\n" +
                "                       end_node_id   INTEGER,\n" +
                "                       dist          INTEGER,\n" +
                "                       way_id        INTEGER\n" +
                ");\n");
    }

    /**
     * Creates the nodes table in the database.
     *
     * @param conn the database connection
     * @throws SQLException if a SQL error occurs
     */
    public static void createNodesTable(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("CREATE TABLE nodes (\n" +
                "                       node_id INTEGER PRIMARY KEY,\n" +
                "                       lon     REAL,\n" +
                "                       lat     REAL\n" +
                ");\n");
    }

    /**
     * Creates the graph nodes table in the database.
     *
     * @param conn the database connection
     * @throws SQLException if a SQL error occurs
     */
    public static void createGraphNodesTable(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("CREATE TABLE graph_nodes (\n" +
                "    node_id   NUMERIC,\n" +
                "    latitude  REAL,\n" +
                "    longitude REAL\n" +
                ");\n");
    }

    /**
     * Creates all tables
     */
    public static void createTables() {
        try {
            Connection conn = ConnectionGrabber.getInstance().getConnectionOSM();
            createGraphTable(conn);
            createNodesTable(conn);
            createGraphNodesTable(conn);
        } catch (SQLException e) {
            loggerService.error("Couldn't create OSM tables");
        }
    }
}
