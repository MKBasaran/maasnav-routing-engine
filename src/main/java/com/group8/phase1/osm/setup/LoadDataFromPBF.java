package com.group8.phase1.osm.setup;

import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.structures.map.Node;
import com.group8.phase1.utils.DistanceCalculator;
import com.wolt.osm.parallelpbf.ParallelBinaryParser;
import com.wolt.osm.parallelpbf.entity.Way;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum LoadDataFromPBF {
    ;

    static final int totalNumberOfNodes = 4102394;
    static final int totalNumberOfWays = 492891;
    private static final int BATCH_SIZE = 30000;
    static int processedNodes = 0;
    static int processedWays = 0;
    static List<Node> batchNodes = new ArrayList<>();
    static List<com.group8.phase1.structures.map.Way> batchWays = new ArrayList<>();
    static ProgressPanel progressViewerStatic;
    private static Connection conn;
    private static Map<Long, Node> nodeMap;

    private static Connection connect() {
        Connection conn = null;
        conn = ConnectionGrabber.getInstance().getConnectionOSM();
        return conn;
    }

    public static void loadPBF(ProgressPanel progressViewer) {
        progressViewerStatic = progressViewer;
        progressViewerStatic.setStatus("Loading OSM Map Data...");
        long startTime = System.currentTimeMillis();
        nodeMap = new HashMap<>();

        conn = connect();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        InputStream input = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("Maastricht.pbf");

        ParallelBinaryParser parser = new ParallelBinaryParser(input, 1)
                .onNode(LoadDataFromPBF::processNode)
                .onWay(LoadDataFromPBF::processWay);

        parser.parse();
        if (batchNodes.size() > 0) {
            PopulateNodesTable.insert(batchNodes, conn);
            batchNodes.clear();
            updateProgress(progressViewerStatic);
            processedNodes = totalNumberOfNodes;
        }
        if (batchWays.size() > 0) {
            PopulateGraphTable.insert(batchWays, conn);
            processedWays = totalNumberOfWays;
            updateProgress(progressViewerStatic);
            batchWays.clear();
        }

        FilterGraphNodes.filter(conn);

        try {
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken: " + duration / 1000 + " seconds");
        progressViewer.setProgress(1);
        progressViewer.setStatus("OSM Data Loaded Successfully!");
    }

    private static void processNode(com.wolt.osm.parallelpbf.entity.Node node) {
        nodeMap.put(node.getId(), new Node(node.getId(), node.getLat(), node.getLon()));
        batchNodes.add(new Node(node.getId(), node.getLat(), node.getLon()));
        if (batchNodes.size() == BATCH_SIZE) {
            PopulateNodesTable.insert(batchNodes, conn);
            batchNodes.clear();
            updateProgress(progressViewerStatic);
            processedNodes += BATCH_SIZE;
        }

    }

    private static void processWay(Way way) {
        if (way.getTags().containsKey("highway")) {

            List<Long> wayNodes = way.getNodes();
            for (int i = 0; i < wayNodes.size() - 1; i++) {
                long sourceId = wayNodes.get(i);
                long destinationId = wayNodes.get(i + 1);

                double dist = DistanceCalculator.calculateInMeters(nodeMap.get(sourceId), nodeMap.get(destinationId));
                batchWays.add(new com.group8.phase1.structures.map.Way(wayNodes.get(i), wayNodes.get(i + 1), way.getId(), dist));

                if (batchWays.size() == BATCH_SIZE) {
                    PopulateGraphTable.insert(batchWays, conn);

                    processedWays += BATCH_SIZE;
                    updateProgress(progressViewerStatic);
                    batchWays.clear();
                }
            }
        }

    }

    private static void updateProgress(ProgressPanel progressViewer) {
        int processed = processedNodes + processedWays;
        int total = totalNumberOfWays + totalNumberOfNodes;

        double progress = ((double) (processedNodes + processedWays) / (double) (totalNumberOfNodes + totalNumberOfWays));
        progressViewer.setProgress(progress);
        progressViewer.setStatus("Database", "Processed node/way " + processed + " out of " + total);

    }
}
