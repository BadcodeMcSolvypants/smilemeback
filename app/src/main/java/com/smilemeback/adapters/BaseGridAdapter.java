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

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smilemeback.GalleryActivityData;
import com.smilemeback.GalleryActivityState;
import com.smilemeback.R;
import com.smilemeback.activities.CategoriesActivity;
import com.smilemeback.activities.GalleryActivity;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.views.IconView;

/**
 * Manages basic gridview operations and ties common functionality with
 * {@link com.smilemeback.activities.GalleryActivity} .
 */
abstract public class BaseGridAdapter extends BaseAdapter {
    protected GalleryActivity activity;
    protected GridAdapterListener listener;
    protected SelectionManager selectionManager;
    protected GalleryActivityData data;

    /**
     * Initialize the {@link com.smilemeback.adapters.BaseGridAdapter}.
     * @param activity
     * @param selectionManager
     * @param data
     */
    public BaseGridAdapter(CategoriesActivity activity, GridAdapterListener listener, SelectionManager selectionManager, GalleryActivityData data) {
        this.activity = activity;
        this.listener = listener;
        this.selectionManager = selectionManager;
        this.data = data;
        initialize();
    }

    /**
     * Subclass must perform all initialization operations in this method.
     */
    public abstract void initialize();

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final IconView view;
        if (convertView != null) {
            view = (IconView)convertView;
        } else {
            view = new IconView(activity, activity.getResources().getLayout(R.layout.icon_view), false);
        }

        prepareIconView(view, position);

        // deal with selection stuff
        view.setOverlayVisibility(View.GONE);
        view.setCheckboxVisible(data.state == GalleryActivityState.SELECT);
        view.setSelected(selectionManager.isSelected(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (data.state) {
                    case VIEW:
                        handleIconClick(view, position);
                        break;
                    case SELECT:
                        selectionManager.toggle(position);
                        view.setChecked(selectionManager.isSelected(position));
                        break;
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.vibrate();
                switch (data.state) {
                    case VIEW:
                        selectionManager.deselectAll();
                        selectionManager.select(position);
                        listener.enterSelectionMode();
                        setSelectedIconsChecked();
                        break;
                    case SELECT:
                        break;
                }
                return true;
            }
        });

        return view;
    }

    public void setSelectedIconsChecked () {
        for (int i=0 ; i<selectionManager.getNumTotal() ; ++i) {
            IconView view = (IconView)data.gridView.getChildAt(i);
            if (view != null) {
                view.setChecked(selectionManager.isSelected(i));
            }
        }
    }

    /**
     * Callback method for settings icons name, image and other data.
     */
    abstract void prepareIconView(final IconView view, int position);

    /**
     * Callback method when icon is clicked in VIEW mode.
     */
    abstract void handleIconClick(final IconView view, int position);
}
