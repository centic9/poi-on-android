package org.apache.poi.javax.imageio;

import org.apache.commons.io.IOUtils;
import org.apache.poi.java.awt.image.BufferedImage;
import org.apache.poi.javax.imageio.metadata.IIOMetadata;
import org.apache.poi.javax.imageio.stream.ImageInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public abstract class ImageReader {
    protected ByteBuffer buffer;

    public void setInput(Object input) {
        if (!(input instanceof ImageInputStream)) {
            throw new IllegalArgumentException("input is not an instance of InputStream");
        }
        ImageInputStream imageInputStream = (ImageInputStream) input;

        try {
            if (!canRead(imageInputStream.getInputStream())) {
                throw new IllegalArgumentException("Incorrect input type!");
            }

            this.buffer = ByteBuffer.wrap(IOUtils.toByteArray(imageInputStream.getInputStream()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect input type!");
        }
    }

    /**
     * Gets the first index of the given <code>byte[]</code> within the {@link ByteBuffer}.
     *
     * @param buf The {@link ByteBuffer} to search.
     * @param b   the byte array to search for.
     * @return empty if not found, otherwise the first index found.
     */
    protected static OptionalInt indexOf(ByteBuffer buf, byte[] b) {
        if (b.length == 0) {
            return OptionalInt.empty();
        }
        return IntStream.rangeClosed(buf.position(), buf.limit() - b.length)
                .filter(i -> IntStream.range(0, b.length).allMatch(j -> buf.get(i + j) == b[j]))
                .findFirst();
    }

    /**
     * Gets the indices of the given <code>byte[]</code> within the {@link ByteBuffer}.
     *
     * @param buf The {@link ByteBuffer} to search.
     * @param b   the byte array to search for.
     * @return empty int array if not found, otherwise an int array of all found indices.
     */
    protected static int[] indicesOf(ByteBuffer buf, byte[] b) {
        if (b.length == 0) {
            return new int[0];
        }
        return IntStream.rangeClosed(buf.position(), buf.limit() - b.length)
                .filter(i -> IntStream.range(0, b.length).allMatch(j -> buf.get(i + j) == b[j]))
                .toArray();
    }

    public abstract BufferedImage read(int imageIndex);
    
    protected abstract boolean canRead(BufferedInputStream inputStream);

    public abstract IIOMetadata getImageMetadata(int var1) throws IOException;

    public void dispose() {
        buffer = null;
    }
}
