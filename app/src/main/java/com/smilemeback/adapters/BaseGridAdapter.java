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
package com.smilemeback.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.GalleryActivityData;
import com.smilemeback.misc.GalleryActivityState;
import com.smilemeback.R;
import com.smilemeback.activities.GalleryBaseActivity;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.views.IconView;
import com.smilemeback.views.IconViewSide;

import java.util.List;

/**
 * Manages basic gridview operations and ties common functionality with
 * {@link com.smilemeback.activities.GalleryBaseActivity} .
 */
abstract public class BaseGridAdapter extends BaseAdapter implements View.OnDragListener{
    protected GalleryBaseActivity activity;
    protected GridAdapterListener listener;
    protected SelectionManager selectionManager;
    protected GalleryActivityData data;
    protected SelectionMode selectionMode;

    protected boolean isDragging = false;

    /**
     * Initialize the {@link com.smilemeback.adapters.BaseGridAdapter}.
     */
    public BaseGridAdapter(GalleryBaseActivity activity, GridAdapterListener listener, SelectionMode selectionMode, SelectionManager selectionManager, GalleryActivityData data) {
        this.activity = activity;
        this.listener = listener;
        this.selectionManager = selectionManager;
        this.data = data;
        this.selectionMode = selectionMode;
        data.gridView.setOnDragListener(this);
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
        view.setPosition(position);
        prepareIconView(view, position);

        // deal with selection stuff
        view.setOverlayVisibility(View.GONE);
        view.setCheckboxVisible(data.state == GalleryActivityState.SELECT);
        view.setChecked(selectionManager.isSelected(position));
        // highlight the icon when we are dragging
        if (isDragging) {
            view.setHighlighted(selectionManager.isSelected(position));
        }

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
                        checkSelectedIcons();
                        dehighlightIcons();
                        return true;
                    case SELECT:
                        selectionManager.select(position);
                        view.setChecked(true);
                        ClipData.Item item = new ClipData.Item(Constants.IMAGE_DRAG_TAG);
                        ClipData dragData = new ClipData(Constants.IMAGE_DRAG_TAG, new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                        View.DragShadowBuilder shadow = new ImageDragShadowBuilder(view);
                        view.setTag(Constants.IMAGE_DRAG_TAG);
                        view.startDrag(dragData, shadow, null, 0);
                        activity.vibrate();
                        break;
                }
                return true;
            }
        });

        return view;
    }

    /**
     * Update the checked states of icons in the view right now.
     */
    public void checkSelectedIcons() {
        final int n = data.gridView.getChildCount();
        for (int i=0 ; i<n ; ++i) {
            IconView view = (IconView)data.gridView.getChildAt(i);
            view.setChecked(selectionManager.isSelected(view.getPosition()));
        }
    }

    /**
     * Update the highlighted states of icons in the view right now.
     */
    public void highlightSelectedIcons() {
        final int n = data.gridView.getChildCount();
        for (int i=0 ; i<n ; ++i) {
            IconView view = (IconView)data.gridView.getChildAt(i);
            view.setHighlighted(selectionManager.isSelected(view.getPosition()));
        }
    }

    public void dehighlightIcons() {
        final int n = data.gridView.getChildCount();
        for (int i=0 ; i<n ; ++i) {
            IconView view = (IconView)data.gridView.getChildAt(i);
            view.setHighlighted(false);
        }
    }

    /**
     * Remove overlays of all IconViews except the one with given index.
     * @param except The index of IconView that can keep the overlay.
     */
    public void removeOverlays(int except) {
        final int n = data.gridView.getChildCount();
        for (int i=0 ; i<n ; ++i) {
            if (i != except) {
                IconView view = (IconView) data.gridView.getChildAt(i);
                view.setOverlayVisibility(View.GONE);
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

    /**
     * Class for building drag shadows.
     */
    private class ImageDragShadowBuilder extends View.DragShadowBuilder {
        private Drawable shadow;

        public ImageDragShadowBuilder(View view) {
            super(view);
            shadow = getCombinedIconViewDrawable();
        }

        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            int width = (int)activity.getResources().getDimension(R.dimen.iconview_side_width);
            int height = (int)activity.getResources().getDimension(R.dimen.iconview_side_height);
            shadow.setBounds(0, 0, shadow.getIntrinsicWidth(), shadow.getIntrinsicHeight());
            size.set(shadow.getIntrinsicWidth(), shadow.getIntrinsicHeight());
            touch.set(0, height);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
        }
    }

    /**
     * Get a drag shadow of selection positions.
     * @return
     */
    private Drawable getCombinedIconViewDrawable() {
        List<String> selectedPaths = getSelectedImagePaths();
        // use the width/height the same as with the IconViewSide dimensions.
        final int singleWidth = (int)activity.getResources().getDimension(R.dimen.iconview_side_width);
        final int singleHeight = (int)(activity.getResources().getDimension(R.dimen.iconview_side_height)*0.8);
        final int offset = (int)(0.1f*singleHeight);
        int totalWidth = singleWidth + (offset*(selectedPaths.size()-1));
        int totalHeight = singleHeight + (offset*(selectedPaths.size()-1));
        final int maxIcons = Math.min(selectedPaths.size(), Constants.MAX_ICONS_IN_DRAG_SHADOW);

        // create a big combined bitmap and erase its contents
        Bitmap combined = Bitmap.createBitmap(
                totalWidth,
                totalHeight,
                Bitmap.Config.ARGB_8888);
        combined.eraseColor(0x00000000);

        Canvas canvas = new Canvas(combined);
        for (int iconIndex=maxIcons-1 ; iconIndex >= 0 ; --iconIndex) {
            Drawable drawable = Drawable.createFromPath(selectedPaths.get(iconIndex));
            drawable.setBounds(
                    iconIndex*offset,
                    iconIndex*offset,
                    singleWidth + iconIndex*offset,
                    singleHeight + iconIndex*offset);
            drawable.draw(canvas);
        }


        return new BitmapDrawable(activity.getResources(), combined);
    }

    /**
     * @return The list of paths representing selected icons/categories.
     */
    abstract List<String> getSelectedImagePaths();

    public void dragStarted() {
        isDragging = true;
        highlightSelectedIcons();
    }

    public void dragEnded() {
        isDragging = false;
        dehighlightIcons();
        removeOverlays(-1);
    }

    @Override
    public boolean onDrag(View dragView, DragEvent event) {
        final int action = event.getAction();
        final int dragX = (int)event.getX();
        final int dragY = (int)event.getY();
        GridView gridView = (GridView)dragView;
        final int childIdx = getIconViewInCoords(gridView, dragX, dragY);
        IconView iconView = null;
        if (childIdx >= 0) {
            iconView = (IconView)gridView.getChildAt(childIdx);
        }

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                dragStarted();
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                selectionMode.setStatusText("");
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                // handle the overlay
                if (childIdx >= 0) {
                    if (!selectionManager.isSelected(iconView.getPosition())) {
                        iconView.setOverlayVisibility(View.VISIBLE);
                    }
                    removeOverlays(childIdx);
                    selectionMode.setStatusText("Switch");
                }
                // handle scrolling
                int dragArea = gridView.getHeight() / 8;
                if (dragY <= dragArea) {
                    gridView.smoothScrollBy(-dragArea, Constants.SMOOTH_SCROLL_DURATION);
                } else if (dragY >= gridView.getHeight() - dragArea) {
                    data.gridView.smoothScrollBy(dragArea, Constants.SMOOTH_SCROLL_DURATION);
                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                selectionMode.setStatusText("");
                removeOverlays(-1);
                return true;
            case DragEvent.ACTION_DROP:
                if (iconView != null) {
                    activity.rearrangeIconsAccordingToTarget(iconView.getPosition());
                }
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                selectionMode.setStatusText("");
                dragEnded();
                return true;
        }
        return false;
    }

    /**
     * The child view of GridView that point (x, y) hits.
     * @return Index of the child or negative integer in case no such child exists.
     */
    private int getIconViewInCoords(GridView gridView, int x, int y) {
        int n = gridView.getChildCount();
        for (int idx = 0; idx < n; ++idx) {
            IconView view = (IconView) gridView.getChildAt(idx);
            Rect bounds = new Rect();
            view.getHitRect(bounds);
            if (bounds.contains(x, y)) {
                return idx;
            }
        }
        return -1;
    }
}

