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
import android.content.res.AssetManager;

import com.smilemeback.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Storage class deals with Android filesystem and
 * manages categories, icons and user data.
 *
 * TODO: needs unit tests and more robust reorganization functions.
 */
public class OldStorage {
    private static Logger logger = Logger.getLogger(OldStorage.class.getCanonicalName());

    /// Where to store application data in internal memory.
    public static final String STORAGE_FOLDER = "SmileMeBack";
    public static final String CATEGORIES_FOLDER = "categories";
    public static final String CATEGORY_THUMBNAIL = "_thumbnail.jpg";
    public static final String IMAGE_SUFFIX = ".jpg";
    public static final String AUDIO_SUFFIX = ".3gpp";
    public static final String TEMPORARY_IMAGE = "temporary_image" + IMAGE_SUFFIX;
    public static final String TEMPORARY_AUDIO = "temporary_audio" + AUDIO_SUFFIX;

    /**
     * Application context.
     */
    protected Context context = null;

    /**
     * Initialize a new Storage using the given application context.
     * @param context
     */
    public OldStorage(final Context context) {
        this.context = context;
    }

    /**
     * Get the storage folder.
     * @param context
     * @return The filesystem storage folder file.
     */
    public static File getStorageFolder(final Context context) {
        return new File(context.getExternalFilesDir(null), STORAGE_FOLDER);
    }

    /**
     * Return the categories folder.
     * @param context
     * @return The filesystem categories storage file.
     */
    public static File getCategoriesFolder(final Context context) {
        return new File(getStorageFolder(context), CATEGORIES_FOLDER);
    }

    /**
     * Get the number of categories in the storage.
     *
     * @return The number of categories.
     * @throws StorageException If the number of categories cannot be computed due to errors
     *  in their enumeration or problems with filesystem.
     */
    public int getNumberOfCategories() throws StorageException {
        return getCategories().size();
    }

