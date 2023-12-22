package org.dstadler.compat;

import org.apache.poi.javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class IOMetadataImpl extends IIOMetadata {
    private final Float horizontal;
    private final Float vertical;

    protected IOMetadataImpl(Float horizontal, Float vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

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

        if (horizontal != null) {
            Element horizontalElement = doc.createElement("HorizontalPixelSize");
            rootElement.appendChild(horizontalElement);
            horizontalElement.setAttribute("value", String.valueOf(horizontal));
        }

        if (vertical != null) {
            Element verticalElement = doc.createElement("VerticalPixelSize");
            rootElement.appendChild(verticalElement);
            verticalElement.setAttribute("value", String.valueOf(vertical));
        }

        return rootElement;
    }
}
