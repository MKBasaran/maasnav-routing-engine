package com.group8.phase1.osm.setup;

import com.group8.phase1.logger.LoggerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for filtering graph nodes and setting up necessary indexes and RTree table.
 */
public enum FilterGraphNodes {
    ;

    /**
     * Filters the graph nodes and sets up the graph nodes table and indexes in the database.
     *
     * @param conn the database connection
     */
    public static void filter(Connection conn) {
        LoggerService logger = LoggerService.getInstance();
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate("DELETE\n" +
                    "FROM graph\n" +
                    "WHERE start_node_id NOT IN (SELECT end_node_id FROM graph)\n" +
                    "AND end_node_id NOT IN (SELECT start_node_id FROM graph);\n" +
                    "\n" +
                    "INSERT INTO graph_nodes (node_id, latitude, longitude)\n" +
                    "SELECT node_id, lat, lon\n" +
                    "FROM nodes\n" +
                    "WHERE node_id IN (\n" +
                    "    SELECT DISTINCT start_node_id\n" +
                    "    FROM graph\n" +
                    "    UNION\n" +
                    "    SELECT DISTINCT end_node_id\n" +
                    "    FROM graph\n" +
                    ");" +
                    "CREATE INDEX lat_lon_index ON graph_nodes (\n" +
                    "    latitude,\n" +
                    "    longitude\n" +
                    ");\n" +
                    "CREATE VIRTUAL TABLE IF NOT EXISTS graph_nodes_rtree USING rtree(\n" +
                    "    id,         \n" +
                    "    min_latitude, \n" +
                    "    max_latitude, \n" +
                    "    min_longitude, \n" +
                    "    max_longitude \n" +
                    ");\n" +
                    "\n" +
                    "INSERT INTO graph_nodes_rtree(id, min_latitude, max_latitude, min_longitude, max_longitude)\n" +
                    "SELECT \n" +
                    "    node_id AS id,\n" +
                    "    latitude AS min_latitude,\n" +
                    "    latitude AS max_latitude,\n" +
                    "    longitude AS min_longitude,\n" +
                    "    longitude AS max_longitude\n" +
                    "FROM graph_nodes;\n" +
                    "\n");
            logger.info("Successfully filtered and indexed OSM database!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            logger.error("Couldn't filter or index OSM database!");
        }
    }
}
