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

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import com.smilemeback.misc.Constants;
import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.misc.GalleryActivityData;
import com.smilemeback.activities.IconsActivity;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Image;
import com.smilemeback.storage.Images;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilemeback.views.IconView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages images/icons in a gallery.
 */
public class IconGridAdapter extends BaseGridAdapter {

    private Images images;
    private Category currentCategory;
    private MediaPlayer player = new MediaPlayer();

    public IconGridAdapter(IconsActivity activity, GridAdapterListener listener, SelectionMode selectionMode, SelectionManager selectionManager, GalleryActivityData data) {
        super(activity, listener, selectionMode, selectionManager, data);
    }

    public void setCurrentCategory(Category category) {
        this.currentCategory = category;
        initialize();
    }

    public Category getCurrentCategory() {
        return currentCategory;
    }

    @Override
    public void initialize()  {
        try {
            images = currentCategory.getImages();
            selectionManager.deselectAll();
            selectionManager.setNumTotal(images.size());
            data.gridView.setAdapter(this);
        } catch (StorageException e) {
            activity.showStorageExceptionAlertAndFinish(e);
        }
    }

    /**
     * Called when activity gets paused, required for releasing mediaplayer resouces.
     */
    public void onPause() {
        player.release();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    void prepareIconView(IconView view, int position) {
        final Image image = images.get(position);
        view.setImageBitmap(image.getImage());
        view.setLabel(image.getName().toString());
    }

    @Override
    void handleIconClick(IconView view, int position) {
        try {
            if (!player.isPlaying()) {
                player.reset();
                player.setDataSource(new FileInputStream(images.get(position).getAudio()).getFD());
                player.prepare();
                player.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    List<String> getSelectedImagePaths() {
        List<String> selected = new ArrayList<>();
        for (int idx=0 ; idx<images.size() ; ++idx) {
            if (selectionManager.isSelected(idx)) {
                Image image = images.get(idx);
                selected.add(image.getImage().getPath());
            }
        }
        return selected;
    }
}
