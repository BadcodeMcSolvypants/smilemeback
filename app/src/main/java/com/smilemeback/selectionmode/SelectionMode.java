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
package com.smilemeback.selectionmode;

import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.smilemeback.R;
import com.smilemeback.activities.IconsActivity;
import com.smilemeback.views.IconView;


public class SelectionMode implements ActionMode.Callback, View.OnClickListener {

    protected Activity activity;
    protected TextView textView;
    protected TextView statusText;
    protected GallerySelectionModeListener listener;
    protected ListPopupWindow popup;
    protected ActionMode actionMode;

    // number of selection vs total collection.
    protected int numSelected;
    protected int total;

    public SelectionMode(Activity activity, GallerySelectionModeListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setNumSelected(int numSelected) {
        this.numSelected = numSelected;
        updateNumSelectedText();
        actionMode.invalidate();
    }

    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return true, if {@literal listener} is an instance of {@link com.smilemeback.activities.IconsActivity}.
     */
    public boolean isListenerIconsActivity() {
        if (listener != null) {
            return listener instanceof IconsActivity;
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
        mode.getMenuInflater().inflate(R.menu.gallery_selectionmode_menu, menu);
        // set up the custom layout
        LayoutInflater inflater = activity.getLayoutInflater();
        View customView = inflater.inflate(R.layout.gallery_selectionmode_actionbar, null);
        mode.setCustomView(customView);
        statusText = (TextView)customView.findViewById(R.id.actionbar_statustext);
        textView = (TextView)customView.findViewById(R.id.actionbar_textview);

        // whenever textview is clicked, we will show the popup.
        // in case the popup is already on screen, we will dismiss it instead.
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a popup is already shown, then close it
                if (popup != null) {
                    popup.dismiss();
                    popup = null;
                    return;
                }
                popup = new ListPopupWindow(activity);
                popup.setAdapter(getSelectionPopupAdapter());
                popup.setAnchorView(textView);
                popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0 && numSelected < total) {
                            setNumSelected(total);
                            listener.selectAllItems();
                        } else {
                            setNumSelected(0);
                            listener.deselectAllItems();
                        }
                        popup.dismiss();
                        popup = null;
                        updateNumSelectedText();
                    }
                });
                popup.setWidth(activity.getResources().getDimensionPixelSize(R.dimen.gallery_selectionmode_text_width));
                popup.show();
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.gallery_selectionmode_menu_add_image).setVisible(isListenerIconsActivity());
        menu.findItem(R.id.gallery_selectionmode_menu_add_album).setVisible(!isListenerIconsActivity());
        menu.findItem(R.id.gallery_selectionmode_menu_rename_image).setVisible(numSelected == 1);
        menu.findItem(R.id.gallery_selectionmode_menu_delete_image).setVisible(numSelected >= 1);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.gallery_selectionmode_menu_add_image:
                listener.addNewIcon();
                return true;
            case R.id.gallery_selectionmode_menu_add_album: // almost duplicates previous behaviour
                listener.addNewIcon();
                return true;
            case R.id.gallery_selectionmode_menu_rename_image:
                listener.renameCurrentlySelectedIcon();
                return true;
            case R.id.gallery_selectionmode_menu_delete_image:
                listener.deleteCurrentlySelectedIcons();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listener.gallerySelectionModeFinished();
    }

    @Override
    public void onClick(View view) {
        IconView iconView = (IconView)view;
    }

    /**
     * Return the {@link android.widget.ListAdapter} that defines the contents of
     * the popup. It uses the number of selection collection in gridview to define what to show.
     *
     * @return
     */
    public ListAdapter getSelectionPopupAdapter() {
        if (numSelected < total && numSelected > 0) {
            // We should show both "Select" and "Deselect" options.
            String[] options = new String[]{
                    activity.getString(R.string.actionbar_select_all),
                    activity.getString(R.string.actionbar_deselect_all)};
            return new ArrayAdapter(activity, R.layout.gallery_selectionmode_popup_list_item, options);
        } else if (numSelected == 0) {
            // Show only "Select all"
            String[] options = new String[]{
                    activity.getString(R.string.actionbar_select_all)};
            return new ArrayAdapter(activity, R.layout.gallery_selectionmode_popup_list_item, options);
        } else {
            // Show only "Deselect all
            String[] options = new String[]{
                    activity.getString(R.string.actionbar_deselect_all)};
            return new ArrayAdapter(activity, R.layout.gallery_selectionmode_popup_list_item, options);
        }
    }

    /**
     * This function updates the number of selection collection in the TextView in the selection
     * actionbar.
     */
    public void updateNumSelectedText() {
        textView.setText("#" + numSelected + " " + activity.getString(R.string.actionbar_num_selected));
    }

    /**
     * Set the text displayed in the actionmode's statustext field.
     * @param text
     */
    public void setStatusText(String text) {
        statusText.setText(text);
    }
}
