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
package com.smilemeback.drag;


import com.smilemeback.views.IconView;

/**
 * Interface that responds to drags that ended on grid elements.
 */
public interface GridDragResultListener {

    /**
     * Move all selection grid elements to given position as a drag result
     * on a grid element identified by {@literal position}.
     * @param position
     */
    void moveSelectedIconsTo(int position);
}
