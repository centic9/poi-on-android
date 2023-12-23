package org.dstadler.compat;

import org.apache.poi.javax.imageio.stream.ImageInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The compatibility implementation for {@link ImageInputStream}.
 */
public class ImageInputStreamImpl implements ImageInputStream {
    /**
     * The input stream.
     */
    private final BufferedInputStream stream;

    /**
     * Constructor
     *
     * @param inputStream the input stream to use.
     */
    public ImageInputStreamImpl(InputStream inputStream){
        stream = new BufferedInputStream(inputStream);
    }

    /**
     * {@inheritDoc}
     */
    public BufferedInputStream getInputStream() {
        return stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        stream.close();
    }
}
