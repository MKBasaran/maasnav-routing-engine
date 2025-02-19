package com.group8.phase1.map;

import com.group8.phase1.imageutil.Resize;
import com.group8.phase1.sociometric.calculation.objects.AmenityNode;
import com.group8.phase1.sociometric.calculation.objects.ShopNode;
import com.group8.phase1.sociometric.calculation.objects.TourismNode;
import com.group8.phase1.sociometric.calculation.objects.TransportNode;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndexPainter implements org.jxmapviewer.painter.Painter<JXMapViewer>{
    private static BufferedImage shopIcon;
    private static BufferedImage amenityIcon;
    private static BufferedImage tourismIcon;
    private static BufferedImage stopsIcon;
    private final ArrayList<TourismNode> tourismNodes;
    private final ArrayList<ShopNode> shopNodes;
    private final ArrayList<AmenityNode> amenityNodes;
    private final HashMap<Integer, TransportNode> stops;
    private final HashMap<Integer, Integer> routeToStops;

    public IndexPainter(ArrayList<AmenityNode> amenityNodes, ArrayList<ShopNode> shopNodes, ArrayList<TourismNode> tourismNodes, HashMap<Integer, TransportNode> stops, HashMap<Integer,Integer> routeToStops){
        this.amenityNodes = amenityNodes;
        this.shopNodes = shopNodes;
        this.tourismNodes = tourismNodes;
        this.stops = stops;
        this.routeToStops = routeToStops;
    }
    public static void loadImages(){
        try{
            URL imageUrl = RoutePainterUtil.class.getResource("components/shopIcon.png");
            shopIcon = ImageIO.read(imageUrl);
            shopIcon = Resize.resize(shopIcon, 40, 40);
            URL imageUrl2 = RoutePainterUtil.class.getResource("components/amenitiesIcon.png");
            amenityIcon = ImageIO.read(imageUrl2);
            amenityIcon = Resize.resize(amenityIcon, 40, 40);
            URL imageUrl3 = RoutePainterUtil.class.getResource("components/tourismIcon.png");
            tourismIcon = ImageIO.read(imageUrl3);
            tourismIcon = Resize.resize(tourismIcon, 40, 40);
            URL imageUrl4 = RoutePainterUtil.class.getResource("components/stopsIcon.png");
            stopsIcon = ImageIO.read(imageUrl4);
            stopsIcon = Resize.resize(stopsIcon, 40, 40);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void paintAmenitites(Graphics2D g, ArrayList<AmenityNode> amenityNodes, JXMapViewer map){
        if(amenityIcon == null){
            loadImages();
        }
        for (AmenityNode node : amenityNodes) {
            GeoPosition position = new GeoPosition(node.getLat(), node.getLon());
            Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());
            g.drawImage(amenityIcon, (int) point.getX() - amenityIcon.getWidth() / 2, (int) point.getY() - amenityIcon.getHeight() / 2, null);
        }
    }
    public static void paintTourism(Graphics2D g, ArrayList<TourismNode> tourismNodes, JXMapViewer map) {
        if (tourismIcon == null) {
            loadImages();
        }
        for (TourismNode node : tourismNodes) {
            GeoPosition position = new GeoPosition(node.getLat(), node.getLon());
            Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());
            g.drawImage(tourismIcon, (int) point.getX() - tourismIcon.getWidth() / 2, (int) point.getY() - tourismIcon.getHeight() / 2, null);
        }
    }
    public static void paintShops(Graphics2D g, ArrayList<ShopNode> shopNodes, JXMapViewer map) {
        if (shopIcon == null) {
            loadImages();
        }
        for (ShopNode node : shopNodes) {
            GeoPosition position = new GeoPosition(node.getLat(), node.getLon());
            Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());
            g.drawImage(shopIcon, (int) point.getX() - shopIcon.getWidth() / 2, (int) point.getY() - shopIcon.getHeight() / 2, null);
        }
    }
    public static void paintStops(Graphics2D g, HashMap<Integer, TransportNode> stops, JXMapViewer map) {
        if (stopsIcon == null) {
            loadImages();
        }
        for (TransportNode node : stops.values()) {
            GeoPosition position = new GeoPosition(node.getStop_lat(), node.getStop_lon());
            Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());
            g.drawImage(stopsIcon, (int) point.getX() - stopsIcon.getWidth() / 2, (int) point.getY() - stopsIcon.getHeight() / 2, null);
        }
    }
    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        paintAmenitites(g, amenityNodes, map);
        paintTourism(g, tourismNodes, map);
        paintShops(g, shopNodes, map);
        paintStops(g, stops, map);
        //paintRoutes(g, stops, routeToStops, map);

    }
}
