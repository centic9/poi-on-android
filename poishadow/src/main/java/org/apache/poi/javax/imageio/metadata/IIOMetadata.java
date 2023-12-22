package org.apache.poi.javax.imageio.metadata;

import org.w3c.dom.Node;

public abstract class IIOMetadata {

    public abstract Node getAsTree(String formatName);

}
