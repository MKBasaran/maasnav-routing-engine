package com.group8.phase1.views.components;

import com.group8.phase1.ErrorUI.ErrorShower;
import com.group8.phase1.imageutil.Resize;
import com.group8.phase1.map.IndexPainter;
import com.group8.phase1.sociometric.calculation.FeatureCalculator;

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

public class SideBar2 extends JPanel {


    private Sidebar mainSidebar;
    private BufferedImage StatsIcon;
    private BufferedImage deleteIcon;
    private JLabel scoreAmenities;
    private JLabel scoreTourism;
    private JLabel scoreShops;
    private JLabel scoreTransport;
    private JLabel scoreTotal;

    /**
     * Constructs a new SideBar2 object.
     *
     * @param mainSidebar the Sidebar object
     */
    public SideBar2(Sidebar mainSidebar) {
        this.mainSidebar = mainSidebar;
        this.setLayout(new BorderLayout());
        int top = 10, left = 10, bottom = 10, right = 10;
        this.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        resizeAndLoadPhotos();
        initComponents();
    }

    /**
     * Initializes the components of the SideBar2 class.
     * This method sets up the input panel, buttons panel, score panel, and interaction panel.
     */
    private void initComponents() {
        JPanel inputPanel = new JPanel();
        GridBagLayout layout =new GridBagLayout();

        inputPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        //icon for input field
        JLabel statphoto=new JLabel();
        statphoto.setIcon(new ImageIcon(StatsIcon));

        JButton clearBtn = new JButton();
        clearBtn.setName("clearAmenityInfoButton");
        clearBtn.setIcon(new ImageIcon(deleteIcon));
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setPreferredSize(new Dimension(35, 35)); // Set the button size to 20 by 20 pixels

        //input field
        JTextField inputField = new RoundTextField("Enter postal code:");
        inputField.setName("inputField");
        addFocusListener(inputField, "Enter postal code:");

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 5;
        gbc.ipadx = 5;
        inputPanel.add(statphoto,gbc);


        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipady = 20;
        gbc.ipadx = 50;
        inputPanel.add(inputField, gbc);

        gbc.fill = GridBagConstraints.NONE; // Ensure the button keeps its preferred size
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.ipadx = 0;
        gbc.ipady = 0;
        inputPanel.add(clearBtn, gbc);

        inputPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        inputPanel.setPreferredSize(new Dimension(320, 150));

        inputPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        inputPanel.setPreferredSize(new Dimension(320,150));

        //Panel for buttons
        JPanel buttonsPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.setHgap(5);
        buttonsPanel.setLayout(gridLayout);

        JButton startBtn=new JButton("See Details");
        startBtn.setName("scoreButton");
        startBtn.setBackground(Color.white);
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startIndexCalculation(inputField.getText());
            }
        });

        clearBtn.setBackground(Color.white);
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scoreAmenities.setText("Amenities Score: ");
                scoreTourism.setText("Tourism Score: ");
                scoreShops.setText("Shops Score: ");
                scoreTransport.setText("Transport Score: ");
                scoreTotal.setText("Total Score: ");
                inputField.setText("Enter postal code:");

            }
        });
        JButton backBtn=new JButton("Back");
        backBtn.setName("toRoutingButton");
        backBtn.setBackground(Color.white);
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainSidebar.switchToSidebar();
                mainSidebar.mapViewer.setOverlayPainter(mainSidebar.painter);
                mainSidebar.mapViewer.repaint();
            }
        });


        buttonsPanel.add(startBtn);
        buttonsPanel.add(backBtn);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


        //Pannel for amenities/tourism/shops
        JPanel scorePanel=new JPanel();
        GridLayout grid1Layout = new GridLayout(3,1);
        grid1Layout.setHgap(5);
        scorePanel.setLayout(grid1Layout);

        JLabel scoreA = new JLabel("Amenities Score: ");
        scoreAmenities=scoreA;
        scoreA.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel scoreT = new JLabel("Tourism Score: ");
        scoreTourism=scoreT;
        scoreT.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));;

        JLabel scoreS = new JLabel("Shops Score: ");
        scoreShops=scoreS;
        scoreS.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel scoreTr = new JLabel("Transport Score: ");
        scoreTransport = scoreTr;
        scoreTransport.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel scoreTotal = new JLabel("Total Score: ");
        this.scoreTotal = scoreTotal;
        scoreTotal.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        scorePanel.add(scoreA);
        scorePanel.add(scoreT);
        scorePanel.add(scoreS);
        scorePanel.add(scoreTr);
        scorePanel.add(scoreTotal);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


        JPanel interactionPanel = new JPanel();
        interactionPanel.setLayout(new BoxLayout(interactionPanel, BoxLayout.Y_AXIS));
        interactionPanel.add(inputPanel);
        interactionPanel.add(buttonsPanel);
        interactionPanel.add(scorePanel);

        this.add(interactionPanel, BorderLayout.NORTH);


        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 400));

    }

    /**
     * Adds a focus listener to the given text field. When the text field gains focus, if its current text is equal to the prompt text, it clears the text field. When the text field
     * loses focus, if its trimmed text is empty, it sets the text field to the prompt text.
     *
     * @param textField the text field to add the focus listener to
     * @param promptText the prompt text to be displayed in the text field when it is empty and loses focus
     */

    void addFocusListener(JTextField textField, String promptText) {
        textField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(promptText)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().trim().equals("")) {
                    textField.setText(promptText);
                }
            }

        });
    }

    /**
     * Resizes and loads photos for the SideBar2 class.
     * This method resizes the AccessImage.png and Delete.png files to specific dimensions and loads them into the StatsIcon and deleteIcon variables respectively.
     */
    private void resizeAndLoadPhotos() {
        try {
            URL imageUrl = getClass().getResource("AccessImage.png");
            if (imageUrl != null) {
                StatsIcon = Resize.resize(ImageIO.read(imageUrl), 70, 70);
            }
            URL imageUrl2 = getClass().getResource("delete.png");
            if(imageUrl2!= null){
                deleteIcon = Resize.resize(ImageIO.read(imageUrl2), 25, 25);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Calculates the start index based on the given postal code.
     *
     * @param postCode The postal code to calculate the start index.
     */
    public void startIndexCalculation(String postCode){
        if (checkValidInput(postCode)) {
            FeatureCalculator featureCalculator = new FeatureCalculator(postCode);
            double postCodeScore = featureCalculator.getTotalScore();
            double amenityScore = featureCalculator.getAmenityScore();
            double tourismScore = featureCalculator.getTourismScore();
            double shopScore = featureCalculator.getShopScore();
            double transportScore = featureCalculator.getTransportScore();
            scoreAmenities.setText("Amenities Score: " + amenityScore);
            scoreTourism.setText("Tourism Score: " + tourismScore);
            scoreShops.setText("Shops Score: " + shopScore);
            scoreTransport.setText("Transport Score: " + transportScore);
            scoreTotal.setText("Total Score: " + postCodeScore);

            mainSidebar.mapViewer.setOverlayPainter(new IndexPainter(featureCalculator.getAmenityNodes(), featureCalculator.getShopNodes(), featureCalculator.getTourismNodes(), featureCalculator.getStops(), featureCalculator.getRouteToStops()));
            mainSidebar.mapViewer.repaint();
        } else {
            showError(postCode);
        }
    }

    /**
     * Displays an error message with the given postal code.
     *
     * @param postCode The postal code that caused the error.
     */
    public void showError(String postCode) {
        String postalError = "Invalid postal code: " + postCode;
        new ErrorShower(postalError);
    }

    /**
     * Checks if the provided postal code is a valid input.
     *
     * @param postCode The postal code to validate.
     * @return True if the postal code is valid, false otherwise.
     */
    public boolean checkValidInput(String postCode) {
        String regex = "^[0-9]{4}[A-Z]{2}$";
        return !postCode.equals("Enter postal code:") && !postCode.equals("") && postCode.matches(regex);
    }
}