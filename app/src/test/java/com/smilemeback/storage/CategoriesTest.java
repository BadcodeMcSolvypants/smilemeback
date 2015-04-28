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
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class CategoriesTest extends FakeContextTestCase {

    @Test
    public void testAdd() throws StorageException, NameException {
        Categories categories = new Categories(storage.getCategoriesFolder());

        // given
        assertThat(categories.size(), is(0));

        // when
        Category A = categories.add(new Name("A"), inputStream());
        Category B = categories.add(new Name("B"), inputStream());

        // then
        assertThat(categories.size(), is(2));
        assertThat(categories.get(0), is(equalTo(A)));
        assertThat(categories.get(1), is(equalTo(B)));
    }

    @Test
    public void testTruncate() throws StorageException, NameException {
        Categories categories = new Categories(storage.getCategoriesFolder());
        categories.add(new Name("A"), inputStream());
        categories.add(new Name("B"), inputStream());

        // given
        assertThat(categories.size(), is(2));

        // when
        categories.truncate();

        // then
        assertThat(categories.size(), is(0));
    }

    @Test
    public void testDelete() throws StorageException, NameException {
        Categories categories = new Categories(storage.getCategoriesFolder());
        Category A = categories.add(new Name("A"), inputStream());
        Category B = categories.add(new Name("B"), inputStream());
        Category C = categories.add(new Name("C"), inputStream());
        Category D = categories.add(new Name("D"), inputStream());

        // given
        assertThat(categories.size(), is(4));

        // when
        categories.delete(Arrays.asList(A, C));

        // then
        assertThat(categories.size(), is(2));
        assertThat(categories.get(0), is(equalTo(B)));
        assertThat(categories.get(0), is(equalTo(D)));
    }

    @Test
    public void testOrganize() throws IOException, StorageException, NameException {
        Categories categories = new Categories(storage.getCategoriesFolder());
        Category A = categories.add(new Name("A"), inputStream());
        Category B = categories.add(new Name("B"), inputStream());
        Category C = categories.add(new Name("C"), inputStream());

        // given
        FileUtils.moveDirectory(A.getFolder(), new File(categories.getParent(), "-1_A"));
        FileUtils.moveDirectory(B.getFolder(), new File(categories.getParent(), "156_B"));
        FileUtils.moveDirectory(C.getFolder(), new File(categories.getParent(), "2345234735452345_C"));

        // when
        categories.organize();

        // then
        assertThat(categories.size(), is(equalTo(3)));
        assertThat(categories.get(0), is(equalTo(A)));
        assertThat(categories.get(1), is(equalTo(B)));
        assertThat(categories.get(2), is(equalTo(C)));
    }
}
