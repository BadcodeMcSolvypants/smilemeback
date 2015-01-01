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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Storage class deals with Android filesystem and
 * manages categories, icons and user data.
 */
public class Storage {
    private static Logger logger = Logger.getLogger(Storage.class.getCanonicalName());

    /// Where to store application data in internal memory.
    public static final String STORAGE_FOLDER = "SmileMeBack";
    public static final String CATEGORIES_FOLDER = "categories";
    public static final String CATEGORY_THUMBNAIL = "_thumbnail.jpg";

    /**
     * Application context.
     */
    protected Context context = null;

    /**
     * Initialize a new Storage using the given application context.
     * @param context
     */
    public Storage(final Context context) {
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
            throw new StorageException(e);
        } finally {
            try {
                thumbnailStream.close();
            } catch (IOException e) {
                throw new StorageException(e);
            }
        }
    }

    /**
     * Append an empty category with default thumbnail.
     * @param name
     * @throws StorageException
     */
    public void appendEmptyCategory(final Name name) throws StorageException {
        appendEmptyCategory(name, context.getResources().openRawResource(R.drawable.iconview_testing));
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
            throw new StorageException(e);
        }
        // Sort the categories according to their name indices.
        Collections.sort(categories);
        // check that the category indices correspond to their positions in the list
        for (int idx=0 ; idx<categories.size() ; ++idx) {
            Category category = categories.get(idx);
            if (category.getIndex() != idx) {
                String err = "Category <" + category + "> index in name <" + category.getIndex() + "> does not correspond to index in category list <" + idx + ">";
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
            throw new StorageException(e);
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
            throw new StorageException(e);
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
            Name imageName = getName(imageFile);
            Name audioName = getName(audioFile);
            if (!imageName.equals(audioName)) {
                String err = "Names for image and audio differ for index <" + idx + ">: <" + imageName + "> <" + audioName + ">";
                throw new StorageException(err);
            }
            images.add(new Image(new Name(imageName), imageFile, audioFile));
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

    /**
     * Category and icon files are stored with index prefix on the filesystem:
     * {index}_filename.
     *
     * This method returns the index, given the filename of a folder or icon.
     *
     * @param file The file to retrieve the index from.
     * @return The index parsed from filename.
     *
     * @throws com.smilemeback.storage.StorageException In case there was an error retrieving the index.
     */
    public static int getIndex(File file) throws StorageException {
        try {
            String fnm = file.getName();
            int underscore_idx = fnm.indexOf("_");
            return Integer.parseInt(fnm.substring(0, underscore_idx));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new StorageException(e);
        }
    }

    /**
     * Category and icon files are stored with index prefix on the filesystem:
     * {index}_filename{optional_suffix}
     *
     * This method returns the name portion of the filename.
     *
     * @param file The file to retrieve the name from.
     */
    public static Name getName(File file) throws StorageException {
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
     * @return The mapping from icon index to files with given suffix.
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
        return scanCategoryFiles(category, ".jpg");
    }

    public static Map<Integer, File> scanCategoryAudio(final Category category) throws StorageException {
        return scanCategoryFiles(category, ".3gpp");
    }
}
