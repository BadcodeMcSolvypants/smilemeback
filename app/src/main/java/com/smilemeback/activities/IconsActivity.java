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
import android.content.Intent;
import android.os.Bundle;

import com.smilemeback.Constants;
import com.smilemeback.GalleryActivityState;
import com.smilemeback.R;
import com.smilemeback.adapters.IconGridAdapter;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;

import java.util.List;

public class IconsActivity extends GalleryActivity {

    protected int startCategoryIndex;
    protected List<Category> categories;
    protected Category currentCategory;

    protected IconGridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define the category we will be living in
        Intent intent = getIntent();
        startCategoryIndex = intent.getIntExtra(Constants.CATEGORY_INDEX, 0);

        // load categories
        loadCategories();
        currentCategory = categories.get(startCategoryIndex);

        gridAdapter = new IconGridAdapter(this, this, selectionManager, data);
        gridAdapter.setCurrentCategory(currentCategory);
        gridAdapter.initialize();
    }

    @Override
    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(getString(R.string.gallery_actionbar_title));
    }

    protected void loadCategories() {
        Storage storage = new Storage(this);
        try {
            categories = storage.getCategories();
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        }
    }

    @Override
    protected void initializeGridView() {
        data.gridView.setAdapter(gridAdapter);
    }


    @Override
    public void initializeListView() {
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

}
