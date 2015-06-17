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
package com.smilemeback.activities;


import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.smilemeback.R;
import com.smilemeback.adapters.CategoryGridAdapter;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.Dialogs;
import com.smilemeback.storage.Categories;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Name;
import com.smilemeback.storage.NameException;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Top-level activity that displays available categories
 * and enables rename/delete/etc actions on them.
 */
public class CategoriesActivity extends GalleryBaseActivity {

    protected CategoryGridAdapter gridAdapter;
    private final static int ADD_CATEGORY_ACTIVITY = 1;

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
        //setupTestingCategories();
    }

    @Override
    protected void refreshGridView() {
        gridAdapter.notifyDataSetChanged();
        gridAdapter.setSelectedIconsChecked();
    }

    @Override
    protected void refreshSidePane() { }

    @Override
    public void rearrangeIconsAccordingToTarget(int position) {
        List<Integer> sortedIdxs = new ArrayList<>(selectionManager.getSelectedPositions());
        Collections.sort(sortedIdxs);
        List<Category> selectedCategories = new ArrayList<>();
        for (int selectedIdx : sortedIdxs) {
            selectedCategories.add((Category) gridAdapter.getItem(selectedIdx));
        }
        Category target = (Category)gridAdapter.getItem(position);

        if (selectedCategories.contains(target)) {
            return;
        }

        try {
            new Storage(this).getCategories().rearrange(selectedCategories, target);
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        } finally {
            reloadGrid();
        }
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
                try {
                    category.rename(new Name(text));
                } catch (StorageException e) {
                    showStorageExceptionAlertAndFinish(e);
                } catch (NameException e) {
                    showStorageExceptionAlertAndFinish(new StorageException(e.getMessage(), e));
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
                logger.info("Deleting selection categories");
                try {
                    List<Category> selectedCategories = new ArrayList<>();
                    for (int idx : selectionManager.getSelectedPositions()) {
                        selectedCategories.add((Category)gridAdapter.getItem(idx));
                    }
                    Categories categories = new Storage(CategoriesActivity.this).getCategories();
                    categories.delete(selectedCategories);
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
        Intent intent = new Intent(this, AddCategoryActivity.class);
        startActivityForResult(intent, ADD_CATEGORY_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CATEGORY_ACTIVITY && resultCode == RESULT_OK) {
            String name = data.getStringExtra(Constants.ADDED_IMAGE_NAME);
            String imagePath = data.getStringExtra(Constants.ADDED_IMAGE_PATH);
            try {
                Categories categories = new Storage(this).getCategories();
                categories.add(new Name(name), new BufferedInputStream(new FileInputStream(new File(imagePath))));
            } catch (NameException | IOException | StorageException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categories_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.gallery_selectionmode_menu_add_album:
                addNewIcon();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
