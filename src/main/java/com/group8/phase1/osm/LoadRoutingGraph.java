package com.group8.phase1.osm;

import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.structures.map.Graph;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public enum LoadRoutingGraph {
    ;
    public static Graph graph;
    static int processedEdges = 0;
    static int totalNumberOfEdges = 0;
    private static ProgressPanel progressViewer;


    // Counts number of Edges ( ways/streets ) in OSM database.
    public static int countEdges() {
        LoggerService loggerService = LoggerService.getInstance();
        try (Connection conn = ConnectionGrabber.getInstance().getConnectionOSM()) {
            try (Statement stmt = conn.createStatement()) {
                String sql = "SELECT COUNT(*) AS total FROM graph";

                try (ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        loggerService.info("Counted " + rs.getInt("total") + " edges in OSM database!");
                        return rs.getInt("total");
                    }
                }
            }
        } catch (SQLException e) {
            loggerService.error("Couldn't count number of Edges in OSM database! " + e.getMessage());
        }
        return 0;
    }

    public static Graph load(ProgressPanel progressViewerReference) {
        totalNumberOfEdges = countEdges();
        progressViewer = progressViewerReference;
        return loadGraph();
    }

    public static Graph loadGraph() {

        graph = new Graph();
        String query = "SELECT * FROM graph";

        try (Connection conn = ConnectionGrabber.getInstance().getConnectionOSM(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long startNodeId = rs.getLong("start_node_id");
                long endNodeId = rs.getLong("end_node_id");
                double distance = rs.getDouble("dist");
                graph.addEdge(startNodeId, endNodeId, distance);

                graph.addEdge(endNodeId, startNodeId, distance);
                processedEdges++;
                if (progressViewer != null) {
                    updateStatus();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return graph;
    }

    private static void updateStatus() {
        if (processedEdges % 10000 == 0) {
            progressViewer.setProgress((double) processedEdges / totalNumberOfEdges);
            progressViewer.setStatus("Routing", "Processing edge " + processedEdges + " out of " + totalNumberOfEdges);
        }
    }
}
