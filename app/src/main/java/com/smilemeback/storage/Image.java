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
 * Image class that represents a single icon in a category.
 */
public class Image implements Comparable<Image> {
    private static final String TAG = Image.class.getCanonicalName();

    public static final String IMAGE_SUFFIX = ".jpg";
    public static final String AUDIO_SUFFIX = ".3gpp";

    protected final Category category;
    protected final Name name;
    protected final File image;
    protected final File audio;
    protected final int position;

    /**
     * Construct a new {@link com.smilemeback.storage.Image} instance.
     * @param category The {@link com.smilemeback.storage.Category} the image is in.
     * @param image The {@link java.io.File} of the image.
     * @param audio The {@link java.io.File} of the audio.
     *
     * @throws com.smilemeback.storage.StorageException In case there were problems with finding image data.
     */
    public Image(final Category category, final File image, final File audio) throws StorageException {
        try {
            this.category = category;
            this.name = StorageNameUtils.parseName(image.getName());
            this.image = image;
            this.audio = audio;
            this.position = StorageNameUtils.parsePosition(image.getName()).get();

            makeAssertions();
        } catch (NameException | IllegalStateException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    private void makeAssertions() throws StorageException, NameException {
        if (category == null || name == null || audio == null) {
            throw new IllegalArgumentException("One of the arguments was null");
        }
        if (!category.getFolder().isDirectory()) {
            throw new StorageException("Category is not a directory!");
        }
        if (!image.isFile()) {
            throw new StorageException("Image <" + image + " is not a file!");
        }
        if (!StorageNameUtils.parseSuffix(image.getName()).get().equals(IMAGE_SUFFIX)) {
            throw new StorageException("Illegal suffix for <" + image.getName() + "> ");
        }
        if (!audio.isFile()) {
            throw new StorageException("Audio <" + audio + " is not a file!");
        }
        if (!StorageNameUtils.parseSuffix(audio.getName()).get().equals(AUDIO_SUFFIX)) {
            throw new StorageException("Illegal suffix for <" + audio.getName() + ">");
        }
        if (StorageNameUtils.parsePosition(image.getName()).get() != StorageNameUtils.parsePosition(audio.getName()).get()) {
            throw new StorageException("Position different for image <" + image + "> and audio <" + audio + ">");
        }
        if (!StorageNameUtils.parseName(image.getName()).equals(StorageNameUtils.parseName(audio.getName()))) {
            throw new StorageException("Position different for image <" + image + "> and audio <" + audio + ">");
        }
    }

    public Category getCategory() {
        return category;
    }

    public Name getName() {
        return name;
    }

    public File getImage() {
        return image;
    }

    public File getAudio() {
        return audio;
    }

    public int getPosition() {
        return position;
    }

    /**
     * Images are compared by their position in the {@link com.smilemeback.storage.Category} .
     * @param another
     * @return
     */
    @Override
    public int compareTo(Image another) {
        return getPosition() - another.getPosition();
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }


    /**
     * Rename the image.
     *
     * @param newName The new {@link com.smilemeback.storage.Name}.
     * @return Instance of the renamed image.
     * @throws StorageException
     */
    public Image rename(Name newName) throws StorageException {
        Log.i(TAG, "Renaming image <" + name + "> to <" + newName + ">");
        if (name.equals(newName)) {
            return this; // same name, do nothing
        }

        File newAudio = new File(
                category.getFolder(),
                StorageNameUtils.constructImageFileName(position, newName, AUDIO_SUFFIX));
        File newImage = new File(
                category.getFolder(),
                StorageNameUtils.constructImageFileName(position, newName, IMAGE_SUFFIX));
        try {
            FileUtils.moveFile(getAudio(), newAudio);
            FileUtils.moveFile(getImage(), newImage);

            return new Image(
                    category,
                    newImage,
                    newAudio);

        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    public void delete() throws StorageException {
        boolean success = true;
        success &= FileUtils.deleteQuietly(image);
        success &= FileUtils.deleteQuietly(audio);
        if (!success) {
            throw new StorageException("Image deletion failed! Either image or audio (or both) were not deleted!");
        }
    }
}
