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

import android.test.AndroidTestCase;

import com.smilemeback.storage.datamover.FakeContextTestCase;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class CategoryTest extends FakeContextTestCase {

    protected void makeCategory(int pos, Name name) throws IOException, StorageException {
        File folder = storage.getCategoriesFolder();
        File category = new File(folder, StorageNameUtils.constructCategoryFileName(pos, name));
        FileUtils.forceMkdir(category);
        File thumbnail = new File(category, Category.THUMBNAIL);
        FileUtils.copyInputStreamToFile(inputStream(), thumbnail);
    }

    @Test
    public void testInitialization() throws IOException, StorageException, NameException {
        Name name = new Name("A");
        int position = 2563453;
        makeCategory(position, name);

        // given
        Category category = new Category(new File(storage.getCategoriesFolder(), "0_A"));

        // then
        assertThat(category.getName(), is(equalTo(name)));
        assertThat(category.getPosition(), is(equalTo(position)));
    }

    @Test(expected = StorageException.class)
    public void testNoThumbnail() {

    }

    @Test(expected = StorageException.class)
    public void testMissingIndex() {

    }

    @Test(expected = StorageException.class)
    public void testMissingName() {

    }
}
