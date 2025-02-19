package com.group8.phase1.sociometric;

public enum Queries {
    
    // ------- Closest Start/End stop queries ----

    DELETE_FROM_START_STOPS(
        "DELETE FROM StartStops;"
    ),
    CREATE_TEMP_START_STOP_TABLE(
        "CREATE TEMP TABLE IF NOT EXISTS StartStops (" +
        "   stop_id TEXT " +
        ");"
    ),
    CREATE_START_STOP_IDX(
        "CREATE INDEX IF NOT EXISTS idx_stop_id_start_stops " +
        "ON StartStops(stop_id);"
    ),
    // 1st position - Search position latitude
    // 2nd position - Search position longitude
    // 3rd position - Limit on number of stops stored
    POPULATE_NEAREST_START_STOPS(
        "INSERT INTO StartStops(stop_id) " +
        "SELECT stop_id " +
        "   FROM (SELECT stop_id " +
        "         FROM Stops " +
        "         ORDER BY abs(Stops.stop_lat - ?) + abs(Stops.stop_lon - ?) ASC " +
        "         LIMIT ?)"
    ),
    DELETE_FROM_END_STOPS(
        "DELETE FROM EndStops;"
    ),
    CREATE_TEMP_END_STOP_TABLE(
        "CREATE TEMP TABLE IF NOT EXISTS EndStops (" +
        "   stop_id TEXT " +
        ");"
    ),
    CREATE_END_STOP_IDX(
        "CREATE INDEX IF NOT EXISTS idx_stop_id_end_stops " +
        "ON EndStops(stop_id);"
    ),
    // 1 position - Limit on number of stops stored
    POPULATE_NEAREST_END_STOPS(
        "INSERT INTO EndStops(stop_id) " +
        "SELECT stop_id " +
        "   FROM (SELECT stop_id " +
        "         FROM Stops " +
        "         ORDER BY abs(Stops.stop_lat - ?) + abs(Stops.stop_lon - ?) ASC " +
        "         LIMIT ?)"
    ),
    GET_START_STOPS(
        "SELECT * FROM Stops, StartStops " +
        "WHERE Stops.stop_id = StartStops.stop_id;"
    ),
    GET_END_STOPS(
        "SELECT * FROM Stops, EndStops " +
        "WHERE Stops.stop_id = StartStops.stop_id;"
    ),

    // --------------------------------

