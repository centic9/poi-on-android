package org.apache.poi.java.awt.image;

public class BufferedImage {
   private final int width;
   private final int height;

   private final float horizontalPixelSize;

   private final float verticalPixelSize;

    public BufferedImage(int width, int height, float horizontalPixelSize, float verticalPixelSize) {
        this.width = width;
        this.height = height;
        this.horizontalPixelSize = horizontalPixelSize;
        this.verticalPixelSize = verticalPixelSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getHorizontalPixelSize() {
        return horizontalPixelSize;
    }

    public float getVerticalPixelSize() {
        return verticalPixelSize;
    }
}
