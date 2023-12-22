package org.apache.poi.javax.imageio.metadata;

import org.w3c.dom.Node;

/**
 * Shadow class for {@link javax.imageio.metadata.IIOMetadata}. Adds some compatibility to systems
 * that do not have access to javax.imageio.
 */
public abstract class IIOMetadata {
    /**
     * Returns an {@link org.w3c.dom.Element} describing metadata for images.
     *
     * @param formatName the desired metadata format.
     * @return an {@link org.w3c.dom.Element} describing metadata for images.
     */
    public abstract Node getAsTree(String formatName);
}
