package org.dstadler.poiandroidtest.poitest.dummy;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        // Add 3 sample items.
        addItem(new DummyItem("1", "/opt/poi/test-data/spreadsheet/SampleSS.xlsx"));
        addItem(new DummyItem("2", "/opt/poi/test-data/spreadsheet/ComplexFunctionTestCaseData.xls"));
        addItem(new DummyItem("3", "/opt/poi/test-data/spreadsheet/WithTable.xlsx"));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;

        public DummyItem(String id, String content) {
            this.id = id;
            this.content = content;

            try {
                Workbook wb = new XSSFWorkbook();
                Sheet sheet = wb.createSheet("Sheet1");
                Row row = sheet.createRow(0);
                Cell cell = row.createCell(0);
                cell.setCellValue("testvalue");

                OutputStream stream = new FileOutputStream("test.xlsx");
                try {
                    wb.write(stream);
                } finally {
                    stream.close();
                }

                wb.close();

                wb = WorkbookFactory.create(new File("test.xlsx"));
                wb.close();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
