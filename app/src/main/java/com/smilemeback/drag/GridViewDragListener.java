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
package com.smilemeback.drag;


import android.graphics.Rect;
import android.view.DragEvent;
import android.view.View;
import android.widget.GridView;

import com.smilemeback.adapters.BaseGridAdapter;
import com.smilemeback.misc.Constants;
import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.views.IconView;

/**
 * Handle drag events when user drags selection icons over unselected icons.
 */
public class GridViewDragListener implements View.OnDragListener {

    protected GridView gridView;
    protected SelectionMode selectionMode;
    protected SelectionManager selectionManager;
    protected GridDragResultListener listener;
    protected BaseGridAdapter adapter;

    public GridViewDragListener(SelectionMode selectionMode, BaseGridAdapter adapter, GridDragResultListener listener, GridView gridView) {
        this.selectionMode = selectionMode;
        this.listener = listener;
        this.gridView = gridView;
        this.adapter = adapter;
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        final int action = event.getAction();
        IconView iconView = (IconView)view;
        final int dragX = (int)event.getX();
        final int dragY = (int)event.getY();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (!iconView.isChecked()) {
                    selectionMode.setStatusText("Switch images");
                    iconView.setOverlayVisibility(View.VISIBLE);
                }
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                int dragArea = iconView.getHeight() / 2;
                Rect rect = new Rect();
                iconView.getHitRect(rect);
                int translatedDragY = rect.top + dragY;
                //Log.d("test", translatedDragY + " " + gridView.getHeight());
                if (translatedDragY <= dragArea) {
                    gridView.smoothScrollBy(-dragArea, Constants.SMOOTH_SCROLL_DURATION);
                } else if (translatedDragY >= gridView.getHeight() - dragArea) {
                    gridView.smoothScrollBy(dragArea, Constants.SMOOTH_SCROLL_DURATION);
                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                selectionMode.setStatusText("");
                iconView.setOverlayVisibility(View.GONE);
                return true;
            case DragEvent.ACTION_DROP:
                listener.rearrangeIconsAccordingToTarget(iconView.getPosition());
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                selectionMode.setStatusText("");
                iconView.setOverlayVisibility(View.GONE);
                adapter.dragEnded();
                return true;
        }
        return false;
    }
}
