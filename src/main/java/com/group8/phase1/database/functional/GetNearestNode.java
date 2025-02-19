package com.group8.phase1.database.functional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group8.phase1.database.ConnectionGrabber;
import com.group8.phase1.logger.LoggerService;

public enum GetNearestNode {
    ;

    public static Long get(double targetLatitude, double targetLongitude) {
        LoggerService logger = LoggerService.getInstance();
        Connection conn = ConnectionGrabber.getInstance().getConnectionOSM();
        long id=-1;
        double latThreshold = 0.0001, lonThreshold = 0.0001;
        while(id==-1 && latThreshold<=0.0110 && lonThreshold<=0.0110) {

                String sql =
                        """
                                SELECT id
                                FROM graph_nodes_rtree
                                WHERE min_latitude >= ? - ? AND max_latitude <= ? + ?\s
                                AND min_longitude >= ? - ? AND max_longitude <= ? + ?\s
                                ORDER BY ABS((SELECT latitude FROM graph_nodes WHERE node_id = id LIMIT 1) - ?) + ABS((SELECT longitude FROM graph_nodes WHERE node_id = id LIMIT 1) - ?)
                                LIMIT 1;""";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setDouble(1, targetLatitude);
                    pstmt.setDouble(2, latThreshold);

                    pstmt.setDouble(3, targetLatitude);
                    pstmt.setDouble(4, latThreshold);


                    pstmt.setDouble(5, targetLongitude);
                    pstmt.setDouble(6, lonThreshold);

                    pstmt.setDouble(7, targetLongitude);
                    pstmt.setDouble(8, lonThreshold);

                    pstmt.setDouble(9, targetLatitude);
                    pstmt.setDouble(10, targetLongitude);



                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            id = rs.getLong("id");
                            logger.info("Found node with id: " + id + " near " + " latitude: " + targetLatitude + " and longitude: " + targetLongitude);
                            return id;
                        }
                    }catch (SQLException e){
                        logger.error("Couldn't execute query when trying to find closest node.");
                    }
                }catch (SQLException e){
                    logger.error("[GetNearestNode] Couldn't create prepared statement!");
                }
            latThreshold += 0.0001;
            lonThreshold += 0.0001;
        }
        logger.error("Couldn't find a nearest node at " + " latitude: " + targetLatitude + " and longitude: " + targetLongitude);
        return null;
    }
}
