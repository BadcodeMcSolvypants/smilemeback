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
package com.smilemeback.adapters;

import android.content.Intent;

import com.smilemeback.activities.CategoriesActivity;
import com.smilemeback.activities.IconsActivity;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.GalleryActivityData;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.storage.Categories;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilemeback.views.IconView;

/**
 * Manages gallery icons in a gridView.
 */
public class CategoryGridAdapter extends BaseGridAdapter {

    private Categories categories;

    public CategoryGridAdapter(CategoriesActivity activity, GridAdapterListener listener, SelectionMode selectionMode, SelectionManager selectionManager, GalleryActivityData data) {
        super(activity, listener, selectionMode, selectionManager, data);
    }

    /**
     * Load the categories and initialize the adapter,
     * also refresh the associated gridview.
     *
     * @throws StorageException
     */
    public void initialize()  {
        try {
            Storage storage = new Storage(activity);
            categories = storage.getCategories();
            selectionManager.setNumTotal(categories.size());
            selectionManager.deselectAll();
            data.gridView.setAdapter(this);
        } catch (StorageException e) {
            activity.showStorageExceptionAlertAndFinish(e);
        }
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    void prepareIconView(IconView view, int position) {
        final Category category = categories.get(position);
        view.setImageBitmap(category.getThumbnail());
        view.setLabel(category.getName().toString());
    }

    @Override
    void handleIconClick(IconView view, int position) {
        Intent intent = new Intent(activity, IconsActivity.class);
        intent.putExtra(Constants.CATEGORY_INDEX, position);
        activity.startActivity(intent);
    }
}