    // ------ Queries for finding all possible trips ----------
    DELETE_FROM_MATCHING_TRIPS(
        "DELETE FROM MatchingTrips;"
    ),
    CREATE_TEMP_MATCHING_TRIP_TABLE(
        "CREATE TEMP TABLE IF NOT EXISTS MatchingTrips ( " +
        "   trip_id TEXT, " +
        "   route_id TEXT, " +
        "   service_id TEXT, " +
        "   trip_headsign TEXT, " +
        "   trip_short_name TEXT, " +
        "   trip_long_name TEXT, " +
        "   direction_id INTEGER, " +
        "   block_id TEXT, " +
        "   shape_id TEXT, " +
        "   wheelchair_accessible INTEGER, " +
        "   bikes_allowed INTEGER, " +
        "   start_stop_id STRING, " +
        "   end_stop_id STRING, " +
        "   start_stop_departure_time STRING," +
        "   start_stop_sequence INTEGER, " +
        "   end_stop_arrival_time STRING," +
        "   total_travel_time INTEGER " +
        ");"
    ),
    CREATE_MATCHING_TRIP_IDS_IDX(
        "CREATE INDEX IF NOT EXISTS idx_matching_trips_ids " +
        "ON MatchingTrips(trip_id, direction_id,);"
    ),
    CREATE_MATCHING_TRIP_TIMES_IDX(
        "CREATE INDEX IF NOT EXISTS idx_matching_trips_times " +
        "ON MatchingTrips(start_stop_departure_time);"
    ),
    FIND_ALL_MATCHING_TRIPS(
        "WITH start_stop AS ( " +
        "    SELECT trip_id, StartStops.stop_id AS start_stop_id, startTimes.departure_time AS start_stop_departure_time, startTimes.stop_sequence AS stop_sequence " +
        "    FROM StopTimes AS startTimes " +
        "    JOIN StartStops ON startTimes.stop_id = StartStops.stop_id " +
        "    ORDER BY " +
        "       startTimes.stop_sequence ASC " +
        "), " +
        "end_stop AS ( " +
        "    SELECT trip_id, EndStops.stop_id AS end_stop_id, endTimes.arrival_time AS end_stop_arrival_time " +
        "    FROM StopTimes AS endTimes " +
        "    JOIN EndStops ON endTimes.stop_id = EndStops.stop_id " +
        "    ORDER BY " +
        "       endTimes.stop_sequence ASC " +
        ") " +
        "INSERT INTO MatchingTrips (trip_id, route_id, service_id, trip_headsign, trip_short_name, trip_long_name, direction_id, block_id, shape_id, wheelchair_accessible, bikes_allowed, start_stop_id, end_stop_id, start_stop_departure_time, start_stop_sequence, end_stop_arrival_time) " +
        "SELECT DISTINCT " +
        "    Trips.trip_id, " +
        "    Trips.route_id, " +
        "    Trips.service_id, " +
        "    Trips.trip_headsign, " +
        "    Trips.trip_short_name, " +
        "    Trips.trip_long_name, " +
        "    Trips.direction_id, " +
        "    Trips.block_id, " +
        "    Trips.shape_id, " +
        "    Trips.wheelchair_accessible, " +
        "    Trips.bikes_allowed, " +
        "    start_stop.start_stop_id, " +
        "    end_stop.end_stop_id, " +
        "    start_stop.start_stop_departure_time, " +
        "    start_stop.stop_sequence, " +
        "    end_stop.end_stop_arrival_time " +
        "FROM Trips " +
        "JOIN start_stop ON Trips.trip_id = start_stop.trip_id " +
        "JOIN end_stop ON Trips.trip_id = end_stop.trip_id " +
        "WHERE start_stop.start_stop_id IS NOT NULL " +
        "AND end_stop.end_stop_id IS NOT NULL " +
        "AND start_stop.start_stop_departure_time < end_stop.end_stop_arrival_time " +
        "GROUP BY Trips.trip_id " +
        //"ORDER BY start_stop.stop_sequence ASC, " +
        "ORDER BY start_stop_departure_time ASC " + 
        "LIMIT 900;"
    ),
    
    // ----------------------------------------------------------------

    // ------ Queries for filtering the trips on certain criteria ----------

    FILTER_BIKES_NOT_ALLOWED(
        "DELETE FROM MatchingTrips " +
        "WHERE MatchingTrips.bikes_allowed = 0;"
    ),
    FILTER_NOT_WHEELCHAIR_ACCESSIBLE(
        "DELETE FROM MatchingTrips " +
        "WHERE MatchingTrips.wheelchair_accessible = 0;"
    ),
    // 1 position - date for which to filter
    FILTER_NOT_RUNNING(
        "DELETE FROM MatchingTrips " +
        "WHERE MatchingTrips.service_id NOT IN ( "+
        "   SELECT service_id FROM CalendarDates " +
        "   WHERE date = ? AND exception_type = 1 " +
        ")"
    ),
    // 1 position - the time for which to remove past trips
    FILTER_EARLY_DEPARTURES(
        "DELETE FROM MatchingTrips " +
        "WHERE start_stop_departure_time < ?;"
    ),
    
    // -------------------------------------------------------------------------

    // -------- Queries for calculating travel time for each remaining trip --------------------------------

