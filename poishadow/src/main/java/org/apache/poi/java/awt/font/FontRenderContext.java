package org.apache.poi.java.awt.font;

import java.awt.geom.AffineTransform;

public class FontRenderContext {
    public FontRenderContext(AffineTransform o, boolean a, boolean b) {
        //You can't crash here yet! If you do, a static field in SheetUtil will fail to load.
        //Then next time SheetUtil will have to be accessed, a NoClassDefFoundError("SheetUtil")
        //will be thrown!
    }
}