package com.group8.phase1.pathfinding;

import java.util.List;

public class Path {

    private final List<Long> nodes;
    private final double distance;

    /**
     * Constructs a new Path object with the specified list of node IDs and total distance.
     *
     * @param nodes    the list of node IDs that make up the path
     * @param distance the total distance of the path
     */
    public Path(List<Long> nodes, double distance) {
        this.nodes = nodes;
        this.distance = distance;
    }

    /**
     * Returns the list of node IDs that make up the path.
     *
     * @return the list of node IDs
     */
    public List<Long> getNodes() {
        return this.nodes;
    }

    /**
     * Returns the total distance of the path.
     *
     * @return the total distance
     */
    public double getDistance() {
        return this.distance;
    }
}
