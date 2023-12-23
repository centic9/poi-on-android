package org.apache.poi.javax.imageio;


import org.apache.poi.javax.imageio.stream.ImageInputStream;
import org.dstadler.compat.BmpReader;
import org.dstadler.compat.ImageInputStreamImpl;
import org.dstadler.compat.JpgReader;
import org.dstadler.compat.PngReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Shadow class for {@link javax.imageio.ImageIO}. Adds some compatibility to systems that do
 * not have access to javax.imageio.
 */
public final class ImageIO {

    /**
     * List of available {@link ImageReader}s.
     */
    private static final List<org.apache.poi.javax.imageio.ImageReader> imageReaders = new ArrayList<>();

    static {
        //Initializes the image readers.
        imageReaders.add(new PngReader());
        imageReaders.add(new JpgReader());
        imageReaders.add(new BmpReader());
    }

    /**
     * Creates an {@link ImageInputStreamImpl} that wraps the given inputStream into
     * a {@link java.io.BufferedInputStream}.
     *
     * @param input must be a {@link InputStream}.
     * @return an {@link ImageInputStreamImpl} that wraps the given inputStream into
     * a {@link java.io.BufferedInputStream}.
     * @throws IOException If an error occurs while reading.
     */
    public static ImageInputStream createImageInputStream(Object input) throws IOException {
        if (!(input instanceof InputStream)) {
            throw new IllegalArgumentException("input is not an instance of InputStream");
        }
        return new ImageInputStreamImpl((InputStream) input);
    }

    /**
     * Returns a iterator of usable {@link ImageReader}s based on the given input.
     * @param input Must be a {@link ImageInputStream}.
     * @return a iterator of usable {@link ImageReader}s based on the given input.
     */
    public static Iterator<ImageReader> getImageReaders(Object input) {
        if (!(input instanceof ImageInputStream)) {
            throw new IllegalArgumentException("input is not an instance of ImageInputStream");
        }

        for (ImageReader reader : imageReaders) {
            if (reader.canRead(((ImageInputStream) input).getInputStream())) {
                ArrayList<ImageReader> returnList = new ArrayList<>();
                returnList.add(reader);
                return returnList.iterator();
            }
        }

        return Collections.emptyIterator();
    }
}
