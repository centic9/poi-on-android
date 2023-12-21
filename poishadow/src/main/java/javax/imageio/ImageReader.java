package javax.imageio;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.stream.ImageInputStream;

public abstract class ImageReader {
    private ByteBuffer buffer;

    public ImageReader(ImageInputStream inputStream){
        try {
            this.buffer = ByteBuffer.wrap(IOUtils.toByteArray(inputStream.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
