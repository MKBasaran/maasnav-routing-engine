package com.group8.phase1.views.components;

import com.group8.phase1.ErrorUI.ErrorShower;
import com.group8.phase1.api.WebInteraction;
import com.group8.phase1.database.functional.GetNearestNode;
import com.group8.phase1.imageutil.Resize;
import com.group8.phase1.initialdataloader.RoutingDataManager;
import com.group8.phase1.map.Painter;
import com.group8.phase1.pathfinding.Path;
import com.group8.phase1.pathfinding.RoutingEngine;
import com.group8.phase1.structures.map.Node;
import com.group8.phase1.views.handlers.BusRouteHandler;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sidebar extends JPanel {

    private final BusRouteHandler busRouteHandler = new BusRouteHandler(this);
    // Routing
    Painter painter;
    JXMapViewer mapViewer;
    private BufferedImage walkingManImage;
    private BufferedImage bicycleImage;
    private BufferedImage busImage;

    private BufferedImage startPointImage;
    private BufferedImage dotsImage;
    private BufferedImage toPointImage;
    private BufferedImage circleImage;
    private BusRoutingViewer busRoutingViewer;

    private JLabel distanceLabel;
    private JLabel timeLabel;
    private JPanel sideBar2;

    public Sidebar(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        this.setLayout(new BorderLayout());
        int top = 10, left = 10, bottom = 10, right = 10;
        this.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));

        resizeAndLoadPhotos();
        initComponents();
    }

    // Format seconds into "x hours x minutes x seconds" format
    public static String formatSeconds(int seconds) {
        int hours = seconds / 3600;
        int remainder = seconds % 3600;
        int minutes = remainder / 60;
        int secs = remainder % 60;

        return String.format("%d hours %d minutes %d seconds", hours, minutes, secs);

    }

    /**
     * Adds custom focus listeners to text fields so that on mouse hover, the text from them disappears
     *
     * @param textField text field to add focus listener to
     * @param promtText text which should be displayed in the text field while not in focus
     */

    void deleteOnClick(JTextField textField, String promtText) {
        textField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(promtText)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().trim().equals("")) {
                    textField.setText(promtText);
                }
            }

        });
    }

    // Initialize UI components
    private void initComponents() {
        JPanel inputPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();

        inputPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        JToggleButton toggleButton = new JToggleButton("Social Economic Details");
        toggleButton.setName("toMetricButton");
        JLabel startInputPhoto = new JLabel();
        startInputPhoto.setIcon(new ImageIcon(startPointImage));
        JLabel toInputPhoto = new JLabel();
        toInputPhoto.setIcon(new ImageIcon(toPointImage));


        JTextField startInputField = new RoundTextField("Enter starting postal code:");
        startInputField.setName("startInputField");

        deleteOnClick(startInputField, "Enter starting postal code:");
        JTextField toInputField = new RoundTextField("Enter destination postal code:");
        toInputField.setName("toInputField");

        deleteOnClick(toInputField, "Enter destination postal code:");
        JLabel middleDotsPhoto = new JLabel();
        middleDotsPhoto.setIcon(new ImageIcon(dotsImage));


        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 5;
        gbc.ipadx = 5;
        inputPanel.add(startInputPhoto, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipady = 20;
        gbc.ipadx = 50;
        inputPanel.add(startInputField, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 5;
        gbc.ipadx = 0;
        inputPanel.add(middleDotsPhoto, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipady = 5;
        gbc.ipadx = 5;
        inputPanel.add(toInputPhoto, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.ipady = 20;
        gbc.ipadx = 220;
        inputPanel.add(toInputField, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.ipadx = 5;
        gbc.ipady = 0;
        inputPanel.add(toggleButton, gbc);
        inputPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        inputPanel.setPreferredSize(new Dimension(320, 150));
        this.add(inputPanel);

        JPanel buttonsPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(1, 3);
        gridLayout.setHgap(5);
        buttonsPanel.setLayout(gridLayout);

        JButton walkingButton = new JButton();
        walkingButton.setName("walkingButton");
        walkingButton.setBackground(Color.white);
        walkingButton.setIcon(new ImageIcon(walkingManImage));
        walkingButton.setToolTipText("Generate walking route.");
        walkingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRouting("Walking", startInputField.getText(), toInputField.getText());
            }
        });

        JButton bicycleButton = new JButton();
        bicycleButton.setName("bikeButton");
        bicycleButton.setBackground(Color.white);
        bicycleButton.setIcon(new ImageIcon(bicycleImage));
        bicycleButton.setToolTipText("Generate bicycle route.");
        bicycleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRouting("Bicycle", startInputField.getText(), toInputField.getText());
            }
        });

        JButton busButton = new JButton();
        busButton.setName("busButton");
        busButton.setBackground(Color.white);
        busButton.setIcon(new ImageIcon(busImage));
        busButton.setToolTipText("Generate bus route.");
        busButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRouting("Bus", startInputField.getText(), toInputField.getText());
            }
        });


        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(toggleButton.isSelected()){
                    switchToSidebar2();
                } else {
                    switchToSidebar();
                }
            }
        });

        buttonsPanel.add(walkingButton);
        buttonsPanel.add(bicycleButton);
        buttonsPanel.add(busButton);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel infoPanel = new JPanel();
        JLabel distanceToDestination = new JLabel("Distance: ");
        distanceLabel = distanceToDestination;
        distanceLabel.setVisible(false);
        JLabel timeToDestination = new JLabel("Time: ");
        timeLabel = timeToDestination;
        timeLabel.setVisible(false);
        infoPanel.add(distanceToDestination);
        infoPanel.add(timeToDestination);
        infoPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

        JPanel interactionPanel = new JPanel();
        interactionPanel.setLayout(new BoxLayout(interactionPanel, BoxLayout.Y_AXIS));
        interactionPanel.add(inputPanel);
        interactionPanel.add(buttonsPanel);
        interactionPanel.add(infoPanel);
        busRoutingViewer = new BusRoutingViewer();
        busRoutingViewer.setVisible(false);
        interactionPanel.add(busRoutingViewer);

        this.add(interactionPanel, BorderLayout.NORTH);

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 400));
    }

    private void resizeAndLoadPhotos() {
        try {
            // Route selection images
            URL imageUrl = getClass().getResource("startPointIcon.png");
            if (imageUrl != null) {
                startPointImage = Resize.resize(ImageIO.read(imageUrl), 20, 20);
            }
            imageUrl = getClass().getResource("dotsIcon.png");
            if (imageUrl != null) {
                dotsImage = Resize.resize(ImageIO.read(imageUrl), 20, 20);
            }
            imageUrl = getClass().getResource("destinationPointIcon.png");
            if (imageUrl != null) {
                toPointImage = Resize.resize(ImageIO.read(imageUrl), 24, 24);
            }

            // Button images
            imageUrl = getClass().getResource("circle.png");
            if (imageUrl != null) {
                circleImage = Resize.resize(ImageIO.read(imageUrl), 45, 45);
            }

            imageUrl = getClass().getResource("man_walking.png");
            if (imageUrl != null) {
                walkingManImage = Resize.resize(ImageIO.read(imageUrl), 30, 30);
            }
            imageUrl = getClass().getResource("man_on_bicycle.png");
            if (imageUrl != null) {
                bicycleImage = Resize.resize(ImageIO.read(imageUrl), 30, 30);
            }
            imageUrl = getClass().getResource("busIcon.jpg");
            if (imageUrl != null) {
                busImage = Resize.resize(ImageIO.read(imageUrl), 30, 30);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void switchToSidebar2() {
        removeAll();
        sideBar2 = new SideBar2(this);
        sideBar2.setName("sidebar2");
        add(sideBar2);
        revalidate();
        repaint();
    }

    void switchToSidebar() {
        removeAll();
        initComponents();
        revalidate();
        repaint();
    }

    /**
     * Handles loading the route between two postal codes for walking and biking, including the visualization, time and distance
     *
     * @param startPostalCode          {@link Node} representing the start postal code
     * @param destinationPostalCode    {@link Node} representing the destination postal code
     * @param nearestStartNodeId       {@link Long} ID representing the nearest node to the start postal code that is available in the OSM node dataset
     * @param nearestDestinationNodeId {@link Long} ID representing the nearest node to the destination postal code that is available in the OSM node dataset
     * @param mode                     {@link String} indicating the routing mode: "Walking" or "Bicycle"
     */
    private void loadRoute(Node startPostalCode, Node destinationPostalCode, Long nearestStartNodeId, Long nearestDestinationNodeId, String mode) {
        RoutingEngine routingEngine = new RoutingEngine(RoutingDataManager.routingGraph);
        Path path = routingEngine.getShortestPath(nearestStartNodeId, nearestDestinationNodeId);
        java.util.List<Long> shortestPath = path.getNodes();
        List<Node> shortPathNodes = new ArrayList<>();
        shortPathNodes.add(startPostalCode);

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Node startNode = RoutingDataManager.nodesMap.get(shortestPath.get(i));
            if (startNode == null) {
                System.out.println(shortestPath.get(i));
            }
            shortPathNodes.add(startNode);
        }

        distanceLabel.setText("Distance: " + Math.floor((path.getDistance() / 1000)*100)/100 + " km");

        int timeTaken = 0;
        if (mode == "Walking") timeTaken = (int) (path.getDistance() / 1000 / 5.0 * 3600);
        else if (mode == "Bicycle") timeTaken = (int) (path.getDistance() / 1000 / 15.0 * 3600);

        String timeText = formatSeconds(timeTaken);
        timeLabel.setText("Time: " + timeText);

        busRoutingViewer.setVisible(false);
        shortPathNodes.add(destinationPostalCode);
        visualizePath(shortPathNodes, startPostalCode, destinationPostalCode);

    }

    /**
     * Displays the walking/biking path on the map
     *
     * @param shortestPath          {@link List<Node>} containing the route nodes in order
     * @param startPostalCode       {@link Node} representing the starting postal code
     * @param destinationPostalCode {@link Node} representing the destination postal code
     */
    public void visualizePath(List<Node> shortestPath, Node startPostalCode, Node destinationPostalCode) {
        List<GeoPosition> track = new ArrayList<>();
        for (Node node : shortestPath) {
            track.add(new GeoPosition(node.getLatitude(), node.getLongitude()));
        }
        painter = new Painter(track, startPostalCode, destinationPostalCode);
        mapViewer.setCenterPosition(track.get(0));
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();

        distanceLabel.setVisible(true);
        timeLabel.setVisible(true);
    }


    /**
     * Displays the bus route on the map, delegating the call to the {@code BusRouteHandler} class
     *
     * @param fromPostalCode {@link Node} representing the starting postal code
     * @param toPostalCode   {@link Node} representing the destination postal code
     */
    public void visualizeBusRoute(Node fromPostalCode, Node toPostalCode) {
        busRouteHandler.visualizeBusRoute(fromPostalCode, toPostalCode);
    }


    /**
     * Method to prepare routing between two postal codes. This retrieves the geographical information for given postal codes
     * using {@code RoutingDataManager}, and {@code WebInteraction} from the internet if required. Handles non-existant postal code errors.
     *
     * @param mode                  {@link String} specifying the routing mode: "Walking", "Bicycle" or "Bus"
     * @param startPostalCode       {@link String} starting postal code retrieved from the input fields
     * @param destinationPostalCode {@link String} ending postal code retrieved from the input fields
     */
    public void startRouting(String mode, String startPostalCode, String destinationPostalCode) {

        boolean showError = false;
        boolean startError = false;
        boolean endError = false;

        Node startPostalNode = RoutingDataManager.postalCodeParser.postalCodes.get(startPostalCode);
        Node destinationPostalNode = RoutingDataManager.postalCodeParser.postalCodes.get(destinationPostalCode);

        if (startPostalNode == null && RoutingDataManager.apiRateLimiter.allowRequest()) {
            startPostalNode = WebInteraction.retrievePostalCodeData(startPostalCode);
            if (startPostalNode == null) {
                showError = true;
                startError = true;
            }
        }
        if (destinationPostalNode == null && RoutingDataManager.apiRateLimiter.allowRequest()) {
            destinationPostalNode = WebInteraction.retrievePostalCodeData(destinationPostalCode);
            if (destinationPostalNode == null) {
                showError = true;
                endError = true;
            }
        }

        if (showError) {
            showError(startError, endError, startPostalCode, destinationPostalCode);
        } else if (startPostalNode != null && destinationPostalNode != null && (Objects.equals(mode, "Walking") || Objects.equals(mode, "Bicycle"))) {
            loadRoute(startPostalNode, destinationPostalNode, GetNearestNode.get(startPostalNode.getLatitude(), startPostalNode.getLongitude()), GetNearestNode.get(destinationPostalNode.getLatitude(), destinationPostalNode.getLongitude()), mode);
        } else if (startPostalNode != null && destinationPostalNode != null && Objects.equals(mode, "Bus")) {
            visualizeBusRoute(startPostalNode, destinationPostalNode);
        }

    }


    /**
     * Method to display an error message to the user incase of incorrect postal code inputs. Uses {@link ErrorShower} to display the error message
     *
     * @param startPostal     {@link boolean} specifying whether to show error for starting postal code (true) or not (false)
     * @param endPostal       {@link boolean} specifying whether to show error for ending postal code (true) or not (false)
     * @param startPostalCode {@link String} starting postal code input from the user
     * @param endPostalCode   {@link String} destination postal code input from the user
     */
    public void showError(boolean startPostal, boolean endPostal, String startPostalCode, String endPostalCode) {
        String postalError = "The following postal codes do no exist in Maastricht: ";
        if (startPostal) {
            postalError += startPostalCode + ", ";
        }
        if (endPostal) {
            postalError += endPostalCode + ", ";
        }
        new ErrorShower(postalError.substring(0, postalError.length() - 2));
    }


    public JXMapViewer getMapViewer() {
        return mapViewer;
    }

    public BusRoutingViewer getBusRoutingViewer() {
        return busRoutingViewer;
    }

    public JLabel getDistanceLabel() {
        return distanceLabel;
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }
}
