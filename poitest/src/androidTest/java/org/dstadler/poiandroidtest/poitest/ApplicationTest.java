package org.dstadler.poiandroidtest.poitest;

import org.dstadler.poiandroidtest.poitest.dummy.DummyContent;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Test
    public void test() {
        // just some very simple things to verify that testing works basically
        DummyContent.addItem(new DummyContent.DummyItem("1", "bla", "some text"));
    }
}
