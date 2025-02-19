package com.group8.phase1.TransferRouting;

import com.group8.phase1.database.ConnectionGrabber;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class ImportGTFSData {
    public static void main(String[] args) {
        GTFSGraph graph = constructGraph();
        for(Vertex vertex : graph.getVertices()){
            for(Edge edge: vertex.getEdges()){
                System.out.println(vertex.getName() + " -> " + edge.getDestination().getName()+ " "+edge.getWeight() + " -> Next Departure: " + edge.getDepartureTime());
            }
        }
    }

    public static GTFSGraph constructGraph() {
        GTFSGraph graph = new GTFSGraph();
        try {
            Connection conn = ConnectionGrabber.getInstance().getConnectionGTFS();
            if (conn != null) {
                System.out.println("Connected to the database");

                // Load stops
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT stop_id, stop_name, stop_lat, stop_lon FROM Stops");
                while (rs.next()) {
                    String stopId = rs.getString("stop_id");
                    String stopName = rs.getString("stop_name");
                    double lat = rs.getDouble("stop_lat");
                    double lon = rs.getDouble("stop_lon");
                    graph.addVertex(stopId, stopName, lat, lon);
                }

                LocalDate currentDate = LocalDate.now();


                // Define the formatter for the output format
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                String formattedDate = currentDate.format(outputFormatter);

                // Create a PreparedStatement
                String query = """
                        SELECT st.trip_id, st.arrival_time, st.departure_time, st.stop_id, st.stop_sequence FROM StopTimes st JOIN Trips t ON st.trip_id = t.trip_id JOIN CalendarDates cd ON t.service_id = cd.service_id
                                                                       WHERE cd.date = strftime('%Y%m%d', 'now') AND st.departure_time>TIME() AND st.departure_time<time(TIME(),"+4 hours")
                                                                       ORDER BY st.trip_id ASC""";
                PreparedStatement pstmt = conn.prepareStatement(query);
              // pstmt.setString(1, formattedDate);

                // Execute the query
                rs = pstmt.executeQuery();

                String lastTripId = null;
                String lastStopId = null;
                String lastDeparture = null;
                while (rs.next()) {
                    String stopId = rs.getString("stop_id");
                    String tripId = rs.getString("trip_id");
                    String arrivalTime = rs.getString("arrival_time");
                    String departureTime = rs.getString("departure_time");

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date arrivalTimeDate = sdf.parse(arrivalTime);
                    Date departureTimeDate = null;

                    if (lastDeparture != null) {
                        departureTimeDate = sdf.parse(lastDeparture);
                    }

                    if (lastTripId != null && !Objects.equals(tripId, lastTripId)) {
                        lastStopId = null;
                        lastDeparture = null;
                    }
                    if (lastStopId != null && lastDeparture != null) {
                        long timeDifference = arrivalTimeDate.getTime() - departureTimeDate.getTime();
                        graph.addEdge(lastStopId,tripId, stopId,departureTime, (int) timeDifference/1000);
                       // System.out.println(tripId);
                    }


                    lastTripId = tripId;
                    lastStopId = stopId;
                    lastDeparture = departureTime;
                }



                // Load stops
                 stmt = conn.createStatement();
                 rs = stmt.executeQuery("SELECT from_stop, to_stop, time_taken FROM PrecomputedWalkingEdges");
                while (rs.next()) {
                    String fromStop = rs.getString("from_stop");
                    String toStop = rs.getString("to_stop");
                    double timeTaken = rs.getDouble("time_taken");
                    graph.addEdge(fromStop,"walking",toStop,"00:00:00", (int) timeTaken);
                    graph.addEdge(toStop,"walking",fromStop,"00:00:00", (int) timeTaken);
                }
            }
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
        return graph;
    }
}
