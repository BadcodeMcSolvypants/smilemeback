package com.smilemeback.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smilemeback.storage.Category;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilememack.R;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    protected Context context;
    protected List<Category> categories;

    public CategoryAdapter(Context context) throws StorageException {
        this.context = context;
        Storage storage = new Storage(context);
        categories = storage.getCategories();
    }

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
            IconView view = new IconView(context, context.getResources().getXml(R.layout.iconview_layout));
            view.setImageBitmap(category.getThumbnail());
            view.setLabel(category.getName().toString());
            return view;
        } else {
            return convertView;
        }
    }
}
