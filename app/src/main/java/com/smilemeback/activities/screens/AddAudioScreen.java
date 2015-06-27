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

import com.smilemeback.R;
import com.smilemeback.activities.AddBaseActivity;
import com.smilemeback.misc.Constants;
import com.smilemeback.misc.Toasts;
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
    private ImageView not_recorded = null;
    private ImageView recording = null;
    private ImageView recorded = null;

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
                        not_recorded.setVisibility(View.GONE);
                        recorded.setVisibility(View.GONE);
                        recording.setVisibility(View.VISIBLE);
                        startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        boolean success = stopRecording();
                        if (success) {
                            audioRecorded = true;
                        }
                        recording.setVisibility(View.GONE);
                        if (audioRecorded) {
                            recorded.setVisibility(View.VISIBLE);
                            not_recorded.setVisibility(View.GONE);
                        } else {
                            recorded.setVisibility(View.GONE);
                            not_recorded.setVisibility(View.VISIBLE);
                        }
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
                    Toasts.checkAndNotifyUserIfVolumeMute(activity);
                }
            }
        });
        not_recorded = (ImageView)activity.findViewById(R.id.mediaplayer_not_recorded);
        recording = (ImageView)activity.findViewById(R.id.mediaplayer_recording);
        recording.setVisibility(View.GONE);
        recorded = (ImageView)activity.findViewById(R.id.mediaplayer_recorded);

        // data
        if (data.containsKey(Constants.ADDED_IMAGE_AUDIO_PATH)) {
            audioRecorded = true;
            not_recorded.setVisibility(View.GONE);
            recorded.setVisibility(View.VISIBLE);
        } else {
            recorded.setVisibility(View.GONE);
            not_recorded.setVisibility(View.VISIBLE);
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

    /**
     * Stop the recording
     * @return true if the recording was successful.
     */
    private boolean stopRecording() {
        boolean result = true;
        try {
            mRecorder.stop();
        } catch (RuntimeException e) {
            result = false;
        }
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
        return result;
    }
}
