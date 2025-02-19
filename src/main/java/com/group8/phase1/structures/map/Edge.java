package com.group8.phase1.structures.map;

/**
 * Represents an edge in a graph.
 */
public class Edge {


    /**
     * Destination node ID
     */
    public Long destination;
    /**
     * Weight of the edge
     */
    public double weight;

    /**
     * Constructs an Edge with the given destination and weight.
     *
     * @param destination The ID of the destination node.
     * @param weight      The weight of the edge.
     */
    public Edge(Long destination, double weight) {
        this.destination = destination;
        this.weight = weight;
    }

    /**
     * Gets the weight of the edge.
     *
     * @return The weight of the edge.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Gets the ID of the destination node.
     *
     * @return The ID of the destination node.
     */
    public Long getDestination() {
        return destination;
    }
}
