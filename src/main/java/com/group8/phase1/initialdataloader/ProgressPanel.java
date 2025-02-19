package com.group8.phase1.initialdataloader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The {@code ProgressPanel} class represents a panel used to display progress information
 * during the initial data loading process ( OSM database creation ).
 */
public class ProgressPanel extends JPanel {
    private JLabel statusLabel;
    private JProgressBar progressBar;

    /**
     * Constructs a new {@code ProgressPanel} with default settings.
     */
    public ProgressPanel() {
        initComponents();
        setupLayout();
    }

    /**
     * Initializes the components of the progress panel.
     */
    private void initComponents() {
        statusLabel = new JLabel("Loading...");
        statusLabel.setFont(new Font("Sans-Serif", Font.PLAIN, 16));
        progressBar = new JProgressBar();
        progressBar.setBackground(new Color(240, 240, 240)); // Light gray background
        progressBar.setForeground(new Color(3, 105, 208));
        progressBar.setValue(0);
    }

    /**
     * Sets up the layout of the progress panel.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Logo Image
        try {
            BufferedImage logoIcon = ImageIO.read(new File("src/main/resources/com/group8/phase1/Logo.png"));
            JLabel logoLabel = new JLabel(new ImageIcon(logoIcon));
            add(logoLabel, BorderLayout.CENTER);
        } catch (IOException e) {
            // Handle image loading error
        }
        JPanel centerPanel = new JPanel();

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(statusLabel);
        centerPanel.add(progressBar);

        add(centerPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets the progress value of the progress bar.
     *
     * @param value the progress value (between 0.0 and 1.0)
     */
    public void setProgress(double value) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue((int) (value * 100));
        });
    }

    /**
     * Sets the status message of the progress panel.
     *
     * @param status the status message to be displayed
     */
    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }

    /**
     * Sets the status message of the progress panel with a specific service description.
     *
     * @param service the service description
     * @param status  the status message to be displayed
     */
    public void setStatus(String service, String status) {
        SwingUtilities.invokeLater(() -> {
            if ("Routing".equals(service)) {
                statusLabel.setText("<html><b>Routing:</b> " + status + "</html>");
            } else if ("Database".equals(service)) {
                statusLabel.setText("<html><b>Database:</b> " + status + "</html>");
            }
        });
    }
}
