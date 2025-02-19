package com.group8.phase1.map;

import com.group8.phase1.structures.map.Node;
import com.group8.phase1.logger.LoggerService;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.List;

/**
 * The {@code Painter} class implements the Painter interface for painting walking/biking routes on a map.
 */
public class Painter implements org.jxmapviewer.painter.Painter<JXMapViewer> {
    private final List<GeoPosition> track;
    private final Node startPostalCode;
    private final Node destinationPostalCode;

    private final LoggerService logger = LoggerService.getInstance();

    /**
     * Constructs a {@code Painter} object with the specified track and postal code nodes.
     *
     * @param track                 the list of GeoPositions representing the route
     * @param startPostalCode       the starting postal code node
     * @param destinationPostalCode the destination postal code node
     */
    public Painter(List<GeoPosition> track, Node startPostalCode, Node destinationPostalCode) {
        this.track = track;
        this.startPostalCode = startPostalCode;
        this.destinationPostalCode = destinationPostalCode;
    }

    /**
     * Clears the track data.
     */
    public void clear() {
        track.clear();
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        // Paint the actual route using graph nodes
        RoutePainterUtil.paintIndividualRoute(g, track.subList(1, track.size() - 1), map, "Walking");

        // Paint the start and end segments ( from initial postal code to first node and from last node to the destination postal code )  of the route
        RoutePainterUtil.paintStraightLine(g, track.get(0), track.get(1), new Color(0, 88, 248, 255), map);
        RoutePainterUtil.paintStraightLine(g, track.get(track.size() - 2), track.get(track.size() - 1), Color.red, map);

        // Paint the start and end points
        RoutePainterUtil.paintStartOfRoute(g, track.get(0), map, startPostalCode.getPostalCode());
        RoutePainterUtil.paintEndOfRoute(g, track.get(track.size() - 1), map, destinationPostalCode.getPostalCode());

        logger.info("Successfully painted walking/biking route!");




        g.dispose();
    }
}
