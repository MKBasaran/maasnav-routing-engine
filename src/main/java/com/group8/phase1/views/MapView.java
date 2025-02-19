package com.group8.phase1.views;

import com.group8.phase1.api.RateLimiter;
import com.group8.phase1.imageutil.Resize;
import com.group8.phase1.initialdataloader.ProgressPanel;
import com.group8.phase1.initialdataloader.RoutingDataManager;
import com.group8.phase1.osm.LoadGraphNodes;
import com.group8.phase1.osm.LoadRoutingGraph;
import com.group8.phase1.osm.checker.CheckDatabase;
import com.group8.phase1.postalcodes.DataParser;
import com.group8.phase1.views.components.FontManager;
import com.group8.phase1.views.components.Sidebar;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MapView extends JFrame {

    private JXMapViewer mapViewer;
    private JPanel mainPanel;
    private JPanel sideBar;
    private BufferedImage logoImage;


    public MapView() {
        try {
            URL logoUrl = getClass().getResource("Logo.png");
            logoImage = Resize.resize(ImageIO.read(logoUrl), 24, 24);

            this.setIconImage(logoImage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setWindowSizeAndCenter();

        loadRoutingData();
        this.revalidate();
        this.repaint();
        changeFont(this, FontManager.REGULAR_FONT);
    }

    public static void changeFont(Component component, Font font) {
        if (component.getFont() != FontManager.BOLD_FONT)
            component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                if (child.getFont() != FontManager.BOLD_FONT)
                    changeFont(child, font);
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MapView().setVisible(true));
    }

    private void loadRoutingData() {
        ProgressPanel progressViewer = new ProgressPanel();
        progressViewer.setName("loaderPanel");
        this.getContentPane().add(progressViewer);
        this.setVisible(true);
        SwingWorker<Void, Void> task = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                CheckDatabase.check(progressViewer);
                RoutingDataManager.nodesMap = LoadGraphNodes.load(progressViewer);
                RoutingDataManager.routingGraph = LoadRoutingGraph.load(progressViewer);

                return null;
            }

            @Override
            protected void done() {
                try {
                    initComponents();
                    getContentPane().remove(progressViewer);
                    progressViewer.setVisible(false);
                    initComponents();
                    getContentPane().add(mainPanel);
                    repaint();
                    revalidate();

                    RoutingDataManager.postalCodeParser = new DataParser();
                    RoutingDataManager.apiRateLimiter = new RateLimiter(new int[]{1, 5, 40, 100}, false);

                } catch (Exception ex) {

                    throw new RuntimeException(ex);
                }
            }
        };
        task.execute();
    }

    private void initMapViewer() {
        TileFactoryInfo info = new OSMTileFactoryInfo();

        mapViewer.setTileFactory(new DefaultTileFactory(info));
        GeoPosition geoPosition = new GeoPosition(50.851709369248134, 5.692473355677425);
        mapViewer.setCenterPosition(geoPosition);
        mapViewer.setZoom(6);

        MouseInputListener pan = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(pan);
        mapViewer.addMouseMotionListener(pan);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));

        mapViewer.repaint();
    }

    private void initComponents() {
        mapViewer = new JXMapViewer();
        initMapViewer();

        mainPanel = new JPanel(new BorderLayout());

        sideBar = new Sidebar(mapViewer);
        sideBar.setName("sidebar");

        mainPanel.add(mapViewer, BorderLayout.CENTER);
        mainPanel.add(sideBar, BorderLayout.EAST);
        mainPanel.setPreferredSize(new Dimension(800, 600)); // Set initial size for mainPanel

    }

    private void setWindowSizeAndCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int newWidth = (int) (screenWidth / 1.5);
        int newHeight = (int) (screenHeight / 1.5);

        setSize(newWidth, newHeight);

        int newX = ((screenWidth - newWidth) / 2);
        int newY = ((screenHeight - newHeight) / 2);

        setLocation(newX, newY);
    }

}
