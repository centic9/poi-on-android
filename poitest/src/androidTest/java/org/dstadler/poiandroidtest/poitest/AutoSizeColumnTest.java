package org.dstadler.poiandroidtest.poitest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class AutoSizeColumnTest {
    /**
     * Test that java.awt classes are accessible and the size returned.
     */
    @Test
    public void testAutoSizeColumn() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("TestSheet");
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        String shortString = "Chaîne courte";
        int smallColumn = -1;
        cell.setCellValue(shortString);
        try {
            sheet.autoSizeColumn(0);
            smallColumn = sheet.getColumnWidth(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert(smallColumn == -1);

        row = sheet.createRow(1);
        cell = row.createCell(1);
        String longString = "Chaîne longue, mais alors vraiment très longue, plus encore qu'on ne pouvait l'imaginer avant de la lire";
        int largeColumn = -1;
        cell.setCellValue(longString);
        try {
            sheet.autoSizeColumn(1);
            largeColumn = sheet.getColumnWidth(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert(largeColumn == -1);

        row = sheet.getRow(0);
        cell = row.createCell(1);
        Font policeGrasse = workbook.createFont();
        policeGrasse.setBold(true);
        CellStyle styleGras = workbook.createCellStyle();
        styleGras.setFont(policeGrasse);
        cell.setCellStyle(styleGras);
        String titleString = "Titre de colonne";
        cell.setCellValue(titleString);
        int titreColumn = -1;
        try {
            sheet.autoSizeColumn(1);
            titreColumn = sheet.getColumnWidth(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert(titreColumn == (titleString.length() + 1));
    }
}
