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


import android.view.DragEvent;
import android.view.View;

import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.views.IconView;

/**
 * Handle drag events when user drags selection icons over unselected icons.
 */
public class GridViewDragListener implements View.OnDragListener {

    protected SelectionMode selectionMode;
    protected SelectionManager selectionManager;
    protected GridDragResultListener listener;

    public GridViewDragListener(SelectionMode selectionMode, SelectionManager selectionManager, GridDragResultListener listener) {
        this.selectionMode = selectionMode;
        this.selectionManager = selectionManager;
        this.listener = listener;
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        final int action = event.getAction();
        IconView iconView = (IconView)view;
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
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                selectionMode.setStatusText("");
                iconView.setOverlayVisibility(View.GONE);
                return true;
            case DragEvent.ACTION_DROP:
                selectionManager.dehighlight();
                listener.moveSelectedIconsTo(iconView.getPosition());
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                selectionMode.setStatusText("");
                iconView.setOverlayVisibility(View.GONE);
                return true;
        }
        return false;
    }
}
