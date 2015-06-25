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

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smilemeback.R;
import com.smilemeback.activities.AddBaseActivity;
import com.smilemeback.misc.Constants;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.smilemeback.views.IconView;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AddAudioScreen extends Screen {
    private static String TAG = AddAudioScreen.class.getCanonicalName();

    //private static final int RECORD_SOUND = 1;

    private Button recordSound;
    private boolean audioRecorded = false;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private File temporaryAudio;
    private IconView iconView = null;
    private ImageView indicator = null;
    private ImageView statusIcon = null;

    public AddAudioScreen(AddBaseActivity activity, int layoutResId) {
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
        recordSound.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        indicator.setVisibility(View.VISIBLE);
                        statusIcon.setVisibility(View.GONE);
                        onRecord(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        onRecord(false);
                        indicator.setVisibility(View.GONE);
                        statusIcon.setImageResource(R.drawable.record_recorded);
                        statusIcon.setVisibility(View.VISIBLE);
                        audioRecorded = true;
                        updateNavButtons();
                        break;
                }
                return false;
            }
        });
        iconView = (IconView)activity.findViewById(R.id.addsound_iconview);
        iconView.setOverlayVisibility(View.GONE);
        iconView.setCheckboxVisible(false);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioRecorded) {
                    onPlay(true);
                }
            }
        });
        indicator = (ImageView)activity.findViewById(R.id.mediaplayer_recording_indicator);
        indicator.setVisibility(View.GONE);
        statusIcon = (ImageView)activity.findViewById(R.id.mediaplayer_status);

        // data
        if (data.containsKey(Constants.ADDED_IMAGE_AUDIO_PATH)) {
            audioRecorded = true;
            statusIcon.setImageResource(R.drawable.record_recorded);
        }
        if (data.containsKey(Constants.ADDED_IMAGE_PATH)) {
            iconView.setImageBitmap(new File(data.get(Constants.ADDED_IMAGE_PATH)));
        }
        if (data.containsKey(Constants.ADDED_IMAGE_NAME)) {
            iconView.setLabel(data.get(Constants.ADDED_IMAGE_NAME));
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

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(temporaryAudio.getAbsolutePath());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioChannels(Constants.AUDIO_NUM_CHANNELS);
        mRecorder.setAudioSamplingRate(Constants.AUDIO_SAMPLING_RATE);
        mRecorder.setAudioEncodingBitRate(Constants.AUDIO_BITRATE);
        mRecorder.setOutputFile(temporaryAudio.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}
