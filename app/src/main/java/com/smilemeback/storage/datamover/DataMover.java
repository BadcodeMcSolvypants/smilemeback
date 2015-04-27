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
package com.smilemeback.storage.datamover;

import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class that deals with moving elements in {@link List}s of comparable elements.
 */
public class DataMover {

    protected final List<Comparable> collection;
    protected final List<Comparable> selection;
    protected final Comparable target;

    /**
     * Initialize {@literal IconSorter}.
     * @param collection All collection in the collection.
     * @param selection Selected collection in the collection.
     * @param target The target item of the collection.
     *
     * @throws IllegalArgumentException In case any element of {@literal selection} is not in {@literal collection}
     *         or when {@literal target} is inside {@literal selection} or outside {@literal collection}.
     */
    public DataMover(final List<Comparable> collection, final List<Comparable> selection, final Comparable target) throws IllegalArgumentException {
        this.collection = new ArrayList<>(collection);
        this.selection = new ArrayList<>(selection);
        this.target = target;
        makeAssertions();
    }

    private void makeAssertions() throws IllegalArgumentException {
        if (collection.size() == 0) {
            throw new IllegalArgumentException("Zero collection size!");
        }
        if (selection.size() == 0) {
            throw new IllegalArgumentException("Zero selection size!");
        }
        if (!collection.containsAll(selection)) {
            throw new IllegalArgumentException("Not all selection elements in collection");
        }
        if (!Ordering.natural().isStrictlyOrdered(selection)) {
            throw new IllegalArgumentException("Selected items not unique or sorted");
        }
        if (!Ordering.natural().isStrictlyOrdered(collection)) {
            throw new IllegalArgumentException("Collection not unique or sorted");
        }
        if (!collection.contains(target)) {
            throw new IllegalArgumentException("Target not in collection!");
        }
        if (selection.contains(target)) {
            throw new IllegalArgumentException("Target in selection");
        }
    }

    private boolean isTargetBeforeSelection() {
        for (Comparable element : selection) {
            if (target.compareTo(element) >= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The reordered copy of the resulting collection.
     */
    public List<Comparable> getResultCollection() {
        List<Comparable> resultCollection = new ArrayList<>(collection);
        resultCollection.removeAll(selection);
        int targetIndex = resultCollection.indexOf(target);
        if (isTargetBeforeSelection()) {
            resultCollection.addAll(targetIndex, selection);
        } else {
            resultCollection.addAll(targetIndex+1, selection);
        }
        assert(collection.size() == resultCollection.size());
        return resultCollection;
    }

    /**
     * @return The mapping of indicies of only comparables that were affected.
     */
    public Map<Integer, Integer> getSourceTargetMapping() {
        Map<Integer, Integer> mapping = new TreeMap<>();
        List<Comparable> result = getResultCollection();
        for (int idx=0 ; idx<result.size() ; ++idx) {
            Comparable obj = result.get(idx);
            int srcIdx = collection.indexOf(obj);
            if (srcIdx != idx) {
                mapping.put(srcIdx, idx);
            }
        }
        return mapping;
    }
}
