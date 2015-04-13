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

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.GalleryActivityData;
import com.smilemeback.misc.GalleryActivityState;
import com.smilemeback.R;
import com.smilemeback.activities.GalleryActivity;
import com.smilemeback.drag.GridViewDragListener;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.views.IconView;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages basic gridview operations and ties common functionality with
 * {@link com.smilemeback.activities.GalleryActivity} .
 */
abstract public class BaseGridAdapter extends BaseAdapter {
    protected GalleryActivity activity;
    protected GridAdapterListener listener;
    protected SelectionManager selectionManager;
    protected GalleryActivityData data;
    protected GridViewDragListener dragListener;

    /**
     * Initialize the {@link com.smilemeback.adapters.BaseGridAdapter}.
     */
    public BaseGridAdapter(GalleryActivity activity, GridAdapterListener listener, SelectionMode selectionMode, SelectionManager selectionManager, GalleryActivityData data) {
        this.activity = activity;
        this.listener = listener;
        this.selectionManager = selectionManager;
        this.data = data;
        this.dragListener = new GridViewDragListener(selectionMode, selectionManager, activity);
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
        view.setOnDragListener(dragListener);
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
                        selectionManager.select(position);
                        view.setChecked(true);
                        selectionManager.highlight();
                        ClipData.Item item = new ClipData.Item(Constants.IMAGE_DRAG_TAG);
                        ClipData dragData = new ClipData(Constants.IMAGE_DRAG_TAG, new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
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
     * Get a drag shadow of selected positions.
     * @return
     */
    private Drawable getCombinedIconViewDrawable() {
        List<IconView> selected = getSelectedIconViews();
        // use the width/height the same as with the IconViewSide dimensions.
        final int singleWidth = (int)activity.getResources().getDimension(R.dimen.iconview_side_width);
        final int singleHeight = (int)(activity.getResources().getDimension(R.dimen.iconview_side_height)*0.8);
        final int offset = (int)(0.1f*singleHeight);
        int totalWidth = singleWidth + (offset*(selected.size()-1));
        int totalHeight = singleHeight + (offset*(selected.size()-1));
        final int maxIcons = Math.min(selected.size(), Constants.MAX_ICONS_IN_DRAG_SHADOW);

        // create a big combined bitmap and erase its contents
        Bitmap combined = Bitmap.createBitmap(
                totalWidth,
                totalHeight,
                Bitmap.Config.ARGB_8888);
        combined.eraseColor(0x00000000);

        Canvas canvas = new Canvas(combined);
        for (int iconIndex=maxIcons-1 ; iconIndex >= 0 ; --iconIndex) {
            IconView iconView = selected.get(iconIndex);
            Drawable drawable = iconView.getDrawable();
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
     * @return The {@link IconView} instances that are selected.
     */
    private List<IconView> getSelectedIconViews() {
        int n = data.gridView.getChildCount();
        List<IconView> selected = new ArrayList<>();
        for (int idx=0 ; idx<n ; ++idx) {
            if (selectionManager.isSelected(idx)) {
                IconView iconView = (IconView)data.gridView.getChildAt(idx);
                if (iconView != null) {
                    selected.add(iconView);
                }
            }
        }
        return selected;
    }
}

