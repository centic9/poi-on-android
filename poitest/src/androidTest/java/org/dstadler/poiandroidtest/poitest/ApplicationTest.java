package org.dstadler.poiandroidtest.poitest;

import android.content.res.Resources;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.poifs.crypt.TestSignatureInfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.dstadler.poiandroidtest.poitest.dummy.DummyContent;
import org.dstadler.poiandroidtest.poitest.test.TestIssue28;
import org.dstadler.poiandroidtest.poitest.test.TestIssue75;
import org.dstadler.poiandroidtest.poitest.test.TestIssue84;
import org.dstadler.poiandroidtest.poitest.test.TestIssue89;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.fail;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    private static final Logger LOG = LogManager.getLogger(ApplicationTest.class);

    @Test
    public void test() {
        // just some very simple things to verify that testing works basically
        DummyContent.addItem(new DummyContent.DummyItem("1", "bla", "some text"));
    }

    @Test
    public void testLog() {
        LOG.info("Testing...");
    }

    @Test
    public void test28() throws IOException {
        TestIssue28.saveExcelFile(NullOutputStream.INSTANCE);
    }

    @Ignore("Could not make reading the files from the raw resources work...")
    @Test
    public void test75() throws IOException {
        Resources resources = getInstrumentation()
                .getContext()
                .getResources();

        try (InputStream pictureStream = resources.openRawResource(R.raw.logo)) {
            TestIssue75.saveExcelFile(pictureStream, NullOutputStream.INSTANCE, Workbook.PICTURE_TYPE_JPEG);
        }

        try (InputStream pictureStream = resources.openRawResource(R.raw.logo_png)) {
            TestIssue75.saveExcelFile(pictureStream, NullOutputStream.INSTANCE, Workbook.PICTURE_TYPE_PNG);
        }

        try (InputStream pictureStream = resources.openRawResource(R.raw.logo_bmp)) {
            TestIssue75.saveExcelFile(pictureStream, NullOutputStream.INSTANCE, Workbook.PICTURE_TYPE_DIB);
        }
    }

    @Test
    public void test84() throws IOException {
        try {
            TestIssue84.saveExcelFile(NullOutputStream.INSTANCE);
            fail("Usually throws an exception because there are missing AWT classes");
        } catch (NoClassDefFoundError e) {
            // expected here
        }
    }

    @Test
    public void test89() {
        TestIssue89.saveExcelFile(NullOutputStream.INSTANCE);
    }

    @Test
    public void testSignatureInfo() {
        new TestSignatureInfo().testConstruct();
    }
}
