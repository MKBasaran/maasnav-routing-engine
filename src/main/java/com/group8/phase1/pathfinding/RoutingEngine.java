package com.group8.phase1.pathfinding;

import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.structures.map.Edge;
import com.group8.phase1.structures.map.Graph;

import java.util.*;


public class RoutingEngine {
    private final Graph graph;
    private final LoggerService loggerService = LoggerService.getInstance();

    /**
     * Constructs a new RoutingEngine ( Dijkstra ) with the specified graph.
     *
     * @param graph the graph in which the shortest path will be found
     */
    public RoutingEngine(Graph graph) {
        this.graph = graph;
    }

    /**
     * Finds the shortest path between two nodes in the graph using Dijkstra's algorithm.
     *
     * @param startNodeId the ID of the starting node
     * @param endNodeId   the ID of the destination node
     * @return the shortest path between the start and end nodes
     */
    public Path getShortestPath(long startNodeId, long endNodeId) {
        // Initialize data structures
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> predecessors = new HashMap<>();
        PriorityQueue<Long> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Long nodeId : graph.graph.keySet()) {
            distances.put(nodeId, Double.MAX_VALUE);
            predecessors.put(nodeId, null);
        }
        distances.put(startNodeId, 0.0);
        priorityQueue.add(startNodeId);

        // Apply Dijkstra's algorithm
        while (!priorityQueue.isEmpty()) {
            long currentNode = priorityQueue.poll();

            if (currentNode == endNodeId) {
                break;
            }
            for (Edge edge : graph.getEdges(currentNode)) {
                long adjacentNode = edge.getDestination();
                double weight = edge.getWeight();
                double newDistance = distances.get(currentNode) + weight;
                if (newDistance < distances.get(adjacentNode)) {
                    distances.put(adjacentNode, newDistance);
                    predecessors.put(adjacentNode, currentNode);
                    priorityQueue.add(adjacentNode);
                }
            }
        }
        loggerService.info("Executed Dijkstra's algorithm!");
        // Reconstruct the shortest path
        return new Path(reconstructPath(predecessors, startNodeId, endNodeId), distances.get(endNodeId));
    }

    /**
     * Reconstructs the shortest path from the predecessors map.
     *
     * @param predecessors the map containing predecessors of each node
     * @param startNodeId  the ID of the starting node
     * @param endNodeId    the ID of the destination node
     * @return the reconstructed shortest path
     */
    private List<Long> reconstructPath(Map<Long, Long> predecessors, long startNodeId, long endNodeId) {
        List<Long> path = new ArrayList<>();
        Long step = endNodeId;

        // Check if a path exists
        if (predecessors.get(step) == null) {
            return path;  //null if no road exists
        }

        // Reconstruct the path
        path.add(step);
        while (step != null && step != startNodeId) {
            step = predecessors.get(step);
            path.add(0, step);
        }
        loggerService.info("Successfully reconstructed Dijkstra's path!");
        return path;
    }
}
