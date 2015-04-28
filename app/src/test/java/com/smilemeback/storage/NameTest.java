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
package com.smilemeback.storage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class NameTest {

    @Test
    public void testCorrectInitialization() {
        new Name("Smiling");
        new Name("Running very fast!");
        new Name("Images_right_here");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroLengthName() {
        new Name("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCharacters() {
        new Name("Good / bad");
    }
}
