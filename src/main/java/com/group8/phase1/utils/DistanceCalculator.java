package com.group8.phase1.utils;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import com.group8.phase1.structures.map.Node;

/**
 * The {@code DistanceCalculator} enum provides methods to calculate the distance between 2 {@link Node} objects.
 */
public enum DistanceCalculator {
    ;

    /**
     * Calculates the distance between two {@link Node} objects in meters using the Haversine formula.
     *
     * @param  source  the source {@link Node} representing the starting point
     * @param  destination the destination {@link Node} representing the end point
     * @return {@link Double} containing the distance between the source and destination nodes in meters
     */
    public static double calculateInMeters(Node source, Node destination) {
        double sourceLongitude = source.getLongitude();
        double sourceLatitude = source.getLatitude();
        double destinationLongitude = destination.getLongitude();
        double destinationLatitude = destination.getLatitude();
        return acos((sin(toRadians(sourceLatitude)) * sin(toRadians(destinationLatitude))) + (cos(toRadians(sourceLatitude)) * cos(toRadians(destinationLatitude))) * (cos(toRadians(destinationLongitude) - toRadians(sourceLongitude)))) * 6371 * 1000;
    }

}
