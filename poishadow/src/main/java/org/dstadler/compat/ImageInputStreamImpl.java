package org.dstadler.compat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;

public class ImageInputStreamImpl implements ImageInputStream {
    private InputStream stream;

    public ImageInputStreamImpl(InputStream inputStream){
        stream = inputStream;
    }

    public InputStream getInputStream() {
        return stream;
    }
}
