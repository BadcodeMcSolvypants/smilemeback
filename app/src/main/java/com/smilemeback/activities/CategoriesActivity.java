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
import android.os.Bundle;

import com.smilemeback.R;
import com.smilemeback.adapters.CategoryGridAdapter;

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
}
