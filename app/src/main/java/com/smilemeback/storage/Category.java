/*
 This file is part of SmileMeBack.

 SmileMeBack is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 SmileMeBack is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with SmileMeBack.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smilemeback.storage;

import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Category contains an category thumbnail image
 * and a list of pictures, their labels and audio recordings.
 */
public class Category implements Comparable<Category> {
    private static final String TAG = Category.class.getCanonicalName();
    public static final String THUMBNAIL = "_thumbnail.jpg";

    protected final int position;
    protected final Name name;
    protected final File folder;
    protected final File thumbnail;
    protected final File storageFolder;

    /**
     * Initialize a new category.
     *
     * The constructor automatically retrieves the {@link #position}, {@link #name} and {@link #thumbnail}
     * of the category.
     *
     * @param folder The folder file of the category.
     * @throws StorageException
     */
    public Category(File folder) throws StorageException {
        try {
            this.folder = folder;
            this.position = StorageNameUtils.parsePosition(folder.getName()).get();
            this.name = StorageNameUtils.parseName(folder.getName());
            this.storageFolder = folder.getParentFile();
            this.thumbnail = new File(this.folder, THUMBNAIL);
        } catch (NameException | IllegalStateException e) {
            throw new StorageException(e.getMessage(), e);
        }
        makeAssertions();
    }

    private void makeAssertions() throws StorageException {
        if (!thumbnail.isFile()) {
            throw new StorageException("Category" + name + " thumbnail <" + thumbnail.getAbsolutePath() + "> not a file or does not exist!");
        }
        if (!storageFolder.isDirectory()) {
            throw new StorageException("Parent folder of category not directory!");
        }
    }

    public int getPosition() { return this.position; }

    public Name getName() { return this.name; }

    public File getFolder() { return this.folder; }

    public File getThumbnail() {
        return thumbnail;
    }

    public Images getImages() throws StorageException {
        return new Images(this);
    }

    @Override
    public String toString() {
        return getFolder().getAbsolutePath();
    }

    @Override
    public int compareTo(Category another) {
        return getPosition() - another.getPosition();
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return position;
    }

    /**
     * Rename the category.
     *
     * @param newName The new {@link com.smilemeback.storage.Name}.
     * @return Instance of the renamed category.
     * @throws StorageException
     */
    public Category rename(Name newName) throws StorageException {
        Log.i(TAG, "Renaming category <" + name + "> to <" + newName + ">");
        if (name.equals(newName)) {
            return this;    // same name, do nothing!
        }
        File newFolder = new File(
                storageFolder,
                StorageNameUtils.constructCategoryFileName(position, newName));
        try {
            FileUtils.moveDirectory(folder, newFolder);
            return new Category(newFolder);
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Delete the category.
     * @throws StorageException
     */
    public void delete() throws StorageException {
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }
}
