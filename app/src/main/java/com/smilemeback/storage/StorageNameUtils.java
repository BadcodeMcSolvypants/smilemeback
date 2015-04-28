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

import com.google.common.base.Optional;

/**
 * Class dealing with parsing and creating {@link com.smilemeback.storage.Image} and
 * {@link com.smilemeback.storage.Category} names.
 */
public class StorageNameUtils {

    /**
     * Function to construct a filename for image audio or data.
     * Throws {@link java.lang.IllegalArgumentException} in case of an illegal suffix.
     *
     * @param position The position of the {@link com.smilemeback.storage.Image} in the category.
     * @param name The {@link com.smilemeback.storage.Name} of the image.
     * @param suffix The desired suffix representing the data type.
     * @return The string representing the filename.
     */
    public static String constructImageFileName(final int position, final Name name, final String suffix) {
        if (!suffix.equals(Image.IMAGE_SUFFIX) || !suffix.equals(Image.AUDIO_SUFFIX)) {
            throw new IllegalArgumentException("Suffix must be either " + Image.IMAGE_SUFFIX + " or " + Image.AUDIO_SUFFIX);
        }
        return String.format("%d_%s%s", position, name.toString(), suffix);
    }

    /**
     * @param position The position of the {@link com.smilemeback.storage.Category}.
     * @param name The {@link com.smilemeback.storage.Name} of the category.
     * @return Constructed filename for the position and category name.
     */
    public static String constructCategoryFileName(final int position, final Name name) {
        return String.format("%d_%s", position, name.toString());
    }

    /**
     * Names are stored with position prefix on the filesystem:
     * {position}_filename{optional_suffix}
     *
     * This method returns the name portion of the filename.
     *
     * @param fileName The file to retrieve the name from.
     * @return The name parsed from the filename.
     */
    public static Name parseName(String fileName) throws NameException {
        int underscore_idx = fileName.indexOf('_') + 1;
        String name = fileName.substring(underscore_idx);
        int suffix_idx = fileName.lastIndexOf('.');
        if (suffix_idx != -1) {
            name = fileName.substring(underscore_idx, suffix_idx);
        }
        return new Name(name);
    }

    /**
     * Positions are stored as a prefix on the filesystem:
     * {position}_filename{optinal_suffix} .
     *
     * This method returns the position, given the filename.
     *
     * @param fileName The file to retrieve the position from.
     * @return The position parsed from filename.
     */
    public static Optional<Integer> parsePosition(String fileName) {
        int underscore_idx = fileName.indexOf("_");
        try {
            return Optional.fromNullable(Integer.parseInt(fileName.substring(0, underscore_idx)));
        } catch (NumberFormatException e) {
            return Optional.fromNullable(null);
        }
    }

    /**
     * @param fileName
     * @return The suffix of the fileName, if it exists.
     */
    public static Optional<String> parseSuffix(String fileName) {
        int idx = fileName.lastIndexOf(".");
        if (idx == -1) {
            return Optional.fromNullable(null);
        }
        return Optional.fromNullable(fileName.substring(idx));
    }
}
