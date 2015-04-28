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
package com.smilemeback.storage.datamover;

import android.content.Context;

import com.smilemeback.storage.Category;
import com.smilemeback.storage.OldStorage;

import java.io.File;
import java.util.List;

public class CategoryDataMover extends DataMover<Category> {

    private boolean categoriesMoved = false;

    public CategoryDataMover(List<Category> categories, List<Category> selection, Category target) {
        super(categories, selection, target);
    }

    public void moveCategories(Context context) {
        if (categoriesMoved) {
            throw new IllegalStateException("Cannot move categories twice!");
        }
        File categoryFolder = OldStorage.getCategoriesFolder(context);
        categoriesMoved = true;
    }
}
