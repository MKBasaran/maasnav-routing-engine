package com.group8.phase1.sociometric.calculation;

import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.sociometric.Prompts;
import com.group8.phase1.sociometric.calculation.objects.AmenityNode;
import com.group8.phase1.sociometric.calculation.objects.ShopNode;
import com.group8.phase1.structures.map.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ShopCalculator {
    private final Node postalCode;
    private Set<Integer> currentCollection;
    private final int[] radiusRanges = {100, 200, 300, 400};
    private final double[] rangeWeights = {0.4, 0.3, 0.2, 0.1};
    private ArrayList<ShopNode> shopNodes = new ArrayList<>();
    private static final double METERS_PER_DEGREE = 111320.0;
    private double globalScore;
    private Connection connection;

    public ShopCalculator(Node postalCode){
        currentCollection = new HashSet<>();
        connection = ConnectionGrabber.getInstance().getConnectionJSON();
        this.postalCode = postalCode;
        for (int i = 0; i < radiusRanges.length; i++) {
            globalScore = globalScore + returnShops(postalCode,radiusRanges[i], i);
            System.out.println("started");
        }
        globalScore = Math.round(globalScore);

    }

    /**
     * Returns the score of the shops within the given distance from the specified postal code,
     * and updates the shopNodes list with the corresponding shops.
     *
     * @param postalCode The Node object representing the postal code location.
     * @param distance   The distance from the postal code in meters.
     * @param number     The index of the rangeWeights array to use for calculating the score.
     * @return The total score of the shops within the specified distance.
     */
    public double returnShops(Node postalCode, int distance, int number){
        double score = 0.0;
        try {

            String sql = Prompts.findShops(postalCode.getLatitude(), postalCode.getLongitude(), distance/METERS_PER_DEGREE);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                if(currentCollection.add(resultSet.getInt(3))){
                    score = score + getScore(resultSet.getDouble(4), rangeWeights[number]);
                    System.out.println(distance + " " + resultSet.getString(2));
                    shopNodes.add(new ShopNode(resultSet.getInt(3), resultSet.getDouble(5), resultSet.getDouble(6)));
                }
            }
            return score;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Calculates the score based on the given score and weight.
     *
     * @param score The current score.
     * @param weight The weight.
     * @return The updated score.
     */
    public double getScore(double score,double weight) {
        int penalty = Prompts.getPenalty(postalCode.getPostalCode(), connection);
        if (score == 0) {
            score += penalty * weight;
        }
        else{
            score += score - (penalty * weight);
        }
        System.out.println("Shop Score :" + score);
        return score;
    }

    /**
     * Retrieves the shop score for a given FeatureCalculator object.
     *
     * @return The shop score.
     */
    public double getShopScore(){
        return globalScore;
    }

    /**
     * Retrieves the list of ShopNode objects.
     *
     * @return The ArrayList of ShopNode objects representing the shops.
     */
    public ArrayList<ShopNode> getShopNodes() {
        return shopNodes;
    }
}
