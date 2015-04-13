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
package com.smilemeback.drag;

import android.graphics.Rect;
import android.view.DragEvent;
import android.view.View;
import android.widget.ListView;

import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.adapters.CategoryListAdapter;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.views.IconViewSide;

/**
 * Handle drag events, when user drags selected icons over the side panel listview.
 */
public class ListViewDragListener implements View.OnDragListener {

    protected SelectionManager selectionManager;
    protected SelectionMode selectionMode;
    protected CategoryListAdapter listAdapter;
    protected ListView listView;
    protected ListDragResultListener listener;

    public ListViewDragListener(SelectionManager selectionManager, SelectionMode selectionMode, CategoryListAdapter listAdapter, ListDragResultListener listener) {
        this.selectionManager = selectionManager;
        this.selectionMode = selectionMode;
        this.listAdapter = listAdapter;
        this.listView = listAdapter.getListView();
        this.listener = listener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        int idx = getListViewChildInCoords((int) event.getX(), (int) event.getY());

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                selectionManager.highlight();
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                selectionMode.setStatusText("Drop selected images to category");
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                if (idx >= 0) {
                    listView.smoothScrollToPosition(idx);
                    if (listAdapter.getHoverPosition() != idx) {
                        listAdapter.setHoverPosition(idx);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                selectionMode.setStatusText("");
                return true;
            case DragEvent.ACTION_DROP:
                listAdapter.setHoverPosition(-1);
                if (idx >= 0) {
                    if (idx != listAdapter.getSelectedItemPosition()) {
                        listener.moveSelectedIconsToCategory(idx);
                    }
                }
                listAdapter.notifyDataSetChanged();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                selectionMode.setStatusText("");
                selectionManager.dehighlight();
                return true;
        }
        return false;
    }

    /**
     * The side listview child that is within given coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return Index of the child or negative integer in case no such child exists.
     */
    private int getListViewChildInCoords(int x, int y) {
        int n = listView.getChildCount();
        for (int idx = 0; idx < n; ++idx) {
            IconViewSide view = (IconViewSide) listView.getChildAt(idx);
            Rect bounds = new Rect();
            view.getHitRect(bounds);
            if (bounds.contains(x, y)) {
                return idx;
            }
        }
        return -1;
    }
}
