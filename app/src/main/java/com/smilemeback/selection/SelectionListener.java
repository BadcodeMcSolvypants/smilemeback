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
package com.smilemeback.selection;

/**
 * Interface for {@link com.smilemeback.selection.SelectionManager} events.
 */
public interface SelectionListener {
    /**
     * Called when a position gets selection.
     * @param position
     */
    void positionSelected(int position);

    /**
     * Called when a position gets deselected.
     * @param position
     */
    void positionDeselected(int position);

    /**
     * Called when total number of elements changed.
     * @param total
     */
    void numTotalChanged(int total);

    /**
     * Called when total number of selection positions changed.
     * @param selected
     */
    void numSelectedChanged(int selected);

    /**
     * Request to highlight element at specified element.
     * It is ensured that the position is selection.
     * @param position
     */
    void highlight(int position);

    /**
     * Request to dehighlight element at specified element.
     * It is ensured that the position is selection.
     * @param position
     */
    void dehighlight(int position);
}
