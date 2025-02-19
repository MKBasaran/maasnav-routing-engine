package com.group8.phase1.initialdataloader;

import com.group8.phase1.api.RateLimiter;
import com.group8.phase1.postalcodes.DataParser;
import com.group8.phase1.structures.map.Graph;
import com.group8.phase1.structures.map.Node;

import java.util.Map;

/**
 * The {@code RoutingDataManager} enum serves as a container for managing routing data during the lifecycle.
 */
public enum RoutingDataManager {
    ;

    /**
     * The routing graph containing nodes and edges.
     */
    public static Graph routingGraph;

    /**
     * A map of node IDs to node objects.
     */
    public static Map<Long, Node> nodesMap;

    /**
     * Parser for postal code data.
     */
    public static DataParser postalCodeParser;

    /**
     * Rate limiter for API requests.
     */
    public static RateLimiter apiRateLimiter;
}
