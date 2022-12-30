package org.dstadler.poiandroidtest.poitest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextParams;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.Version;
import org.apache.poi.common.usermodel.HyperlinkType;
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

import androidx.fragment.app.DialogFragment;

public class MainActivity extends Activity {
	private int idCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
		System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
		System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

		try {
			setupContent();

			final ListView listview = (ListView) findViewById(R.id.mylist);
			/*String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
					"Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
					"Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
					"OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
					"Android", "iPhone", "WindowsMobile" };


			for (int i = 0; i < values.length; ++i) {
				list.add(values[i]);
			}*/

			final ArrayList<String> list = new ArrayList<>();
			for (DummyContent.DummyItem item : DummyContent.ITEMS) {
				list.add(item.toString());
			}

			final StableArrayAdapter adapter = new StableArrayAdapter(this,
					android.R.layout.simple_list_item_1, list);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
										int position, long id) {
					//final String item = (String) parent.getItemAtPosition(position);

					DummyContent.DummyItem content = DummyContent.ITEMS.get(position);

					AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(view.getContext());
					dlgAlert.setMessage(content.getLongContent());
					dlgAlert.setTitle(content.toString());
					dlgAlert.setPositiveButton("OK", null);
					dlgAlert.setCancelable(true);
					dlgAlert.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// You don't have to do anything here if you just
									// want it dismissed when clicked
								}
							});
					dlgAlert.create().show();

					/*MessageBox box = new MessageBox(getApplication());
					box.show(content.toString(), content.getLongContent());*/

					/*DialogFragment dialog = new MyDialogFragment(content);
					dialog.show(dialog.getFragmentManager(), "MyDialogFragmentTag");*/


					/*view.animate().setDuration(2000).alpha(0)
							.withEndAction(new Runnable() {
								@Override
								public void run() {
									list.remove(item);
									adapter.notifyDataSetChanged();
									view.setAlpha(1);
								}
							});*/
				}

			});

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}



	public class MessageBox
	{
		final Context context;

		public MessageBox(Context context) {
			this.context = context;
		}

		void show(String title, String message)
		{
			dialog = new AlertDialog.Builder(context) // Pass a reference to your main activity here
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton("OK", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							dialog.cancel();
						}
					})
					.show();
		}

		private AlertDialog dialog;
	}

	public class MyDialogFragment extends DialogFragment {
		private final DummyContent.DummyItem item;

		public MyDialogFragment(DummyContent.DummyItem item) {
			this.item = item;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(item.toString());
			builder.setMessage(item.getLongContent());
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// You don't have to do anything here if you just
					// want it dismissed when clicked
				}
			});

			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {


		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

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
				() -> "Apache " + Version.getProduct() + " " + Version.getVersion() + " (" + Version.getReleaseDate() + ")"));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Callable",
				() -> "This is the result from the callable"));

		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Signature Info - May crash",
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
		DummyContent.addItem(new DummyItemWithCode("c" + (idCount++), "Test Issue 75 - Crashes!!", () -> {
			try (InputStream pictureStream = getResources().openRawResource(R.raw.logo);
				 OutputStream outputStream = openFileOutput("issue75.xlsx", Context.MODE_PRIVATE)) {
				TestIssue75.saveExcelFile(pictureStream, outputStream);
			}

			return "Issue 75 - XMLSlideShow constructed successfully";
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
	}
}
