package com.group8.phase1.sociometric.parser;

import com.group8.phase1.database.ConnectionGrabber;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ScoreAndMultiplier{;

    private static List<InsertStatement> scoreStatements;
    private static List<InsertStatement> multiplierStatements;

    /**
     * Inserts data into the database tables based on the provided CSV files.
     */
    public static void performInsert() {
        String amenitiesFilePath = "./GeoJSON/amenities.csv"; // Replace with your file path
        String shopsFilePath = "./GeoJSON/shops.csv";         // Replace with your file path
        String tourismFilePath = "./GeoJSON/tourism.csv";
        Connection connection = ConnectionGrabber.getInstance().getConnectionJSON();

        Map<String, Integer> amenityPoints = new HashMap<>();
        amenityPoints.put("Education", 5);
        amenityPoints.put("Health", 5);
        amenityPoints.put("Social Services", 4);
        amenityPoints.put("Utilities and Public Services", 4);
        amenityPoints.put("Transport and Infrastructure", 3);
        amenityPoints.put("Recreation and Culture", 3);
        amenityPoints.put("Food and Drink", 2);
        amenityPoints.put("Hygiene and Sanitation", 1);
        amenityPoints.put("Miscellaneous/Irrelevant", 1);

        Map<String, Integer> shopPoints = new HashMap<>();
        shopPoints.put("Essential Retail", 5);
        shopPoints.put("Specialty Goods", 3);
        shopPoints.put("Fashion and Apparel", 2);
        shopPoints.put("Home and Garden", 2);
        shopPoints.put("Electronics and Appliances", 2);
        shopPoints.put("Health and Beauty", 2);
        shopPoints.put("Hobbies and Leisure", 1);

        Map<String, Integer> tourismPoints = new HashMap<>();
        tourismPoints.put("Accommodation", 5);
        tourismPoints.put("Attractions", 4);
        tourismPoints.put("Information and Services", 3);

        scoreStatements = new ArrayList<>();
        multiplierStatements = new ArrayList<>();

        // Read and process the amenities file
        readAndProcessFile(amenitiesFilePath, "amenities", amenityPoints);

        // Read and process the shops file
        readAndProcessFile(shopsFilePath, "shops", shopPoints);

        // Read and process the tourism file
        readAndProcessFile(tourismFilePath, "tourism", tourismPoints);

        System.out.println("-- MULTIPLIER TABLE INSERT STATEMENTS --");
        for (InsertStatement statement : multiplierStatements) {
            System.out.println(statement.toSQL("multiplier"));
            try {
                Statement statement1 = connection.createStatement();
                statement1.executeUpdate(statement.toSQL("multiplier"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        // Print the SQL statements
        System.out.println("-- SCORE TABLE INSERT STATEMENTS --");
        for (InsertStatement statement : scoreStatements) {
            System.out.println(statement.toSQL("score"));
            try {
                Statement statement1 = connection.createStatement();
                statement1.executeUpdate(statement.toSQL("score"));
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Reads and processes a file, generating SQL statements based on the contents of the file.
     *
     * @param filePath   the path to the file to be read
     * @param category   the category of the data in the file
     * @param pointsMap  a mapping of types to their corresponding points
     */
    private static void readAndProcessFile(String filePath, String category, Map<String, Integer> pointsMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(":");
                if (parts.length != 2) continue;

                String superType = parts[0].trim();
                String[] types = parts[1].split(",");

                int points = pointsMap.getOrDefault(superType, 0);
                multiplierStatements.add(new InsertStatement(superType, points));
                // Generate SQL statements for each type
                for (String type : types) {
                    type = type.trim();
                    int score = pointsMap.getOrDefault(type,0);
                    scoreStatements.add(new InsertStatement(type, superType, points));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A class that represents an insert statement.
     */
    static class InsertStatement {
        private String type;
        private String superType;
        private int score;
        private int bonus;

        /**
         * Represents an insert statement.
         */
        public InsertStatement(String type, String superType, int score) {
            this.type = type;
            this.superType = superType;
            this.score = score;
        }

        // Constructor for multiplier table
        public InsertStatement(String superType, int bonus) {
            this.superType = superType;
            this.bonus = bonus;
        }

        /**
         * Converts an insert statement to SQL format based on the given table name.
         *
         * @param tableName the name of the table to insert into
         * @return the SQL string representing the insert statement
         */
        public String toSQL(String tableName) {
            if (tableName.equals("score")) {
                return String.format("INSERT INTO %s (type, superType, score) VALUES ('%s', '%s', %d);", tableName, type, superType, score);
            } else {
                return String.format("INSERT INTO %s (superType, bonus) VALUES ('%s', %d);", tableName, superType, bonus);
            }
        }
    }
}
