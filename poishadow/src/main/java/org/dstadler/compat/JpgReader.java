package org.dstadler.compat;

import org.apache.poi.java.awt.image.BufferedImage;
import org.apache.poi.javax.imageio.ImageReader;
import org.apache.poi.javax.imageio.metadata.IIOMetadata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.OptionalInt;

import javax.imageio.metadata.IIOMetadataNode;

public class JpgReader extends ImageReader {
    @Override
    public BufferedImage read(int imageIndex) {
        //Start Of Frame (Baseline DCT), SOFn segment
        //Get the last SOF segment since that is what has the size
        byte[] sofn = {(byte) 0xFF, (byte) 0xC0};
        int[] indicesOfSof = indicesOf(buffer, sofn);
        int lastSofn = indicesOfSof[indicesOfSof.length - 1];

        buffer.position(lastSofn + 5);
        int height = buffer.getShort();
        int width = buffer.getShort();
        buffer.rewind();

        return new BufferedImage(width, height);
    }

    @Override
    protected boolean canRead(BufferedInputStream inputStream) {
        return false;
    }

    @Override
    public IIOMetadata getImageMetadata(int var1) throws IOException {
        //JFIF extension APP0 segment
        byte[] app0 = {(byte) 0xFF, (byte) 0xE0};
        OptionalInt indexOfApp0 = indexOf(buffer, app0);
        Float horizontal = null;
        Float vertical = null;

        if (indexOfApp0.isPresent()) {
            buffer.position(indexOfApp0.getAsInt() + 11);
            int unit = Byte.toUnsignedInt(buffer.get());
            int xDensity = buffer.getShort();
            int yDensity = buffer.getShort();


            // Pixel size
            if (unit != 0) {
                // 1 == dpi, 2 == dpc
                float scale = (unit == 1) ? 25.4F : 10.0F;
                horizontal = scale / xDensity;
                vertical = scale / yDensity;
            }
        }
        return new IOMetadataImpl(horizontal, vertical);
    }
}
