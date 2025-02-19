package com.group8.phase1.map.components;

import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * The {@code Arrow} enum is used for creating arrow shapes between 2 points.
 */
public enum Arrow {
    ;

    /**
     * Creates an arrow shape between two points.
     *
     * @param fromPt    the starting point of the arrow
     * @param toPt      the ending point of the arrow
     * @param zoomLevel the current zoom level of the map
     * @return the arrow shape
     */
    public static Shape createArrowShape(Point fromPt, Point toPt, int zoomLevel) {
        Polygon arrowPolygon = new Polygon();
        arrowPolygon.addPoint(0, 0);  // Tip of the arrow
        arrowPolygon.addPoint(10, 5); // Right side of the arrow
        arrowPolygon.addPoint(10, -5);

        Point midPoint = midpoint(fromPt, toPt);

        double rotate = Math.atan2(fromPt.y - toPt.y, fromPt.x - toPt.x);

        AffineTransform transform = new AffineTransform();
        transform.translate(midPoint.x, midPoint.y);

        double scaleFactor = 1.0 / zoomLevel; // You can adjust this factor as needed
        if (zoomLevel == 0)
            scaleFactor = 2;
        transform.scale(scaleFactor, scaleFactor);
        transform.rotate(rotate);

        return transform.createTransformedShape(arrowPolygon);
    }


    /**
     * Calculates the middle point between the 2 points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the midpoint
     */
    private static Point midpoint(Point p1, Point p2) {
        return new Point((int) ((p1.x + p2.x) / 2.0),
                (int) ((p1.y + p2.y) / 2.0));
    }
}
