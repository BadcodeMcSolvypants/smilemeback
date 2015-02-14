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
package com.smilemeback.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smilemeback.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * IconView represents a clickable image with optional label.
 * These are used to display both categories and pictures.
 *
 * IconView can also be in edit mode, including a custom checkbox.
 */
public class IconView extends RelativeLayout implements Checkable {

    protected ImageView image;
    protected TextView label;

    // checkbox with blank layer hovering over the image
    //protected FrameLayout blank;
    protected ImageView checkbox;

    protected boolean isChecked = false;

    /**
     * Initialize a new IconView.
     * @param context
     * @param attrs
     */
    public IconView(Context context, AttributeSet attrs, boolean useDefaultImage) {
        super(context, attrs);
        inflateLayout(context);

        image = (ImageView) findViewById(R.id.iconview_image);
        if (!useDefaultImage) {
            image.setImageBitmap(null);
        }
        label = (TextView) findViewById(R.id.iconview_label);
        //blank = (FrameLayout) findViewById(R.id.iconview_blank);
        checkbox = (ImageView) findViewById(R.id.iconview_chkbox_image);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.iconview);
        boolean showLabel = array.getBoolean(R.styleable.iconview_showLabel, true);
        setLabelVisible(showLabel);
        setCheckboxVisible(isCheckboxVisible());
        setChecked(isChecked());
        updateBlankVisibility();
    }

    /**
     * Override this method to inflate layouts in subclasses.
     * @param context
     */
    protected void inflateLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.icon_view, this, true);
    }

    public boolean isLabelVisible() {
        return label.getVisibility() == VISIBLE;
    }

    public void setLabelVisible(boolean visible) {
        if (visible) {
            label.setVisibility(VISIBLE);
        } else {
            label.setVisibility(GONE);
        }
    }

    public boolean isCheckboxVisible() {
        return checkbox.getVisibility() == VISIBLE;
    }

    public void setCheckboxVisible(boolean visible) {
        if (visible) {
            checkbox.setVisibility(VISIBLE);
        } else {
            checkbox.setVisibility(GONE);
        }
        updateBlankVisibility();
    }

    private void updateBlankVisibility() {
        if (isCheckboxVisible() && isChecked()) {
            //blank.setVisibility(VISIBLE);
        } else {
            //blank.setVisibility(GONE);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            if (checked) {
                checkbox.setBackgroundResource(R.drawable.chkbox_checked);
            } else {
                checkbox.setBackgroundResource(R.drawable.chkbox);
            }
        }
        isChecked = checked;
        updateBlankVisibility();
    }

    /**
     * Load the views bitmap from a file.
     * @param file
     */
    public void setImageBitmap(File file) {
        Picasso.with(getContext())
                .load(file)
                .fit()
                .centerCrop()
                .into(image);
    }

    /**
     * Load the views bitmap from a file.
     * @param resourceId The resource to load into IconView.
     */
    public void setImageBitmap(int resourceId) {
        Picasso.with(getContext())
                .load(resourceId)
                .fit()
                .centerCrop()
                .into(image);
    }

    public void setLabel(String text) {
        label.setText(text);
    }

}
