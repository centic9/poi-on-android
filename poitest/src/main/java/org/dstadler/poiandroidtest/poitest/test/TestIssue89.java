package org.dstadler.poiandroidtest.poitest.test;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.OutputStream;

public class TestIssue89 {
    public static void saveExcelFile(OutputStream outputStream) {
        try (Workbook wb1 = new SXSSFWorkbook(100)) {
            Sheet sheet = wb1.createSheet("Sheet1");
            Row row1 = sheet.createRow(0);
            Cell cell = row1.createCell(0);
            cell.setCellValue("cell-1");
            cell = row1.createCell(1);
            cell.setCellValue("cell-2");
            cell = row1.createCell(2);
            cell.setCellValue("cell-3");

            XSSFCellStyle style = (XSSFCellStyle) wb1.createCellStyle();
            style.setFillBackgroundColor(new XSSFColor(new org.apache.poi.java.awt.Color(1, 2, 3), new DefaultIndexedColorMap()));

            Hyperlink link = wb1.getCreationHelper().createHyperlink(HyperlinkType.URL);
            link.setAddress("http://www.google.at");
            link.setLabel("Google");
            cell.setHyperlink(link);

            cell.setCellStyle(style);

            sheet.setPrintGridlines(true);

            wb1.write(outputStream);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
