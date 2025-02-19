package com.group8.phase1.sociometric.calculation;

import com.group8.phase1.ErrorUI.ErrorShower;
import com.group8.phase1.api.WebInteraction;
import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.initialdataloader.RoutingDataManager;
import com.group8.phase1.postalcodes.DataParser;
import com.group8.phase1.sociometric.Prompts;
import com.group8.phase1.sociometric.Queries;
import com.group8.phase1.sociometric.calculation.objects.TransportNode;
import com.group8.phase1.structures.map.Node;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TransportCalculator {
    private String postCode;
    private HashMap<Integer, TransportNode> stops;
    private Set<Integer> routeIds;
    private Connection connection;
    private Connection connectionGTFS;
    private HashMap<Integer,Integer> routeToStops;
    private final int[] radiusRanges = {100, 200, 300, 400};
    private final double[] rangeWeights = {0.4, 0.3, 0.2, 0.1}; // Added weights for radius ranges
    private final int NO_OF_BUS_STOPS = 23;
    private double score;
    private int SCALING_FACTOR = 1000;

    /**
     * Initializes a TransportCalculator object with the given postal code.
     *
     * @param postCode The postal code used for calculations.
     */
    public TransportCalculator(String postCode){
        connection = ConnectionGrabber.getInstance().getConnectionJSON();
        connectionGTFS = ConnectionGrabber.getInstance().getConnectionGTFS();
        routeIds = new HashSet<>();
        this.postCode = postCode;
        stops = new HashMap<>();
        routeToStops = new HashMap<>();
        for (int i = 0; i < radiusRanges.length; i++) {
            score = score + handleStops(radiusRanges[i], i);
        }
        score *= SCALING_FACTOR;
        System.out.println("Transport Score final: " + score);
        score = Math.round(score);
    }

    /**
     * Retrieves the stops associated with the TransportCalculator object.
     *
     * @return A HashMap containing the stops as TransportNode objects. The key is the stop ID.
     */
    public HashMap<Integer, TransportNode> getStops() {
        return stops;
    }

    /**
     * Handles the stops calculation based on the given distance and number.
     *
     * @param distance The distance used for stop calculation.
     * @param number The number of stops used for weight calculation.
     * @return The score calculated based on the stops and weights.
     * @throws RuntimeException if a SQLException occurs while accessing the database.
     */
    private double handleStops(int distance, int number) {
        double score = 0;
        try {
            PreparedStatement statement = connectionGTFS.prepareStatement(Queries.GET_COUNT_NEAREST_STOPS.getQuery());
            RoutingDataManager.postalCodeParser = new DataParser();
            Node originPostalCode = getPostalNode(postCode);
            statement.setDouble(1,originPostalCode.getLatitude());
            statement.setDouble(2, originPostalCode.getLongitude());
            statement.setDouble(3, originPostalCode.getLatitude());
            statement.setDouble(4, distance);
            statement.setDouble(5, originPostalCode.getLatitude());
            statement.setDouble(6, originPostalCode.getLongitude());
            statement.setDouble(7, originPostalCode.getLatitude());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int stop_id = resultSet.getInt(1);
                Double stopLat = resultSet.getDouble(2);
                Double stopLon = resultSet.getDouble(3);
                TransportNode node = new TransportNode(stop_id, stopLat, stopLon);
                stops.put(stop_id, node);
                int count = fillBusLines(stop_id);
                score += (count / (double)NO_OF_BUS_STOPS) * rangeWeights[number]; // Apply weight
            }
            return score;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the number of bus lines associated with a specific stop ID.
     *
     * @param stop_id The ID of the stop.
     * @return The number of bus lines associated with the stop ID.
     */
    private int fillBusLines(int stop_id){
        try{
            PreparedStatement statement = connectionGTFS.prepareStatement(Queries.GET_BUS_ROUTE_BY_STOP_ID.getQuery());
            statement.setInt(1, stop_id);
            ResultSet resultSet = statement.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                if(routeIds.add(resultSet.getInt(1))){
                    routeToStops.put(resultSet.getInt(1),stop_id);
                    count++;
                }
            }
            return count;
        }catch (SQLException e){
            System.out.println(e);
        }
        return 0;
    }

    /**
     * Retrieves the Node object associated with the given postal code.
     *
     * @param startPostalCode The postal code for which to retrieve the Node object.
     * @return The Node object associated with the given postal code. If the postal code is not found in the postal code parser,
     *         the method will make an API request to retrieve the postal code data and return the corresponding Node object.
     *         If the postal code does not exist in Maastricht, an ErrorShower object will be created and an error message
     *         will be displayed.
     */
    public Node getPostalNode(String startPostalCode) {
        // 50.84800467,5.70166964
        Node startPostalNode = RoutingDataManager.postalCodeParser.postalCodes.get(startPostalCode);

        if (startPostalNode == null && RoutingDataManager.apiRateLimiter.allowRequest()) {
            startPostalNode = WebInteraction.retrievePostalCodeData(startPostalCode);
            if (startPostalNode == null) {
                new ErrorShower("The following postal code does not exist in Maastricht: " + startPostalCode);
            }
        }
        return startPostalNode;
    }

    /**
     * Retrieves the score for the transport component.
     *
     * @return The score calculated based on the transport component.
     */
    public double getScore() {
        int penalty = Prompts.getPenalty(postCode, connection);
        double finalScore = score - penalty;
        System.out.println("Transport Score: " + finalScore);
        return finalScore < 0 ? 0 : finalScore;
    }

    /**
     * Retrieves the route to stops associated with the TransportCalculator object.
     *
     * @return A HashMap containing the routes to stops as key-value pairs,
     *         where the key is the stop ID and the value is the route ID.
     */
    public HashMap<Integer, Integer> getRouteToStops() {
        return routeToStops;
    }
}
