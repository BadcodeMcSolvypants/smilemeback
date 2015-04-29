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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a list of images.
 */
public class Images implements Iterable<Image> {
    private static String TAG = Storage.class.getCanonicalName();

    private final Category category;
    private final List<Image> images = new ArrayList<>();

    public Images(final Category category) throws StorageException {
        this.category = category;
    }

    @Override
    public Iterator<Image> iterator() {
        return null;
    }

    public int size() {
        return images.size();
    }

    private void parseImages() throws StorageException {
        // map icon indices to image and audio files
        Map<Integer, File> imageFiles = scanCategoryImages(category);
        Map<Integer, File> audioFiles = scanCategoryAudio(category);

        // zip them together and initialize icons
        try {
            List<Image> images = new ArrayList<>();
            for (int idx : imageFiles.keySet()) {
                File imageFile = imageFiles.get(idx);
                File audioFile = audioFiles.get(idx);
                Name imageName = StorageNameUtils.parseName(imageFile.getName());
                Name audioName = StorageNameUtils.parseName(audioFile.getName());
                if (!imageName.equals(audioName)) {
                    String err = "Names for image and audio differ for position <" + idx + ">: <" + imageName + "> <" + audioName + ">";
                    throw new StorageException(err);
                }
                images.add(new Image(category, imageFile, audioFile));
            }
            Collections.sort(images);

            // check positions
            for (int idx = 0; idx < images.size(); ++idx) {
                if (images.get(idx).getPosition() != idx) {
                    throw new StorageException("Image at position " + idx + " reports position " + images.get(idx).getPosition());
                }
            }
        } catch (NameException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Filter files with given suffix and return them as indexed map.
     * Assumes that the files are from a category folder, therefore
     * ignores "_thumbnail.jpg" image.
     *
     * It assumes that the filenames are encoded as following:
     * {icon_index}_{name}{suffix}
     *
     * @param category
     * @param suffix The suffix the desired filenames have to end with.
     * @return The mapping from icon position to files with given suffix.
     *
     * @throws com.smilemeback.storage.StorageException
     */
    public static Map<Integer, File> scanCategoryFiles(final Category category, final String suffix) throws StorageException {
        Map<Integer, File> files = new HashMap<>();
        for (File f : category.getFolder().listFiles()) {
            String fnm = f.getName();
            if (!fnm.equals(Category.THUMBNAIL)) {
                if (fnm.endsWith(suffix)) {
                    Log.d(TAG, "Scanning file <" + f.getAbsolutePath() + ">");
                    int position = StorageNameUtils.parsePosition(fnm).get();
                    files.put(position, f);
                }
            }
        }
        return files;
    }

    public static Map<Integer, File> scanCategoryImages(final Category category) throws StorageException {
        return scanCategoryFiles(category, Image.IMAGE_SUFFIX);
    }

    public static Map<Integer, File> scanCategoryAudio(final Category category) throws StorageException {
        return scanCategoryFiles(category, Image.AUDIO_SUFFIX);
    }


    public Image add(final Name name, final File imagePath, final File audioPath) throws StorageException {
        int position = size();
        File imageFile = new File(
                category.getFolder(),
                StorageNameUtils.constructImageFileName(position, name, Image.IMAGE_SUFFIX));
        File audioFile = new File(
                category.getFolder(),
                StorageNameUtils.constructImageFileName(position, name, Image.AUDIO_SUFFIX));
        Log.i(TAG, "Adding new Image to category " + category.getFolder());
        try {
            Log.d(TAG, "Copying image from " + imagePath + " to " + imageFile);
            FileUtils.copyFile(imagePath, imageFile);
            Log.d(TAG, "Copying audio from " + audioPath + " to " + audioFile);
            FileUtils.copyFile(audioPath, audioFile);
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            Image image = new Image(category, imageFile, audioFile);
            images.add(image);
            return image;
        }
    }

    public void delete(final Collection<Image> selection) throws StorageException {
        if (!images.containsAll(selection)) {
            throw new StorageException("Selection contains images not in this category!");
        }
        try {
            for (Image image : selection) {
                image.delete();
            }
        } catch (StorageException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            organize();
        }
    }

    public void organize() throws StorageException {
        // TODO: make code in this method more readable and more robust
        Log.d(TAG, "Reorganizing category <" + category.getName() + ">");
        Map<Integer, File> images = scanCategoryImages(category);
        Map<Integer, File> sounds = scanCategoryAudio(category);
        Set<Integer> common = new LinkedHashSet<>(images.keySet());
        common.retainAll(sounds.keySet());
        List<Integer> commonIdxs = new ArrayList<>(common);
        Collections.sort(commonIdxs);
        int lastUnused = 0;
        Set<File> keepPaths = new HashSet<>();
        keepPaths.add(category.getThumbnail());
        try {
            Log.d(TAG, "Removing gaps between icons");
            for (int current : commonIdxs) {
                if (current > lastUnused) {
                    // rename the indices
                    Name name = StorageNameUtils.parseName(images.get(current).getName());
                    File imagePath = new File(
                            category.getFolder(),
                            StorageNameUtils.constructImageFileName(lastUnused, name, Image.IMAGE_SUFFIX));
                    File audioPath = new File(
                            category.getFolder(),
                            StorageNameUtils.constructImageFileName(lastUnused, name, Image.AUDIO_SUFFIX));
                    // move files
                    FileUtils.deleteQuietly(imagePath);
                    FileUtils.deleteQuietly(audioPath);
                    Log.d(TAG, "Moving <" + images.get(current) + "> to <" + imagePath + ">");
                    FileUtils.moveFile(images.get(current), imagePath);
                    Log.d(TAG, "Moving <" + sounds.get(current) + "> to <" + audioPath + ">");
                    FileUtils.moveFile(sounds.get(current), audioPath);
                    keepPaths.add(imagePath);
                    keepPaths.add(audioPath);
                } else {
                    keepPaths.add(images.get(current));
                    keepPaths.add(sounds.get(current));
                }
                lastUnused += 1;
            }

            // remove all files that should not be in directory
            Log.d(TAG, "Deleting unused files");
            for (File file : category.getFolder().listFiles()) {
                if (!keepPaths.contains(file)) {
                    Log.d(TAG, "Deleting unused file <" + file + ">");
                    FileUtils.forceDelete(file);
                }
            }
        } catch (IOException | NameException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            images.clear();
            parseImages();
        }
    }
}
