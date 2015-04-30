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
package com.smilemeback.activities.screens;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;

import com.smilemeback.R;
import com.smilemeback.activities.AddBaseActivity;
import com.smilemeback.misc.Constants;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AddAudioScreen extends Screen {

    protected static final int RECORD_SOUND = 1;

    protected Button recordSound;
    protected boolean audioRecorded = false;

    private File temporaryAudio;

    public AddAudioScreen(AddBaseActivity activity, @LayoutRes int layoutResId) {
        super(activity, layoutResId);
    }

    public void onSetDefaultScreen(final Map<String, String> data) {
        super.onSetDefaultScreen(data);

        // get temporary audio path
        try {
            temporaryAudio = new Storage(activity).getTemporaryAudioFile();
        } catch (StorageException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        // UI
        recordSound = (Button)activity.findViewById(R.id.recordSound);
        recordSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchAudioRecordIntent();
            }
        });

        // data
        if (data.containsKey(Constants.ADDED_IMAGE_AUDIO_PATH)) {
            audioRecorded = true;
        }

        updateNavButtons();
    }

    @Override
    public boolean canGoForward() {
        return audioRecorded;
    }

    @Override
    public boolean canGoBack() {
        return true;
    }

    @Override
    public Map<String, String> collectData(Map<String, String> data) {
        if (audioRecorded) {
            data.put(Constants.ADDED_IMAGE_AUDIO_PATH, temporaryAudio.getAbsolutePath());
        }
        return data;
    }

    private void dispatchAudioRecordIntent() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Constants.MAX_RECORDING_DURATION);
        activity.startActivityForResult(intent, RECORD_SOUND);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_SOUND && resultCode == activity.RESULT_OK) {
            try {
                Uri recorderAudioUri = data.getData();
                FileUtils.copyFile(new File(getRealPathFromURI(recorderAudioUri)), temporaryAudio);
                // todo: make a good media player
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(temporaryAudio.getAbsolutePath());
                player.prepare();
                player.start();
                audioRecorded = true;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        updateNavButtons();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
