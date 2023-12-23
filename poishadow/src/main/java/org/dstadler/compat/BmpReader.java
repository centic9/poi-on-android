package org.dstadler.compat;

import org.apache.poi.java.awt.image.BufferedImage;
import org.apache.poi.javax.imageio.ImageReader;
import org.apache.poi.javax.imageio.metadata.IIOMetadata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Image reader for BMP files.
 */
public class BmpReader extends ImageReader {
    /**
     * The BMP's magic bytes
     */
    private static final byte[] BMP_MAGIC_BYTES = new byte[]{0x42, 0x4D};

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage read(int imageIndex) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        //set to with offset which is followed by height
        buffer.position(0x12);
        int width = buffer.getInt();
        int height = buffer.getInt();
        buffer.rewind();

        return new BufferedImage(width, height);
    }

    /**
     * Specific for BMP image files.<p>
     * {@inheritDoc}
     */
    @Override
    protected boolean canRead(BufferedInputStream inputStream) {
        return magicBytesPresent(BMP_MAGIC_BYTES, inputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        //set to XpixelsPerM offset which is followed by YpixelsPerM
        buffer.position(0x26);
        int yPixelsPerMeter = buffer.getInt();
        int xPixelsPerMeter = buffer.getInt();

        buffer.rewind();
        return new IOMetadataImpl(1000.0F / xPixelsPerMeter,
                1000.0F / yPixelsPerMeter);
    }
}
