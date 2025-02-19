package com.group8.phase1.sociometric.calculation;

import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.sociometric.Prompts;
import com.group8.phase1.sociometric.calculation.objects.AmenityNode;
import com.group8.phase1.structures.map.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AmenityCalculator {
    private Set<Integer> currentCollection;
    private final int[] radiusRanges = {100, 200, 300, 400};
    private final double[] rangeWeights = {0.4, 0.3, 0.2, 0.1};
    private ArrayList<AmenityNode> amenityNodes = new ArrayList<>();

    private static final double METERS_PER_DEGREE = 111320.0;
    private double globalScore;
    private Node postalCode;
    private Connection connection;

    public AmenityCalculator(Node postalCode){
        currentCollection = new HashSet<>();
        this.postalCode = postalCode;
        connection = ConnectionGrabber.getInstance().getConnectionJSON();
        for (int i = 0; i < radiusRanges.length; i++) {
            globalScore = globalScore + returnAmenities(postalCode,radiusRanges[i], i);
            System.out.println("started");
        }
        globalScore = Math.round(globalScore);
    }

    /**
     * Calculates the score for amenities based on the given postal code, distance, and number.
     *
     * @param postalCode The postal code node.
     * @param distance   The distance in meters.
     * @param number     The range number.
     * @return The score for amenities.
     * @throws RuntimeException If a SQL error occurs.
     */
    public double returnAmenities(Node postalCode, int distance, int number){
        double score = 0.0;
        try {
            String sql = Prompts.findAmenities(postalCode.getLatitude(), postalCode.getLongitude(), distance/METERS_PER_DEGREE);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                if(currentCollection.add(resultSet.getInt(3))){
                    score = score + getScore(resultSet.getDouble(4), rangeWeights[number]);
                    System.out.println(distance + " " + resultSet.getString(2));
                    amenityNodes.add(new AmenityNode(resultSet.getInt(3), resultSet.getDouble(5), resultSet.getDouble(6)));
                }
            }
            return score;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates the final score for amenities based on the given score and weight.
     *
     * @param score The initial score.
     * @param weight The weight of the score.
     * @return The final score for amenities.
     */
    public double getScore(double score,double weight) {
        int penalty = Prompts.getPenalty(postalCode.getPostalCode(), connection);
        if (score == 0) {
            score += penalty * weight;
        }
        else{
            score += score - (penalty * weight);
        }
        System.out.println("Amenity Score :" + score);
        return score;
    }

    /**
     * Retrieves the amenity score for the AmenityCalculator object.
     *
     * @return The amenity score.
     */
    public double getAmenityScore(){
        return globalScore;
    }

    /**
     * Retrieves the list of AmenityNodes.
     *
     * @return The ArrayList containing AmenityNode objects.
     */
    public ArrayList<AmenityNode> getAmenityNodes() {
        return amenityNodes;
    }
}