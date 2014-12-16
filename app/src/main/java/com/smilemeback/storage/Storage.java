package com.smilemeback.storage;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.smilememack.R;

/**
 * Storage class deals with Android filesystem and
 * manages categories, icons and user data.
 */
public class Storage {
    private static Logger logger = Logger.getLogger(Storage.class.getCanonicalName());

    /// Where to store application data in internal memory.
    public static String STORAGE_FOLDER = "SmileMeBack";
    public static String CATEGORIES_FOLDER = "categories";
    public static String CATEGORY_THUMBNAIL = "_thumbnail.jpg";

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
     * @return The filesystem storage of the application.
     */
    public static File getStorageFolder(final Context context) {
        return new File(context.getExternalFilesDir(null), STORAGE_FOLDER);
    }

    /**
     * Return the categories folder.
     * @param context
     * @return
     */
    public static File getCategoriesFolder(final Context context) {
        return new File(getStorageFolder(context), CATEGORIES_FOLDER);
    }

    /**
     * Add an empty category with thumbnail.
     * @param name The name of the category
     * @param thumbnailStream The input stream containing the
     * @throws StorageException In case creating a new empty directory fails.
     */
    public Category addEmptyCategory(CategoryName name, InputStream thumbnailStream) throws StorageException {
        logger.info("Adding empty category with name <" + name + ">");
        File category = new File(getCategoriesFolder(context), name.toString());
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
     * Add an empty category with default thumbnail.
     * @param name
     * @throws StorageException
     */
    public void addEmptyCategory(CategoryName name) throws StorageException {
        addEmptyCategory(name, context.getResources().openRawResource(R.drawable.iconview_testing));
    }

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
        return categories;
    }

    /**
     * This function deletes all categories with all data.
     * @throws StorageException
     */
    public void truncateAllCategories() throws StorageException {
        try {
            for (Category category : getCategories()) {
                FileUtils.deleteDirectory(category.getFolder());
            }
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }

    public void initializeTestingCategories() throws StorageException {
        truncateAllCategories();
        for (int i=0 ; i<8 ; ++i) {
            addEmptyCategory(new CategoryName("Testing category " + i));
        }
    }
}
