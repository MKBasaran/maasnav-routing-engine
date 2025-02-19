package com.group8.phase1.osm.setup;


import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.structures.map.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public enum PopulateNodesTable {
    ;

    /**
     * Inserts a batch of Node objects into the nodes table.
     *
     * @param batchNodes the list of Node objects to be inserted
     * @param connection the database connection
     */
    public static void insert(List<Node> batchNodes, Connection connection) {
        LoggerService loggerService = LoggerService.getInstance();

        String sql = "INSERT INTO nodes(node_id,lat,lon) VALUES(?,?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Node node : batchNodes) {
                pstmt.setLong(1, node.getId());
                pstmt.setDouble(2, node.getLatitude());
                pstmt.setDouble(3, node.getLongitude());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            loggerService.info("Successfully inserted batch of nodes into OSM database!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            loggerService.error("Couldn't insert batch of nodes into OSM database! " + e.getMessage());
        }
    }

}
