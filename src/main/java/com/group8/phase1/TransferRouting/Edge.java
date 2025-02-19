package com.group8.phase1.TransferRouting;

public class Edge {
    private final Vertex destination;
    private String departureTime;
    private String tripId;
    private final int weight;

    public Edge(Vertex destination,String tripId,String departureTime, int weight) {
        this.destination = destination;
        this.tripId = tripId;
        this.departureTime = departureTime;
        this.weight = weight;

    }
    public String getDepartureTime(){
        return this.departureTime;
    }

    public String getTripId(){
        return this.tripId;
    }

    public Vertex getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}
