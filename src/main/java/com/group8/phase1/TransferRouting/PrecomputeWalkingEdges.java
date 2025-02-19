package com.group8.phase1.TransferRouting;

import com.group8.phase1.structures.map.Graph;
import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.database.functional.GetNearestNode;
import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.osm.LoadRoutingGraph;
import com.group8.phase1.pathfinding.Path;
import com.group8.phase1.pathfinding.RoutingEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrecomputeWalkingEdges {

    static RoutingEngine routingEngine;

    public static void main(String[] args) {
        precompute();
    }
    public static void precompute(){
        Graph OSMGraph = LoadRoutingGraph.loadGraph();
        routingEngine = new RoutingEngine(OSMGraph);
        List<Stop> stops = new ArrayList<>();

        Statement stmt = null;
        Connection connection = null;
        ResultSet rs = null;
        try{
             connection = ConnectionGrabber.getInstance().getConnectionGTFS();

            // Load stops
            stmt = connection.createStatement();
             rs = stmt.executeQuery("SELECT stop_id, stop_lat, stop_lon FROM Stops");
            while (rs.next()) {
                String stopId = rs.getString("stop_id");

                double lat = rs.getDouble("stop_lat");
                double lon = rs.getDouble("stop_lon");
                stops.add(new Stop(stopId,lat,lon));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(rs!=null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        int index = 0;
        try(Connection connectiongtfs = ConnectionGrabber.getInstance().getConnectionGTFS()){
        List<Edge> edges = new ArrayList<>();
        for(int i=315;i<stops.size();i++){
            Stop stop = stops.get(i);
           for(int j=i+1; j<stops.size();j++) {
               Stop stop2 = stops.get(j);
               if (stop != stop2 && haversine(stop.lat, stop.lon, stop2.lat, stop2.lon) < 1.5) {
                   double distance = getOSMDistance(stop.lat, stop.lon, stop2.lat, stop2.lon);
                   if(distance > 10000)
                       distance = 10000;
                   edges.add(new Edge(stop.stopId,stop2.stopId,distance/ 5 * 3600));

               }
               if(edges.size()>10){
                   insert(edges,connectiongtfs);
                   edges.clear();
               }
           }
           System.out.println("Processed stop: "+ index + " out of " + stops.size());
           index++;
        }

        insert(edges,connection);
        edges.clear();


    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void insert(List<Edge> batchEdges, Connection connection) {
        LoggerService loggerService = LoggerService.getInstance();
        String sql = "INSERT INTO PrecomputedWalkingEdges(from_stop,to_stop,time_taken) VALUES(?,?,?)";
        PreparedStatement pstmt = null;
        try  {
            pstmt = connection.prepareStatement(sql);
            for (Edge edge : batchEdges) {
                pstmt.setString(1, edge.fromId);
                pstmt.setString(2, edge.toId);
                pstmt.setDouble(3, edge.time_taken);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
           // loggerService.info("Successfully inserted another batch of edges into Precomputed table!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
          //  loggerService.error("Couldn't insert  " + e.getMessage());
        }finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static double getOSMDistance(double startLat, double startLon, double endLat, double endLon) {
        //TODO: Get nearest nodes to the lat's and lon's
        Long nearestToStart = GetNearestNode.get(startLat, startLon);
        Long nearestToTarget = GetNearestNode.get(endLat, endLon);

        Path path = routingEngine.getShortestPath(nearestToStart, nearestToTarget);
        //TODO: Return distance of path
//        System.out.println(path.getDistance()/1000);
//        System.out.println(path.getDistance()/1000/5*3600);
        double distance = path.getDistance()/1000;
        path = null;
        return distance;
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

   public static class Edge{
        public String fromId,toId;
        public double time_taken;

        public Edge(String fromId,String toId, double time_taken ){
            this.fromId = fromId;
            this.toId = toId;
            this.time_taken = time_taken;
        }
    }

    static class Stop{
        public String stopId;
        public double lat,lon;
        public Stop(String stopId, double lat, double lon){
            this.stopId = stopId;
            this.lat = lat;
            this.lon = lon;
        }
    }

}
