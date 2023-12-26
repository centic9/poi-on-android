package org.dstadler.poiandroidtest.poitest;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    private static final Logger LOG = LogManager.getLogger(ApplicationTest.class);

    @Test
    public void testLog() {
        LOG.info("Testing...");
    }
}
