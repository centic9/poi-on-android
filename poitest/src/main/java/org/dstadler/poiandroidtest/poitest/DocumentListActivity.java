package org.dstadler.poiandroidtest.poitest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.poifs.crypt.TestSignatureInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.dstadler.poiandroidtest.poitest.dummy.DummyContent;
import org.dstadler.poiandroidtest.poitest.dummy.DummyItemWithCode;
import org.dstadler.poiandroidtest.poitest.test.TestIssue28;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * An activity representing a list of Documents. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DocumentDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link DocumentListFragment} and the item details
 * (if present) is a {@link DocumentDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link DocumentListFragment.Callbacks} interface
 * to listen for item selections.
 */
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class DocumentListActivity extends Activity
        implements DocumentListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);


        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        try {
            writeWorkbook();

            InputStream input = openFileInput("test.xlsx");
            Workbook wb = WorkbookFactory.create(input);

            // refresh the content as we re-enter here if the user navigates back from the detail view
            DummyContent.initialize();

            // replace the dummy-content to show that we could write and read the cell-values
            int i = 0;
            Row row = wb.getSheetAt(0).getRow(0);
            for (Map.Entry<String, DummyContent.DummyItem> entry : DummyContent.ITEM_MAP.entrySet()) {
                Cell cell = row.getCell(i);
                entry.getValue().setContent(cell.getStringCellValue());

                // read hyperlink back in and add it to the displayed text
                Hyperlink hyperlink = cell.getHyperlink();
                if(hyperlink != null) {
                    entry.getValue().appendContent(hyperlink.getAddress());
                }

                i++;
            }

            wb.close();

            DummyContent.addItem(new DummyItemWithCode("c1", "Test Callable", new Callable<String>() {
                @Override
                public String call() {
                    return "Result from Test Callable";
                }
            }));

            DummyContent.addItem(new DummyItemWithCode("c2", "Test Signature Info - Crashes!!", new Callable<String>() {
                @Override
                public String call() {
                    TestSignatureInfo test = new TestSignatureInfo();
                    test.testConstruct();
                    return "Signature Info constructed successfully";
                }
            }));

            DummyContent.addItem(new DummyItemWithCode("c3", "Test Issue 28", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    TestIssue28.saveExcelFile(openFileOutput("issue28.xlsx", Context.MODE_PRIVATE));
                    return "Issue 28 tested successfully";
                }
            }));

            InputStream docFile = getResources().openRawResource(R.raw.lorem_ipsum);
            try {
                XWPFDocument doc = new XWPFDocument(docFile);
                try {
                    for(XWPFParagraph paragraph : doc.getParagraphs()) {
                        String content = StringUtils.abbreviate(paragraph.getText(), 20);
                        if(StringUtils.isEmpty(content)) {
                            content = "<empty>";
                        }
                        DummyContent.addItem(new DummyContent.DummyItem("z" + i, content, paragraph.getText()));
                        i++;
                    }

                    final XWPFParagraph title = doc.createParagraph();
                    final XWPFRun titleRun = title.createRun();
                    // TODO: enable this with POI 4.0.0 as only then we have CTSignedTwipsMeasure: titleRun.setCharacterSpacing(2);
                    FileOutputStream stream = openFileOutput("test.docx", Context.MODE_PRIVATE);
                    try {
                        doc.write(stream);
                    } finally {
                        stream.close();
                    }

                    int sheetCount = doc.getProperties().getExtendedProperties().getPages();

                    DummyContent.addItem(new DummyItemWithCode("c4", "SheetCount " + sheetCount, new Callable<String>() {
                        @Override
                        public String call() {
                            return "Called";
                        }
                    }));
                } finally {
                    doc.close();
                }
            } finally {
                docFile.close();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        if (findViewById(R.id.document_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((DocumentListFragment) getFragmentManager()
                    .findFragmentById(R.id.document_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    private void writeWorkbook() throws java.io.IOException {
        Workbook wb = new XSSFWorkbook();
        try {
            Sheet sheet = wb.createSheet("Sheet1");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("cell-1");
            cell = row.createCell(1);
            cell.setCellValue("cell-2");
            cell = row.createCell(2);
            cell.setCellValue("cell-3");

            XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
            style.setFillBackgroundColor(new XSSFColor(new org.apache.poi.java.awt.Color(1, 2, 3)));

            Hyperlink link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
            link.setAddress("http://www.google.at");
            link.setLabel("Google");
            cell.setHyperlink(link);

            cell.setCellStyle(style);

            sheet.setPrintGridlines(true);

            OutputStream stream = openFileOutput("test.xlsx", Context.MODE_PRIVATE);
            try {
                wb.write(stream);
            } finally {
                stream.close();
            }
        } finally {
            wb.close();
        }
    }

    /**
     * Callback method from {@link DocumentListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(DocumentDetailFragment.ARG_ITEM_ID, id);
            DocumentDetailFragment fragment = new DocumentDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.document_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, DocumentDetailActivity.class);
            detailIntent.putExtra(DocumentDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
