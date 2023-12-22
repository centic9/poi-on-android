package org.dstadler.compat;

import org.apache.poi.javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The compatibility implementation for {@link IIOMetadata}.
 */
public class IOMetadataImpl extends IIOMetadata {
    /**
     * The horizontal pixel size
     */
    private final Float horizontalPixelSize;

    /**
     * The vertical pixel size
     */
    private final Float verticalPixelSize;

    /**
     * Constructor
     *
     * @param horizontalPixelSize The horizontal pixel size
     * @param verticalPixelSize The vertical pixel size
     */
    protected IOMetadataImpl(Float horizontalPixelSize, Float verticalPixelSize) {
        this.horizontalPixelSize = horizontalPixelSize;
        this.verticalPixelSize = verticalPixelSize;
    }

    /**
     * Currently only returns horizontal and vertical pixles sizes.<p>
     * {@inheritDoc}
     */
    @Override
    public Node getAsTree(String formatName) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Dimension");

        if (horizontalPixelSize != null) {
            Element horizontalElement = doc.createElement("HorizontalPixelSize");
            rootElement.appendChild(horizontalElement);
            horizontalElement.setAttribute("value", String.valueOf(horizontalPixelSize));
        }

        if (verticalPixelSize != null) {
            Element verticalElement = doc.createElement("VerticalPixelSize");
            rootElement.appendChild(verticalElement);
            verticalElement.setAttribute("value", String.valueOf(verticalPixelSize));
        }

        return rootElement;
    }
}
