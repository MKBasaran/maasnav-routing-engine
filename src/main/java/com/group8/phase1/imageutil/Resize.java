package com.group8.phase1.imageutil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The {@code Resize} enum contains a function to resize images.
 */
public enum Resize {
    ;

    /**
     * Resizes the input image to the specified dimensions while maintaining aspect ratio.
     *
     * @param img  the input image to resize
     * @param newW the new width of the resized image
     * @param newH the new height of the resized image
     * @return the resized image
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        double aspectRatio = (double) img.getWidth() / img.getHeight();

        if (newW == 0) {
            newW = (int) (newH * aspectRatio);
        } else if (newH == 0) {
            newH = (int) (newW / aspectRatio);
        } else {
            double targetAspectRatio = (double) newW / newH;
            if (aspectRatio > targetAspectRatio) {
                newH = (int) (newW / aspectRatio);
            } else {
                newW = (int) (newH * aspectRatio);
            }
        }

        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