    GET_MATCHING_TRIPS_ALL(
        "SELECT * FROM MatchingTrips;"
    ),
    // 1st position - trip ID
    // 2nd position - Start stop ID
    // 3rd position - End stop ID
    SELECT_TIMES(
        "WITH trip AS ( " +
        "   SELECT ? AS trip_id" +
        "), " +
        "start_stop_sequence AS ( " +
        "   SELECT stop_sequence FROM StopTimes WHERE trip_id = (SELECT trip_id FROM trip) AND stop_id = ?" +
        "), " +
        "end_stop_sequence AS ( " +
        "   SELECT stop_sequence FROM StopTimes WHERE trip_id = (SELECT trip_id FROM trip) AND stop_id = ?" +
        ") " +
        "SELECT departure_time, arrival_time " +
        "FROM StopTimes " +
        "WHERE trip_id = (SELECT trip_id FROM trip)" +
        "  AND stop_sequence >= (SELECT stop_sequence FROM start_stop_sequence) " +
        "  AND stop_sequence <= (SELECT stop_sequence FROM end_stop_sequence) " +
        "ORDER BY stop_sequence ASC;"
    ),
    INSERT_TRAVEL_TIME(
        "UPDATE MatchingTrips " +
        "SET total_travel_time = ? " +
        "WHERE trip_id = ? AND start_stop_id = ?;"
    ),

    // ----------------------------------------------------------------

    GET_POSSIBLE_TRIP_COUNT(
        "SELECT COUNT(*) FROM MatchingTrips;"
    ),

    // ----------------------------------------------------------------

    // ------ Queries for populating travel advice object --------------------------------

