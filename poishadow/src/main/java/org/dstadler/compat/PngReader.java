package org.dstadler.compat;

import org.apache.poi.java.awt.image.BufferedImage;
import org.apache.poi.javax.imageio.ImageReader;
import org.apache.poi.javax.imageio.metadata.IIOMetadata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.OptionalInt;

/**
 * Image reader for PNG files.
 */
public class PngReader extends ImageReader {
    /**
     * Magic bytes for a PNG file.
     */
    private final static byte[] PNG_MAGIC_BYTES = new byte[]{(byte) (137 & 0xFF),
            (byte) (80 & 0xFF), (byte) (78 & 0xFF), (byte) (71 & 0xFF), (byte) (13 & 0xFF),
            (byte) (10 & 0xFF), (byte) (26 & 0xFF), (byte) (10 & 0xFF)};

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage read(int imageIndex) {
        //Move position to PNG header; 11.2.2 IHDR Image header
        buffer.position(16);
        int width = buffer.getInt();
        int height = buffer.getInt();
        buffer.rewind();

        return new BufferedImage(width, height);
    }

    /**
     * Specific for PNG image files.<p>
     * {@inheritDoc}
     */
    @Override
    protected boolean canRead(BufferedInputStream inputStream) {
        return magicBytesPresent(PNG_MAGIC_BYTES, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IIOMetadata getImageMetadata(int var1) throws IOException {
        //Find the pHYs chunk; 11.3.5.3 pHYs Physical pixel dimensions
        byte[] physChunk = {'p', 'H', 'Y', 's'};
        OptionalInt indexOfPhys = indexOf(buffer, physChunk);

        int ppuX = 0;
        int ppuY = 0;
        //If the pHYs chunk is present, use the pixel sizes
        if (indexOfPhys.isPresent()) {
            buffer.position(indexOfPhys.getAsInt() + physChunk.length);
            int ppuXValue = buffer.getInt();
            int ppuYValue = buffer.getInt();

            //The following values are defined for the unit specifier:
            // 0 = unit is unknown
            // 1 = unit is the meter
            int unit = Byte.toUnsignedInt(buffer.get());
            if (unit == 1) {
                ppuX = ppuXValue;
                ppuY = ppuYValue;
            }
        }

        buffer.rewind();
        Float horizontal = ppuX == 0 ? null : 1000.0F / ppuX;
        Float vertical = ppuY == 0 ? null : 1000.0F / ppuY;
        return new IOMetadataImpl(horizontal, vertical);
    }
}
