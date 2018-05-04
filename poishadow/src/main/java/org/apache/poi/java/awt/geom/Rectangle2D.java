package org.apache.poi.java.awt.geom;

/**
 * See {@link java.awt.geom.Rectangle2D}
 */
public abstract class Rectangle2D {
    public abstract double getX();

    public abstract double getY();

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract boolean isEmpty();

    public abstract void setRect(double v, double v1, double v2, double v3);

    public abstract int outcode(double v, double v1);

    public abstract Rectangle2D createIntersection(Rectangle2D rectangle2D);

    public abstract Rectangle2D createUnion(Rectangle2D rectangle2D);
}
