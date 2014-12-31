package com.smilemeback;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.smilemeback.storage.Category;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilemeback.views.IconView;

import java.util.ArrayList;
import java.util.List;


public class IconDisplayActivity extends Activity {

    /// should we enable adding new categories / images in the view
    protected boolean locked = true;

    /// are we displaying all categories or contents of a single category
    protected boolean displayCategories = true;

    /// grid of IconView instances
    protected GridView gridView;

    /// List of categories
    protected List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icondisplay);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        try {
            Storage storage = new Storage(this);
            //storage.initializeTestingCategories();
            categories = storage.getCategories();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
        gridView = (GridView)findViewById(R.id.iconGridView);
        gridView.setAdapter(getAdapter());
    }

    public BaseAdapter getAdapter() {
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
                    Category category = categories.get(position);
                    final IconView view = new IconView(IconDisplayActivity.this, IconDisplayActivity.this.getResources().getXml(R.layout.iconview_layout), false);
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
                                // else do whatever, when we are unlocked
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_icon, container, false);
            return rootView;
        }
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
