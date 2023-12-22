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

/**
 * Shadow class for {@link javax.imageio.ImageReader}. Adds some compatibility to systems that do
 * not have access to javax.imageio.
 */
public abstract class ImageReader {
    /**
     * The buffer used when reading.
     */
    protected ByteBuffer buffer;

    /**
     * Sets the input stream.
     *
     * @param input Must be a {@link ImageInputStream}.
     */
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

    /**
     * Returns true if the magic bytes given match the same starting bytes in the given inputStream.
     *
     * @param magic       the magic bytes to match.
     * @param inputStream The input stream to use.
     * @return true if the magic bytes given match the same starting bytes in the given inputStream.
     */
    protected boolean magicBytesPresent(byte[] magic, BufferedInputStream inputStream) {
        byte[] bytes = new byte[magic.length];
        inputStream.mark(magic.length);

        try {
            inputStream.read(bytes, 0, magic.length);
            inputStream.reset();
        } catch (IOException e) {
            return false;
        }

        return Arrays.equals(bytes, magic);
    }

    /**
     * Returns a {@link BufferedImage} by reading the buffer.
     *
     * @param imageIndex this is ignored.
     * @return a {@link BufferedImage} by reading the buffer.
     */
    public abstract BufferedImage read(int imageIndex);

    /**
     * Returns true if the inherited class can read this input stream as a specific image.
     *
     * @param inputStream The input stream to use.
     * @return true if the inherited class can read this input stream as a specific image.
     */
    protected abstract boolean canRead(BufferedInputStream inputStream);

    /**
     * Returns a {@link IIOMetadata} by reading the buffer.
     *
     * @param imageIndex ignored.
     * @return a {@link IIOMetadata} by reading the buffer.
     * @throws IOException If an error occurs during reading.
     */
    public abstract IIOMetadata getImageMetadata(int imageIndex) throws IOException;

    /**
     * Disposes of any used resources.
     */
    public void dispose() {
        buffer = null;
    }
}
