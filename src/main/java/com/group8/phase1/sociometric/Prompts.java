package com.group8.phase1.sociometric;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Prompts {
    /**
     * Finds amenities within a specified radius from a given origin point.
     *
     * @param originX The x-coordinate of the origin point.
     * @param originY The y-coordinate of the origin point.
     * @param radius  The radius within which to search for amenities.
     * @return A SQL query string that retrieves amenities within the specified radius.
     */
    public static String findAmenities(double originX, double originY, double radius){
        return String.format("WITH point_buffer AS (\n" +
                        "\tSELECT %f AS originX, %f AS originY, %f AS radius\n" +
                        ")\n" +
                        "SELECT a.amenity_type, a.name , a.landmark_id, a.amenityScore, ra.minX, ra.minY FROM amenities a\n" +
                        "JOIN rtree_amenities ra ON a.landmark_id = ra.id \n" +
                        "JOIN point_buffer pb ON\n" +
                        "\t\tra.minX >= (pb.originX - pb.radius) AND ra.maxX <= (pb.originX + pb.radius)\n" +
                        "\t\tAND ra.minY >= (pb.originY - pb.radius) AND ra.maxY <= (pb.originY + pb.radius)\n" +
                        "WHERE\n" +
                        "    ((a.lat - pb.originX) * (a.lat - pb.originX)) + ((a.long - pb.originY) * (a.long - pb.originY)) <= (pb.radius * pb.radius);",
                originX, originY, radius);

    }

    /**
     * Finds tourism landmarks within a specified radius from a given origin point.
     *
     * @param originX The x-coordinate of the origin point.
     * @param originY The y-coordinate of the origin point.
     * @param radius The radius within which to search for tourism landmarks.
     * @return A SQL query string that retrieves tourism landmarks within the specified radius.
     */
    public static String findTourism(double originX, double originY, double radius){
        return String.format("WITH point_buffer AS (\n" +
                        "\tSELECT %f AS originX, %f AS originY, %f AS radius\n" +
                        ")\n" +
                        "SELECT t.tourism_type, t.name , t.landmark_id, t.tourismScore, rt.minX, rt.minY FROM tourism t\n" +
                        "JOIN rtree_tourism rt ON t.landmark_id = rt.id \n" +
                        "JOIN point_buffer pb ON\n" +
                        "\t\trt.minX >= (pb.originX - pb.radius) AND rt.maxX <= (pb.originX + pb.radius)\n" +
                        "\t\tAND rt.minY >= (pb.originY - pb.radius) AND rt.maxY <= (pb.originY + pb.radius)\n" +
                        "WHERE\n" +
                        "    ((t.lat - pb.originX) * (t.lat - pb.originX)) + ((t.long - pb.originY) * (t.long - pb.originY)) <= (pb.radius * pb.radius);",
                originX, originY, radius);

    }

    /**
     * Finds shops within a specified radius from a given origin point.
     *
     * @param originX The x-coordinate of the origin point.
     * @param originY The y-coordinate of the origin point.
     * @param radius  The radius within which to search for shops.
     * @return A SQL query string that retrieves the details of shops within the specified radius.
     */
    public static String findShops(double originX, double originY, double radius){
        return String.format("WITH point_buffer AS (\n" +
                        "\tSELECT %f AS originX, %f AS originY, %f AS radius\n" +
                        ")\n" +
                        "SELECT s.shop_type, s.name , s.landmark_id, s.shopScore, rs.minX, rs.minY FROM shops s\n" +
                        "JOIN rtree_shops rs ON s.landmark_id = rs.id \n" +
                        "JOIN point_buffer pb ON\n" +
                        "\t\trs.minX >= (pb.originX - pb.radius) AND rs.maxX <= (pb.originX + pb.radius)\n" +
                        "\t\tAND rs.minY >= (pb.originY - pb.radius) AND rs.maxY <= (pb.originY + pb.radius)\n" +
                        "WHERE\n" +
                        "    ((s.lat - pb.originX) * (s.lat - pb.originX)) + ((s.long - pb.originY) * (s.long - pb.originY)) <= (pb.radius * pb.radius);",
                originX, originY, radius);

    }

    /**
     * Finds the penalty for a given postal code.
     *
     * @param postalCode The postal code for which to find the penalty.
     * @return A SQL query string that retrieves the penalty for the specified postal code.
     */
    public static String findPenalty(String postalCode){
        return String.format("SELECT AgeCategory FROM CodeToAge WHERE postalCode = '%s'",postalCode);
    }

    /**
     * Retrieves the penalty for a given postal code.
     *
     * @param postCode The postal code for which to find the penalty.
     * @param connection The database connection.
     * @return The penalty for the specified postal code.
     */
    public static int getPenalty(String postCode, Connection connection){
        try {
            PreparedStatement statement = connection.prepareStatement(Prompts.findPenalty(postCode));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                return resultSet.getInt(1);
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return 0;
    }
}
