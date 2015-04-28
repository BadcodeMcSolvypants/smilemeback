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

import com.google.common.base.Optional;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class representing a list of categories.
 */
public class Categories implements Iterable<Category> {
    private static String TAG = Categories.class.getCanonicalName();

    private final File parent;
    private final List<Category> categories = new ArrayList<>();


    public Categories(final File parent) throws StorageException {
        super();
        this.parent = parent;

        if (!parent.isDirectory()) {
            throw new IllegalArgumentException("Parent folder <" + parent.getName() + "> not a directory!");
        }

        parseCategories();
    }

    @Override
    public Iterator<Category> iterator() {
        return categories.iterator();
    }

    public Category get(int position) {
        return categories.get(position);
    }

    public int size() {
        return categories.size();
    }

    public File getParent() {
        return parent;
    }

    private void parseCategories() throws StorageException {
        // Parse all directories as categories.
        for (File folder : parent.listFiles()) {
            if (folder.isDirectory()) {
                categories.add(new Category(folder));
            } else {
                throw new StorageException("Non-directory in categories folder <" + folder.getAbsolutePath() + ">");
            }
        }
        Collections.sort(categories);
        // check that the category indices correspond to their positions in the list
        for (int idx=0 ; idx<categories.size() ; ++idx) {
            Category category = categories.get(idx);
            if (category.getPosition() != idx) {
                String err = "Category <" + category + "> position in name <" + category.getPosition() + "> does not correspond to position in category list <" + idx + ">";
                throw new StorageException(err);
            }
        }
    }

    /**
     * Delete all categories.
     * @throws StorageException
     */
    public void truncate() throws StorageException {
        Log.d(TAG, "Truncating all categories");
        try {
            FileUtils.deleteDirectory(parent);
            FileUtils.forceMkdir(parent);
            categories.clear();
            parseCategories();
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Add a new category, given its {@link com.smilemeback.storage.Name} and a
     * {@link java.io.InputStream} resource to its thumbnail image.
     *
     * @param name The name of the category
     * @param thumbnailStream The input stream containing the thumbnail.
     * @throws StorageException
     */
    public Category add(final Name name, final InputStream thumbnailStream) throws StorageException {
        Log.d(TAG, "Adding empty category with name <" + name + ">");
        File folder = new File(
                parent,
                StorageNameUtils.constructCategoryFileName(size(), name));
        // create category and add thumbnail directory.
        try {
            FileUtils.forceMkdir(folder);
            File thumbNailFile = new File(folder, Category.THUMBNAIL);
            FileUtils.copyInputStreamToFile(thumbnailStream, thumbNailFile);
            Category category = new Category(folder);
            categories.add(category);
            return category;
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            try {
                thumbnailStream.close();
            } catch (IOException e) {
                throw new StorageException(e.getMessage(), e);
            }
        }
    }

    public void delete(final Collection<Category> selection) throws StorageException {
        if (!categories.containsAll(selection)) {
            throw new StorageException("Selection contains categories not in storage!");
        }
        try {
            for (Category category : selection) {
                category.delete();
            }
        } catch (StorageException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            organize();
        }
    }

    public void organize() throws StorageException {
        // TODO: make the code in this method more readable
        Map<Integer, File> categories = new HashMap<>();
        Map<Integer, Name> names = new HashMap<>();
        try {
            FileUtils.forceMkdir(parent);
            for (File category : parent.listFiles()) {
                if (category.isDirectory()) {
                    try {
                        Name name = StorageNameUtils.parseName(category.getName());
                        Optional<Integer> pos = StorageNameUtils.parsePosition(category.getName());
                        if (pos.isPresent()) {
                            categories.put(pos.get(), category);
                            names.put(pos.get(), name);
                        } else {
                            Log.e(TAG, "Deleting category <" + category + "> as no valid position!");
                        }
                    } catch (NameException e) {
                        Log.e(TAG, "Deleting category with invalid name: <" + category.getName() + ">");
                        FileUtils.deleteDirectory(category);
                    }
                } else {
                    Log.w(TAG, "Deleting non-directory <" + category + ">");
                    FileUtils.forceDelete(category);
                }
            }
            List<Integer> indices = new ArrayList<>(categories.keySet());
            Collections.sort(indices);
            int nextPos = 0;
            for (int idx : indices) {
                File srcFile = categories.get(idx);
                Name name = names.get(idx);
                File destFile = new File(parent, nextPos + "_" + name);
                if (srcFile.compareTo(destFile) != 0) {
                    Log.d(TAG, "Moving category <" + srcFile + "> to <" + destFile + ">");
                    FileUtils.moveDirectory(srcFile, destFile);
                }
                nextPos += 1;
            }
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            categories.clear();
            parseCategories();
        }
    }
}
