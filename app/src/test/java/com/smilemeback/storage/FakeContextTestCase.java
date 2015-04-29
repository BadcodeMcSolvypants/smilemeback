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
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Testcase that provides fake {@link android.content.Context} .
 */
public class FakeContextTestCase {

    protected final FakeContext context;
    protected final Storage storage;

    public FakeContextTestCase() {
        super();
        context = new FakeContext();
        storage = new Storage(context);
    }

    protected static InputStream inputStream() {
        StringBuilder sb = new StringBuilder();
        for (int i=0 ; i<1024*64 ; ++i) {
            sb.append('a');
        }
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    protected void makeCategory(int pos, Name name, boolean makeThumb) throws IOException, StorageException {
        File folder = storage.getCategoriesFolder();
        File category = new File(folder, StorageNameUtils.constructCategoryFileName(pos, name));
        FileUtils.forceMkdir(category);
        if (makeThumb) {
            File thumbnail = new File(category, Category.THUMBNAIL);
            FileUtils.copyInputStreamToFile(inputStream(), thumbnail);
        }
    }

    protected Category initCategory(int pos, Name name) throws StorageException {
        Category category = new Category(
                new File(
                        storage.getCategoriesFolder(),
                        StorageNameUtils.constructCategoryFileName(pos, name)
                )
        );
        return category;
    }

    protected void makeImage(Category category, int pos, Name name) throws IOException, StorageException {
        File imageFile = new File(
                category.getFolder(),
                StorageNameUtils.constructImageFileName(pos, name, Image.IMAGE_SUFFIX));
        File audioFile = new File(
                category.getFolder(),
                StorageNameUtils.constructImageFileName(pos, name, Image.AUDIO_SUFFIX));
        FileUtils.copyInputStreamToFile(inputStream(), imageFile);
        FileUtils.copyInputStreamToFile(inputStream(), audioFile);
    }

    @Before
    public void setUp() throws IOException {
        context.deleteFiles();
    }
}
