package com.group8.phase1.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import com.group8.phase1.ErrorUI.ErrorShower;
import com.group8.phase1.logger.LoggerService;

/**
 * This class implements rate limiting functionality to restrict the number of requests
 * a user can send within specific intervals.
 */
public class RateLimiter {
    private static File rateLimitFile;

    // Seconds, Minutes, Hours, Days in milliseconds
    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    private final int[] maxRequests;
    private final long[] intervals;
    private final long[][] timestamps;

    private boolean isTesting;

    LoggerService logger;


    /**
     * Constructs a RateLimiter with the specified maximum request limits.
     *
     * @param maxRequests The number of requests a user can send respecting the intervals: { 5 seconds, 1 minute, 1 hour, 1 day }
     */
    public RateLimiter(int[] maxRequests, boolean testing) {
        try {
            rateLimitFile = Paths.get(Objects.requireNonNull(getClass().getResource("rateLimitData.txt")).toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        isTesting = testing;
        logger = LoggerService.getInstance();
        this.maxRequests = maxRequests;
        this.intervals = new long[]{SECOND * 5, MINUTE, HOUR, DAY};
        this.timestamps = new long[maxRequests.length][];
        loadRateLimitData();
    }

    /**
     * Checks if a request is allowed based on the rate limits.
     *
     * @return true if the request is allowed, false otherwise.
     */
    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < maxRequests.length; i++) {
            long intervalDuration = intervals[i];
            long[] timestampArray = timestamps[i];
            int currentRequestLimit = maxRequests[i];

            if (timestampArray == null || timestampArray.length != currentRequestLimit) {
                timestamps[i] = new long[currentRequestLimit];
                timestampArray = timestamps[i];
            }
            // Remove past timestamps
            for (int j = 0; j < timestampArray.length; j++) {
                if (now - timestampArray[j] > intervalDuration) {
                    timestampArray[j] = 0;
                }
            }
            // Count valid timestamps
            int count = (int) Arrays.stream(timestampArray).filter(timestamp -> timestamp > 0).count();
            if (count >= currentRequestLimit) {
                logger.warn("Request was blocked by the rate limiter -> Too many requests!");
                if(!isTesting)
                    new ErrorShower("Too many requests, try again later!");
                return false;
            }
            for (int j = 0; j < timestampArray.length; j++) {
                if (timestampArray[j] == 0) {
                    timestampArray[j] = now;
                    break;
                }
            }
        }
        saveRateLimitData();
        logger.info("Request was let through by the rate limiter!");
        return true;
    }

    /**
     * Loads rate limiter data from the storage file.
     */
    private void loadRateLimitData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(rateLimitFile))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null && lineNumber < timestamps.length) {
                String[] parts = line.split(",");

                timestamps[lineNumber] = new long[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    timestamps[lineNumber][i] = Long.parseLong(parts[i]);
                }
                lineNumber++;
            }
            logger.info("Successfully read rate limiter data");
        } catch (IOException e) {
            logger.error("Couldn't load rate limiter data.");
            logger.error(e.getMessage());
        }
    }

    /**
     * Saves rate limiter data to the storage file.
     */
    private void saveRateLimitData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rateLimitFile))) {
            for (long[] timestampArray : timestamps) {
                if (timestampArray != null) {
                    StringBuilder sb = new StringBuilder();
                    for (long timestamp : timestampArray) {
                        if (timestamp != 0) {
                            sb.append(timestamp).append(",");
                        }
                    }
                    sb.setLength(sb.length() - 1);
                    writer.write(sb.toString());
                    writer.newLine();
                    logger.info("Wrote new rate limit data to file.");
                }
            }
        } catch (IOException e) {
            logger.error("Couldn't write rate limiter data to file!");
            logger.error(e.getMessage());
        }
    }
}
