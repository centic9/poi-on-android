package org.apache.poi.java.awt.image;

public class BufferedImage {
   private final int width;
   private final int height;

    public BufferedImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
