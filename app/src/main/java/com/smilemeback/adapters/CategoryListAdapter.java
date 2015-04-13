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
package com.smilemeback.adapters;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.smilemeback.R;
import com.smilemeback.activities.IconsActivity;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilemeback.views.IconViewSide;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages categories in side pane list view.
 */
public class CategoryListAdapter extends BaseAdapter {
    protected IconsActivity activity;
    protected int selectedPosition = 0;
    protected int hoverPosition = -1;
    protected List<Category> categories = new ArrayList<>();
    protected Category currentCategory;
    protected ListAdapterListener listener;
    protected ListView listView;

    public CategoryListAdapter(IconsActivity activity, final ListAdapterListener listener, final Category currentCategory, ListView listView) {
        this.activity = activity;
        this.listener = listener;
        this.currentCategory = currentCategory;
        this.listView = listView;

        try {
            Storage storage = new Storage(activity);
            categories = storage.getCategories();
        } catch (StorageException e) {
            activity.showStorageExceptionAlertAndFinish(e);
        }

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = categories.get(position);
                if (category != currentCategory) {
                    setSelectedItemPosition(position);
                    setHoverPosition(-1);
                    notifyDataSetChanged();
                    listener.categorySelected(category);
                }
            }
        });
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
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedItemPosition(int pos) {
        selectedPosition = pos;
    }

    public void setHoverPosition(int pos) {
        hoverPosition = pos;
    }

    public int getSelectedItemPosition() {
        return selectedPosition;
    }

    public int getHoverPosition() {
        return hoverPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Category category = categories.get(position);
        IconViewSide view;
        if (convertView != null) {
            view = (IconViewSide)convertView;
        } else {
            view = new IconViewSide(activity, activity.getResources().getLayout(R.layout.icon_view_side), false);
        }
        view.setPosition(position);

        view.setImageBitmap(category.getThumbnail());
        view.setLabel(category.getName().toString());

        view.setCheckboxVisible(false);

        if (selectedPosition == position) {
            view.setBackgroundResource(R.drawable.listview_selector_selected);
        } else if (hoverPosition == position) {
            view.setBackgroundResource(R.drawable.listview_selector_pressed);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    public ListView getListView() {
        return listView;
    }
}