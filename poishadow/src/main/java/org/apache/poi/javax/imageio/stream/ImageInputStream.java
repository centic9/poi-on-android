package org.apache.poi.javax.imageio.stream;

import com.sun.imageio.spi.FileImageInputStreamSpi;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.InputStream;

public interface ImageInputStream extends Closeable {
    BufferedInputStream getInputStream();
}
