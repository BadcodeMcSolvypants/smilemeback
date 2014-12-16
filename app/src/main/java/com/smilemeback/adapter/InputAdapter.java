package com.smilemeback.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smilemeback.views.IconView;
import com.smilememack.R;

/**
 * Adapter that allows using "+" icon in addition
 * to actual icons being displayed.
 */
public abstract class InputAdapter extends BaseAdapter {
    protected boolean inputEnabled = false;
    protected Context context;

    public InputAdapter(Context context) {
        this.context = context;
    }

    public boolean isInputEnabled() {
        return inputEnabled;
    }

    public void setInputEnabled(boolean inputEnabled) {
        this.inputEnabled = inputEnabled;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        if (isInputEnabled()) {
            return getActualCount() + 1;
        }
        return getActualCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @return The actual count of items.
     */
    public abstract int getActualCount();

    @Override
    public Object getItem(int position) {
        if (isInputEnabled()) {
            if (position > 0) {
                return getActualItem(position - 1);
            } else {
                return null;
            }
        }
        return getActualItem(position);
    }

    /**
     * @return The actual item with given position.
     */
    public abstract Object getActualItem(int position);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (isInputEnabled()) {
                if (position == 0) {
                    IconView addView = new IconView(context, context.getResources().getXml(R.layout.iconview_layout));
                    addView.setLabelVisible(false);
                    addView.setImageBitmap(R.drawable.add);
                    return addView;
                } else {
                    return getActualView(position-1, convertView, parent);
                }
            } else {
                return getActualView(position, convertView, parent);
            }
        }
        return convertView;
    }

    public abstract View getActualView(int position, View convertView, ViewGroup parent);
}
