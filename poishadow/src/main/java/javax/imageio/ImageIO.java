package javax.imageio;




import org.dstadler.compat.ImageInputStreamImpl;
import org.dstadler.compat.PngReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.stream.ImageInputStream;

public final class ImageIO {
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

        ArrayList<ImageReader> readerArrayList = new ArrayList<>();
        readerArrayList.add(new PngReader((ImageInputStream) input));

        return readerArrayList.iterator();
    }


}
