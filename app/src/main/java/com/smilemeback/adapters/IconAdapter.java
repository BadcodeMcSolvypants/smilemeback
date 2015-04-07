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
package com.smilemeback.adapters;

/**
 * Manages images/icons in a gallery.
 */
public class IconAdapter {
    /*protected GalleryActivity activity;

    public IconAdapter(GalleryActivity activity) {
        this.activity = activity;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        IconView view;
        if (convertView != null) {
            view = (IconView)convertView;
        } else {
            view = new IconView(activity, activity.getResources().getLayout(R.layout.icon_view), false);
        }
        view.setOverlayVisibility(View.GONE);
        view.setOnDragListener(dragListener);

        final Image image = images.get(position);
        view.setImageBitmap(image.getImage());
        view.setLabel(image.getName().toString());

        view.setCheckboxVisible(state == GalleryActivityState.SELECT);
        view.setChecked(checkedImages.contains(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IconView iconView = (IconView)view;
                switch (state) {
                    case VIEW:
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
                        break;
                    case SELECT:
                        iconView.toggle();
                        if (iconView.isChecked()) {
                            checkedImages.add(position);
                        } else {
                            checkedImages.remove(position);
                        }
                        selectionMode.setNumSelected(getNumSelectedInGridView());
                        break;
                    default:
                        break;
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                IconView iconView = (IconView)view;
                switch (state) {
                    case VIEW:
                        setAllGridViewItemsChecked(false);
                        iconView.setChecked(true);
                        checkedImages.add(getIconViewPositionInGridView(iconView));
                        gotoSelectionModeState();
                        break;
                    case SELECT:
                        if (!iconView.isChecked()) {
                            iconView.setChecked(true);
                            checkedImages.add(position);
                        }
                        setSelectedIconViewsAlpha(Constants.SELECTED_ICONVIEW_ALPHA);
                        ClipData.Item item = new ClipData.Item(Constants.IMAGE_DRAG_TAG);
                        ClipData dragData = new ClipData(Constants.IMAGE_DRAG_TAG, new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                        View.DragShadowBuilder shadow = new ImageDragShadowBuilder(iconView);
                        iconView.setTag(Constants.IMAGE_DRAG_TAG);
                        iconView.startDrag(dragData, shadow, null, 0);
                        vibrate();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        return view;
    }*/
}
