package com.smilemeback.storage;

import android.test.AndroidTestCase;

import java.util.List;

public class StorageTest extends AndroidTestCase {

    public void testGetStorageFolder() {;
        assertTrue(Storage.getStorageFolder(getContext()).getAbsolutePath().endsWith(Storage.STORAGE_FOLDER));
    }

    public void testGetCategoriesFolder() {
        assertTrue(Storage.getCategoriesFolder(getContext()).getAbsolutePath().endsWith(Storage.CATEGORIES_FOLDER));
    }

    public void testAddAndTruncateCategory() throws Exception {
        Storage storage = new Storage(getContext());
        storage.truncateAllCategories();
        storage.addEmptyCategory(getTestCategoryName());
        List<Category> cats = storage.getCategories();
        assertEquals(cats.size(), 1);
        assertEquals(cats.get(0).getName(), getTestCategoryName());
        storage.truncateAllCategories();
        assertEquals(0, storage.getCategories().size());
    }

    private CategoryName getTestCategoryName() {
        return new CategoryName("Test category 157");
    }
}
