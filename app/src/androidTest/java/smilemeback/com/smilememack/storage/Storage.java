package smilemeback.com.smilememack.storage;

import java.util.List;

/**
 * Storage is a place where all program data, including categories, pictures,
 * audio recordings, user profiles etc is stored.
 */
public interface Storage {
    /**
     * @return List of all categories in the storage.
     */
    List<Category> getCategories();

    /**
     * Add a new category to storage.
     * @param category
     */
    void addCategory(Category category);

    /**
     * Delete a category from storage.
     * @param category
     */
    void deleteCategory(Category category);

    /**
     * Rename a category in storage.
     * @param category
     * @param newName The new name for the category.
     */
    void renameCategory(Category category, String newName);
}
