package com.group8.phase1.structures.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a graph data structure.
 */
public class Graph {


    /**
     * Adjacency list representation of the graph
     */
    public Map<Long, List<Edge>> graph;

    /**
     * Constructs an empty graph.
     */
    public Graph() {
        graph = new HashMap<>();
    }

    /**
     * Adds an edge to the graph.
     *
     * @param sourceId    The ID of the source node.
     * @param destination The destination node.
     * @param weight      The weight of the edge.
     */
    public void addEdge(Long sourceId, Long destination, double weight) {
        graph.putIfAbsent(sourceId, new ArrayList<>());
        graph.get(sourceId).add(new Edge(destination, weight));
    }

    /**
     * Retrieves the edges for a given node.
     *
     * @param current The ID of the node.
     * @return The list of edges for the node.
     */
    public List<Edge> getEdges(Long current) {
        return graph.get(current);
    }

    /**
     * Retrieves a list of all nodes in the graph.
     *
     * @return The list of all node IDs.
     */
    public List<Long> getNodes() {
        return new ArrayList<>(graph.keySet());
    }
}
