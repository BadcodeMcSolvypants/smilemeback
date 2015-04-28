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

import android.test.mock.MockContext;
import android.util.Log;

import com.google.common.io.Files;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Fake {@link android.content.Context} for storage testing.
 */
public class FakeContext extends MockContext {
    private static final String TAG = FakeContext.class.getCanonicalName();

    private final File tempDir = Files.createTempDir();

    public FakeContext() {
        tempDir.deleteOnExit();
        Log.d(TAG, "Temporary directory is <" + tempDir.getAbsolutePath() + ">");
    }

    @Override
    public File getExternalFilesDir(String type) {
        return tempDir;
    }

    public void deleteFiles() throws IOException {
        for (File file : tempDir.listFiles()) {
            FileUtils.forceDelete(file);
        }
    }
}
