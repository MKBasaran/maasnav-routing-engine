package com.group8.phase1.map;

import com.group8.phase1.structures.map.Node;
import com.group8.phase1.TransferRouting.Shape;
import com.group8.phase1.TransferRouting.TravelNode;
import com.group8.phase1.logger.LoggerService;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code BusRoutePainter} class implements the Painter interface for painting bus route between 2 postal codes on a map.
 */
public class BusRoutePainter implements org.jxmapviewer.painter.Painter<JXMapViewer> {
    private final List<GeoPosition> pathToStartStationGeoPoints;
    private final List<GeoPosition> pathToTargetPostalCodeGeoPoints;

    private final Node startPostalCode;
    private final Node destinationPostalCode;
    private List<TravelNode> busRoutes = new ArrayList<>();

    private final LoggerService loggerService = LoggerService.getInstance();
    private final List<List<GeoPosition>> walkingGeoPositions;


    /**
     * Constructs a {@code BusRoutePainter} object with the specified parameters.
     *
     * @param pathToStartStationGeoPoints     the path to the first bus station from the source postal code as GeoPositions
     * @param pathToTargetPostalCodeGeoPoints the path to the target postal code from the last bus stop as GeoPositions
     * @param startPostalCode                 the start postal code node
     * @param destinationPostalCode           the destination postal code node
     */
    public BusRoutePainter(List<GeoPosition> pathToStartStationGeoPoints,
                           List<GeoPosition> pathToTargetPostalCodeGeoPoints, List<TravelNode> busRoutes,
                           List<List<GeoPosition>> walkingGeoPositions,
                           Node startPostalCode, Node destinationPostalCode) {
        this.pathToStartStationGeoPoints = pathToStartStationGeoPoints;
        this.pathToTargetPostalCodeGeoPoints = pathToTargetPostalCodeGeoPoints;
        this.busRoutes = busRoutes;
        this.walkingGeoPositions = walkingGeoPositions;
        this.startPostalCode = startPostalCode;
        this.destinationPostalCode = destinationPostalCode;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        GeoPosition fromPostalCodeGeo = new GeoPosition(startPostalCode.getLatitude(),startPostalCode.getLongitude());
        GeoPosition toPostalCodeGeo = new GeoPosition(destinationPostalCode.getLatitude(),destinationPostalCode.getLongitude());
        Point2D fromPostalPoint =  map.getTileFactory().geoToPixel(fromPostalCodeGeo, map.getZoom());
        Point2D toPostalPoint =  map.getTileFactory().geoToPixel(toPostalCodeGeo, map.getZoom());





        for(List<GeoPosition> walkingRoute : walkingGeoPositions){
          //  System.out.println("WALKING ROUTE: " + walkingRoute);
            RoutePainterUtil.paintIndividualRoute(g,walkingRoute,map,"AssistStart");
        }

        List<List<GeoPosition>> busGeoPositions = new ArrayList<>();

        for(TravelNode node : busRoutes){
            List<GeoPosition> shapeRoute = new ArrayList<>();
            for(Shape shape : node.shapes){
                shapeRoute.add(new GeoPosition(shape.lat,shape.lon));
            }
            busGeoPositions.add(shapeRoute);
        }

        for(int i = 0 ;i < busGeoPositions.size();i++) {
            List<GeoPosition> busLine = busGeoPositions.get(i);
            Color color = Color.decode("#"+busRoutes.get(i).color);
           // System.out.println(busLine.toString());
            RoutePainterUtil.paintBusStops(g, busLine,color, "Line: " + busRoutes.get(i).line,map);
            //colors[index] = null;
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        RoutePainterUtil.paintStartOfRoute(g, fromPostalCodeGeo, map, startPostalCode.getPostalCode());

        RoutePainterUtil.paintEndOfRoute(g, toPostalCodeGeo,map, destinationPostalCode.getPostalCode());
        loggerService.info("Successfully displayed the bus route and all it's components!");

        g.dispose();
    }
}
