package org.dstadler.poiandroidtest.poitest.dummy;

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
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<>();

    static {
        initialize();
    }

    public static void initialize() {
        DummyContent.ITEM_MAP.clear();
        DummyContent.ITEMS.clear();

        // Add 3 sample items.
        addItem(new DummyItem("1", "Item 1", "Long Item 1"));
        addItem(new DummyItem("2", "Item 2", "Long Item 1"));
        addItem(new DummyItem("3", "Item 3", "Long Item 1"));
    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;
        public String longContent;

        public DummyItem(String id, String content, String longContent) {
            this.id = id;
            this.content = content;
            this.longContent = longContent;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
