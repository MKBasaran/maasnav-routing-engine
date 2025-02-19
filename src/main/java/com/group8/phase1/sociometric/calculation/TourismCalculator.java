package com.group8.phase1.sociometric.calculation;

import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.sociometric.Prompts;
import com.group8.phase1.sociometric.calculation.objects.TourismNode;
import com.group8.phase1.structures.map.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TourismCalculator {
    private static final double METERS_PER_DEGREE = 111320.0;
    private final int[] radiusRanges = {100, 200, 300, 400};
    private final double[] rangeWeights = {0.4, 0.3, 0.2, 0.1};
    private final Set<Integer> currentCollection;
    private final ArrayList<TourismNode> tourismNodes = new ArrayList<>();
    private final Node postalCode;
    private double globalScore;
    private Connection connection;

    /**
     * Calculates the tourism score for a given postal code.
     *
     * @param postalCode The postal code for which to calculate the tourism score.
     */
    public TourismCalculator(Node postalCode) {
        currentCollection = new HashSet<>();
        connection = ConnectionGrabber.getInstance().getConnectionJSON();
        this.postalCode = postalCode;
        for (int i = 0; i < radiusRanges.length; i++) {
            globalScore = globalScore + returnTourism(postalCode, radiusRanges[i], i);
            System.out.println("started");
        }
        globalScore = Math.round(globalScore);
    }

    /**
     * Calculates the tourism score for a given postal code and parameters.
     *
     * @param postalCode The postal code for which to calculate the tourism score.
     * @param distance   The maximum distance in meters from the postal code to consider for calculating the tourism score.
     * @param number     The weight index used for calculating the tourism score.
     * @return The tourism score calculated for the given postal code.
     */
    public double returnTourism(Node postalCode, int distance, int number) {
        double score = 0.0;
        try {
            String sql = Prompts.findTourism(postalCode.getLatitude(), postalCode.getLongitude(), distance / METERS_PER_DEGREE);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (currentCollection.add(resultSet.getInt(3))) {
                    score = score + getScore(resultSet.getDouble(4), rangeWeights[number]);
                    System.out.println(distance + " " + resultSet.getString(2));
                    tourismNodes.add(new TourismNode(resultSet.getInt(3), resultSet.getDouble(5), resultSet.getDouble(6)));
                }
            }
            return score;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Calculates the shop score based on the given score and weight.
     *
     * @param score The score to calculate the shop score for.
     * @param weight The weight to apply to the score.
     * @return The shop score calculated based on the given score and weight.
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
     * Returns the tourism score for the given object.
     *
     * @return The tourism score.
     */
    public double getTourismScore() {
        return globalScore;
    }

    /**
     * Retrieves the list of tourism nodes.
     *
     * @return An ArrayList containing the TourismNode objects.
     */
    public ArrayList<TourismNode> getTourismNodes() {
        return tourismNodes;
    }
}
