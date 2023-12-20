package org.apache.poi.ss.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.apache.poi.util.Units.EMU_PER_PIXEL;

public class ImageUtils {
    private static final Logger LOG = LogManager.getLogger(ImageUtils.class);
    private static final int WIDTH_UNITS = 1024;
    private static final int HEIGHT_UNITS = 256;

    /**
     * Return the dimension of this image
     *
     * @param is   the stream containing the image data
     * @param type type of the picture: {@link Workbook#PICTURE_TYPE_JPEG},
     *             {@link Workbook#PICTURE_TYPE_PNG} or {@link Workbook#PICTURE_TYPE_DIB}
     * @return image dimension in pixels
     */
    public static Dimension getImageDimension(InputStream is, int type) {
        Dimension size = new Dimension();

        //Read PNG into a byte buffer
        byte[] data;
        try {
            data = IOUtils.toByteArray(is);
        } catch (IOException e) {
            //silently return if ImageIO failed to read the image
            LOG.atWarn().withThrowable(e).log("Failed to determine image dimensions");
            return size;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        //The fallback DPI:
        //if DPI is zero then assume standard 96 DPI
        //since cannot divide by zero
        int hdpi = Units.PIXEL_DPI;
        int vdpi = Units.PIXEL_DPI;

        if (type == Workbook.PICTURE_TYPE_PNG) {
            //Move position to PNG header; 11.2.2 IHDR Image header
            byteBuffer.position(16);
            int width = byteBuffer.getInt();
            int height = byteBuffer.getInt();

            //Find the pHYs chunk; 11.3.5.3 pHYs Physical pixel dimensions
            byteBuffer.rewind();
            byte[] physChunk = {'p', 'H', 'Y', 's'};
            OptionalInt indexOfPhys = indexOf(byteBuffer, physChunk);

            //If the pHYs chunk is present, use the pixel sizes
            if (indexOfPhys.isPresent()) {
                byteBuffer.position(indexOfPhys.getAsInt() + physChunk.length);
                int ppuX = byteBuffer.getInt();
                int ppuY = byteBuffer.getInt();

                //The following values are defined for the unit specifier:
                // 0 = unit is unknown
                // 1 = unit is the meter
                int unit = Byte.toUnsignedInt(byteBuffer.get());
                if (unit == 1) {
                    hdpi = ppuX;
                    vdpi = ppuY;
                }
            }

            size.width = width * Units.PIXEL_DPI / getDotsPerInch(hdpi);
            size.height = height * Units.PIXEL_DPI / getDotsPerInch(vdpi);
        } else if (type == Workbook.PICTURE_TYPE_JPEG) {
            //Start Of Frame (Baseline DCT), SOFn segment
            //Get the last SOF segment since that is what has the size
            byte[] sofn = {(byte) 0xFF, (byte) 0xC0};
            int[] indicesOfSof = indicesOf(byteBuffer, sofn);
            int lastSofn = indicesOfSof[indicesOfSof.length - 1];

            byteBuffer.position(lastSofn + 5);
            int height = byteBuffer.getShort();
            int width = byteBuffer.getShort();
            byteBuffer.rewind();

            //JFIF extension APP0 segment
            byte[] app0 = {(byte) 0xFF, (byte) 0xE0};
            OptionalInt indexOfApp0 = indexOf(byteBuffer, app0);
            if (indexOfApp0.isPresent()) {
                byteBuffer.position(indexOfApp0.getAsInt() + 11);
                int unit = Byte.toUnsignedInt(byteBuffer.get());
                int xDensity = byteBuffer.getShort();
                int yDensity = byteBuffer.getShort();

                //Units for the following pixel density fields
                //00 : No units; width:height pixel aspect ratio = Ydensity:Xdensity
                //01 : Pixels per inch (2.54 cm)
                //02 : Pixels per centimeter
                if (unit == 1) {
                    hdpi = xDensity;
                    vdpi = yDensity;
                } else if (unit == 2) {
                    hdpi = Math.round(xDensity * 2.54f);
                    vdpi = Math.round(yDensity * 2.54f);
                }
            }

            size.width = width * Units.PIXEL_DPI / hdpi;
            size.height = height * Units.PIXEL_DPI / vdpi;
        } else if (type == Workbook.PICTURE_TYPE_DIB) {
            ByteBuffer bb = byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

            //set to with offset which is followed by height
            bb.position(0x12);
            int width = bb.getInt();
            int height = bb.getInt();

            //set to XpixelsPerM offset which is followed by YpixelsPerM
            bb.position(0x26);
            int yPixelsPerMeter = bb.getInt();
            int xPixelsPerMeter = bb.getInt();

            size.width = width * Units.PIXEL_DPI / getDotsPerInch(xPixelsPerMeter);
            size.height = height * Units.PIXEL_DPI / getDotsPerInch(yPixelsPerMeter);
        }
        return size;
    }

    /**
     * Gets the first index of the given <code>byte[]</code> within the {@link ByteBuffer}.
     *
     * @param buf The {@link ByteBuffer} to search.
     * @param b   the byte array to search for.
     * @return empty if not found, otherwise the first index found.
     */
    public static OptionalInt indexOf(ByteBuffer buf, byte[] b) {
        if (b.length == 0) {
            return OptionalInt.empty();
        }
        return IntStream.rangeClosed(buf.position(), buf.limit() - b.length)
                .filter(i -> IntStream.range(0, b.length).allMatch(j -> buf.get(i + j) == b[j]))
                .findFirst();
    }

    /**
     * Gets the indices of the given <code>byte[]</code> within the {@link ByteBuffer}.
     *
     * @param buf The {@link ByteBuffer} to search.
     * @param b   the byte array to search for.
     * @return empty int array if not found, otherwise an int array of all found indices.
     */
    public static int[] indicesOf(ByteBuffer buf, byte[] b) {
        if (b.length == 0) {
            return new int[0];
        }
        return IntStream.rangeClosed(buf.position(), buf.limit() - b.length)
                .filter(i -> IntStream.range(0, b.length).allMatch(j -> buf.get(i + j) == b[j]))
                .toArray();
    }

    /**
     * Converts pixels per meter to dots per inch.
     *
     * @param pixelsPerMeters the pixels per meters to convert.
     * @return the converted dots per inch value.
     */
    private static int getDotsPerInch(int pixelsPerMeters) {
        return Math.round((float) pixelsPerMeters / 39.370079f);
    }

    /**
     * Calculate and set the preferred size (anchor) for this picture.
     *
     * @param scaleX the amount by which image width is multiplied relative to the original width.
     * @param scaleY the amount by which image height is multiplied relative to the original height.
     * @return the new Dimensions of the scaled picture in EMUs
     * @throws IllegalArgumentException if scale values lead to negative or infinite results
     * @throws IllegalStateException    if the picture data is corrupt
     */
    public static Dimension setPreferredSize(Picture picture, double scaleX, double scaleY) {
        ClientAnchor anchor = picture.getClientAnchor();
        boolean isHSSF = (anchor instanceof HSSFClientAnchor);
        PictureData data = picture.getPictureData();
        Sheet sheet = picture.getSheet();

        // in pixel
        final Dimension imgSize;
        try {
            imgSize = (scaleX == Double.MAX_VALUE || scaleY == Double.MAX_VALUE)
                    ? getImageDimension(UnsynchronizedByteArrayInputStream.builder().setByteArray(data.getData()).get(), data.getPictureType())
                    : new Dimension();
        } catch (IOException e) {
            // is actually impossible with ByteArray, but still declared in the interface
            throw new IllegalStateException(e);
        }

        // in emus
        final Dimension anchorSize = (scaleX != Double.MAX_VALUE || scaleY != Double.MAX_VALUE)
                ? ImageUtils.getDimensionFromAnchor(picture)
                : new Dimension();

        final double scaledWidth = (scaleX == Double.MAX_VALUE)
                ? imgSize.getWidth() : anchorSize.getWidth() / EMU_PER_PIXEL * scaleX;
        final double scaledHeight = (scaleY == Double.MAX_VALUE)
                ? imgSize.getHeight() : anchorSize.getHeight() / EMU_PER_PIXEL * scaleY;

        scaleCell(scaledWidth, anchor.getCol1(), anchor.getDx1(), anchor::setCol2, anchor::setDx2,
                isHSSF ? WIDTH_UNITS : 0, sheet::getColumnWidthInPixels);

        scaleCell(scaledHeight, anchor.getRow1(), anchor.getDy1(), anchor::setRow2, anchor::setDy2,
                isHSSF ? HEIGHT_UNITS : 0, (row) -> getRowHeightInPixels(sheet, row));

        return new Dimension(
                (int) Math.round(scaledWidth * EMU_PER_PIXEL),
                (int) Math.round(scaledHeight * EMU_PER_PIXEL)
        );
    }

    /**
     * Calculates the dimensions in EMUs for the anchor of the given picture
     *
     * @param picture the picture containing the anchor
     * @return the dimensions in EMUs
     */
    public static Dimension getDimensionFromAnchor(Picture picture) {
        ClientAnchor anchor = picture.getClientAnchor();
        boolean isHSSF = (anchor instanceof HSSFClientAnchor);
        Sheet sheet = picture.getSheet();

        // default to image size (in pixel), if the anchor is only specified for Col1/Row1
        Dimension imgSize = null;
        if (anchor.getCol2() < anchor.getCol1() || anchor.getRow2() < anchor.getRow1()) {
            PictureData data = picture.getPictureData();
            try {
                imgSize = getImageDimension(UnsynchronizedByteArrayInputStream.builder().setByteArray(data.getData()).get(), data.getPictureType());
            } catch (IOException e) {
                // not possible with ByteArray but still declared in the API
                throw new IllegalStateException(e);
            }
        }

        int w = getDimFromCell(imgSize == null ? 0 : imgSize.getWidth(), anchor.getCol1(), anchor.getDx1(), anchor.getCol2(), anchor.getDx2(),
                isHSSF ? WIDTH_UNITS : 0, sheet::getColumnWidthInPixels);

        int h = getDimFromCell(imgSize == null ? 0 : imgSize.getHeight(), anchor.getRow1(), anchor.getDy1(), anchor.getRow2(), anchor.getDy2(),
                isHSSF ? HEIGHT_UNITS : 0, (row) -> getRowHeightInPixels(sheet, row));

        return new Dimension(w, h);
    }

    public static double getRowHeightInPixels(Sheet sheet, int rowNum) {
        Row r = sheet.getRow(rowNum);
        double points = (r == null) ? sheet.getDefaultRowHeightInPoints() : r.getHeightInPoints();
        return Units.toEMU(points) / (double) EMU_PER_PIXEL;
    }

    private static void scaleCell(final double targetSize,
                                  final int startCell,
                                  final int startD,
                                  Consumer<Integer> endCell,
                                  Consumer<Integer> endD,
                                  final int hssfUnits,
                                  Function<Integer, Number> nextSize) {
        if (targetSize < 0) {
            throw new IllegalArgumentException("target size < 0");
        }
        if (Double.isInfinite(targetSize) || Double.isNaN(targetSize)) {
            throw new IllegalArgumentException("target size " + targetSize + " is not supported");
        }

        int cellIdx = startCell;
        double dim, delta;
        for (double totalDim = 0, remDim; ; cellIdx++, totalDim += remDim) {
            dim = nextSize.apply(cellIdx).doubleValue();
            remDim = dim;
            if (cellIdx == startCell) {
                if (hssfUnits > 0) {
                    remDim *= 1 - startD / (double) hssfUnits;
                } else {
                    remDim -= startD / (double) EMU_PER_PIXEL;
                }
            }
            delta = targetSize - totalDim;
            if (delta < remDim) {
                break;
            }
        }

        double endDval;
        if (hssfUnits > 0) {
            endDval = delta / dim * (double) hssfUnits;
        } else {
            endDval = delta * EMU_PER_PIXEL;
        }
        if (cellIdx == startCell) {
            endDval += startD;
        }

        endCell.accept(cellIdx);
        endD.accept((int) Math.rint(endDval));
    }

    private static int getDimFromCell(double imgSize, int startCell, int startD, int endCell, int endD, int hssfUnits,
                                      Function<Integer, Number> nextSize) {
        double targetSize;
        if (endCell < startCell) {
            targetSize = imgSize * EMU_PER_PIXEL;
        } else {
            targetSize = 0;
            for (int cellIdx = startCell; cellIdx <= endCell; cellIdx++) {
                final double dim = nextSize.apply(cellIdx).doubleValue() * EMU_PER_PIXEL;
                double leadSpace = 0;
                if (cellIdx == startCell) {
                    //space in the leftmost cell
                    leadSpace = (hssfUnits > 0)
                            ? dim * startD / (double) hssfUnits
                            : startD;
                }

                double trailSpace = 0;
                if (cellIdx == endCell) {
                    // space after the rightmost cell
                    trailSpace = (hssfUnits > 0)
                            ? dim * (hssfUnits - endD) / (double) hssfUnits
                            : dim - endD;
                }
                targetSize += dim - leadSpace - trailSpace;
            }
        }

        return (int) Math.rint(targetSize);
    }
}
