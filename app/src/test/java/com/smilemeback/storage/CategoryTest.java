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

import com.smilemeback.storage.datamover.FakeContextTestCase;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class CategoryTest extends FakeContextTestCase {

    protected void makeCategory(int pos, Name name, boolean makeThumb) throws IOException, StorageException {
        File folder = storage.getCategoriesFolder();
        File category = new File(folder, StorageNameUtils.constructCategoryFileName(pos, name));
        FileUtils.forceMkdir(category);
        if (makeThumb) {
            File thumbnail = new File(category, Category.THUMBNAIL);
            FileUtils.copyInputStreamToFile(inputStream(), thumbnail);
        }
    }

    protected Category initialize(int pos, Name name) throws StorageException {
        Category category = new Category(
                new File(
                        storage.getCategoriesFolder(),
                        StorageNameUtils.constructCategoryFileName(pos, name)
                )
        );
        return category;
    }

    @Test
    public void testInitialization() throws IOException, StorageException, NameException {
        Name name = new Name("A");
        int position = 2563453;
        makeCategory(position, name, true);

        // given
        Category category = initialize(position, name);

        // then
        assertThat(category.getName(), is(equalTo(name)));
        assertThat(category.getPosition(), is(equalTo(position)));
    }

    @Test(expected = StorageException.class)
    public void testNoThumbnail() throws IOException, StorageException, NameException {
        Name name = new Name("A");
        int position = 2563453;
        makeCategory(position, name, false);

        // given
        initialize(position, name);

    }

    @Test(expected = StorageException.class)
    public void testMissingIndex() throws IOException, StorageException, NameException {
        Name name = new Name("A");
        int position = 0;
        makeCategory(position, name, false);

        // given
        Category category = initialize(position, name);
        File folder = new File(category.getFolder().getParent(), "_wrong");
        FileUtils.moveDirectory(category.getFolder(), folder);

        // when
        new Category(folder);

        // then exception thrown
    }

    @Test(expected = StorageException.class)
    public void testMissingName() throws IOException, StorageException, NameException {
        Name name = new Name("A");
        int position = 0;
        makeCategory(position, name, false);

        // given
        Category category = initialize(position, name);
        File folder = new File(category.getFolder().getParent(), "0_");
        FileUtils.moveDirectory(category.getFolder(), folder);

        // when
        new Category(folder);

        // then exception thrown
    }

    @Test
    public void testRename() throws IOException, StorageException, NameException {
        int position = 12;
        Name name = new Name("Funny cat pictures");
        makeCategory(position, name, true);

        // given
        Category category = initialize(position, name);

        // when
        Name newName = new Name("Ugly dog photos");
        category = category.rename(newName);

        // then
        assertThat(category.getName(), is(equalTo(newName)));
        assertThat(category.getPosition(), is(equalTo(position)));
    }

    @Test
    public void testDelete() throws IOException, StorageException, NameException {
        int position = 0;
        Name name = new Name("A");
        makeCategory(position, name, true);

        // given
        Category category = initialize(position, name);

        // when
        category.delete();

        // then
        assertThat(category.getFolder().exists(), is(false));
    }
}
