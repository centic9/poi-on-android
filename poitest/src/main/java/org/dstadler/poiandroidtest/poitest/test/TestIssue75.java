package org.dstadler.poiandroidtest.poitest.test;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestIssue75 {
    public static void saveExcelFile(InputStream picture, OutputStream outputStream, int workBookPictureType) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {

            CreationHelper helper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("Sheet 1");
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            int pictureIndex = workbook.addPicture(IOUtils.toByteArray(picture), workBookPictureType);

            anchor.setCol1(0);
            anchor.setRow1(2);
            anchor.setRow2(3);
            anchor.setCol2(1);
            Picture pict = drawing.createPicture(anchor, pictureIndex);
            pict.resize();

            workbook.write(outputStream);
        }
    }
}
