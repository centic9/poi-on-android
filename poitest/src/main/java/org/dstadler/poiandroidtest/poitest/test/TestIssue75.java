package org.dstadler.poiandroidtest.poitest.test;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestIssue75 {
    public static void saveExcelFile(InputStream picture, OutputStream outputStream, int workBookPictureType) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFCreationHelper helper = workbook.getCreationHelper();
        XSSFSheet sheet = workbook.createSheet("Sheet 1");
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        XSSFClientAnchor anchor = helper.createClientAnchor();
        anchor.setAnchorType( ClientAnchor.AnchorType.MOVE_AND_RESIZE );
        int pictureIndex = workbook.addPicture(IOUtils.toByteArray(picture), workBookPictureType);

        anchor.setCol1( 0 );
        anchor.setRow1( 2);
        anchor.setRow2( 3 );
        anchor.setCol2( 1 );
        XSSFPicture pict = drawing.createPicture( anchor, pictureIndex );
        pict.resize();

        workbook.write(outputStream);
    }
}
