package com.group8.phase1.postalcodes;

import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.structures.map.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataParser {

    private final LoggerService loggerService = LoggerService.getInstance();
    public Map<String, Node> postalCodes;
    public Map<String, Node> stops;


    public DataParser() {
        this.postalCodes = new HashMap<>();
        this.stops = new HashMap<>();
        readPostalCodeData();
    }


    public void readPostalCodeData() {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(new File(Objects.requireNonNull(getClass().getResource("PostalCodeData.csv")).toURI())));
            String line = reader.readLine();

            while (line != null) {
                //dataParts = { postalCode, latitude, longitude }
                String[] dataParts = line.split(",");

                Node postalNode = new Node(dataParts[0], Float.parseFloat(dataParts[1]), Float.parseFloat(dataParts[2]));
                postalCodes.put(dataParts[0], postalNode);

                line = reader.readLine();
            }
            loggerService.info("Successfully read postal codes!");
            System.out.println("succesfull");
            reader.close();
        } catch (IOException | URISyntaxException e) {
            loggerService.error("Couldn't read postal codes! " + e.getMessage());
            System.out.println("nope");
        }
    }

}

