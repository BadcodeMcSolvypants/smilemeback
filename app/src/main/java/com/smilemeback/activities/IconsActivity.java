/**
 * This file is part of SmileMeBack.

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
package com.smilemeback.activities;


import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.smilemeback.adapters.CategoryListAdapter;
import com.smilemeback.adapters.IconGridAdapter;
import com.smilemeback.adapters.ListAdapterListener;
import com.smilemeback.drag.ListDragResultListener;
import com.smilemeback.drag.ListViewDragListener;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.Dialogs;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Image;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import android.support.v4.app.NavUtils;

import java.util.ArrayList;
import java.util.List;

public class IconsActivity extends GalleryActivity implements ListAdapterListener, ListDragResultListener {

    protected IconGridAdapter gridAdapter;
    protected CategoryListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define the category we will be living in
        Intent intent = getIntent();
        int startCategoryIndex = intent.getIntExtra(Constants.CATEGORY_INDEX, 0);

        // load categories
        List<Category> categories = loadCategories();
        Category currentCategory = categories.get(startCategoryIndex);

        gridAdapter = new IconGridAdapter(this, this, selectionMode, selectionManager, data);
        gridAdapter.setCurrentCategory(currentCategory);
        gridAdapter.initialize();

        setActionBarTitle(currentCategory.getName().toString());
    }

    @Override
    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    protected void setActionBarTitle(String title) {
        getActionBar().setTitle(title);
    }

    protected List<Category> loadCategories() {
        List<Category> categories = null;
        Storage storage = new Storage(this);
        try {
            categories = storage.getCategories();
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        }
        return categories;
    }

    /**
     * Deselect all items, reload and initialize grid.
     */
    protected void reloadGrid() {
        selectionManager.deselectAll();
        data.gridView.setAdapter(gridAdapter);
        gridAdapter.initialize();
    }

    @Override
    protected void initializeGridView() {
        data.gridView.setAdapter(gridAdapter);
    }


    @Override
    public void initializeListView() {
        listAdapter = new CategoryListAdapter(this, this, gridAdapter.getCurrentCategory(), data.listView);
        data.listView.setAdapter(listAdapter);
        ListViewDragListener dragListener = new ListViewDragListener(selectionManager, selectionMode, listAdapter, this);
        data.listView.setOnDragListener(dragListener);
    }

    @Override
    protected void refreshGridView() {
        gridAdapter.notifyDataSetChanged();
        gridAdapter.setSelectedIconsChecked();
    }

    @Override
    protected void refreshSidePane() {

    }

    @Override
    public void gallerySelectionModeFinished() {
        super.gallerySelectionModeFinished();
        animateListViewOut();
    }

    @Override
    public void enterSelectionMode() {
        super.enterSelectionMode();
        animateListViewIn();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void categorySelected(Category category) {
        gridAdapter.setCurrentCategory(category);
        setActionBarTitle(category.getName().toString());
    }

    @Override
    public void moveSelectedIconsTo(int position) {
    }

    @Override
    public void moveSelectedIconsToCategory(int categoryIndex) {
    }

    @Override
    public void renameCurrentlySelectedIcon() {
        final Image image = (Image)gridAdapter.getItem(selectionManager.getSelectedPosition());
        String title = "Rename image";
        String posTitle = "Rename";
        String negTitle = "Cancel";
        Dialogs.InputCallback callback = new Dialogs.InputCallback() {
            @Override
            public void inputDone(String text) {
                logger.info("Renaming current icon to " + text);
                Storage storage = new Storage(IconsActivity.this);
                try {
                    storage.renameImage(image, text);
                } catch (StorageException e) {
                    showStorageExceptionAlertAndFinish(e);
                }
                reloadGrid();
            }
        };
        Dialogs.input(this, title, image.getName().toString(), posTitle, negTitle, callback);
    }

    @Override
    public void deleteCurrentlySelectedIcons() {
        String title = "Really delete " + selectionManager.getNumSelected() + " image";
        if (selectionManager.getNumSelected() > 1) {
            title += "s?";
        } else {
            title += '?';
        }
        String posTitle = "Delete";
        String negTitle = "Cancel";
        DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logger.info("Deleting selected images");
                try {
                    Storage storage = new Storage(IconsActivity.this);
                    List<Image> selectedImages = new ArrayList<>();
                    for (int idx : selectionManager.getSelectedPositions()) {
                        selectedImages.add((Image)gridAdapter.getItem(idx));
                    }
                    storage.deleteImages(gridAdapter.getCurrentCategory(), selectedImages);
                } catch (StorageException e) {
                    showStorageExceptionAlertAndFinish(e);
                }
                reloadGrid();
            }
        };
        Dialogs.confirmation(this, title, posTitle, negTitle, callback);
    }
}
