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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class DataMoverTest {

    public static List<Integer> collection() {
        return list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    public static List<Integer> list(Integer... elements) {
        return Arrays.asList(elements);
    }

    public static Map<Integer, Integer> map(List<Integer> original, List<Integer> result) {
        assertThat(original.size(), is(equalTo(result.size())));
        Map<Integer, Integer> map = new TreeMap<>();
        for (int idx=0 ; idx<original.size() ; ++idx) {
            int o = original.get(idx);
            int dest = result.indexOf(o);
            if (idx != dest) {
                map.put(idx, dest);
            }
        }
        return map;
    }

    @Test
    public void testSuccessfulInitialization() {
        new DataMover<>(
                collection(),
                list(0, 1),
                2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedInitializationAsSelectedItemNotInCollection() {
        new DataMover<>(
                collection(),
                list(1, 2, 20),
                9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedInitializationAsTargetNotInCollection() {
        new DataMover<>(
                collection(),
                list(1, 2),
                -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedInitializationAsTargetAlsoSelected() {
        new DataMover<>(
                collection(),
                list(1, 2),
                2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedInitializationAsSeveralItemsNotUnique() {
        new DataMover<>(
                list(0, 1, 1),
                list(0),
                1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedInitializationAsSelectedItemsNotUnique() {
        new DataMover<>(
                list(0, 1, 2),
                list(0, 0),
                1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedInitializationAsCollectionNotSorted() {
        new DataMover<>(
                list(1, 0),
                list(0),
                1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailedInitializationAsSelectionNotSorted() {
        new DataMover<>(
                collection(),
                list(5, 6, 4),
                1);
    }

    /**
     * Test that a single icon that is before that target icon,
     * gets moved after the target icon.
     * The selection icon and target icons are consequent.
     */
    @Test
    public void testSingleSelectedMovedAfterTargetIfTargetConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),           // all items
                list(4),                // selection
                5                       // drop target
        );
        List<Integer> result = list(0,1,2,3,5,4,6,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that a single icon that is before that target icon,
     * gets moved after the target icon.
     * The selection icon and target icon are not consequent and there are icons between them.
     */
    @Test
    public void testSingleSelectedMovedAfterTargetIfTargetNotConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(4),
                6
        );
        List<Integer> result = list(0,1,2,3,5,6,4,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that a single icon that is after the target icon,
     * gets moved before the target icon.
     * The target and selection icon are consequent.
     */
    @Test
    public void testSingleSelectedMovedBeforeTargetIfTargetNotConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(6),
                4
        );
        List<Integer> result = list(0,1,2,3,6,4,5,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that a single icon that is after the target icon,
     * gets moved before the target icon.
     * The target and selection icon are not consequent.
     */
    @Test
    public void testSingleSelectedMovedBeforeTargetIfTargetConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(5),
                4
        );
        List<Integer> result = list(0,1,2,3,5,4,6,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple consequent selection icons are moved after target that
     * is not consequent.
     */
    @Test
    public void testMultipleConsequentMovedAfterTargetIfTargetNotConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(1,2),
                5
        );
        List<Integer> result = list(0,3,4,5,1,2,6,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple consequent selection icons are moved after target that
     * is consequent.
     */
    @Test
    public void testMultipleConsequentMovedAfterTargetIfTargetConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(1,2),
                3
        );
        List<Integer> result = list(0,3,1,2,4,5,6,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple consequent selection icons are moved before target that
     * is consequent to the range.
     */
    @Test
    public void testMultipleConsequentMovedBeforeTargetIfTargetConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(2,3),
                1
        );
        List<Integer> result = list(0,2,3,1,4,5,6,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple consequent selection icons are moved before target that
     * is not consequent to the range.
     */
    @Test
    public void testMultipleConsequentMovedBeforeTargetIfTargetNotConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(5,6),
                0
        );
        List<Integer> result = list(5,6,0,1,2,3,4,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple scattered selection icons are moved after that target
     * that is consequent to the last selection icon.
     */
    @Test
    public void testMultipleScatteredMovedAfterTargetIfTargetConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(1,3,5),
                6
        );
        List<Integer> result = list(0,2,4,6,1,3,5,7,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple scattered selection icons are moved after that target
     * that is not consequent to the last selection icon.
     */
    @Test
    public void testMultipleScatteredMovedAfterTargetIfTargetNotConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(1,3,5),
                7
        );
        List<Integer> result = list(0,2,4,6,7,1,3,5,8,9);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple scattered selection icons are moved before the target
     * that is consequent to the first selection icon.
     */
    @Test
    public void testMultipleScatteredMovedBeforeTargetIfTargetConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(7,9),
                6
        );
        List<Integer> result = list(0,1,2,3,4,5,7,9,6,8);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple scattered selection icons are moved before the target
     * that is not consequent to the first selection icon.
     */
    @Test
    public void testMultipleScatteredMovedBeforeTargetIfTargetNotConsequent() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(7,9),
                1
        );
        List<Integer> result = list(0,7,9,1,2,3,4,5,6,8);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }

    /**
     * Test that multiple scattered icons are moved after the target that is
     * inside the range of selection icons, but is not itself selection.
     */
    @Test
    public void testMultipleScatteredMovedAfterTargetThatIsInsideDirtyRange() {
        List<Integer> original = collection();
        DataMover dm = new DataMover<>(
                collection(),
                list(1,3,5,7,9),
                4
        );
        List<Integer> result = list(0,2,4,1,3,5,7,9,6,8);
        // assert
        List<Integer> actualCollection = dm.getResultCollection();
        Map<Integer, Integer> actualMapping = dm.getSourceTargetMapping();
        assertThat(actualCollection, is(equalTo(result)));
        assertThat(actualMapping, is(equalTo(map(original, result))));
    }
}
