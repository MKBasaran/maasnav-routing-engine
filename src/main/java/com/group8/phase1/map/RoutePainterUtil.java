package com.group8.phase1.map;

import com.group8.phase1.imageutil.Resize;
import com.group8.phase1.map.components.Arrow;
import com.group8.phase1.views.components.FontManager;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;


public enum RoutePainterUtil {
    ;

    static final Stroke dashed = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[]{9}, 0);
    private static BufferedImage destinationImage;
    private static BufferedImage startImage;
    private static BufferedImage arrowIcon;

    public static void LoadImages() {
        try {
            URL imageUrl = RoutePainterUtil.class.getResource("components/locationIcon.png");
            destinationImage = ImageIO.read(imageUrl);
            destinationImage = Resize.resize(destinationImage, 24, 24);
            URL startImageUrl = RoutePainterUtil.class.getResource("components/startIcon.png");
            startImage = ImageIO.read(startImageUrl);
            startImage = Resize.resize(startImage, 24, 24);
            URL arrowIconUrl = RoutePainterUtil.class.getResource("components/arrowIcon.png");
            arrowIcon = ImageIO.read(arrowIconUrl);
            arrowIcon = Resize.resize(arrowIcon, 24, 24);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Paints the route made by a GeoPosition List path on the map.
     *
     * @param g    the Graphics2D object used for painting
     * @param path the list of GeoPositions representing the route
     * @param map  the map viewer
     * @param mode the mode used for painting the route (e.g., "Walking", "AssistStart", "AssistEnd")
     */
    public static void paintIndividualRoute(Graphics2D g, List<GeoPosition> path, JXMapViewer map, String mode) {
        if (arrowIcon == null)
            LoadImages();

        int index = 0;
        Point2D prev = null;

        g.setStroke(new BasicStroke(11));
        if (Objects.equals(mode, "AssistStart") || Objects.equals(mode, "AssistEnd"))
            g.setStroke(dashed);

        for (GeoPosition gp : path) {
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

            g.setColor(new Color(0, 88, 248, 255));
            if (Objects.equals(mode, "AssistEnd"))
                g.setColor(Color.red);

            if (prev != null) {
                g.drawLine((int) prev.getX(), (int) prev.getY(), (int) pt.getX(), (int) pt.getY());
                g.setColor(new Color(255, 255, 255, 240));
                if (Objects.equals(mode, "Walking"))
                    g.fill(Arrow.createArrowShape(new Point((int) prev.getX(), (int) prev.getY()), new Point((int) pt.getX(), (int) pt.getY()), map.getZoom()));
            }
            prev = pt;
            index++;
        }
    }

    /**
     * Paints the start of a route on the map.
     *
     * @param g      the Graphics2D object
     * @param target the GeoPosition representing the start
     * @param map    the map viewer
     * @param name   the name of the start location
     */
    public static void paintStartOfRoute(Graphics2D g, GeoPosition target, JXMapViewer map, String name) {
        if (startImage == null)
            LoadImages();
        g.setColor(new Color(0, 88, 248, 255));
        g.setFont(FontManager.BOLD_FONT);
        Point2D targetPoint = map.getTileFactory().geoToPixel(target, map.getZoom());
        g.drawImage(startImage, (int) targetPoint.getX() - startImage.getWidth() / 2, (int) targetPoint.getY() - startImage.getHeight() / 2, null);
        g.drawString(name, (int) (targetPoint.getX() - startImage.getWidth() / 2), (int) (targetPoint.getY() - startImage.getHeight()));
    }

    /**
     * Paints the end of a route on the map.
     *
     * @param g      the Graphics2D object
     * @param target the GeoPosition representing the end
     * @param map    the map viewer
     * @param name   the name of the end location
     */
    public static void paintEndOfRoute(Graphics2D g, GeoPosition target, JXMapViewer map, String name) {
        if (destinationImage == null)
            LoadImages();
        g.setColor(Color.RED);
        g.setFont(FontManager.BOLD_FONT);

        Point2D targetPoint = map.getTileFactory().geoToPixel(target, map.getZoom());
        g.drawImage(destinationImage, (int) targetPoint.getX() - destinationImage.getWidth() / 2, (int) targetPoint.getY() - destinationImage.getHeight() / 2, null);
        g.drawString(name, (int) (targetPoint.getX() - startImage.getWidth() / 2), (int) (targetPoint.getY() - startImage.getHeight()));
    }

    /**
     * Paints bus stops along a route on the map.
     *
     * @param g            the Graphics2D object
     * @param stops        the list of GeoPositions representing bus stops
     * @param map          the map viewer
     */
    public static void paintBusStops(Graphics2D g, List<GeoPosition> stops,Color color,String line, JXMapViewer map) {
        g.setColor(Color.RED);
        GeoPosition lastStop = null;
        for (GeoPosition stop : stops) {
            Point2D stopPoint = map.getTileFactory().geoToPixel(stop, map.getZoom());
            if(stop!=stops.get(0)){
                paintBusLine(g,lastStop,stop,color,map);
            }
            lastStop = stop;
         //   g.fillOval((int) stopPoint.getX(), (int) stopPoint.getY(), 10, 10);
        }




        g.setColor(Color.red);
        g.setFont(FontManager.BOLD_FONT);

    }

    /**
     * Paints a straight line between two GeoPositions on the map.
     *
     * @param g     the Graphics2D object
     * @param start the starting GeoPosition
     * @param end   the ending GeoPosition
     * @param color the color of the line
     * @param map   the map viewer
     */
    public static void paintStraightLine(Graphics2D g, GeoPosition start, GeoPosition end, Color color, JXMapViewer map) {
        Point2D startPoint = map.getTileFactory().geoToPixel(start, map.getZoom());
        Point2D endPoint = map.getTileFactory().geoToPixel(end, map.getZoom());

        g.setStroke(dashed);
        g.setColor(color);
        g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), (int) endPoint.getX(), (int) endPoint.getY());
    }

    /**
     * Paints a straight line between two GeoPositions on the map.
     *
     * @param g     the Graphics2D object
     * @param start the starting GeoPosition
     * @param end   the ending GeoPosition
     * @param color the color of the line
     * @param map   the map viewer
     */
    public static void paintBusLine(Graphics2D g, GeoPosition start, GeoPosition end, Color color, JXMapViewer map) {
        Point2D startPoint = map.getTileFactory().geoToPixel(start, map.getZoom());
        Point2D endPoint = map.getTileFactory().geoToPixel(end, map.getZoom());

        g.setStroke(new BasicStroke(5));
        g.setColor(color);
        g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), (int) endPoint.getX(), (int) endPoint.getY());
    }
}
