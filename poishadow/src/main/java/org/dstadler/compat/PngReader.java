package org.dstadler.compat;

import org.apache.poi.java.awt.image.BufferedImage;
import org.apache.poi.javax.imageio.ImageReader;
import org.apache.poi.util.Units;

import java.nio.ByteOrder;
import java.util.OptionalInt;

public class PngReader extends ImageReader {
    @Override
    public BufferedImage read(int imageIndex) {
        //Move position to PNG header; 11.2.2 IHDR Image header
        buffer.position(16);
        int width = buffer.getInt();
        int height = buffer.getInt();

        //Find the pHYs chunk; 11.3.5.3 pHYs Physical pixel dimensions
        buffer.rewind();
        byte[] physChunk = {'p', 'H', 'Y', 's'};
        OptionalInt indexOfPhys = indexOf(buffer, physChunk);

        //If the pHYs chunk is present, use the pixel sizes
        if (indexOfPhys.isPresent()) {
            buffer.position(indexOfPhys.getAsInt() + physChunk.length);
            int ppuX = buffer.getInt();
            int ppuY = buffer.getInt();

            //The following values are defined for the unit specifier:
            // 0 = unit is unknown
            // 1 = unit is the meter
            int unit = Byte.toUnsignedInt(buffer.get());
            if (unit == 0) {
                ppuX = 0;
                ppuY = 0;
            }
        }

        return new BufferedImage(width, height, 0, 0);
    }

    protected byte[] magicBytes(){
        //PNG signature
        return new byte[]{(byte) (137 & 0xFF), (byte) (80 & 0xFF), (byte) (78 & 0xFF),
                (byte) (71 & 0xFF), (byte) (13 & 0xFF), (byte) (10 & 0xFF), (byte) (26 & 0xFF),
                (byte) (10 & 0xFF)};
    }


}
