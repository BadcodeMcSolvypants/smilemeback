package com.smilemeback.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smilememack.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * IconView represents a clickable image with optional label.
 * These are used to display both categories and pictures.
 */
public class IconView extends RelativeLayout {

    protected ImageView image;
    protected TextView label;

    /**
     * Initialize a new IconView.
     * @param context
     * @param attrs
     */
    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.iconview_layout, this, true);

        image = (ImageView) findViewById(R.id.iconview_image);
        label = (TextView) findViewById(R.id.iconview_label);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.iconview);
        boolean showLabel = array.getBoolean(R.styleable.iconview_showLabel, true);
        setLabelVisible(showLabel);
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
