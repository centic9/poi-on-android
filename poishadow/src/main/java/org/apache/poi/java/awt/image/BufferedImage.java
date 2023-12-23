package org.apache.poi.java.awt.image;

/**
 * Shadow class for java.awt.image.BufferedImage. Adds some compatibility to systems that do
 * not have access to javax.awt.
 */
public class BufferedImage {
    /**
     * The pixel width of the image.
     */
    private final int width;

    /**
     * The pixel height of the image.
     */
    private final int height;

    /**
     * Constructor
     *
     * @param width  The pixel width of the image.
     * @param height The pixel height of the image.
     */
    public BufferedImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns The pixel width of the image.
     *
     * @return The pixel width of the image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns The pixel height of the image.
     *
     * @return The pixel height of the image.
     */
    public int getHeight() {
        return height;
    }

}
