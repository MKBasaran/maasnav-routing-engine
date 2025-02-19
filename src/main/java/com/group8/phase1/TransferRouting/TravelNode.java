package com.group8.phase1.TransferRouting;

import com.group8.phase1.structures.map.Graph;
import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.database.functional.GetNearestNode;
import com.group8.phase1.osm.LoadRoutingGraph;
import com.group8.phase1.pathfinding.RoutingEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TravelNode {


    public List<Shape> shapes;
    private List<Shape> stops;
    public List<Long> walkingNodes;
    public String line;
    public String tripId;

    public String color;


    public TravelNode(String tripId, long fromStopId, long toStopId){
        this.tripId = tripId;
       // System.out.println("TRIP ID: " + tripId + " from: " + fromStopId + " to: " + toStopId);
        shapes = new ArrayList<>();
        String sql = """
                WITH current_trip AS (
                                                                   SELECT ? AS trip_id
                                                               ),
                                                               trip_shape_id AS (
                                                                   SELECT shape_id FROM Trips WHERE Trips.trip_id = (SELECT trip_id FROM current_trip)
                                                               ),
                                                               start_shape_pt AS (
                                                                   SELECT Shapes.shape_pt_sequence FROM Shapes WHERE Shapes.shape_id = (SELECT shape_id FROM trip_shape_id)
                                                                   ORDER BY ABS(Shapes.shape_pt_lat - (SELECT stop_lat FROM Stops WHERE Stops.stop_id = ?)) + ABS(Shapes.shape_pt_lon - (SELECT stop_lon FROM Stops WHERE Stops.stop_id = ?)) ASC
                                                                   LIMIT 1
                                                               ),
                                                               end_shape_pt AS (
                                                                   SELECT Shapes.shape_pt_sequence FROM Shapes WHERE Shapes.shape_id = (SELECT shape_id FROM trip_shape_id)
                                                                   ORDER BY ABS(Shapes.shape_pt_lat - (SELECT stop_lat FROM Stops WHERE Stops.stop_id = ?)) + ABS(Shapes.shape_pt_lon - (SELECT stop_lon FROM Stops WHERE Stops.stop_id = ?)) ASC
                                                                   LIMIT 1
                                                               )
                                                               SELECT shape_pt_sequence, shape_pt_lat, shape_pt_lon FROM Shapes
                                                               WHERE
                                                                   Shapes.shape_pt_sequence >= (SELECT shape_pt_sequence FROM start_shape_pt)
                                                                   AND Shapes.shape_pt_sequence <= (SELECT shape_pt_sequence FROM end_shape_pt)
                                                                   AND Shapes.shape_id = (SELECT shape_id FROM trip_shape_id)
                                                               ORDER BY Shapes.shape_pt_sequence ASC;
                                                               
                """;

        try (Connection conn = ConnectionGrabber.getInstance().getConnectionGTFS();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
          //  System.out.println(fromStopId);
           // System.out.println(toStopId);
           // System.out.println(tripId);
            pstmt.setString(1, tripId);
            pstmt.setLong(2, fromStopId);
            pstmt.setLong(3, fromStopId);
            pstmt.setLong(4,toStopId);
            pstmt.setLong(5,toStopId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                   // String routeShortName = rs.getString("route_short_name");
                    double shapePtLat = rs.getDouble("shape_pt_lat");
                    double shapePtLon = rs.getDouble("shape_pt_lon");
                    //this.line = routeShortName;
                    this.shapes.add(new Shape(shapePtLat,shapePtLon));

                }
            }
            sql = "SELECT route_short_name, route_color FROM Routes WHERE route_id=(SELECT route_id FROM Trips WHERE trip_id=?)";
            PreparedStatement pstmt2 = conn.prepareStatement(sql);
            pstmt2.setString(1,tripId);
            try(ResultSet rs = pstmt2.executeQuery()){
                while (rs.next()) {
                    line = rs.getString("route_short_name");
                    color = rs.getString("route_color");

                }
            }


        } catch (SQLException e) {
           System.out.println( e.getMessage());
        }


    }
    public TravelNode( double fromLat, double fromLon, double toLat, double toLon,long fromStopId,long toStopId) {
        this.tripId = "Walking";
        this.line = "Walking";
        this.color ="0058f8";
        Graph OSMGraph = LoadRoutingGraph.loadGraph();
        RoutingEngine routingEngine = new RoutingEngine(OSMGraph);

        if (fromStopId != -1 && toStopId != -1){
            walkingNodes = routingEngine.getShortestPath( GetNearestNode.get(fromLat,fromLon),  GetNearestNode.get(toLat,toLon)).getNodes();
        }
        else if(fromStopId==-1){
            walkingNodes = routingEngine.getShortestPath( GetNearestNode.get(fromLat,fromLon),GetNearestNode.get(toLat,toLon)).getNodes();
        }else {
            walkingNodes = routingEngine.getShortestPath(GetNearestNode.get(fromLat,fromLon),GetNearestNode.get(toLat,toLon)).getNodes();
        }
    }
}
