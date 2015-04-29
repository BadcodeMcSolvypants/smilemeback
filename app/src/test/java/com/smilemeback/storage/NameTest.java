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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class NameTest {

    @Test
    public void testCorrectInitialization() throws NameException {
        new Name("Smiling");
        new Name("Running very fast!");
    }

    @Test(expected = NameException.class)
    public void testZeroLengthName() throws NameException {
        new Name("");
    }

    @Test(expected = NameException.class)
    public void testInvalidCharacters() throws NameException {
        new Name("Good / bad ._");
    }

    @Test
    public void testNameEquals() throws NameException {
        Name a = new Name("Smiling");
        Name b = new Name("Smiling");
        Name c = new Name("Running");

        assertThat(a, is(equalTo(b)));
        assertThat(a, is(not(equalTo(c))));
    }
}
