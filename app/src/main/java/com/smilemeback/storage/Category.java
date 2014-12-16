package com.smilemeback.storage;

import java.io.File;

/**
 * Category contains an category thumbnail image
 * and a list of pictures, their labels and audio recordings.
 */
public class Category {

    private CategoryName name;
    private File folder;
    private File thumbnail;

    public Category(File categoryFile) throws StorageException {
        this.folder = categoryFile;
        this.name = new CategoryName(categoryFile.getName());
        this.thumbnail = new File(this.folder, Storage.CATEGORY_THUMBNAIL);
        if (!thumbnail.exists()) {
            throw new StorageException("Category" + name + " thumbnail <" + thumbnail.getAbsolutePath() + "> does not exist!");
        }
    }

    public CategoryName getName() { return this.name; }

    public File getFolder() { return this.folder; }

    public File getThumbnail() {
        return thumbnail;
    }

    @Override
    public String toString() {
        return getName().toString();
    }
}
