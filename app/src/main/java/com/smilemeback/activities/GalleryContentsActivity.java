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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;

import com.smilemeback.Constants;
import com.smilemeback.R;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Image;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilemeback.views.IconView;
import com.smilemeback.views.IconViewSide;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GalleryContentsActivity extends Activity {
    private static Logger logger = Logger.getLogger(GalleryContentsActivity.class.getCanonicalName());

    // Categories displayed in the selection mode in the left.
    protected List<Category> categories;

    // List of images displayed in the gridview
    protected List<Image> images;

    // GridView showing the list of images.
    protected GridView gridView;

    // Listview displaying the categories in the system.
    protected ListView listView;

    protected ImageDragEventListener dragListener = new ImageDragEventListener();

    // textview in selection mode that shows the number of selected images
    protected TextView numSelectedTextView;

    // reference to currently shown popup in image selection menu
    protected ListPopupWindow popup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            loadContents();
        } catch (StorageException e) {
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
    }

    protected void loadContents() throws StorageException {
        Intent intent = getIntent();
        int category_idx = intent.getIntExtra(Constants.CATEGORY_INDEX, 0);
        Storage storage = new Storage(this);
        categories = storage.getCategories();
        if (categories.size() > 0) {
            setContentView(R.layout.gallery_contents);
            images = storage.getCategoryImages(categories.get(category_idx));
            gridView = (GridView) findViewById(R.id.gallery_contents_grid_view);
            gridView.setAdapter(new ImageAdapter());
            listView = (ListView) findViewById(R.id.gallery_contents_list_view);
            listView.setAdapter(new CategoryAdapter());
            listView.setOnDragListener(dragListener);
            loadSelectionActionBar();
        } else {
            setContentView(R.layout.gallery_contents_empty);
        }

    }

    private void loadSelectionActionBar() {
        ActionBar actionbar = getActionBar();
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.selection_actionbar, null);
        actionbar.setCustomView(customView);
        actionbar.setDisplayShowCustomEnabled(true);
        numSelectedTextView = (TextView)customView.findViewById(R.id.actionbar_textview);
        GalleryContentsActivity.this.popup = null;

        numSelectedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a popup is already shown, then close it
                if (GalleryContentsActivity.this.popup != null) {
                    GalleryContentsActivity.this.popup.dismiss();
                    GalleryContentsActivity.this.popup = null;
                    return;
                }
                final ListPopupWindow popup = new ListPopupWindow(GalleryContentsActivity.this);
                GalleryContentsActivity.this.popup = popup;
                popup.setAdapter(getSelectionPopupAdapter());
                popup.setAnchorView(numSelectedTextView);
                popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int numTotal = images.size();
                        int numSelected = getNumSelectedInGridView();
                        if (position == 0 && numSelected < numTotal) {
                            setAllGridViewItemsChecked(true);
                        } else {
                            setAllGridViewItemsChecked(false);
                        }
                        popup.dismiss();
                        GalleryContentsActivity.this.popup = null;
                        updateNumSelectedText();
                    }
                });
                popup.setWidth(getResources().getDimensionPixelSize(R.dimen.actionbar_popup_width));
                popup.show();
            }
        });
        updateNumSelectedText();
    }

    public ListAdapter getSelectionPopupAdapter() {
        int numTotal = images.size();
        int numSelected = getNumSelectedInGridView();
        if (numSelected < numTotal && numSelected > 0) {
            // We should show both "Select" and "Deselect" options.
            String[] options = new String[] {
                    getString(R.string.actionbar_select_all),
                    getString(R.string.actionbar_deselect_all)};
            return new ArrayAdapter(this, R.layout.popup_list_item, options);
        } else if (numSelected == 0) {
            // Show only "Select all"
            String[] options = new String[] {
                    getString(R.string.actionbar_select_all)};
            return new ArrayAdapter(this, R.layout.popup_list_item, options);
        } else {
            // Show only "Deselect all
            String[] options = new String[] {
                    getString(R.string.actionbar_deselect_all)};
            return new ArrayAdapter(this, R.layout.popup_list_item, options);
        }
    }

    public void updateNumSelectedText() {
        numSelectedTextView.setText("#" + getNumSelectedInGridView() + " " + getString(R.string.actionbar_num_selected));
        invalidateOptionsMenu();
    }

    class ImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Image image = images.get(position);
            final IconView view = new IconView(GalleryContentsActivity.this, getResources().getXml(R.layout.icon_view), false);
            view.setImageBitmap(image.getImage());
            view.setLabel(image.getName().toString());

            //view.setCheckboxVisible(!isLocked());
            view.setCheckboxVisible(true);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.toggle();
                    updateNumSelectedText();
                    //spinnerAdapter.setNumSelected(getNumSelectedInGridView());
                    //refreshActionBar();
                    /*if (!isLocked()) {
                        view.setChecked(!view.isChecked());
                        invalidateOptionsMenu();
                    } else {
                        try {
                            if (!player.isPlaying()) {
                                player.reset();
                                player.setDataSource(new FileInputStream(image.getAudio()).getFD());
                                player.prepare();
                                player.start();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData.Item item = new ClipData.Item(Constants.IMAGE_DRAG_TAG);
                    ClipData dragData = new ClipData(Constants.IMAGE_DRAG_TAG, new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                    View.DragShadowBuilder shadow = new ImageDragShadowBuilder(v);
                    v.setTag(Constants.IMAGE_DRAG_TAG);
                    v.startDrag(dragData, shadow, null, 0);
                    return true;
                }
            });

            return view;
        }
    }

    protected static class ImageDragShadowBuilder extends View.DragShadowBuilder {
        private static Drawable shadow;

        public ImageDragShadowBuilder(View v) {
            super(v);
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            int width, height;
            width = getView().getWidth();
            height = getView().getHeight();
            shadow.setBounds(0, 0, width, height);
            size.set(width, height);
            touch.set(width/2, height/2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
        }
    }

    protected class ImageDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    int idx = getListViewChildInCoords((int)event.getX(), (int)event.getY());
                    logger.info(event.getX() + " " + event.getY() + " " + idx);
                    if (idx >= 0) {
                        //listView.smoothScrollToPosition(idx);
                        listView.setSelection(idx);
                    }
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DROP:
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
            }
            return false;
        }
    }

    /**
     * The the side listview child that is within given coordinates.
     * @param x
     * @param y
     * @return Index of the child or negative integer in case no such child exists.
     */
    protected int getListViewChildInCoords(int x, int y) {
        int n = listView.getChildCount();
        for (int idx=0 ; idx<n ; ++idx) {
            IconViewSide view = (IconViewSide)listView.getChildAt(idx);
            Rect bounds = new Rect();
            view.getHitRect(bounds);
            if (bounds.contains(x, y)) {
                return idx;
            }
        }
        return -1;
    }

    class CategoryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Category category = categories.get(position);
            final IconView view = new IconViewSide(GalleryContentsActivity.this,getResources().getXml(R.layout.icon_view_side), false);
            view.setImageBitmap(category.getThumbnail());
            view.setLabel(category.getName().toString());

            //view.setCheckboxVisible(!isLocked());
            view.setCheckboxVisible(false);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //view.toggle();
                    //updateNumSelectedText();
                    //spinnerAdapter.setNumSelected(getNumSelectedInGridView());
                    //refreshActionBar();
                    if (!isLocked()) {
                        view.setChecked(!view.isChecked());
                        invalidateOptionsMenu();
                    } else {
                        try {
                            if (!player.isPlaying()) {
                                player.reset();
                                player.setDataSource(new FileInputStream(image.getAudio()).getFD());
                                player.prepare();
                                player.start();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });*/

            /*view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isLocked()) {
                        setLocked(false);
                        Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                        if (vib.hasVibrator()) {
                            vib.vibrate(100);
                        }
                        return true;
                    }
                    return false;
                }
            });*/

            return view;
        }
    }

    /**
     * @return The number of selected icons in the gridView.
     */
    protected int getNumSelectedInGridView() {
        final int n = gridView.getChildCount();
        int count = 0;
        for (int idx=0 ; idx<n ; ++idx) {
            IconView view = (IconView)gridView.getChildAt(idx);
            if (view.isChecked()) {
                count += 1;
            }
        }
        return count;
    }

    protected void setAllGridViewItemsChecked(boolean checked) {
        final int n = gridView.getChildCount();
        for (int idx=0 ; idx<n ; ++idx) {
            IconView view = (IconView)gridView.getChildAt(idx);
            view.setChecked(checked);
        }
        gridView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_contents_select_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int n = getNumSelectedInGridView();
        menu.findItem(R.id.menu_item_rename_image).setVisible(n == 1);
        menu.findItem(R.id.menu_item_delete_image).setVisible(n >= 1);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
