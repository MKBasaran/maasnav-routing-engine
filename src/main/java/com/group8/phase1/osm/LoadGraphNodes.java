package com.group8.phase1.osm;

import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.structures.map.Node;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public enum LoadGraphNodes {
    ;
    static int totalNumberOfNodes = 0;
    static int processedNodes = 0;
    private static ProgressPanel progressViewer;

    // Count nodes present in the OSM database.
    public static int countNodes() {
        LoggerService loggerService = LoggerService.getInstance();
        try (Connection conn = ConnectionGrabber.getInstance().getConnectionOSM()) {
            try (Statement stmt = conn.createStatement()) {
                String sql = "SELECT COUNT(*) AS total FROM graph_nodes";

                try (ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        loggerService.info("Counted " + rs.getInt("total") + " OSM nodes.");
                        return rs.getInt("total");
                    }
                }
            }
        } catch (SQLException e) {
            loggerService.error("Couldn't count number of nodes in OSM database! " + e.getMessage());
        }
        return 0;
    }

    public static Map<Long, Node> load(ProgressPanel progressViewerReference) {
        totalNumberOfNodes = countNodes();
        progressViewer = progressViewerReference;
        return loadNodes();
    }

    public static Map<Long, Node> loadNodes() {

        Map<Long, Node> nodeMap = new HashMap<>();
        String query = "SELECT * FROM graph_nodes";

        try (Connection conn = ConnectionGrabber.getInstance().getConnectionOSM(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long node_id = rs.getLong("node_id");
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                nodeMap.put(node_id, new Node(node_id, latitude, longitude));
                processedNodes++;
                if (progressViewer != null) updateStatus();
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return nodeMap;
    }

    private static void updateStatus() {
        if (processedNodes % 10000 == 0) {
            progressViewer.setProgress((double) processedNodes / totalNumberOfNodes);
            progressViewer.setStatus("Routing", "Processing node " + processedNodes + " out of " + totalNumberOfNodes);
        }
    }
}
