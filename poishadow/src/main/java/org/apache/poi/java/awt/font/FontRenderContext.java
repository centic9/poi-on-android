package org.apache.poi.java.awt.font;

import java.awt.geom.AffineTransform;

public class FontRenderContext {

    /**
     * Constructs a new <code>FontRenderContext</code>
     * object.
     *
     */
    protected FontRenderContext() {
    }

    /**
     * Constructs a <code>FontRenderContext</code> object from an
     * optional {@link AffineTransform} and two <code>boolean</code>
     * values that determine if the newly constructed object has
     * anti-aliasing or fractional metrics.
     * In each case the boolean values <CODE>true</CODE> and <CODE>false</CODE>
     * correspond to the rendering hint values <CODE>ON</CODE> and
     * <CODE>OFF</CODE> respectively.
     * <p>
     * To specify other hint values, use the constructor which
     * specifies the rendering hint values as parameters :
     * {@link #FontRenderContext(AffineTransform, Object, Object)}.
     * @param tx the transform which is used to scale typographical points
     * to pixels in this <code>FontRenderContext</code>.  If null, an
     * identity transform is used.
     * @param isAntiAliased determines if the newly constructed object
     * has anti-aliasing.
     * @param usesFractionalMetrics determines if the newly constructed
     * object has fractional metrics.
     */
    public FontRenderContext(AffineTransform tx,
                             boolean isAntiAliased,
                             boolean usesFractionalMetrics) {
    }

    /**
     * Constructs a <code>FontRenderContext</code> object from an
     * optional {@link AffineTransform} and two <code>Object</code>
     * values that determine if the newly constructed object has
     * anti-aliasing or fractional metrics.
     * @param tx the transform which is used to scale typographical points
     * to pixels in this <code>FontRenderContext</code>.  If null, an
     * identity tranform is used.
     * @param aaHint - one of the text antialiasing rendering hint values
     * defined in {@link java.awt.RenderingHints java.awt.RenderingHints}.
     * Any other value will throw <code>IllegalArgumentException</code>.
     * {@link java.awt.RenderingHints#VALUE_TEXT_ANTIALIAS_DEFAULT VALUE_TEXT_ANTIALIAS_DEFAULT}
     * may be specified, in which case the mode used is implementation
     * dependent.
     * @param fmHint - one of the text fractional rendering hint values defined
     * in {@link java.awt.RenderingHints java.awt.RenderingHints}.
     * {@link java.awt.RenderingHints#VALUE_FRACTIONALMETRICS_DEFAULT VALUE_FRACTIONALMETRICS_DEFAULT}
     * may be specified, in which case the mode used is implementation
     * dependent.
     * Any other value will throw <code>IllegalArgumentException</code>
     * @throws IllegalArgumentException if the hints are not one of the
     * legal values.
     * @since 1.6
     */
    public FontRenderContext(AffineTransform tx, Object aaHint, Object fmHint){
    }

    /**
     * Indicates whether or not this <code>FontRenderContext</code> object
     * measures text in a transformed render context.
     * @return  <code>true</code> if this <code>FontRenderContext</code>
     *          object has a non-identity AffineTransform attribute.
     *          <code>false</code> otherwise.
     * @see     java.awt.font.FontRenderContext#getTransform
     * @since   1.6
     */
    public boolean isTransformed() {
        return true;
    }

    /**
     * Returns the integer type of the affine transform for this
     * <code>FontRenderContext</code> as specified by
     * {@link AffineTransform#getType()}
     * @return the type of the transform.
     * @see AffineTransform
     * @since 1.6
     */
    public int getTransformType() {
        return 0;
    }

    /**
    *   Gets the transform that is used to scale typographical points
    *   to pixels in this <code>FontRenderContext</code>.
    *   @return the <code>AffineTransform</code> of this
    *    <code>FontRenderContext</code>.
    *   @see AffineTransform
    */
    public AffineTransform getTransform() {
        return new AffineTransform();
    }

    /**
    * Returns a boolean which indicates whether or not some form of
    * antialiasing is specified by this <code>FontRenderContext</code>.
    * Call {@link #getAntiAliasingHint() getAntiAliasingHint()}
    * for the specific rendering hint value.
    *   @return    <code>true</code>, if text is anti-aliased in this
    *   <code>FontRenderContext</code>; <code>false</code> otherwise.
    *   @see        java.awt.RenderingHints#KEY_TEXT_ANTIALIASING
    *   @see #FontRenderContext(AffineTransform,boolean,boolean)
    *   @see #FontRenderContext(AffineTransform,Object,Object)
    */
    public boolean isAntiAliased() {
        return true;
    }

    /**
    * Returns a boolean which whether text fractional metrics mode
    * is used in this <code>FontRenderContext</code>.
    * Call {@link #getFractionalMetricsHint() getFractionalMetricsHint()}
    * to obtain the corresponding rendering hint value.
    *   @return    <code>true</code>, if layout should be performed with
    *   fractional metrics; <code>false</code> otherwise.
    *               in this <code>FontRenderContext</code>.
    *   @see java.awt.RenderingHints#KEY_FRACTIONALMETRICS
    *   @see #FontRenderContext(AffineTransform,boolean,boolean)
    *   @see #FontRenderContext(AffineTransform,Object,Object)
    */
    public boolean usesFractionalMetrics() {
        return true;
    }

    /**
     * Return the text anti-aliasing rendering mode hint used in this
     * <code>FontRenderContext</code>.
     * This will be one of the text antialiasing rendering hint values
     * defined in {@link java.awt.RenderingHints java.awt.RenderingHints}.
     * @return  text anti-aliasing rendering mode hint used in this
     * <code>FontRenderContext</code>.
     * @since 1.6
     */
    public Object getAntiAliasingHint() {
        return null;
    }

    /**
     * Return the text fractional metrics rendering mode hint used in this
     * <code>FontRenderContext</code>.
     * This will be one of the text fractional metrics rendering hint values
     * defined in {@link java.awt.RenderingHints java.awt.RenderingHints}.
     * @return the text fractional metrics rendering mode hint used in this
     * <code>FontRenderContext</code>.
     * @since 1.6
     */
    public Object getFractionalMetricsHint() {
        return null;
    }

    /**
     * Return true if obj is an instance of FontRenderContext and has the same
     * transform, antialiasing, and fractional metrics values as this.
     * @param obj the object to test for equality
     * @return <code>true</code> if the specified object is equal to
     *         this <code>FontRenderContext</code>; <code>false</code>
     *         otherwise.
     */
    public boolean equals(Object obj) {
        try {
            return equals((FontRenderContext)obj);
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * Return true if rhs has the same transform, antialiasing,
     * and fractional metrics values as this.
     * @param rhs the <code>FontRenderContext</code> to test for equality
     * @return <code>true</code> if <code>rhs</code> is equal to
     *         this <code>FontRenderContext</code>; <code>false</code>
     *         otherwise.
     * @since 1.4
     */
    public boolean equals(FontRenderContext rhs) {
        return true;
    }

    /**
     * Return a hashcode for this FontRenderContext.
     */
    public int hashCode() {
        return super.hashCode();
    }
}
