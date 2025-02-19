package com.group8.phase1.views.components;

import com.group8.phase1.imageutil.Resize;
import com.group8.phase1.views.handlers.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BusRoutingViewer extends JPanel {


    private BufferedImage startPointImage = null;
    private BufferedImage toPointImage = null;
    private BufferedImage dotsImage = null;


    private JLabel startingStationLabel, startingTimeLabel;
    private JLabel endStationLabel, endTimeLabel;
    private JPanel busLineInfo;
    private JLabel busLineLabel = null;

    private JPanel routeInformation;

    public BusRoutingViewer() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        resizeAndLoadImages();
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        initComponents();
    }

    private void initComponents() {
        routeInformation = new JPanel();
        routeInformation.setLayout(new BoxLayout(routeInformation, BoxLayout.Y_AXIS));


        JPanel routeStartNodeInformation = new JPanel();
        routeStartNodeInformation.setLayout(new GridLayout(1, 3));
        JLabel routeStartIcon = new JLabel(new ImageIcon(startPointImage));
        JLabel routeStartNodeName = new JLabel("Maastricht Station");
        startingStationLabel = routeStartNodeName;
        JLabel routeStartTime = new JLabel("12:45");
        startingTimeLabel = routeStartTime;
        routeStartTime.setFont(FontManager.BOLD_FONT);
        routeStartIcon.setDoubleBuffered(true);
        routeStartNodeInformation.add(routeStartIcon);
        routeStartNodeInformation.add(routeStartNodeName);
        routeStartNodeName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        routeStartNodeInformation.add(routeStartTime);



        JPanel routeDestinationNodeInformation = new JPanel();
        routeDestinationNodeInformation.setLayout(new GridLayout(1, 3));
        JLabel routeDestinationIcon = new JLabel(new ImageIcon(toPointImage));
        JLabel routeDestinationNodeName = new JLabel("Daalhof");
        endStationLabel = routeDestinationNodeName;
        routeDestinationNodeName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
        JLabel routeDestinationTime = new JLabel("13:42");
        endTimeLabel = routeDestinationTime;
        routeDestinationTime.setFont(FontManager.BOLD_FONT);
        routeDestinationNodeInformation.add(routeDestinationIcon);
        routeDestinationNodeInformation.add(routeDestinationNodeName);
        routeDestinationNodeInformation.add(routeDestinationTime);

        routeInformation.add(routeStartNodeInformation);
        busLineInfo = new JPanel();
        routeInformation.add(busLineInfo);
        routeInformation.add(routeDestinationNodeInformation);





        this.add(routeInformation);
    }

    private void resizeAndLoadImages() {
        try {
            URL imageUrl = getClass().getResource("startPointIcon.png");
            startPointImage = Resize.resize(ImageIO.read(imageUrl), 20, 20);
            imageUrl = getClass().getResource("dotsIcon.png");
            dotsImage = Resize.resize(ImageIO.read(imageUrl), 20, 20);
            imageUrl = getClass().getResource("destinationPointIcon.png");
            toPointImage = Resize.resize(ImageIO.read(imageUrl), 24, 24);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Updates the data displayed in the panel.
     *
     * @param startingStation The starting station.
     * @param startTime       The starting time.
     * @param endStation      The destination station.
     * @param endTime         The arrival time.
     * @param busLines         The bus line information.
     */
    public void updateData(String startingStation, String startTime, String endStation, String endTime, List<Pair> busLines) {
        startingStationLabel.setText(startingStation);
        startingTimeLabel.setText(startTime);
        endStationLabel.setText(endStation);
        endTimeLabel.setText(endTime);

        routeInformation.remove(busLineInfo);
        busLineInfo = new JPanel();
        busLineInfo.setLayout(new GridLayout(busLines.size(), 3));
        for (Pair busLine : busLines) {
            busLineInfo.add(new JLabel());
            JLabel busLineText;
            if(Objects.equals(busLine.lineName, "Walking"))
                busLineText = new JLabel( busLine.lineName);
            else
                busLineText = new JLabel("Bus " + busLine.lineName);
            busLineLabel = busLineText;
            busLineText.setOpaque(true);
            busLineText.setForeground(Color.WHITE);
            busLineText.setBackground(Color.decode("#" + busLine.color));
            busLineText.setHorizontalAlignment(SwingConstants.CENTER);
            busLineText.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
            busLineInfo.add(busLineText);
            busLineInfo.add(new JLabel());

        }
        busLineInfo.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        routeInformation.add(busLineInfo,1);
        routeInformation.revalidate();
        routeInformation.repaint();
    }

}
