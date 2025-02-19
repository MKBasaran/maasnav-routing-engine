package com.group8.phase1.sociometric.calculation;

import com.group8.phase1.ErrorUI.ErrorShower;
import com.group8.phase1.api.WebInteraction;
import com.group8.phase1.initialdataloader.RoutingDataManager;
import com.group8.phase1.postalcodes.DataParser;
import com.group8.phase1.sociometric.calculation.objects.AmenityNode;
import com.group8.phase1.sociometric.calculation.objects.ShopNode;
import com.group8.phase1.sociometric.calculation.objects.TourismNode;
import com.group8.phase1.sociometric.calculation.objects.TransportNode;
import com.group8.phase1.structures.map.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class FeatureCalculator {
    private static final double METERS_PER_DEGREE = 111320.0;
    private final double totalScore;
    private final ArrayList<TourismNode> tourismNodes;
    private final ArrayList<ShopNode> shopNodes;
    private final ArrayList<AmenityNode> amenityNodes;
    private final double amenityScore;
    private final double shopScore;
    private final double tourismScore;
    private final double transportScore;

    private final HashMap<Integer, TransportNode> stops;
    private final HashMap<Integer,Integer> routeToStops;

    public FeatureCalculator(String postalCode) {
        double start = System.nanoTime();
        RoutingDataManager.postalCodeParser = new DataParser();
        Node originPostalCode = getPostalNode(postalCode);
        AmenityCalculator amenityCalculator = new AmenityCalculator(originPostalCode);
        TourismCalculator tourismCalculator = new TourismCalculator(originPostalCode);
        ShopCalculator shopCalculator = new ShopCalculator(originPostalCode);
        TransportCalculator transportCalculator = new TransportCalculator(postalCode);

        amenityScore = amenityCalculator.getAmenityScore();
        shopScore = shopCalculator.getShopScore();
        tourismScore = tourismCalculator.getTourismScore();
        transportScore = transportCalculator.getScore();
        totalScore = amenityScore + shopScore + tourismScore + transportScore;
        routeToStops = transportCalculator.getRouteToStops();
        tourismNodes = tourismCalculator.getTourismNodes();
        shopNodes = shopCalculator.getShopNodes();
        amenityNodes = amenityCalculator.getAmenityNodes();
        stops = transportCalculator.getStops();
        double end = System.nanoTime();
        System.out.println("Done in time: " + (end-start));
    }

    /**
     * Responsible for retrieving the Node of the postal code
     * @param startPostalCode The string text of the postal code
     * @return return the postal code node
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
     * Retrieve total score
     * @return double score
     */

    public double getTotalScore() {
        return totalScore;
    }

    /**
     * List of the tourism nodes
     * @return Arraylist storing type of TourismNode
     */
    public ArrayList<TourismNode> getTourismNodes() {
        return tourismNodes;
    }

    /**
     * List of the shop nodes
     * @return Arraylist of type ShopNode
     */
    public ArrayList<ShopNode> getShopNodes() {
        return shopNodes;
    }

    /**
     * Retrieves the list of AmenityNodes.
     *
     * @return The ArrayList containing AmenityNode objects.
     */
    public ArrayList<AmenityNode> getAmenityNodes() {
        return amenityNodes;
    }

    /**
     * Retrieves the amenity score.
     *
     * @return The amenity score.
     */
    public double getAmenityScore() {
        return amenityScore;
    }

    /**
     * Retrieves the shop score for a given FeatureCalculator object.
     *
     * @return The shop score.
     */
    public double getShopScore() {
        return shopScore;
    }

    /**
     * Retrieves the tourism score.
     *
     * @return The tourism score.
     */
    public double getTourismScore() {
        return tourismScore;
    }

    /**
     * Retrieves the transport score.
     *
     * @return The transport score.
     */
    public double getTransportScore() {
        return transportScore;
    }

    /**
     * Retrieves the stops associated with the FeatureCalculator.
     *
     * @return A HashMap containing the stops as TransportNode objects. The key is the stop ID.
     */
    public HashMap<Integer, TransportNode> getStops() {
        return stops;
    }

    /**
     * Retrieves the route to stops associated with the FeatureCalculator.
     *
     * @return A HashMap containing the routes to stops as key-value pairs,
     *         where the key is the stop ID and the value is the route ID.
     */
    public HashMap<Integer, Integer> getRouteToStops() {
        return routeToStops;
    }
}
