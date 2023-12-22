package org.dstadler.compat;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.javax.imageio.stream.ImageInputStream;

public class ImageInputStreamImpl implements ImageInputStream, Closeable {
    private final BufferedInputStream stream;

    public ImageInputStreamImpl(InputStream inputStream){
        stream = new BufferedInputStream(inputStream);
    }

    public BufferedInputStream getInputStream() {
        return stream;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
