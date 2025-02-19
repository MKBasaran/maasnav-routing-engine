package com.group8.phase1.TransferRouting;

import java.util.*;

public class Vertex {
    private final String id;
    private String name;
    private double lat;
    private double lon;
    private String trip_id;
    private long stop_id;

    public double fromLat=-1,fromLon=-1;
    public String fromStop;

    private final List<Edge> edges;

    public Vertex(String id,String name,double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.edges = new LinkedList<>();

        this.trip_id = null;
    }

    public void setFromCoordinates(double lat, double lon){
        this.fromLat = lat;
        this.fromLon = lon;
    }
    public void setFromStop(String stopid){
        this.fromStop = stopid;
    }

    public String getId() {
        return id;
    }
    public String getTrip_id(){
        return this.trip_id;
    }
    public void setTrip_id(String tripId){
        this.trip_id = tripId;
    }

    public long getStop_id(){
        return this.stop_id;
    }
    public void setStop_id(long stop_id){
        this.stop_id = stop_id;
    }

    public List<Edge> getEdges() {
        return edges;
    }
    public String getName(){
        return this.name;
    }
    public double getLat(){
        return this.lat;
    }
    public double getLon(){
        return this.lon;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
}
