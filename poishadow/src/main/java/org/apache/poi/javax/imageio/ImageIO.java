package org.apache.poi.javax.imageio;




import org.dstadler.compat.ImageInputStreamImpl;
import org.dstadler.compat.PngReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.javax.imageio.stream.ImageInputStream;

public final class ImageIO {
    private static final List<org.apache.poi.javax.imageio.ImageReader> imageReaders = new ArrayList<>();

    static {
        imageReaders.add(new PngReader());
    }

    public static ImageInputStream createImageInputStream(Object input) throws IOException {
        if (!(input instanceof InputStream)) {
            throw new IllegalArgumentException("input is not an instance of InputStream");
        }
        return new ImageInputStreamImpl((InputStream) input);
    }

    public static Iterator<ImageReader> getImageReaders(Object input){
        if (!(input instanceof ImageInputStream)) {
            throw new IllegalArgumentException("input is not an instance of ImageInputStream");
        }

        return new ArrayList<>(imageReaders).iterator();
    }


}
