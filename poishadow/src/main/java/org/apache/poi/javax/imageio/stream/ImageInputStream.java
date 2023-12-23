package org.apache.poi.javax.imageio.stream;

import java.io.BufferedInputStream;
import java.io.Closeable;

/**
 * The shadow interface for {@link javax.imageio.stream.ImageInputStream}. Adds some compatibility
 * to systems that do not have access to javax.imageio.
 */
public interface ImageInputStream extends Closeable {

    /**
     * Returns the input stream to use
     *
     * @return the input stream to use
     */
    BufferedInputStream getInputStream();
}
