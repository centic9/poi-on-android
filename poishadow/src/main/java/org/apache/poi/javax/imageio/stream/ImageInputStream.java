package org.apache.poi.javax.imageio.stream;

import java.io.BufferedInputStream;
import java.io.Closeable;

public interface ImageInputStream extends Closeable {
    BufferedInputStream getInputStream();
}
