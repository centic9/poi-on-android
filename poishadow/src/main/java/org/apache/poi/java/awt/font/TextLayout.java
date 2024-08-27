package org.apache.poi.java.awt.font;

import java.text.AttributedCharacterIterator;

public class TextLayout {
    public TextLayout(AttributedCharacterIterator iterator, FontRenderContext fontRenderContext) {
        //This is called in:
        //SXSSFSheet.java:106
        //AutoSizeColumnTracker.java:117
        //SheetUtil.java:353
        //This tricks SXSSFSheet constructor into not creating AutoSizeColumnTracker.
        throw new NoClassDefFoundError("X11FontManager");
    }
}
