package com.group8.phase1.osm.setup;

import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.structures.map.Way;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public enum PopulateGraphTable {
    ;

    /**
     * Inserts a batch of Way objects into the graph table.
     *
     * @param batchWays  the list of Way objects to be inserted
     * @param connection the database connection
     */
    public static void insert(List<Way> batchWays, Connection connection) {
        LoggerService loggerService = LoggerService.getInstance();
        String sql = "INSERT INTO graph(start_node_id,end_node_id,dist,way_id) VALUES(?,?,?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Way way : batchWays) {
                pstmt.setLong(1, way.getSource());
                pstmt.setDouble(2, way.getDestination());
                pstmt.setDouble(3, way.getWeight());
                pstmt.setLong(4, way.getWayId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            loggerService.info("Successfully inserted another batch of ways into OSM database!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            loggerService.error("Couldn't insert batch of ways into OSM database: " + e.getMessage());
        }
    }
}
