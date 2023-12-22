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

public final class ImageIO {
    private static final List<org.apache.poi.javax.imageio.ImageReader> imageReaders = new ArrayList<>();

    static {
        imageReaders.add(new PngReader());
        imageReaders.add(new JpgReader());
        imageReaders.add(new BmpReader());
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

        for(ImageReader reader : imageReaders){
            if (reader.canRead(((ImageInputStream) input).getInputStream())){
                ArrayList<ImageReader> returnList = new ArrayList<>();
                returnList.add(reader);
                return returnList.iterator();
            }
        }

        return Collections.emptyIterator();
    }


}
