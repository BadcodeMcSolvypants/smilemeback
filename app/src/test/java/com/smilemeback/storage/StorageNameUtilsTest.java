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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class StorageNameUtilsTest {

    @Test
    public void testConstructImageFileName() throws NameException {
        assertThat(
                StorageNameUtils.constructImageFileName(0, new Name("image"), Image.IMAGE_SUFFIX),
                is(equalTo("0_image" + Image.IMAGE_SUFFIX))
        );
        assertThat(
                StorageNameUtils.constructImageFileName(0, new Name("image"), Image.AUDIO_SUFFIX),
                is(equalTo("0_image" + Image.AUDIO_SUFFIX))
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructImageFileName() throws NameException, IllegalArgumentException {
        StorageNameUtils.constructImageFileName(0, new Name("image"), ".wrongsuffix");
    }

    @Test
    public void testConstructCategoryName() throws NameException {
        assertThat(
                StorageNameUtils.constructCategoryFileName(0, new Name("CATEGORY A")),
                is(equalTo("0_CATEGORY A"))
        );
    }

    @Test
    public void testParseName() throws NameException {
        assertThat(
                StorageNameUtils.parseName("12354_file name.3gpp"),
                is(equalTo(new Name("file name")))
        );
        assertThat(
                StorageNameUtils.parseName("12354_file name"),
                is(equalTo(new Name("file name")))
        );
    }

    @Test
    public void testParsePosition() {
        assertThat(
                StorageNameUtils.parsePosition("15_name").get(),
                is(equalTo(15))
        );
        assertThat(
                StorageNameUtils.parsePosition("_name").isPresent(),
                is(false)
        );
    }

    @Test
    public void testParseSuffix() {
        assertThat(
                StorageNameUtils.parseSuffix("lalala.jpg").get(),
                is(equalTo(".jpg"))
        );
        assertThat(
                StorageNameUtils.parseSuffix("15_lalala.3gpp").get(),
                is(equalTo(".3gpp"))
        );
        assertThat(
                StorageNameUtils.parseSuffix("15_lalala").isPresent(),
                is(false)
        );
    }
}
