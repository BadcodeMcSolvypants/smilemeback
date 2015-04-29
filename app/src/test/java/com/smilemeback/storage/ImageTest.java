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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ImageTest extends FakeContextTestCase {

    private Name name() throws NameException {
        return new Name("Awesome picture");
    }

    private int position() {
        return 25;
    }

    private Image image() throws NameException, StorageException, IOException{
        int categoryPosition = 0;
        Name categoryName = new Name("CATEGORY A");
        makeCategory(categoryPosition, categoryName, true);
        Category category = initCategory(categoryPosition, categoryName);
        Name name = name();
        int position = position();
        makeImage(category, position, name);

        Image image = new Image(category,
                new File(category.getFolder(),
                        StorageNameUtils.constructImageFileName(position, name, Image.IMAGE_SUFFIX)),
                new File(category.getFolder(),
                        StorageNameUtils.constructImageFileName(position, name, Image.AUDIO_SUFFIX))
        );
        return image;
    }

    @Test
    public void testInitialization() throws NameException, StorageException, IOException {
        // given
        Image image = image();

        // then
        assertThat(image.getName(), is(equalTo(name())));
        assertThat(image.getPosition(), is(equalTo(position())));
    }

    @Test(expected = StorageException.class)
    public void testNoImage() throws NameException, StorageException, IOException {
        // given
        Image image = image();
        FileUtils.forceDelete(image.getImage());

        // when
        new Image(image.getCategory(), image.getImage(), image.getAudio());
        // then raises exception
    }

    @Test(expected = StorageException.class)
    public void testNoAudio() throws NameException, StorageException, IOException {
        // given
        Image image = image();
        FileUtils.forceDelete(image.getAudio());

        // when
        new Image(image.getCategory(), image.getImage(), image.getAudio());
        // then raises exception
    }

    @Test
    public void testRename() throws NameException, StorageException, IOException {
        // given
        Image image = image();
        Name newName = new Name("More awesome picture");
        assertThat(image.getName(), is(not(equalTo(newName))));

        // when
        Image newImage = image.rename(newName);

        // then
        assertThat(
                newImage.getName(),
                is(equalTo(newName))
        );
    }

    @Test
    public void testDelete()  throws NameException, StorageException, IOException {
        // given
        Image image = image();

        // when
        image.delete();

        // then
        assertThat(
                image.getAudio().exists(),
                is(false)
        );
        assertThat(
                image.getAudio().exists(),
                is(false)
        );
    }
}
