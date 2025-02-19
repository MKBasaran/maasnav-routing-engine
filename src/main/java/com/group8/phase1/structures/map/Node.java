package com.group8.phase1.structures.map;

/**
 * Represents a geographical node.
 */
public class Node {
    private double longitude;
    private double latitude;
    private String postalCode;
    private long id;

    /**
     * Constructs a Node with the given postal code, latitude, and longitude.
     *
     * @param postalCode The postal code of the node.
     * @param latitude   The latitude coordinate of the node.
     * @param longitude  The longitude coordinate of the node.
     */
    public Node(String postalCode, double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.postalCode = postalCode;
    }

    /**
     * Constructs a Node with the given ID, latitude, and longitude.
     *
     * @param id        The ID of the node.
     * @param latitude  The latitude coordinate of the node.
     * @param longitude The longitude coordinate of the node.
     */
    public Node(long id, double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
    }

    /**
     * Gets the ID of the node.
     *
     * @return The ID of the node.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets the longitude coordinate of the node.
     *
     * @return The longitude coordinate of the node.
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Sets the longitude coordinate of the node.
     *
     * @param longitude The longitude coordinate to set.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the latitude coordinate of the node.
     *
     * @return The latitude coordinate of the node.
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Sets the latitude coordinate of the node.
     *
     * @param latitude The latitude coordinate to set.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the postal code of the node.
     *
     * @return The postal code of the node.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Returns a string representation of the node.
     *
     * @return A string representation of the node.
     */
    public String toString() {
        return postalCode + "; " + latitude + "; " + longitude;
    }
}