    // 1st position - limit on rows returned
    GET_MATCHING_TRIPS_ORDERED(
        "SELECT DISTINCT * FROM MatchingTrips " +
        "ORDER BY start_stop_departure_time ASC " +
        "LIMIT ?;"
    ),
    // 1st position - trip ID
    // 2nd position - Start stop ID
    // 3rd position - Start stop ID
    // 4th position - End stop ID
    // 5th position - End stop ID
    GET_SHAPES_FOR_TRIP(
        "WITH current_trip AS ( " +
        "    SELECT ? AS trip_id" +
        "), " +
        "trip_shape_id AS ( " +
        "   SELECT shape_id FROM Trips WHERE Trips.trip_id = (SELECT trip_id FROM current_trip) " +
        "), " +
        "start_shape_pt AS ( " +
        "   SELECT Shapes.shape_pt_sequence FROM Shapes WHERE Shapes.shape_id = (SELECT shape_id FROM trip_shape_id) " +
        "   ORDER BY ABS(Shapes.shape_pt_lat - (SELECT stop_lat FROM Stops WHERE Stops.stop_id = ?)) + ABS(Shapes.shape_pt_lon - (SELECT stop_lon FROM Stops WHERE Stops.stop_id = ?)) ASC " +
        "   LIMIT 1" +
        "), " +
        "end_shape_pt AS ( " +
        "   SELECT Shapes.shape_pt_sequence FROM Shapes WHERE Shapes.shape_id = (SELECT shape_id FROM trip_shape_id) " +
        "   ORDER BY ABS(Shapes.shape_pt_lat - (SELECT stop_lat FROM Stops WHERE Stops.stop_id = ?)) + ABS(Shapes.shape_pt_lon - (SELECT stop_lon FROM Stops WHERE Stops.stop_id = ?)) ASC " +
        "   LIMIT 1" +
        ") " +
        "SELECT shape_pt_sequence, shape_pt_lat, shape_pt_lon FROM Shapes " +
        "WHERE " +
        "   Shapes.shape_pt_sequence >= (SELECT shape_pt_sequence FROM start_shape_pt) " +
        "   AND Shapes.shape_pt_sequence <= (SELECT shape_pt_sequence FROM end_shape_pt) " +
        "   AND Shapes.shape_id = (SELECT shape_id FROM trip_shape_id) " +
        "ORDER BY Shapes.shape_pt_sequence ASC;"
    ),
    GET_STOPS_FOR_TRIP(
        "WITH current_trip_id AS ( " +
        "    SELECT ? AS trip_id " +
        "), " +
        "start_stop_sq AS ( " +
        "    SELECT MAX(StopTimes.stop_sequence) AS stop_sequence FROM StopTimes WHERE StopTimes.trip_id = (SELECT trip_id FROM current_trip_id) AND StopTimes.stop_id = ? " +
        "), " +
        "end_stop_sq AS ( " +
        "    SELECT MIN(StopTimes.stop_sequence) AS stop_sequence FROM StopTimes WHERE StopTimes.trip_id = (SELECT trip_id FROM current_trip_id) AND StopTimes.stop_id = ?" +
        ") " +
        "SELECT DISTINCT Stops.*, (SELECT stop_sequence FROM end_stop_sq) - (SELECT stop_sequence FROM start_stop_sq) AS stop_count FROM StopTimes AS StopTimes1 " +
        "INNER JOIN StopTimes AS StopTimes2 ON StopTimes1.trip_id = StopTimes2.trip_id " +
        "INNER JOIN Stops ON StopTimes1.stop_id = Stops.stop_id " +
        "WHERE StopTimes1.trip_id = (SELECT trip_id FROM current_trip_id) " +
        "AND StopTimes1.stop_sequence >= (SELECT stop_sequence FROM start_stop_sq) " +
        "AND StopTimes1.stop_sequence <= (SELECT stop_sequence FROM end_stop_sq) " +
        "ORDER BY StopTimes1.stop_sequence ASC;"
    ),
    GET_ROUTE_FOR_TRIP(
        "SELECT * FROM Routes " +
        "WHERE Routes.route_id = (SELECT Trips.route_id FROM Trips WHERE Trips.trip_id = ?) "
    ),
    GET_COUNT_NEAREST_STOPS(
                    "SELECT stop_id ,stop_lat, stop_lon " +
                    "FROM (SELECT stop_id ,stop_lat, stop_lon " +
                    "      FROM Stops " +
                    "      WHERE (6371000 * acos(cos(radians(?)) * cos(radians(stop_lat)) * cos(radians(stop_lon) - radians(?)) + sin(radians(?)) * sin(radians(stop_lat)))) <= ? " +
                    "      ORDER BY (6371000 * acos(cos(radians(?)) * cos(radians(stop_lat)) * cos(radians(stop_lon) - radians(?)) + sin(radians(?)) * sin(radians(stop_lat)))) ASC);"
    ),
    GET_BUS_ROUTE_BY_STOP_ID(
            "WITH RouteInfo AS ( " +
                    "    SELECT DISTINCT Trips.route_id " +
                    "    FROM StopTimes " +
                    "    JOIN Trips ON StopTimes.trip_id = Trips.trip_id " +
                    "    WHERE StopTimes.stop_id = ? " +
                    "), " +
                    "TripStops AS ( " +
                    "    SELECT StopTimes.stop_id, Stops.stop_name, Stops.stop_lat, Stops.stop_lon, Trips.route_id, StopTimes.stop_sequence " +
                    "    FROM StopTimes " +
                    "    JOIN Stops ON StopTimes.stop_id = Stops.stop_id " +
                    "    JOIN Trips ON StopTimes.trip_id = Trips.trip_id " +
                    "    WHERE Trips.route_id = (SELECT route_id FROM RouteInfo) " +
                    "    ORDER BY StopTimes.stop_sequence " +
                    ") " +
                    "SELECT DISTINCT route_id FROM TripStops;"
    ),
    GET_ROUTE_COLOR_BY_ROUTE_ID(
        "SELECT DISTINCT route_color " +
                "FROM Routes " +
                "WHERE route_id = ?;"
    ),
    GET_SHAPES_BY_ROUTE_ID(
            "SELECT Shapes.shape_pt_lat, Shapes.shape_pt_lon, Shapes.shape_pt_sequence " +
                    "FROM Shapes " +
                    "JOIN Trips ON Shapes.shape_id = Trips.shape_id " +
                    "WHERE Trips.route_id = ? " +
                    "ORDER BY Shapes.shape_pt_sequence"
    );

    private final String query;

    Queries(String query){
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

}
