package com.group8.phase1.structures.map;


public class Way {
    private final long source;
    private final long destination;
    private final long wayId;
    private final double weight;

    /**
     * Constructs a Way with the given source, destination, way ID, and weight.
     *
     * @param source      The ID of the source node.
     * @param destination The ID of the destination node.
     * @param wayId       The ID of the way.
     * @param weight      The weight of the way.
     */
    public Way(long source, long destination, long wayId, double weight) {
        this.source = source;
        this.destination = destination;
        this.wayId = wayId;
        this.weight = weight;
    }

    /**
     * Gets the ID of the source node.
     *
     * @return The ID of the source node.
     */
    public long getSource() {
        return this.source;
    }

    /**
     * Gets the ID of the destination node.
     *
     * @return The ID of the destination node.
     */
    public long getDestination() {
        return this.destination;
    }

    /**
     * Gets the ID of the way.
     *
     * @return The ID of the way.
     */
    public long getWayId() {
        return this.wayId;
    }

    /**
     * Gets the weight of the way.
     *
     * @return The weight of the way.
     */
    public double getWeight() {
        return this.weight;
    }


}
