package com.group8.phase1.sociometric.calculation.objects;

public class TransportNode {
    private int stop_id;
    private double stop_lat;
    private double stop_lon;

    public int getStop_id() {
        return stop_id;
    }

    public double getStop_lat() {
        return stop_lat;
    }

    public double getStop_lon() {
        return stop_lon;
    }

    public TransportNode(int stop_id, double stop_lat, double stop_lon){
        this.stop_id = stop_id;
        this.stop_lat = stop_lat;
        this.stop_lon = stop_lon;
    }
}
