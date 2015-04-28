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

import com.smilemeback.storage.FakeContext;
import com.smilemeback.storage.Storage;

import org.junit.Before;

import java.io.ByteArrayInputStream;
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

    @Before
    public void setUp() throws IOException {
        context.deleteFiles();
    }
}