    /**
     * Append an empty category with thumbnail.
     *
     * @param name The name of the category
     * @param thumbnailStream The input stream containing the
     * @throws StorageException In case creating a new empty directory fails.
     */
    public Category appendEmptyCategory(final Name name, final InputStream thumbnailStream) throws StorageException {
        logger.info("Adding empty category with name <" + name + ">");
        File category = new File(getCategoriesFolder(context), getNumberOfCategories() + "_" + name);
        if (category.exists() && !category.isDirectory()) {
            String msg = "Cannot add empty category with name <" + category + ">. " +
                    " File with same name exists: <" + category.getAbsolutePath() + ">";
            throw new StorageException(msg);
        } else if (category.exists() && category.isDirectory()) {
            String msg = "Cannot add empty category with name <" + category + ">. " +
                    " Directory with same name exists: <" + category.getAbsolutePath() + ">";
            throw new StorageException(msg);
        }
        // create category and add thumbnail directory.
        try {
            FileUtils.forceMkdir(category);
            File thumbNailFile = new File(category, CATEGORY_THUMBNAIL);
            FileUtils.copyInputStreamToFile(thumbnailStream, thumbNailFile);
            return new Category(category);
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

    /**
     * Append an empty category with default thumbnail.
     * @param name
     * @throws StorageException
     */
    public void appendEmptyCategory(final Name name) throws StorageException {
        appendEmptyCategory(name, context.getResources().openRawResource(+R.drawable.iconview_default));
    }

    /**
     * Get all categories.
     * If the folder containing the categories does not exist, it creates it automatically.
     * In this case, of course, the function returns empty list.
     *
     * @return
     * @throws StorageException
     */
    public List<Category> getCategories() throws StorageException {
        File categoriesFolder = getCategoriesFolder(context);
        List<Category> categories = new ArrayList<>();
        try {
            FileUtils.forceMkdir(categoriesFolder);
            for (File categoryFolder : categoriesFolder.listFiles()) {
                if (categoriesFolder.isDirectory()) {
                    categories.add(new Category(categoryFolder));
                } else {
                    logger.warning("Non-directory exists in categories folder <" + categoriesFolder.getAbsolutePath() + ">");
                }
            }
        } catch (IOException | StorageException e) {
            throw new StorageException(e.getMessage(), e);
        }
        // Sort the categories according to their name indices.
        Collections.sort(categories);
        // check that the category indices correspond to their positions in the list
        for (int idx=0 ; idx<categories.size() ; ++idx) {
            Category category = categories.get(idx);
            if (category.getPosition() != idx) {
                String err = "Category <" + category + "> position in name <" + category.getPosition() + "> does not correspond to position in category list <" + idx + ">";
                throw new StorageException(err);
            }
        }
        // log the category names
        StringBuilder sb = new StringBuilder("Returning categories: ");
        for (Category category : categories) {
            sb.append(category.getName() + " ");
        }
        logger.info(sb.toString());
        return categories;
    }

    /**
     * This function deletes all categories with all data.
     * @throws StorageException
     */
    public void truncateAllCategories() throws StorageException {
        logger.info("Truncating all categories");
        try {
            FileUtils.deleteDirectory(getCategoriesFolder(context));
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Set up a number of categories in the assets folder for demo and development purposes.
     * @throws StorageException
     */
    public void initializeTestingCategories() throws StorageException {
        try {
            truncateAllCategories();
            File categoriesFolder = getCategoriesFolder(context);
            AssetManager asm = context.getAssets();
            int catIdx = 0;
            for (String dnm : asm.list("categories")) {
                File categoryFolder = new File(categoriesFolder, catIdx + "_" + dnm);
                categoriesFolder.mkdirs();
                for (String fnm : asm.list("categories/" + dnm)) {
                    File dest = new File(categoryFolder, fnm);
                    FileUtils.copyInputStreamToFile(asm.open("categories/" + dnm + "/" + fnm), dest);
                    logger.info("Copying demo asset <" + dnm + "/" + fnm + "> to <" + dest.getAbsolutePath() + ">");
                }
                catIdx += 1;
            }
        } catch (IOException | StorageException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Return the list of images in the category.
     * Images also contain an audio clip saying what is seen in the image.
     *
     * @param category
     * @return
     * @throws StorageException
     */
    public List<Image> getCategoryImages(final Category category) throws StorageException {
        // map icon indices to image and audio files
        Map<Integer, File> imageFiles = scanCategoryImages(category);
        Map<Integer, File> audioFiles = scanCategoryAudio(category);

        // zip them together and initialize icons
        List<Image> images = new ArrayList<>();
        for (int idx : imageFiles.keySet()) {
            File imageFile = imageFiles.get(idx);
            File audioFile = audioFiles.get(idx);
            try {
                Name imageName = getName(imageFile);
                Name audioName = getName(audioFile);

                if (!imageName.equals(audioName)) {
                    String err = "Names for image and audio differ for position <" + idx + ">: <" + imageName + "> <" + audioName + ">";
                    throw new StorageException(err);
                }
                images.add(new Image(category, imageFile, audioFile));
            } catch (NameException e) {
                throw new StorageException(e.getMessage(), e);
            }
        }
        Collections.sort(images);

        // check positions
        for (int idx=0 ; idx<images.size() ; ++idx) {
            if (images.get(idx).getPosition() != idx) {
                throw new StorageException("Image at position " + idx + " reports position " + images.get(idx).getPosition());
            }
        }

        // log the images
        // log the category names
        StringBuilder sb = new StringBuilder("Returning images for category <" + category.getName() + ">: ");
        for (Image image : images) {
            sb.append(image.getName() + " ");
        }
        logger.info(sb.toString());

        return images;
    }

    public void addCategoryImage(final Category category, String name, File imagePath, File audioPath) throws StorageException {
        List<Image> images = getCategoryImages(category);
        int nextIdx = images.size();
        try {
            // copy the files
            File imageFile = new File(category.getFolder(), constructStringFromName(nextIdx, new Name(name), IMAGE_SUFFIX));
            File audioFile = new File(category.getFolder(), constructStringFromName(nextIdx, new Name(name), AUDIO_SUFFIX));
            logger.info("Adding new Image to Category " + category.getFolder());

            logger.info("Copying image from " + imagePath + " to " + imageFile);
            FileUtils.copyFile(imagePath, imageFile);
            logger.info("Copying audio from " + audioPath + " to " + audioFile);
            FileUtils.copyFile(audioPath, audioFile);
        } catch (IOException | NameException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    public void deleteImages(final Category category, List<Image> images) throws StorageException {
        logger.info("Deleting " + images.size() + " images from category <" + category.getName() + ">");
        try {
            for (Image image : images) {
                logger.info("Deleting image <" + image.getImage() + ">");
                FileUtils.forceDelete(image.getImage());
                logger.info("Deleting audio <" + image.getAudio() + ">");
                FileUtils.forceDelete(image.getAudio());
            }
        } catch(IOException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            reorganizeCategory(category);
        }
    }

    public void deleteCategories(List<Category> categories) throws StorageException {
        try {
            for (Category category : categories) {
                FileUtils.deleteDirectory(category.getFolder());
            }
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            reorganizeCategories();
        }
    }

    /**
     * Reorganize categories.
     * Removes gaps in category indices, if there are any.
     *
     * TODO: create temporary
     */
    public void reorganizeCategories() throws StorageException {
        File categoriesFolder = getCategoriesFolder(context);
        Map<Integer, File> categories = new HashMap<>();
        Map<Integer, String> names = new HashMap<>();
        try {
            FileUtils.forceMkdir(categoriesFolder);
            for (File category : categoriesFolder.listFiles()) {
                if (category.isDirectory()) {
                    String[] tokens = category.getName().split("_");
                    int pos = Integer.parseInt(tokens[0]);
                    String name = tokens[1];
                    categories.put(pos, category);
                    names.put(pos, name);
                } else {
                    logger.info("Deleting non-directory <" + category + ">");
                    FileUtils.forceDelete(category);
                }
            }
            List<Integer> indices = new ArrayList<>(categories.keySet());
            Collections.sort(indices);
            int nextPos = 0;
            for (int idx : indices) {
                File srcFile = categories.get(idx);
                String name = names.get(idx);
                File destFile = new File(categoriesFolder, nextPos + "_" + name);
                if (srcFile.compareTo(destFile) != 0) {
                    logger.info("Moving category <" + srcFile + "> to <" + destFile + ">");
                    FileUtils.moveDirectory(srcFile, destFile);
                }
                nextPos += 1;
            }
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    public void moveImage(final Category srcCategory, final Image image, final Category destCategory) throws StorageException {
        List<Image> images = new ArrayList<>();
        images.add(image);
        moveImages(srcCategory, images, destCategory);
    }

    /**
     * Move a list of images from one category directory to another.
     * The moved images are appended to the end of the destination directory.
     * @param srcCategory Source category.
     * @param images List of images in source category.
     * @param destCategory Destination category
     * @throws StorageException
     */
    public void moveImages(final Category srcCategory, final List<Image> images, final Category destCategory) throws StorageException {
        List<Image> destImages = getCategoryImages(destCategory);
        int destIndex = destImages.size();
        File destFolderPath = destCategory.getFolder();
        logger.info("Moving images from category <" + srcCategory.getFolder() + "> to <" + destCategory.getFolder() + ">");
        try {
            for (Image image : images) {
                File imagePath = new File(destFolderPath, constructStringFromName(destIndex, image.getName(), IMAGE_SUFFIX));
                File audioPath = new File(destFolderPath, constructStringFromName(destIndex, image.getName(), AUDIO_SUFFIX));
                logger.info("Moving <" + image.getImage() + "> to <" + imagePath +">");
                FileUtils.moveFile(image.getImage(), imagePath);
                logger.info("Moving <" + image.getAudio() + "> to <" + audioPath +">");
                FileUtils.moveFile(image.getAudio(), audioPath);
                destIndex += 1;
            }
            reorganizeCategory(srcCategory);
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            reorganizeCategory(srcCategory);
        }
    }

    /**
     * Reorganize given category.
     * - Corrects image and audio indices, fills gaps and moved folders.
     * - Removes any files that should not belong to the category folder.
     * @param category
     * @throws StorageException
     */
    public void reorganizeCategory(final Category category) throws StorageException {
        logger.info("Reorganizing category <" + category.getName() + ">");
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
            logger.info("Removing gaps between icons");
            for (int current : commonIdxs) {
                if (current > lastUnused) {
                    // rename the indices
                    Name name = getName(images.get(current));
                    File imagePath = new File(category.getFolder(), constructStringFromName(lastUnused, name, IMAGE_SUFFIX));
                    File audioPath = new File(category.getFolder(), constructStringFromName(lastUnused, name, AUDIO_SUFFIX));
                    // move files
                    FileUtils.deleteQuietly(imagePath);
                    FileUtils.deleteQuietly(audioPath);
                    logger.info("Moving <" + images.get(current) + "> to <" + imagePath +">");
                    FileUtils.moveFile(images.get(current), imagePath);
                    logger.info("Moving <" + sounds.get(current) + "> to <" + audioPath +">");
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
            logger.info("Deleting unused files");
            for (File file : category.getFolder().listFiles()) {
                if (!keepPaths.contains(file)) {
                    logger.info("Deleting unused file <" + file + ">");
                    FileUtils.forceDelete(file);
                }
            }
        } catch (IOException | NameException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Rename given image.
     * @param image The image to rename
     * @param newName New name for the image.
     * @throws StorageException
     */
    public void renameImage(Image image, String newName) throws StorageException {
        if (image.getName().toString().compareTo(newName) == 0) {
            // same name, do nothing
            return;
        }
        try {
            Name name = new Name(newName);
            File newAudio = new File(image.getCategory().getFolder(),
                    constructStringFromName(image.getPosition(), name, AUDIO_SUFFIX));
            File newImage = new File(image.getCategory().getFolder(),
                    constructStringFromName(image.getPosition(), name, IMAGE_SUFFIX));

            FileUtils.moveFile(image.getAudio(), newAudio);
            FileUtils.moveFile(image.getImage(), newImage);
        } catch (IOException | NameException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Rename category.
     * @param category The category instance to rename.
     * @param newName The new name.
     * @throws StorageException In case rename failed.
     */
    public void renameCategory(Category category, String newName) throws StorageException {
        if (category.getName().toString().compareTo(newName) == 0) {
            // same name, do nothing!
            return;
        }
        File categories = getCategoriesFolder(context);
        File newCategory = new File(categories, category.getPosition() + "_" + newName);
        try {
            FileUtils.moveDirectory(category.getFolder(), newCategory);
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Switch the positions of selection selectedImages.
     * @param selectedImages
     * @param switchImage
     */
    public void switchImages(List<Image> selectedImages, Image switchImage) throws StorageException {
        ImageSwitcher imageSwitcher = new ImageSwitcher(this, selectedImages, switchImage);
        List<Integer> srcPositions = imageSwitcher.getSourcePositions();
        List<Integer> destPositions = imageSwitcher.getDestinationPositions();

        // copy everything to temporary positions
        Category category = switchImage.getCategory();
        List<File> tempImages = new ArrayList<>();
        List<File> tempAudios = new ArrayList<>();
        for (int tempIdx=0 ; tempIdx<srcPositions.size() ; ++tempIdx) {
            File tempImage = new File(category.getFolder(), "temp_ " + tempIdx + IMAGE_SUFFIX);
            File tempAudio = new File(category.getFolder(), "temp_" + tempIdx + AUDIO_SUFFIX);
            tempImages.add(tempImage);
            tempAudios.add(tempAudio);
        }

        try {
            // move source data to temporary data
            List<Image> catImages = getCategoryImages(category);
            for (int idx = 0; idx < srcPositions.size(); ++idx) {
                int srcIdx = srcPositions.get(idx);
                Image catImage = catImages.get(srcIdx);
                FileUtils.moveFile(catImage.getImage(), tempImages.get(idx));
                FileUtils.moveFile(catImage.getAudio(), tempAudios.get(idx));
            }

            // move temporary data to final positions
            for (int idx = 0; idx < destPositions.size(); ++idx) {
                int srcIdx = srcPositions.get(idx);
                int destIdx = destPositions.get(idx);
                Name name = catImages.get(srcIdx).getName();
                File destImage = new File(category.getFolder(), constructStringFromName(destIdx, name, IMAGE_SUFFIX));
                File destAudio = new File(category.getFolder(), constructStringFromName(destIdx, name, AUDIO_SUFFIX));
                FileUtils.moveFile(tempImages.get(idx), destImage);
                FileUtils.moveFile(tempAudios.get(idx), destAudio);
            }
        } catch (IOException e) {
            throw new StorageException(e.getMessage(), e);
        } finally {
            reorganizeCategory(category);
        }
    }

    /**
     * @return The file pointing to the path that should be used for temporary images.
     */
    public File getTemporaryImageFile() {
        return new File(getStorageFolder(context), TEMPORARY_IMAGE);
    }

    /**
     * @return The file pointing to the path that should be used for temporary audio recordings.
     */
    public File getTemporaryAudioFile() {
        return new File(getStorageFolder(context), TEMPORARY_AUDIO);
    }

    /**
     * Category and icon files are stored with position prefix on the filesystem:
     * {position}_filename.
     *
     * This method returns the position, given the filename of a folder or icon.
     *
     * @param file The file to retrieve the position from.
     * @return The position parsed from filename.
     *
     * @throws com.smilemeback.storage.StorageException In case there was an error retrieving the position.
     */
    public static int getIndex(File file) throws StorageException {
        try {
            String fnm = file.getName();
            int underscore_idx = fnm.indexOf("_");
            return Integer.parseInt(fnm.substring(0, underscore_idx));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    /**
     * Category and icon files are stored with position prefix on the filesystem:
     * {position}_filename{optional_suffix}
     *
     * This method returns the name portion of the filename.
     *
     * @param file The file to retrieve the name from.
     */
    public static Name getName(File file) throws NameException{
        String fnm = file.getName();
        // if the file has a suffix, then trim the suffix, otherwise use everything
        // after the "_" separator.
        int underscore_idx = fnm.indexOf('_') + 1;
        String name = fnm.substring(underscore_idx);
        int suffix_idx = fnm.lastIndexOf('.');
        if (suffix_idx != -1) {
            name = fnm.substring(underscore_idx, suffix_idx);
        }
        return new Name(name);
    }

    public String constructStringFromName(int position, Name name, String suffix) {
        return String.format("%d_%s%s", position, name.toString(), suffix);
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
            if (!fnm.equals(CATEGORY_THUMBNAIL)) {
                if (fnm.endsWith(suffix)) {
                    logger.info("Scanning file <" + f.getAbsolutePath() + ">");
                    int idx = getIndex(f);
                    if (idx != -1) {
                        files.put(idx, f);
                    }
                }
            }
        }
        return files;
    }

    public static Map<Integer, File> scanCategoryImages(final Category category) throws StorageException {
        return scanCategoryFiles(category, IMAGE_SUFFIX);
    }

    public static Map<Integer, File> scanCategoryAudio(final Category category) throws StorageException {
        return scanCategoryFiles(category, AUDIO_SUFFIX);
    }

}
