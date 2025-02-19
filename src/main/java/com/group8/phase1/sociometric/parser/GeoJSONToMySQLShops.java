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

public enum GeoJSONToMySQLShops {;

    /**
     * Inserts shop data into the database from a GeoJSON file.
     */
    public static void insertShopsData() {
        Connection connection = ConnectionGrabber.getInstance().getConnectionJSON();

        String geoJsonFilePath = "./GeoJSON/shop.geojson";
        File geoJsonFile = new File(geoJsonFilePath);

        if (!geoJsonFile.exists()) {
            System.err.println("Error: GeoJSON Shop file not found at path: " + geoJsonFilePath);
            return;
        }

        try (InputStream is = new FileInputStream(geoJsonFilePath)) {

            JSONObject geoJson = new JSONObject(new JSONTokener(is));
            JSONArray features = geoJson.getJSONArray("features");




            String sql = "INSERT INTO shops (shop_type, name, lat, long) VALUES (?, ?, ?, ?);\n";
            String sql2 = "INSERT INTO rtree_shops (id, minX, maxX, minY, maxY) VALUES (last_insert_rowid(), ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            PreparedStatement statement1 = connection.prepareStatement(sql2);

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");

                String amenityName = properties.getString("shop");
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
