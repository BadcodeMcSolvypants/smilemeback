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

import com.smilemeback.R;
import com.smilemeback.adapters.CategoryGridAdapter;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.Dialogs;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Image;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Top-level activity that displays available categories
 * and enables rename/delete/etc actions on them.
 */
public class CategoriesActivity extends GalleryActivity {

    protected CategoryGridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridAdapter = new CategoryGridAdapter(this, this, selectionMode, selectionManager, data);
        gridAdapter.initialize();
    }

    @Override
    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.gallery_actionbar_title));
    }

    @Override
    protected void initializeGridView() {
        data.gridView.setAdapter(gridAdapter);
        gridAdapter.initialize();
    }

    protected void reloadGrid() {
        selectionManager.deselectAll();
        data.gridView.setAdapter(gridAdapter);
        gridAdapter.initialize();
    }

    @Override
    protected void initializeListView() {
    }

    @Override
    protected void refreshGridView() {
        gridAdapter.notifyDataSetChanged();
        gridAdapter.setSelectedIconsChecked();
    }

    @Override
    protected void refreshSidePane() { }

    @Override
    public void moveSelectedIconsTo(int position) {
    }

    @Override
    public void renameCurrentlySelectedIcon() {
        final Category category = (Category)gridAdapter.getItem(selectionManager.getSelectedPosition());
        String title = "Rename category";
        String posTitle = "Rename";
        String negTitle = "Cancel";
        Dialogs.InputCallback callback = new Dialogs.InputCallback() {
            @Override
            public void inputDone(String text) {
                logger.info("Renaming current category to " + text);
                Storage storage = new Storage(CategoriesActivity.this);
                try {
                    storage.renameCategory(category, text);
                } catch (StorageException e) {
                    showStorageExceptionAlertAndFinish(e);
                }
                reloadGrid();
            }
        };
        Dialogs.input(this, title, category.getName().toString(), posTitle, negTitle, callback);
    }

    @Override
    public void deleteCurrentlySelectedIcons() {
        String title = "Really delete " + selectionManager.getNumSelected() + " categor";
        if (selectionManager.getNumSelected() > 1) {
            title += "ies?";
        } else {
            title += "y?";
        }
        String posTitle = "Delete";
        String negTitle = "Cancel";
        DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logger.info("Deleting selected categories");
                try {
                    Storage storage = new Storage(CategoriesActivity.this);
                    List<Category> selectedCategories = new ArrayList<>();
                    for (int idx : selectionManager.getSelectedPositions()) {
                        selectedCategories.add((Category)gridAdapter.getItem(idx));
                    }
                    storage.deleteCategories(selectedCategories);
                } catch (StorageException e) {
                    showStorageExceptionAlertAndFinish(e);
                }
                reloadGrid();
            }
        };
        Dialogs.confirmation(this, title, posTitle, negTitle, callback);
    }

    @Override
    public void addNewIcon() {
        Intent intent = new Intent(this, AddPictureActivity.class);
        startActivityForResult(intent, OldGalleryActivity.ADD_PICUTURE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == ADD_PICUTURE_INTENT && resultCode == RESULT_OK) {
            String name = data.getStringExtra(Constants.ADDED_IMAGE_NAME);
            String imagePath = data.getStringExtra(Constants.ADDED_IMAGE_PATH);
            String audioPath = data.getStringExtra(Constants.ADDED_IMAGE_AUDIO_PATH);
            Storage storage = new Storage(this);
            try {
                storage.addCategoryImage(currentCategory, name, new File(imagePath), new File(audioPath));
                loadImages(currentCategory);
                gridView.setAdapter(imageAdapter);
                deselectAllItems();
                selectionMode.setTotal(images.size());
                selectionMode.setNumSelected(0);
            } catch (StorageException e) {
                showStorageExceptionAlertAndFinish(e);
            }
        }*/
    }
}
