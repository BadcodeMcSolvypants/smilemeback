package com.smilemeback.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.smilemeback.AddPictureActivity;
import com.smilemeback.R;
import com.smilemeback.storage.Category;
import com.smilemeback.storage.Image;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilemeback.views.IconView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class IconDisplayActivity extends Activity {
    private static Logger logger = Logger.getLogger(IconDisplayActivity.class.getCanonicalName());
    private static final String CATEGORY_IDX_STRING = "CATEGORY_IDX";

    /// should we enable adding new categories / images in the view
    protected boolean locked = true;

    /// are we displaying all categories or contents of a single category
    protected boolean displayCategories = true;

    /// grid of IconView instances
    protected GridView gridView;

    /// List of categories
    protected List<Category> categories;

    /// List of images
    protected List<Image> images;

    // media player
    private MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_contents);

        // initialize view with suitable adapter
        gridView = (GridView)findViewById(R.id.gallery_contents_grid_view);

        // receive intent data
        Intent intent = getIntent();
        int category_idx = intent.getIntExtra(CATEGORY_IDX_STRING, -1);

        // load the categories/images
        ActionBar actionBar = getActionBar();
        try {
            Storage storage = new Storage(this);
            //storage.initializeTestingCategories();
            categories = storage.getCategories();
            if (category_idx == -1) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                displayCategories = true;
                gridView.setAdapter(getCategoryAdapter());
            } else {
                displayCategories = false;
                Category category = categories.get(category_idx);
                images = storage.getCategoryImages(category);
                gridView.setAdapter(getImageAdapter());
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(category.getName().toString());
            }
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }

    }

    public BaseAdapter getCategoryAdapter() {
        return new BaseAdapter() {
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
                if (convertView == null) {
                    final Category category = categories.get(position);
                    final IconView view = new IconView(IconDisplayActivity.this, IconDisplayActivity.this.getResources().getXml(R.layout.icon_view), false);
                    view.setImageBitmap(category.getThumbnail());
                    view.setLabel(category.getName().toString());

                    view.setCheckboxVisible(!isLocked());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isLocked()) {
                                view.setChecked(!view.isChecked());
                                invalidateOptionsMenu();
                            } else {
                                Intent intent = new Intent(IconDisplayActivity.this, IconDisplayActivity.class);
                                intent.putExtra(CATEGORY_IDX_STRING, category.getIndex());
                                startActivity(intent);
                            }
                        }
                    });
                    return view;
                } else {
                    return convertView;
                }
            }
        };
    }

    public BaseAdapter getImageAdapter() {
        return new BaseAdapter() {
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
                if (convertView == null) {
                    final Image image = images.get(position);
                    final IconView view = new IconView(IconDisplayActivity.this, IconDisplayActivity.this.getResources().getXml(R.layout.icon_view), false);
                    view.setImageBitmap(image.getImage());
                    view.setLabel(image.getName().toString());

                    view.setCheckboxVisible(!isLocked());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                    });

                    view.setOnLongClickListener(new View.OnLongClickListener() {
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
                    });

                    return view;
                } else {
                    return convertView;
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icondisplay, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean d = isDisplayCategories();
        boolean i = isLocked();
        int c = getSelectedCount();

        menu.findItem(R.id.add_category).setVisible(d);
        menu.findItem(R.id.add_image).setVisible(!d);
        menu.findItem(R.id.rename_category).setVisible(d && c == 1);
        menu.findItem(R.id.rename_image).setVisible(!d && c == 1);
        menu.findItem(R.id.delete_category).setVisible(d && c == 1);
        menu.findItem(R.id.delete_image).setVisible(!d && c == 1);
        menu.findItem(R.id.delete_categories).setVisible(d && c > 1);
        menu.findItem(R.id.delete_images).setVisible(!d && c > 1);
        menu.findItem(R.id.categories).setVisible(!d);

        menu.findItem(R.id.lock).setVisible(!i);
        menu.findItem(R.id.unlock).setVisible(i);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.add_category:
                break;
            case R.id.add_image:
                Intent intent = new Intent(IconDisplayActivity.this, AddPictureActivity.class);
                startActivity(intent);
                break;
            case R.id.rename_category:
                break;
            case R.id.rename_image:
                break;
            case R.id.delete_category:
                break;
            case R.id.delete_categories:
                break;
            case R.id.delete_image:
                break;
            case R.id.delete_images:
                break;
            case R.id.categories:
                finish();
                break;
            case R.id.unlock:
                setLocked(false);
                break;
            case R.id.lock:
                setLocked(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /// Get the list of IconViews that are selected
    public List<IconView> getSelected() {
        List<IconView> views = new ArrayList<>();
        if (!isLocked()) {
            for (int i = 0; i < gridView.getChildCount(); ++i) {
                IconView view = (IconView) gridView.getChildAt(i);
                if (view.isChecked()) {
                    views.add(view);
                }
            }
        }
        return views;
    }

    /// Get the number of selected IconViews
    public int getSelectedCount() {
        int count = 0;
        if (!isLocked()) {
            for (int i = 0; i < gridView.getChildCount(); ++i) {
                IconView view = (IconView) gridView.getChildAt(i);
                if (view.isChecked()) {
                    count += 1;
                }
            }
        }
        return count;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        // update gridview checkbox visibility
        for (int i=0 ; i<gridView.getChildCount() ; i++) {
            IconView view = (IconView)gridView.getChildAt(i);
            view.setCheckboxVisible(!locked);
            view.setChecked(false);
        }
        invalidateOptionsMenu();
    }

    public boolean isDisplayCategories() {
        return displayCategories;
    }
}
