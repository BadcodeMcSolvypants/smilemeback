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

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Storage class deals with Android filesystem and
 * manages categories, icons and user data.
 */
public class Storage {
    public static final String STORAGE_FOLDER = "SmileMeBack";
    public static final String CATEGORIES_FOLDER = "categories";
    public static final String TEMPORARY_IMAGE = "temporary_image" + Image.IMAGE_SUFFIX;
    public static final String TEMPORARY_AUDIO = "temporary_audio" + Image.AUDIO_SUFFIX;

    /**
     * Application context.
     */
    protected Context context = null;

    /**
     * Initialize a new {@link Storage} using the given application context.
     * @param context
     */
    public Storage(final Context context) {
        this.context = context;
    }

    /**
     * Get the storage folder.
     * @return The filesystem storage folder {@link java.io.File}.
     */
    public File getStorageFolder() throws StorageException {
        try {
            File folder = new File(context.getExternalFilesDir(null), STORAGE_FOLDER);
            FileUtils.forceMkdir(folder);
            return folder;
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Return the categories folder.
     * @return The filesystem categories storage file.
     */
    public File getCategoriesFolder() throws StorageException {
        try {
            File folder = new File(getStorageFolder(), CATEGORIES_FOLDER);
            FileUtils.forceMkdir(folder);
            return folder;
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Get all categories.
     * If the folder containing the categories does not exist, it creates it automatically.
     * In this case, of course, the function returns empty list.
     *
     * @return
     * @throws StorageException
     */
    public Categories getCategories() throws StorageException {
        return new Categories(getCategoriesFolder());
    }

    /**
     * @return The file pointing to the path that should be used for temporary images.
     */
    public File getTemporaryImageFile() throws StorageException {
        return new File(getStorageFolder(), TEMPORARY_IMAGE);
    }

    /**
     * @return The file pointing to the path that should be used for temporary audio recordings.
     */
    public File getTemporaryAudioFile() throws StorageException {
        return new File(getStorageFolder(), TEMPORARY_AUDIO);
    }

}
