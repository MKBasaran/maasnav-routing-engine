package com.group8.phase1.TransferRouting;


import com.group8.phase1.structures.map.Graph;
import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.database.functional.GetNearestNode;
import com.group8.phase1.osm.LoadRoutingGraph;
import com.group8.phase1.pathfinding.Path;
import com.group8.phase1.pathfinding.RoutingEngine;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Dijkstra {
    private static Graph OSMGraph;
    private static RoutingEngine routingEngine;
    private static final double TRIP_CHANGE_PENALTY = 5000; // 10 minutes penalty for switching trips
    private static final double WALKING_SPEED = 5.0 / 3600; // 5 km/h in km/min



    static List<TravelNode> formatPath(List<Vertex> path, double[] startCoordinates, double[] endCoordinates) {
        List<TravelNode> formattedPath = new ArrayList<>();
        if (path == null || path.isEmpty()) {
            return formattedPath;
        }


        Vertex start = path.get(0);
        String currentTripId = start.getTrip_id();
        if (currentTripId == null) {
            currentTripId = "walking";
            start.setTrip_id("walking");
        }
        Vertex segmentStart = start;

        formattedPath.add(new TravelNode(startCoordinates[0], startCoordinates[1], start.getLat(), start.getLon(), Long.parseLong(start.getId()), -1));

        for (int i = 1; i < path.size(); i++) {
            Vertex current = path.get(i);
            if (Objects.equals(current.getTrip_id(), "walking") || current.getTrip_id() == null) {
                System.out.println("Added Walking Edge: " + current.fromLat + " " + current.fromLon + " to " + current.getLat() + " " + current.getLon());
                formattedPath.add(new TravelNode(path.get(i - 1).getLat(), path.get(i - 1).getLon(), current.getLat(), current.getLon(), -1, -1));
            } else {

                formattedPath.add(new TravelNode(current.getTrip_id(), Long.parseLong(path.get(i - 1).getId()), Long.parseLong(current.getId())));
                System.out.println("Added bus edge: " + current.getTrip_id() + " " + current.getLat() + " ");
            }
        }


        Vertex end = path.get(path.size() - 1);
        if (Objects.equals(currentTripId, "walking"))
            formattedPath.add(new TravelNode(end.getLat(), end.getLon(), endCoordinates[0], endCoordinates[1], -1, Long.parseLong(end.getId())));
        else
            formattedPath.add(new TravelNode(currentTripId, Long.parseLong(segmentStart.getId()), Long.parseLong(path.get(path.size() - 1).getId())));

        return formattedPath;
    }


    /**
     * Function to run Dijsktras with necessary helper functions
     * @param startCoordinates 
     * @param endCoordinates
     * @param departureTime departure time String in hh:mm:ss format
     * @return TravelResults object containing the fastest found trip
     */
    public static TravelResults run(double[] startCoordinates, double[] endCoordinates, String departureTime) {
        OSMGraph = LoadRoutingGraph.loadGraph();
        routingEngine = new RoutingEngine(OSMGraph);

        GTFSGraph graph = ImportGTFSData.constructGraph();


        List<Vertex> nearestNodesToStart = getNearestStops(graph, startCoordinates[0], startCoordinates[1], 1000);
        List<Vertex> nearestNodesToDestination = getNearestStops(graph, endCoordinates[0], endCoordinates[1], 1000);


        Result bestResult = new Result(null, Long.MAX_VALUE);

        for (Vertex startStop : nearestNodesToStart) {
            int initialPenalty = (int) (haversine(startStop.getLat(), startStop.getLon(), startCoordinates[0], startCoordinates[1]) / WALKING_SPEED);

            for (Vertex endStop : nearestNodesToDestination) {
                int endPenalty = (int) (haversine(endStop.getLat(), endStop.getLon(), endCoordinates[0], endCoordinates[1]) / WALKING_SPEED);
                Result result = dijkstraSearch(graph, startStop, endStop, departureTime + initialPenalty, initialPenalty, endPenalty);


                assert result != null;
                if (bestResult.totalTime > result.totalTime) {
                    bestResult = result;

                }

            }
        }

        boolean isWalkRoute = true;
        for(Vertex vertex : bestResult.getPath()){
            if (vertex.getTrip_id() != null && !vertex.getTrip_id().equals("walking")) {
                isWalkRoute = false;
                break;
            }
        }
        if(isWalkRoute){
            return  run(startCoordinates,endCoordinates,departureTime);
        }



        System.out.println("Total time taken: " + bestResult.getTotalTime() + " seconds");

        return new TravelResults(formatPath(bestResult.getPath(), startCoordinates, endCoordinates), bestResult.getTotalTime());
    }

    /**
     * Helper function to find a set of nearest stops at given coordinates
     * @param graph
     * @param lat
     * @param lon
     * @param distance search radius in meters
     * @return List of graph vertices coorelating to the found stops
     */
    public static List<Vertex> getNearestStops(GTFSGraph graph, double lat, double lon, double distance) {
        List<Vertex> nearestNodes = new ArrayList<>();

        // SQL query
        String query = "SELECT stop_id " +
                "FROM (SELECT stop_id, (6371 * acos(cos(radians(?)) * cos(radians(stop_lat)) * cos(radians(stop_lon) - radians(?)) + sin(radians(?)) * sin(radians(stop_lat)))) AS distance " +
                "      FROM Stops ORDER BY distance ASC) AS derived_table " +
                "WHERE distance < ?" +
                "LIMIT 15";

        try {
            Connection connection = ConnectionGrabber.getInstance().getConnectionGTFS();
            PreparedStatement pstmt = connection.prepareStatement(query);
            // Set the input parameters for the query
            pstmt.setDouble(1, lat);
            pstmt.setDouble(2, lon);
            pstmt.setDouble(3, lat);
            pstmt.setDouble(4, distance);

            // Execute the query and get the result set
            ResultSet rs = pstmt.executeQuery();

            // Process the result set
            while (rs.next()) {
                nearestNodes.add(graph.getVertex(String.valueOf(rs.getInt("stop_id"))));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nearestNodes;
    }

    public static Result dijkstraSearch(GTFSGraph graph, Vertex start, Vertex goal, String initialDepartureTime, int initialPenalty, int endPenalty) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getG));
        Map<Vertex, Node> allNodes = new HashMap<>();


        long initialTime = parseTime(initialDepartureTime);
        Node startNode = new Node(start, null, 0, (initialTime + initialPenalty * 1000L), null);
        allNodes.put(start, startNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            Vertex currentVertex = currentNode.getVertex();

            if (currentVertex.equals(goal)) {
                currentNode.setTime(currentNode.getTime() + endPenalty * 1000L);
                List<Vertex> path = reconstructPath(currentNode);
                long totalTime = (currentNode.getTime() - initialTime) / 1000;
                return new Result(path, totalTime);
            }


            for (Edge edge : currentVertex.getEdges()) {
                Vertex neighbor = edge.getDestination();

                long edgeDepartureTime = parseTime(edge.getDepartureTime());
                if (edgeDepartureTime <= 0 || edgeDepartureTime > currentNode.getTime()) {
                    long waitTime = Math.max(0, edgeDepartureTime - currentNode.getTime());

                    double tentativeG = getTentativeG(edge, currentNode, waitTime);
                    long newTime = currentNode.getTime() + edge.getWeight() * 1000L;

                    Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor));
                    if (tentativeG < neighborNode.getG()) {
                        neighborNode.setPrevious(currentNode);
                        neighborNode.setG(tentativeG);
                        neighborNode.setTime(newTime);
                        neighborNode.setTimeInSeconds((int) ((newTime - currentNode.getTime()) / 1000));
                        neighborNode.setTripId(edge.getTripId());
                        neighborNode.setDepartureTime(edgeDepartureTime);
                        allNodes.put(neighbor, neighborNode);

                        if (!openSet.contains(neighborNode)) {
                            openSet.add(neighborNode);
                        }
                    }
                }
            }
        }


        return null;
    }

    private static double getTentativeG(Edge edge, Node currentNode, long waitTime) {
        double tentativeG = currentNode.getG() + edge.getWeight() + waitTime / 1000.0;


        return tentativeG;
    }

    public static double getOSMDistance(double startLat, double startLon, double endLat, double endLon) {
        //TODO: Get nearest nodes to the lat's and lon's
        Long nearestToStart = GetNearestNode.get(startLat, startLon);
        Long nearestToTarget = GetNearestNode.get(endLat, endLon);

        Path path = routingEngine.getShortestPath(nearestToStart, nearestToTarget);
        //TODO: Return distance of path
        return path.getDistance() / 1000;
    }


    private static List<Vertex> reconstructPath(Node node) {
        List<Vertex> path = new ArrayList<>();
        while (node != null) {
            node.getVertex().setTrip_id(node.getTripId());
            Node previous = node.getPrevious();
            path.add(node.getVertex());
            System.out.println(node.getTripId() + " " + node.getTime() + " departure: " + node.getDepartureTime() + node.getVertex().getLat() + " " + node.getVertex().getLon() + " cost in seconds: " + node.getTimeInSeconds());
            node = previous;
        }
        Collections.reverse(path);
        return path;
    }

    private static long parseTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static class Node {
        private Vertex vertex;
        private Node previous;
        private double g = Double.MAX_VALUE;
        private long time;
        private String tripId;
        private long departureTime;
        private int timeInSeconds;

        public Node(Vertex vertex) {
            this(vertex, null, Double.POSITIVE_INFINITY, 0, null);
        }

        public Node(Vertex vertex, Node previous, double g, long time, String tripId) {
            this.vertex = vertex;
            this.previous = previous;
            this.g = g;
            this.time = time;
            this.tripId = tripId;
        }

        public long getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(long departureTime) {
            this.departureTime = departureTime;
        }

        public void setTimeInSeconds(int timeInSeconds) {
            this.timeInSeconds = timeInSeconds;
        }

        public int getTimeInSeconds() {
            return this.timeInSeconds;
        }

        public Vertex getVertex() {
            return vertex;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        public double getG() {
            return g;
        }

        public void setG(double g) {
            this.g = g;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getTripId() {
            return tripId;
        }

        public void setTripId(String tripId) {
            this.tripId = tripId;
        }
    }

    private static class Result {
        private List<Vertex> path;
        private long totalTime;

        public Result(List<Vertex> path, long totalTime) {
            this.path = path;
            this.totalTime = totalTime;
        }

        public List<Vertex> getPath() {
            return path;
        }

        public long getTotalTime() {
            return totalTime;
        }
    }

}
