package com.group8.phase1.TransferRouting;

import java.util.*;

public class GTFSGraph {
    private final Map<String, Vertex> vertices;

    public GTFSGraph() {
        this.vertices = new HashMap<>();
    }

    public Vertex getVertex(String id) {
        return vertices.get(id);
    }

    public void addVertex(String id,String name, Double lat, Double lon) {
        vertices.putIfAbsent(id, new Vertex(id,name,lat,lon));
    }

    public void addEdge(String sourceId,String tripId, String destinationId,String departureTime, int weight) {
        Vertex source = vertices.get(sourceId);
        Vertex destination = vertices.get(destinationId);

        if (source == null || destination == null) {
            throw new IllegalArgumentException("Vertex not found");
        }

        source.addEdge(new Edge(destination,tripId,departureTime, weight));
    }

    public List<Vertex> getVertices() {
        return new ArrayList<>(vertices.values());
    }


}
