package org.dstadler.poiandroidtest.poitest.dummy;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.Callable;

public class DummyItemWithCode extends DummyContent.DummyItem {
    private final Callable<String> callable;

    public DummyItemWithCode(String id, String content, Callable<String> callable) {
        super(id, content, null);
        this.callable = callable;
    }

    @Override
    public String getLongContent() {
        try {
            return callable.call();
        } catch (Exception e) {
            return ExceptionUtils.getStackTrace(e);
        }
    }
}
