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
import android.view.MenuItem;

import com.smilemeback.adapters.CategoryListAdapter;
import com.smilemeback.adapters.IconGridAdapter;
import com.smilemeback.adapters.ListAdapterListener;
import com.smilemeback.drag.ListDragResultListener;
import com.smilemeback.drag.ListViewDragListener;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.Dialogs;
import com.smilemeback.storage.Categories;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Image;
import com.smilemeback.storage.Images;
import com.smilemeback.storage.Name;
import com.smilemeback.storage.NameException;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import android.support.v4.app.NavUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
        Categories categories = loadCategories();
        Category currentCategory = categories.get(startCategoryIndex);

        gridAdapter = new IconGridAdapter(this, this, selectionMode, selectionManager, data);
        gridAdapter.setCurrentCategory(currentCategory);
        gridAdapter.initialize();

        listAdapter = new CategoryListAdapter(this, this, gridAdapter.getCurrentCategory(), data.listView);
        listAdapter.setSelectedItemPosition(startCategoryIndex);

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

    protected Categories loadCategories() {
        try {
            return new Storage(this).getCategories();
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        }
        return null;
    }

    /**
     * Deselect all collection, reload and initialize grid.
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
    public void rearrangeIconsAccordingToTarget(int position) {
        List<Integer> sortedIdxs = new ArrayList<>(selectionManager.getSelectedPositions());
        Collections.sort(sortedIdxs);
        List<Image> selectedImages = new ArrayList<>();
        for (int selectedIdx : sortedIdxs) {
            selectedImages.add((Image)gridAdapter.getItem(selectedIdx));
        }
        Image target = (Image)gridAdapter.getItem(position);

        try {
            gridAdapter.getCurrentCategory().getImages().rearrange(selectedImages, target);
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        } finally {
            reloadGrid();
        }
    }

    @Override
    public void moveSelectedIconsToCategory(int categoryIndex) {
        List<Image> selectedImages = new ArrayList<>();
        for (int idx : selectionManager.getSelectedPositions()) {
            selectedImages.add((Image)gridAdapter.getItem(idx));
        }
        try {
            Categories categories = loadCategories();
            Category destination = categories.get(categoryIndex);
            gridAdapter.getCurrentCategory().getImages().moveTo(destination, selectedImages);
            gridAdapter.setCurrentCategory(destination);
            listAdapter.setSelectedItemPosition(categoryIndex);
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        }
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
                try {
                    image.rename(new Name(text));
                } catch (StorageException e) {
                    showStorageExceptionAlertAndFinish(e);
                } catch (NameException e) {
                    showStorageExceptionAlertAndFinish(new StorageException(e.getMessage(), e));
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
                logger.info("Deleting selection images");
                try {
                    List<Image> selectedImages = new ArrayList<>();
                    for (int idx : selectionManager.getSelectedPositions()) {
                        selectedImages.add((Image)gridAdapter.getItem(idx));
                    }
                    Images images = new Images(gridAdapter.getCurrentCategory());
                    images.delete(selectedImages);
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
        startActivityForResult(intent, IconsActivity.ADD_PICUTURE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PICUTURE_INTENT && resultCode == RESULT_OK) {
            String name = data.getStringExtra(Constants.ADDED_IMAGE_NAME);
            String imagePath = data.getStringExtra(Constants.ADDED_IMAGE_PATH);
            String audioPath = data.getStringExtra(Constants.ADDED_IMAGE_AUDIO_PATH);
            try {
                Images images = new Images(gridAdapter.getCurrentCategory());
                images.add(new Name(name), new File(imagePath), new File(audioPath));
                reloadGrid();
            } catch (StorageException e) {
                showStorageExceptionAlertAndFinish(e);
            } catch (NameException e) {
                showStorageExceptionAlertAndFinish(new StorageException(e.getMessage(), e));
            }
        }
    }
}
