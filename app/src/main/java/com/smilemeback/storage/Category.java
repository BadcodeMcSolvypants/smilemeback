package com.smilemeback.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Category contains an category thumbnail image
 * and a list of pictures, their labels and audio recordings.
 */
public class Category implements Comparable<Category> {

    /**
     * The index of the category.
     * All categories must have unique indices that define their display order.
     */
    private int index;

    /**
     * The name of the category.
     */
    private Name name;

    /**
     * The actual folder file of the category.
     */
    private File folder;

    /**
     * The thumbnail file of the category.
     * It must reside inside the category with name "_thumbnail.jpg".
     */
    private File thumbnail;


    /**
     * Initialize a new category.
     *
     * The constructor automatically retrieves the {@link #index}, {@link #name} and {@link #thumbnail}
     * of the category.
     *
     * @param categoryFile The folder file of the category.
     * @throws StorageException
     */
    public Category(File categoryFile) throws StorageException {
        this.folder = categoryFile;
        this.index = Storage.getIndex(categoryFile);
        this.name = Storage.getName(categoryFile);
        this.thumbnail = new File(this.folder, Storage.CATEGORY_THUMBNAIL);
        if (!thumbnail.exists()) {
            throw new StorageException("Category" + name + " thumbnail <" + thumbnail.getAbsolutePath() + "> does not exist!");
        }
    }

    public int getIndex() { return this.index; }

    public Name getName() { return this.name; }

    public File getFolder() { return this.folder; }

    public File getThumbnail() {
        return thumbnail;
    }

    @Override
    public String toString() {
        return getFolder().getAbsolutePath();
    }

    @Override
    public int compareTo(Category another) {
        return getIndex() - another.getIndex();
    }
}
