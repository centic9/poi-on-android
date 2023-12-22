package org.dstadler.compat;

import org.apache.poi.javax.imageio.stream.ImageInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageInputStreamImpl implements ImageInputStream {
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
