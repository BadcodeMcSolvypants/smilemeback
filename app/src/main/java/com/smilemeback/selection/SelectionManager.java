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
package com.smilemeback.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that manager GalleryActivity gridView selections.
 */
public class SelectionManager {

    protected Set<Integer> selectedPositions = new HashSet<>();
    protected int numTotal = 0;
    protected List<SelectionListener> listeners = new ArrayList<>();

    /**
     * Add a selection listener that will be notified of selection events.
     * @param listener
     */
    public void addListener(SelectionListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a selection listener.
     * @param listener
     */
    public void removeListener(SelectionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Remove all selection listeners.
     */
    public void removeListeners() {
        listeners.clear();
    }

    /**
     * Select all positions from 0 up to {@literal numTotal}.
     */
    public void selectAll() {
        boolean change = false;
        for (int i=0 ; i<numTotal ; ++i) {
            if (!selectedPositions.contains(i)) {
                selectedPositions.add(i);
                notifyPositionSelected(i);
                change = true;
            }
        }
        if (change) {
            notifyNumSelectedChanged(selectedPositions.size());
        }
    }

    /**
     * Reset the selection positions.
     */
    public void deselectAll() {
        for (int pos : selectedPositions) {
            notifyPositionDeselected(pos);
        }
        selectedPositions.clear();
        notifyNumSelectedChanged(0);
    }

    /**
     * Set the number of elements that could be selection.
     * If later any position that is out of range 0<= pos < {@literal numTotal} is trying to get
     * selection, an error will be thrown.
     * @param numTotal
     */
    public void setNumTotal(int numTotal) {
        if (numTotal < 0) {
            throw new RuntimeException("Number of elements cannot be less than 0, but is " + numTotal);
        }
        for (int pos : selectedPositions) {
            if (pos >= numTotal) {
                throw new RuntimeException("Programming error. Cannot set num elements " + numTotal + " while selection positions contain " + pos);
            }
        }
        boolean change = this.numTotal != numTotal;
        this.numTotal = numTotal;
        if (change) {
            notifyNumTotalChanged(numTotal);
        }
    }

    /**
     * Get the number of total positions.
     * @return
     */
    public int getNumTotal() {
        return numTotal;
    }

    /**
     * Return the number of selection positions.
     * @return
     */
    public int getNumSelected() {
        return selectedPositions.size();
    }

    /**
     * Select a position between 0 <= {@literal position} < {@literal numTotal} .
     * @param position
     */
    public void select(int position) {
        if (position < 0 || position >= numTotal) {
            throw new RuntimeException("Selected positions must be between 0 and " + numTotal);
        }
        selectedPositions.add(position);
        notifyPositionSelected(position);
        notifyNumSelectedChanged(selectedPositions.size());
    }

    /**
     * Deselect a previously selection position. It will not throw an error, if the element
     * was not previously selection.
     *
     * @param position
     */
    public void deselect(int position) {
        boolean contains = selectedPositions.contains(position);
        selectedPositions.remove(position);
        if (contains) {
            notifyPositionDeselected(position);
            notifyNumSelectedChanged(selectedPositions.size());
        }
    }

    /**
     * Get the set of all selection positions.
     */
    public Set<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    /**
     * Get a single selection position.
     * Throws {@link RuntimeException} in case there are more.
     * @return
     */
    public int getSelectedPosition() {
        if (getNumSelected() != 1) {
            throw new RuntimeException("Exactly one item has to be selection. There are " + getNumSelected() + " currently selection!");
        }
        return (int)selectedPositions.toArray()[0];
    }

    /**
     * Is the position selection.
     * @param position
     * @return
     */
    public boolean isSelected(int position) {
        return selectedPositions.contains(position);
    }

    /**
     * Toggle a position's selection.
     * @param position
     */
    public void toggle(int position) {
        if (selectedPositions.contains(position)) {
            deselect(position);
        } else {
            select(position);
        }
    }

    /**
     * Notify all listeners to highlight selection positions.
     */
    public void highlight() {
        for (SelectionListener listener : listeners) {
            for (int selected : selectedPositions) {
                listener.highlight(selected);
            }
        }
    }

    /**
     * Notify all listeners to dehighlight selection positions.
     */
    public void dehighlight() {
        for (SelectionListener listener : listeners) {
            for (int selected : selectedPositions) {
                listener.dehighlight(selected);
            }
        }
    }

    /**
     * Notify listeners about the position that got selection.
     * @param pos
     */
    protected void notifyPositionSelected(int pos) {
        for (SelectionListener listener : listeners) {
            listener.positionSelected(pos);
        }
    }

    /**
     * Notify listeners about the position that got deselected.
     * @param pos
     */
    protected void notifyPositionDeselected(int pos) {
        for (SelectionListener listener : listeners) {
            listener.positionDeselected(pos);
        }
    }

    /**
     * Notify listeners about total number of elements that can be selection.
     * @param numTotal
     */
    protected void notifyNumTotalChanged(int numTotal) {
        for (SelectionListener listener : listeners) {
            listener.numTotalChanged(numTotal);
        }
    }

    /**
     * Notify the listeners when number of selection elements change.
     * @param numSelected
     */
    protected void notifyNumSelectedChanged(int numSelected) {
        for (SelectionListener listener : listeners) {
            listener.numSelectedChanged(numSelected);
        }
    }
}
