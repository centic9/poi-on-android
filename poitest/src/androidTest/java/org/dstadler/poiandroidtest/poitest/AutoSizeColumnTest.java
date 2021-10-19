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

import static org.junit.Assert.assertEquals;

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
        cell.setCellValue(shortString);
        sheet.autoSizeColumn(0);
        int smallColumn = sheet.getColumnWidth(0);
        assertEquals("Font rendering is not fully implemented in the custom java.awt implementation",
                2048, smallColumn);

        row = sheet.createRow(1);
        cell = row.createCell(1);
        String longString = "Chaîne longue, mais alors vraiment très longue, plus encore qu'on ne pouvait l'imaginer avant de la lire";
        cell.setCellValue(longString);
        sheet.autoSizeColumn(1);
        int largeColumn = sheet.getColumnWidth(1);
        assertEquals("Font rendering is not fully implemented in the custom java.awt implementation",
                2048, largeColumn);

        row = sheet.getRow(0);
        cell = row.createCell(1);
        Font policeGrasse = workbook.createFont();
        policeGrasse.setBold(true);
        CellStyle styleGras = workbook.createCellStyle();
        styleGras.setFont(policeGrasse);
        cell.setCellStyle(styleGras);
        String titleString = "Titre de colonne";
        cell.setCellValue(titleString);
        sheet.autoSizeColumn(1);
        int titreColumn = sheet.getColumnWidth(2);
        assertEquals("Font rendering is not fully implemented in the custom java.awt implementation",
                2048, titreColumn);
    }
}
