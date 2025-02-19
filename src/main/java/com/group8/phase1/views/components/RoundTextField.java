package com.group8.phase1.views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A JTextField with rounded corners for smoother design
 */
public class RoundTextField extends JTextField {
    private Shape shape;

    /**
     * Constructs a RoundTextField with the specified initial text.
     *
     * @param text The initial text to be displayed in the text field.
     */
    public RoundTextField(String text) {
        super(text);
        setOpaque(false);
    }

    /**
     * Paints the component, filling a rounded rectangle with the background color.
     *
     * @param g The Graphics context.
     */
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        super.paintComponent(g);
    }

    /**
     * Paints the border of the component as a rounded rectangle.
     *
     * @param g The Graphics context.
     */
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
    }


    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
        return shape.contains(x, y);
    }
    public void changeRoundText(String text){
        setText(text);
    }
}
