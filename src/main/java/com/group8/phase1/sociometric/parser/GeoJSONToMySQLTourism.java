package com.group8.phase1.sociometric.parser;

import com.group8.phase1.database.ConnectionGrabber;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Class to insert tourism data from a GeoJSON file into a MySQL database.
 */
public class GeoJSONToMySQLTourism {
    public static void insertTourismData() {
        Connection connection = ConnectionGrabber.getInstance().getConnectionJSON();
        String geoJsonFilePath = "./GeoJSON/tourism.geojson";
        try (InputStream is = new FileInputStream(geoJsonFilePath)) {

            JSONObject geoJson = new JSONObject(new JSONTokener(is));
            File geoJsonFile = new File(geoJsonFilePath);
            JSONArray features = geoJson.getJSONArray("features");

            if (!geoJsonFile.exists()) {
                System.err.println("Error: GeoJSON Tourism file not found at path: " + geoJsonFilePath);
                return;
            }


            String sql = "INSERT INTO tourism (tourism_type, name, lat, long) VALUES (?, ?, ?, ?)\n;";
            String sql2 = "INSERT INTO rtree_tourism (id, minX, maxX, minY, maxY) VALUES (last_insert_rowid(), ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            PreparedStatement statement1 = connection.prepareStatement(sql2);

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");

                String amenityName = properties.getString("tourism");
                String name;
                if(properties.has("name")){
                    name = properties.getString("name");
                }
                else { name = ""; }
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                double longitude = coordinates.getDouble(0);
                double latitude = coordinates.getDouble(1);

                statement.setString(1, amenityName);
                statement.setString(2, name);
                statement.setDouble(3, latitude);
                statement.setDouble(4,longitude);
                statement.executeUpdate();
                statement1.setDouble(1, latitude);
                statement1.setDouble(2, latitude);
                statement1.setDouble(3, longitude);
                statement1.setDouble(4, longitude);
                statement1.executeUpdate();
            }


            statement.close();
            connection.close();

            System.out.println("Data inserted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
