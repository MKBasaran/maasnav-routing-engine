package com.group8.phase1.sociometric.calculation.objects;

public class ShopNode {
    private int landmark_id;
    private double lat;
    private double lon;
    public ShopNode(int landmark_id, double lat, double lon){
        this.landmark_id = landmark_id;
        this.lat = lat;
        this.lon = lon;
    }

    public int getLandmark_id() {
        return landmark_id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
