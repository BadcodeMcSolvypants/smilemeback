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
package com.smilemeback.activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.smilemeback.drag.GridDragResultListener;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.GalleryActivityData;
import com.smilemeback.misc.GalleryActivityState;
import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.selectionmode.GallerySelectionModeListener;
import com.smilemeback.R;
import com.smilemeback.adapters.GridAdapterListener;
import com.smilemeback.selection.SelectionListener;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link com.smilemeback.activities.GalleryActivity} is the main activity of the application,
 * which manages interactions between all other activities of the application.
 */
public abstract class GalleryActivity extends Activity implements GallerySelectionModeListener, SelectionListener, GridAdapterListener, GridDragResultListener {
    protected static Logger logger = Logger.getLogger("SmileMeBack");

    protected GalleryActivityData data = new GalleryActivityData();
    protected SelectionManager selectionManager = new SelectionManager();
    protected SelectionMode selectionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupTestingCategories();

        selectionManager.addListener(this);
        selectionMode = new SelectionMode(this, this);
        setContentView(R.layout.gallery);
        initializeDataAndFindLayoutItems();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupActionBar();
        initializeListView();
        initializeGridView();
    }

    /**
     * Abstract method that subclasses must override to set up action bar with settings
     * relevant for the subclass.
     */
    abstract protected void setupActionBar();

    /**
     * Initialize the main grid view.
     */
    abstract protected void initializeGridView();

    /**
     * Initialize the sidebar list view.
     */
    abstract protected void initializeListView();

    /**
     * Refresh grid view.
     */
    abstract protected void refreshGridView();

    /**
     * Refresh sidebar list view.
     */
    abstract protected void refreshSidePane();
    /**
     * Method that deletes all content and sets up some categories to test the application
     * with.
     * This is intended only for development purposes.
     */
    protected void setupTestingCategories() {
        try {
            Storage storage = new Storage(this);
            storage.truncateAllCategories();
            storage.initializeTestingCategories();
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        }
    }

    /**
     * Show a dialog of {@link com.smilemeback.storage.StorageException}, which caused due
     * to a problem with storage is indicates a programming error.
     *
     * @param e The exception instance.
     */
    public void showStorageExceptionAlertAndFinish(StorageException e) {
        logger.log(Level.SEVERE, e.getMessage());
        new AlertDialog.Builder(this)
                .setTitle("Storage exception")
                .setMessage(e.getMessage())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    /**
     * Method that vibrates the phone.
     */
    public void vibrate() {
        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vib.hasVibrator()) {
            vib.vibrate(50);
        }
    }

    /**
     * Function that initializes various views and data defined in
     * {@link GalleryActivityData}.
     */
    protected void initializeDataAndFindLayoutItems() {
        data.gridView = (GridView) findViewById(R.id.gallery_contents_grid_view);
        data.listView = (ListView) findViewById(R.id.gallery_list_view);
        data.listViewContainer = (LinearLayout)findViewById(R.id.gallery_listview_container);
        setListViewWeight(0);
    }

    /**
     * Related to animation. Sets the weight of the sidebar listview container.
     * @param weight
     */
    private void setListViewWeight(float weight) {
        ((LinearLayout.LayoutParams)data.listViewContainer.getLayoutParams()).weight = weight;
        data.listViewContainer.requestLayout();
    }

    /**
     * Animate the sidebar listview, so that it becomes visible.
     */
    protected void animateListViewIn() {
        ValueAnimator va = ValueAnimator.ofFloat(0, 1);
        va.setRepeatMode(ValueAnimator.RESTART);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setListViewWeight((float) animation.getAnimatedValue());
            }
        });
        va.start();
    }

    /**
     * Animate the sidebar listview, so that it becomes invisible.
     */
    protected void animateListViewOut() {
        ValueAnimator va = ValueAnimator.ofFloat(1, 0);
        va.setRepeatMode(ValueAnimator.RESTART);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setListViewWeight((float) animation.getAnimatedValue());
            }
        });
        va.start();
    }

    @Override
    public void gallerySelectionModeFinished() {
        data.state = GalleryActivityState.VIEW;
        refreshGridView();
    }

    @Override
    public void selectAllItems() {
        selectionManager.selectAll();
        refreshGridView();
    }

    @Override
    public void deselectAllItems() {
        selectionManager.deselectAll();
        refreshGridView();
    }

    @Override
    public void renameCurrentlySelectedIcon() {

    }

    @Override
    public void deleteCurrentlySelectedIcons() {

    }

    @Override
    public void positionSelected(int position) { }

    @Override
    public void positionDeselected(int position) { }

    @Override
    public void numTotalChanged(int total) {
        if (data.state == GalleryActivityState.SELECT) {
            selectionMode.setTotal(total);
        }
    }

    @Override
    public void numSelectedChanged(int selected) {
        if (data.state == GalleryActivityState.SELECT) {
            selectionMode.setNumSelected(selected);
        }
    }

    @Override
    public void highlight(int position) {
        View view = data.gridView.getChildAt(position);
        if (view != null) {
            view.setAlpha(Constants.SELECTED_ICONVIEW_ALPHA);
        }
    }

    @Override
    public void dehighlight(int position) {
        View view = data.gridView.getChildAt(position);
        if (view != null) {
            view.setAlpha(1f);
        }
    }

    @Override
    public void enterSelectionMode() {
        data.state = GalleryActivityState.SELECT;
        startActionMode(selectionMode);
        refreshGridView();
        refreshSidePane();
        selectionMode.setTotal(selectionManager.getNumTotal());
        selectionMode.setNumSelected(selectionManager.getNumSelected());
    }
}
