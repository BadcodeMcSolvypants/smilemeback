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

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ImagesTest extends FakeContextTestCase {

    private Category category() throws IOException, StorageException, NameException {
        int categoryPosition = 525;
        Name categoryName = new Name("CATEGORY A");
        makeCategory(categoryPosition, categoryName, true);
        return initCategory(categoryPosition, categoryName);
    }

    @Test
    public void testInitialization() throws IOException, StorageException, NameException {
        // given
        Category category = category();
        Name nameA = new Name("first image");
        Name nameB = new Name("second image");
        makeImage(category, 0, nameA);
        makeImage(category, 1, nameB);

        // when
        Images images = new Images(category);

        // then
        assertThat(images.size(), is(2));
        assertThat(images.get(0).getName(), is(equalTo(nameA)) );
        assertThat(images.get(0).getPosition(), is(0));
        assertThat(images.get(1).getName(), is(equalTo(nameB)) );
        assertThat(images.get(1).getPosition(), is(1));

    }

    @Test
    public void testAdd() throws IOException, StorageException, NameException {
        // given
        Category category = category();
        Images images = new Images(category);
        assertThat(images.size(), is(0));

        // when
        Name name = new Name("my image");
        images.add(name, tempFileWithContents(), tempFileWithContents());

        // then
        assertThat(images.size(), is(1));
        assertThat(images.get(0).getName(), is(equalTo(name)));
        assertThat(images.get(0).getPosition(), is(0));
    }

    @Test
    public void testDelete() throws IOException, StorageException, NameException {
        // given
        Category category = category();
        Images images = new Images(category);
        Name nameA = new Name("my image");
        Name nameB = new Name("my image2");
        Name nameC = new Name("my image3");
        Name nameD = new Name("my image4");
        images.add(nameA, tempFileWithContents(), tempFileWithContents());
        images.add(nameB, tempFileWithContents(), tempFileWithContents());
        images.add(nameC, tempFileWithContents(), tempFileWithContents());
        images.add(nameD, tempFileWithContents(), tempFileWithContents());
        assertThat(images.size(), is(4));

        // when
        images.delete(Arrays.asList(images.get(1), images.get(3)));

        // then
        assertThat(images.size(), is(2));
        assertThat(images.get(0).getName(), is(equalTo(nameA)));
        assertThat(images.get(1).getName(), is(equalTo(nameC)));
    }


    @Test
    public void testOrganize() throws IOException, StorageException, NameException {
        // given
        Category category = category();
        Images images = new Images(category);
        Name nameA = new Name("my image");
        Name nameB = new Name("my image2");
        Name nameC = new Name("my image3");
        Name nameD = new Name("my image4");
        images.add(nameA, tempFileWithContents(), tempFileWithContents());
        images.add(nameB, tempFileWithContents(), tempFileWithContents());
        images.add(nameC, tempFileWithContents(), tempFileWithContents());
        images.add(nameD, tempFileWithContents(), tempFileWithContents());

        Image A = images.get(0);
        Image D = images.get(3);

        // change index if first image
        FileUtils.moveFile(A.getImage(),
                new File(
                        category.getFolder(),
                        StorageNameUtils.constructImageFileName(-5, nameA, Image.IMAGE_SUFFIX)
                )
        );
        FileUtils.moveFile(A.getAudio(),
                new File(
                        category.getFolder(),
                        StorageNameUtils.constructImageFileName(-5, nameA, Image.AUDIO_SUFFIX)
                )
        );

        // currupt last image, so it gets deleted
        FileUtils.moveFile(D.getAudio(), new File(category.getFolder(), "someaudio" + Image.AUDIO_SUFFIX));

        // when
        images.organize();

        // then
        assertThat(images.size(), is(3));
        assertThat(images.get(0).getName(), is(equalTo(nameA)));
        assertThat(images.get(1).getName(), is(equalTo(nameB)));
        assertThat(images.get(2).getName(), is(equalTo(nameC)));
    }
}
