package com.group8.phase1.views.handlers;

import com.group8.phase1.structures.map.Node;
import com.group8.phase1.utils.DateTimeUtil;
import com.group8.phase1.TransferRouting.Dijkstra;
import com.group8.phase1.TransferRouting.TravelNode;
import com.group8.phase1.TransferRouting.TravelResults;
import com.group8.phase1.initialdataloader.RoutingDataManager;
import com.group8.phase1.map.BusRoutePainter;
import com.group8.phase1.views.components.Sidebar;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
    The BusRouteHandler class actually handles the display of the bus route with all of its functional components
 */
public class BusRouteHandler implements Serializable {
    private final Sidebar sidebar;

    public BusRouteHandler(Sidebar sidebar) {
        this.sidebar = sidebar;
    }

    public void visualizeBusRoute(Node fromPostalCode, Node toPostalCode) {
        sidebar.getDistanceLabel().setVisible(false);
        long startTime = System.nanoTime();

        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

            List<GeoPosition> pathToStartStation = new ArrayList<>();
            List<GeoPosition> pathToEndStation = new ArrayList<>();

            List<List<GeoPosition>> walkingRoutes = new ArrayList<>();
            List<TravelNode> busRoutes = new ArrayList<>();

            TravelResults path = Dijkstra.run(new double[]{fromPostalCode.getLatitude(), fromPostalCode.getLongitude()},new double[]{toPostalCode.getLatitude(), toPostalCode.getLongitude()},formattedTime);
      //  System.out.println(path.toString());
            for(int i=0;i<path.travelNodes.size();i++){

                    if(path.travelNodes.get(i).tripId.equals("Walking")){

                        List<GeoPosition> walkRoute = new ArrayList<>();
                        for(Long shape:path.travelNodes.get(i).walkingNodes){
                            walkRoute.add(new GeoPosition(RoutingDataManager.nodesMap.get(shape).getLatitude(),RoutingDataManager.nodesMap.get(shape).getLongitude()));
                        }
                        walkingRoutes.add(walkRoute);
                    }else{
                        busRoutes.add(path.travelNodes.get(i));
                    }

            }
        BusRoutePainter painter = new BusRoutePainter(pathToStartStation, pathToEndStation, busRoutes,walkingRoutes,fromPostalCode, toPostalCode);
     sidebar.getMapViewer().setCenterPosition(new GeoPosition(fromPostalCode.getLatitude(), fromPostalCode.getLongitude()));
    sidebar.getMapViewer().setOverlayPainter(painter);
    sidebar.getMapViewer().repaint();

        LocalTime departureTimeObject = LocalTime.parse(DateTimeUtil.correctDataTime(formattedTime));
        LocalTime arrivalTimeObject = LocalTime.parse(DateTimeUtil.correctDataTime(formattedTime)).plusSeconds(path.timeTaken);

        String startingTime = departureTimeObject.toString();
        String endTIME = arrivalTimeObject.toString();
        List<Pair> busLines = new ArrayList<>();

        String last=path.travelNodes.get(0).line;
            busLines.add(new Pair(last,path.travelNodes.get(0).color));
        for(TravelNode travelNode : path.travelNodes){
            if(!Objects.equals(travelNode.line, last)){
                busLines.add(new Pair(travelNode.line,travelNode.color));
            }
            last = travelNode.line;
        }

        sidebar.getTimeLabel().setVisible(true);
        sidebar.getTimeLabel().setText("Time: " + Duration.between(departureTimeObject, arrivalTimeObject).toMinutes() + " minutes");
        sidebar.getBusRoutingViewer().updateData(fromPostalCode.getPostalCode(), startingTime, toPostalCode.getPostalCode(), endTIME, busLines);
        sidebar.getBusRoutingViewer().setVisible(true);

    }


}