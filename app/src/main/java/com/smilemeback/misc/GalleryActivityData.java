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
package com.smilemeback.misc;

import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Class that encapsulates the data of a {@link com.smilemeback.activities.GalleryActivity}.
 */
public class GalleryActivityData {
    public ListView listView;
    public LinearLayout listViewContainer; // this is required for animation
    public GridView gridView;
    public GalleryActivityState state = GalleryActivityState.VIEW;
}
