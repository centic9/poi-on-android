package org.dstadler.poiandroidtest.poitest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.Version;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.poifs.crypt.TestSignatureInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.dstadler.poiandroidtest.poitest.dummy.DummyContent;
import org.dstadler.poiandroidtest.poitest.dummy.DummyItemWithCode;
import org.dstadler.poiandroidtest.poitest.test.TestIssue28;
import org.dstadler.poiandroidtest.poitest.test.TestIssue75;
import org.dstadler.poiandroidtest.poitest.test.TestIssue84;
import org.dstadler.poiandroidtest.poitest.test.TestIssue89;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
	// Request code for creating a PDF document.
	private static final int CREATE_DOCX_FILE = 2;

	private int idCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// set some system-properties to instruct the code to use the fasterxml parsers
		System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
		System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
		System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

		try {
			// create all the list items
			setupContent();

			// populate the list
			final ListView listview = findViewById(R.id.mylist);
			final ArrayList<String> list = new ArrayList<>();
			for (DummyContent.DummyItem item : DummyContent.ITEMS) {
				list.add(item.toString());
			}

			final StableArrayAdapter adapter = new StableArrayAdapter(this,
					android.R.layout.simple_list_item_1, list);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener((parent, view, position, id) -> {
				// when an item is clicked, show a message-box with the resulting content
				DummyContent.DummyItem content = DummyContent.ITEMS.get(position);
				MessageBox box = new MessageBox(view.getContext());
				box.show(content.toString(), content.getLongContent());
			});

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static class MessageBox {
		final Context context;

		public MessageBox(Context context) {
			this.context = context;
		}

		void show(String title, String message) {
			dialog = new AlertDialog.Builder(context) // Pass a reference to your main activity here
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton("OK", (dialogInterface, i) -> dialog.cancel())
					.show();
		}

		private AlertDialog dialog;
	}

	private static class StableArrayAdapter extends ArrayAdapter<String> {
		HashMap<String, Integer> mIdMap = new HashMap<>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

	private void writeWorkbook(String name) throws java.io.IOException {
		try (Workbook wb = new XSSFWorkbook()) {
			Sheet sheet = wb.createSheet("Sheet1");
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue("cell-1");
			cell = row.createCell(1);
			cell.setCellValue("cell-2");
			cell = row.createCell(2);
			cell.setCellValue("cell-3");

			XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
			style.setFillBackgroundColor(new XSSFColor(
					new org.apache.poi.java.awt.Color(1, 2, 3), new DefaultIndexedColorMap()));

			Hyperlink link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
			link.setAddress("http://www.google.at");
			link.setLabel("Google");
			cell.setHyperlink(link);

			cell.setCellStyle(style);

			sheet.setPrintGridlines(true);

			try (OutputStream stream = openFileOutput(name, Context.MODE_PRIVATE)) {
				wb.write(stream);
			}
		}
	}

	private void setupContent() throws IOException {
		writeWorkbook("test.xlsx");

		int i = 0;
		InputStream input = openFileInput("test.xlsx");
		try (Workbook wb = WorkbookFactory.create(input)) {

			// refresh the content as we re-enter here if the user navigates back from the detail view
			DummyContent.initialize();

			// replace the dummy-content to show that we could write and read the cell-values
			Row row = wb.getSheetAt(0).getRow(0);
			for (Map.Entry<String, DummyContent.DummyItem> entry : DummyContent.ITEM_MAP.entrySet()) {
				Cell cell = row.getCell(i);
				entry.getValue().setContent(cell.getStringCellValue());

				// read hyperlink back in and add it to the displayed text
				Hyperlink hyperlink = cell.getHyperlink();
				if (hyperlink != null) {
					entry.getValue().appendContent(hyperlink.getAddress());
				}

				i++;
			}
		}

		DummyContent.addItem(new DummyItemWithCode("v" + (idCount++), "POI Version",
				() ->
						"Apache " + Version.getProduct() + " " + Version.getVersion() + "\n" +
						"App " + BuildConfig.VERSION_NAME + " - " + BuildConfig.VERSION_CODE));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++),
				"DOCX with image",
				() -> {
					Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
					intent.putExtra(Intent.EXTRA_TITLE, "DocWithImage.docx");

					// Optionally, specify a URI for the directory that should be opened in
					// the system file picker when your app creates the document.
					//intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "DocWithImage.docx");

					startActivityForResult(intent, CREATE_DOCX_FILE);

					return "Writing to selected file";
				}));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Callable",
				() -> "This is the result from the callable"));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Signature Info",
				() -> {
					TestSignatureInfo test = new TestSignatureInfo();
					test.testConstruct();
					return "Signature Info constructed successfully";
				}));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Issue 28", () -> {
			try (OutputStream outputStream = openFileOutput("issue28.xlsx", Context.MODE_PRIVATE)) {
				TestIssue28.saveExcelFile(outputStream);
			}
			return "Issue 28 tested successfully";
		}));

		// reproducer for https://github.com/centic9/poi-on-android/issues/75
		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test resizing JPEG (#75)", () -> {
			try (InputStream pictureStream = getResources().openRawResource(R.raw.logo);
				 CountingOutputStream outputStream = new CountingOutputStream(openFileOutput("issue75.xlsx", Context.MODE_PRIVATE))) {
				TestIssue75.saveExcelFile(pictureStream, outputStream, Workbook.PICTURE_TYPE_JPEG);

				return "Issue 75 - XSSFWorkbook constructed successfully, wrote " + outputStream.getByteCount() + " bytes";
			}
		}));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test resizing PNG (#75)", () -> {
			try (InputStream pictureStream = getResources().openRawResource(R.raw.logo_png);
				 CountingOutputStream outputStream = new CountingOutputStream(openFileOutput("resizePng.xlsx", Context.MODE_PRIVATE))) {
				TestIssue75.saveExcelFile(pictureStream, outputStream, Workbook.PICTURE_TYPE_PNG);

				return "Test resizing PNG file - XSSFWorkbook constructed successfully, wrote " + outputStream.getByteCount() + " bytes";
			}
		}));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test resizing BMP (#75)", () -> {
			try (InputStream pictureStream = getResources().openRawResource(R.raw.logo_bmp);
				 CountingOutputStream outputStream = new CountingOutputStream(openFileOutput("resizeBmp.xlsx", Context.MODE_PRIVATE))) {
				TestIssue75.saveExcelFile(pictureStream, outputStream, Workbook.PICTURE_TYPE_DIB);

				return "Test resizing BMP file - XSSFWorkbook constructed successfully, wrote " + outputStream.getByteCount() + " bytes";
			}
		}));

		// reproducer for https://github.com/centic9/poi-on-android/issues/84
		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Issue 84 - Crashes!!", () -> {
			try (OutputStream outputStream = openFileOutput("issue84.xlsx", Context.MODE_PRIVATE)) {
				TestIssue84.saveExcelFile(outputStream);
			}

			return "Issue 84 - XMLSlideShow constructed successfully";
		}));

		// reproducer for https://github.com/centic9/poi-on-android/issues/89
		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Issue 89 - Crashes!!", () -> {
			try (OutputStream outputStream = openFileOutput("issue89.xlsx", Context.MODE_PRIVATE)) {
				TestIssue89.saveExcelFile(outputStream);
			}

			return "Issue 89 - SXSSFWorkbook constructed successfully";
		}));

		// reproducer for https://github.com/centic9/poi-on-android/issues/98
		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Issue 98", () -> {
			StringBuilder text = new StringBuilder();
			for (int resource : new int[] {
					R.raw.simple,
					R.raw.sample2,
					R.raw.sample,
					R.raw.lorem_ipsum,
			} ) {
				try (InputStream pictureStream = getResources().openRawResource(resource);
					 POITextExtractor extractor = ExtractorFactory.createExtractor(pictureStream)) {

					text.append("\nResource-").append(resource).append(": ").append(extractor.getText());
				}
			}
			return "Issue 98 - Had text: " + text;
		}));

		// reproducer for https://github.com/centic9/poi-on-android/issues/103
		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Issue 103", () -> {
			try (InputStream inputStream = getResources().openRawResource(R.raw.sample);
				XMLSlideShow slideShow = new XMLSlideShow(inputStream)) {

				return "Issue 103 - XMLSlideShow constructed successfully: " + slideShow.getSlides().size();
			}
		}));

		try (InputStream slidesFile = getResources().openRawResource(R.raw.sample)) {
			try (XMLSlideShow slides = new XMLSlideShow(slidesFile)) {
				for (XSLFSlide slide : slides.getSlides()) {
					DummyContent.addItem(new DummyItemWithCode("c" + (idCount++),
							"Slide - " + slide.getSlideName(),
							slide::getTitle));
				}
			}
		}

		try (InputStream slidesFile = getResources().openRawResource(R.raw.sample2)) {
			try (HSLFSlideShow slides = new HSLFSlideShow(slidesFile)) {
				for (HSLFSlide slide : slides.getSlides()) {
					DummyContent.addItem(new DummyItemWithCode("c" + (idCount++),
							"Slide - " + slide.getSlideName(),
							slide::getTitle));
				}
			}
		}

		try (InputStream docFile = getResources().openRawResource(R.raw.lorem_ipsum)) {
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
				titleRun.setCharacterSpacing(2);
				FileOutputStream stream = openFileOutput("test.docx", Context.MODE_PRIVATE);
				try {
					doc.write(stream);
				} finally {
					stream.close();
				}

				int sheetCount = doc.getProperties().getExtendedProperties().getPages();

				DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "SheetCount " + sheetCount,
						() -> "Called"));
			} finally {
				doc.close();
			}
		}
	}

	// Copied from Apache POI Utils class to not require code from java.awt
	public static int toEMU(double points) {
		return (int)Math.rint(12700.0D * points);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		if (requestCode == CREATE_DOCX_FILE
				&& resultCode == Activity.RESULT_OK) {
			// The result data contains a URI for the document or directory that
			// the user selected.
			if (resultData != null) {
				Uri uri = resultData.getData();
				// Perform operations on the document using its URI.

				try (InputStream docFile = getResources().openRawResource(R.raw.lorem_ipsum)) {
					XWPFDocument doc = new XWPFDocument(docFile);

					try (InputStream pictureStream = getResources().openRawResource(R.raw.logo)) {
						XWPFParagraph p = doc.createParagraph();

						XWPFRun r = p.createRun();
						r.setText("logo.jpg");
						r.addBreak();
						r.addPicture(pictureStream, Document.PICTURE_TYPE_JPEG,
								"logo.jpg", toEMU(200),
								toEMU(200)); // 200x200 pixels
						r.addBreak(BreakType.PAGE);
					}

					try (ParcelFileDescriptor docx = getContentResolver().openFileDescriptor(uri, "w")) {
						try (OutputStream outputStream = new FileOutputStream(docx.getFileDescriptor())) {
							doc.write(outputStream);
						}
					}
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}
}
