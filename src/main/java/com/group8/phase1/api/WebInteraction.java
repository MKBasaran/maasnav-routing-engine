package com.group8.phase1.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.group8.phase1.ErrorUI.ErrorShower;
import com.group8.phase1.logger.LoggerService;
import com.group8.phase1.structures.map.Node;

/**
 * The {@code WebInteraction} enum provides a method to interact with the Maastricht API in order to get
 * geographic coordinates for a given postal code.
 */
public enum WebInteraction {
    ;

    private static final String API_URL = "https://project12.ashish.nl/get_coordinates";

    /**
     * Retrieves the coordinates (latitude and longitude) for a given postal code by
     * making a POST request to the Maastricht University API.
     *
     * @param postalCode the postal code for which to retrieve the geographic coordinates
     * @return a {@link Node} containing the postal code and its corresponding latitude and longitude,
     * or {@code null} if the coordinates could not be retrieved
     */
    public synchronized static Node retrievePostalCodeData(String postalCode) {
        LoggerService logger = LoggerService.getInstance();
        try {
            URL obj = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (con != null) {
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setDoOutput(true);
                con.setDoInput(true);
                OutputStream os = con.getOutputStream();

                String jsonString = "{\n" +
                        "    \"postcode\": \"" + postalCode + "\"\n" +
                        "}";
                os.write(jsonString.getBytes());
                os.flush();

                int responseCode = con.getResponseCode();
                if (responseCode == 200 || responseCode== 202) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;

                    int lineCount = 0;
                    float latitude = 0, longitude = 0;
                    while ((inputLine = in.readLine()) != null) {
                        if (lineCount == 1)
                            latitude = Float.parseFloat(inputLine.split(": ")[1].replaceAll("\"", "").replace(",", ""));
                        else if (lineCount == 2)
                            longitude = Float.parseFloat(inputLine.split(": ")[1].replaceAll("\"", "").replace(",", ""));
                        lineCount++;
                    }
                    in.close();
                    logger.info("Retrieved postal code: " + postalCode + " with lat:" + latitude + " and lon:" + longitude);
                    return new Node(postalCode, latitude, longitude);
                } else {
                    logger.warn("Couldn't retrieve any node for postal code: " + postalCode);
                    return null;
                }
            }
        } catch (IOException e) {
            logger.error("Connection couldn't be created to " + API_URL);
            new ErrorShower("You are probably not connected to a UM network or UM VPN");
        }
        logger.warn("Web interaction is not responding");
        return null;
    }
}
