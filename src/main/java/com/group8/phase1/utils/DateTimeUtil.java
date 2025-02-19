package com.group8.phase1.utils;


import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public enum DateTimeUtil {
    ;

    private static String timezoneID = "Europe/Amsterdam"; // Default timezone for our app

    // Method to get the current date in "YYYYMMDD" format
    public static String getCurrentDate() {
        ZoneId zone = ZoneId.of(timezoneID);
        ZonedDateTime currentDate = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDate.format(formatter);
    }

    // Method to get the current date and time in "YYYY-MM-DD HH:MM:SS" format
    public static String getCurrentTime() {
        ZoneId zone = ZoneId.of(timezoneID);
        ZonedDateTime currentTime = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    public static void setTimezone(String timezone) {
        timezoneID = timezone;
    }

    public static String correctDataTime(String timeString) {
        int hours = Integer.parseInt(timeString.substring(0, 2));
        if (hours >= 24) {
            timeString = "0" + (hours - 24) + timeString.substring(2);
        }
        return timeString;
    }

    public static int calculateDifference(String startTime, String endTime) {

        startTime = correctDataTime(startTime);
        endTime = correctDataTime(endTime);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        Duration duration = Duration.between(start, end);

        long minutes = duration.toMinutes();
        return (int) minutes;
    }

}
