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
package com.smilemeback.activities;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.smilemeback.R;
import com.smilemeback.adapters.BaseGridAdapter;
import com.smilemeback.adapters.GridAdapterListener;
import com.smilemeback.application.SmbApplication;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.Dialogs;
import com.smilemeback.misc.GalleryActivityData;
import com.smilemeback.misc.GalleryActivityState;
import com.smilemeback.selection.SelectionListener;
import com.smilemeback.selection.SelectionManager;
import com.smilemeback.selectionmode.GallerySelectionModeListener;
import com.smilemeback.selectionmode.SelectionMode;
import com.smilemeback.storage.Categories;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link GalleryBaseActivity} is the main activity of the application,
 * which manages interactions between all other activities of the application.
 */
public abstract class GalleryBaseActivity extends Activity implements GallerySelectionModeListener, SelectionListener, GridAdapterListener {
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
     * In case there are no categories or no images in a category, show a help layout
     * with instructions how to add one.
     * @param gridAdapter The adapter to check the count of items.
     * @param layout The layout to display.
     */
    protected void showHelpLayoutIfNecessary(BaseGridAdapter gridAdapter, int layout) {
        RelativeLayout container = (RelativeLayout)findViewById(R.id.container);
        if (gridAdapter.getCount() == 0 && data.state == GalleryActivityState.VIEW) {
            if (findViewById(R.id.help_layout) == null) {
                View helpLayout = getLayoutInflater().inflate(layout, null);
                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                container.addView(helpLayout, params);
                container.invalidate();
            }
        } else {
            View helpLayout = findViewById(R.id.help_layout);
            if (helpLayout != null) {
                helpLayout.setVisibility(View.GONE);
                container.removeView(helpLayout);
                container.invalidate();
            }
        }
    }

    /**
     * Abstract method that subclasses must override to set up action bar with settings
     * relevant for the subclass.
     */
    abstract protected void setupActionBar();

    /**
     a* Initialize the main grid view.
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
            Categories categories = new Storage(this).getCategories();
            categories.initializeTestingCategories(this);
        } catch (StorageException e) {
            showStorageExceptionAlertAndFinish(e);
        }
    }

    /**
     * Show a dialog_information of {@link com.smilemeback.storage.StorageException}, which caused due
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
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                data.gridView.smoothScrollToPosition(selectionManager.getSelectedPosition());
            }
            @Override
            public void onAnimationStart(Animator animation) { }
            @Override
            public void onAnimationCancel(Animator animation) { }
            @Override
            public void onAnimationRepeat(Animator animation) { }
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
    public void enterSelectionMode() {
        data.state = GalleryActivityState.SELECT;
        startActionMode(selectionMode);
        refreshGridView();
        refreshSidePane();
        selectionMode.setTotal(selectionManager.getNumTotal());
        selectionMode.setNumSelected(selectionManager.getNumSelected());
        ((BaseGridAdapter)data.gridView.getAdapter()).dehighlightIcons();
    }

    public abstract void rearrangeIconsAccordingToTarget(int position);

    public SmbApplication getSmbApplication() {
        return (SmbApplication)getApplication();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean locked = getSmbApplication().isLocked();
        for (int i=0 ; i<menu.size() ; ++i) {
            menu.getItem(i).setVisible(!locked);
        }
        menu.findItem(R.id.unlock_app).setVisible(locked);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contacts_page:
                startActivity(new Intent(this, ContactsActivity.class));
                return true;
            case R.id.tutorials_page:
                startActivity(new Intent(this, TutorialActivity.class));
                return true;
            case R.id.lock_app:
                lockApp();
                return true;
            case R.id.unlock_app:
                unlockApp();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Lock the application.
     */
    public void lockApp() {
        Dialogs.information(
                this,
                getString(R.string.dialog_lockapp_title),
                getString(R.string.dialog_lockapp_content),
                getString(R.string.prefs_show_lock_help));
        getSmbApplication().setLocked(true);
        invalidateOptionsMenu();
    }

    /**
     * Unlock the application.
     * @param retry true if the dialog is called recursively
     *              after typing the wrong password.
     */
    public void unlockApp(final boolean retry, String lastpass) {
        String title = getString(R.string.dialog_unlockapp_title);
        if (retry) {
            title = getString(R.string.dialog_unlockapp_title_retry);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String userpass = prefs.getString(getString(R.string.prefs_password), Constants.PREFS_DEFAULT_PASSWORD);
        Dialogs.input(this,
                title,
                lastpass,
                getString(R.string.dialog_unlockapp_confirmation),
                getString(R.string.dialog_unlockapp_cancel),
                new Dialogs.InputCallback() {
                    @Override
                    public void inputDone(String text) {
                        if (text.equalsIgnoreCase(Constants.PREFS_DEFAULT_PASSWORD) ||
                            text.equalsIgnoreCase(userpass)) {
                            getSmbApplication().setLocked(false);
                            invalidateOptionsMenu();
                        } else {
                            unlockApp(true, text);
                        }
                    }
                });
    }

    public void unlockApp() {
        unlockApp(false, "");
    }

    protected void showHowToEditPopup() {
        Dialogs.information(this,
                getString(R.string.dialog_edit_title),
                getString(R.string.dialog_edit_text),
                getString(R.string.prefs_show_editmode));
    }

}
