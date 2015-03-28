/**
 * This file is part of SmileMeBack.

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
package com.smilemeback.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;

import com.smilemeback.Constants;
import com.smilemeback.R;
import com.smilemeback.storage.Storage;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AddPictureActivity extends Activity {
    private static Logger logger = Logger.getLogger(AddPictureActivity.class.getCanonicalName());

    protected static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static final int PICK_PHOTO_GALLERY = 2;
    protected static final int RECORD_SOUND = 3;
    protected AddPictureActivityState state = AddPictureActivityState.ADD_PICTURE;

    // add picture
    protected Button fromCameraButton;
    protected Button fromDeviceButton;
    protected Button prevButton;
    protected Button nextButton;
    protected ImageView imageView;
    protected ImageView progressImageView;

    protected boolean imageAdded = false;

    // add name
    protected EditText imageEditText;
    protected String imageName = "";

    // add sound
    protected Button recordSound;
    protected boolean audioRecorded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterAddPictureState();
    }

    protected void enterAddPictureState() {
        state = AddPictureActivityState.ADD_PICTURE;

        setContentView(R.layout.create_picture_addpicture);
        fromCameraButton = (Button)findViewById(R.id.fromCameraButton);
        fromDeviceButton = (Button)findViewById(R.id.fromDeviceButton);
        imageView = (ImageView)findViewById(R.id.imageView);
        progressImageView = (ImageView)findViewById(R.id.progressImageView);
        prevButton = (Button)findViewById(R.id.prevButton);
        nextButton = (Button)findViewById(R.id.nextButton);

        fromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        fromDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPickPhotoIntent();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == AddPictureActivityState.ADD_PICTURE && imageAdded) {
                    enterAddNameState();
                }
            }
        });

        if (imageAdded) {
            Storage storage = new Storage(this);
            loadImageFromFile(storage.getTemporaryImageFile());
        }

        updatePrevNextButtons();
    }

    protected void enterAddNameState() {
        state = AddPictureActivityState.ADD_NAME;
        setContentView(R.layout.create_picture_addname);

        imageEditText = (EditText)findViewById(R.id.imageName);
        imageEditText.setText(imageName);
        progressImageView = (ImageView)findViewById(R.id.progressImageView);
        prevButton = (Button)findViewById(R.id.prevButton);
        nextButton = (Button)findViewById(R.id.nextButton);

        imageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                imageName = s.toString();
                updatePrevNextButtons();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == AddPictureActivityState.ADD_NAME) {
                    enterAddPictureState();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == AddPictureActivityState.ADD_NAME && imageName.length() > 0) {
                    enterRecordSoundState();
                }
            }
        });

        updatePrevNextButtons();
    }

    protected void enterRecordSoundState() {
        state = AddPictureActivityState.RECORD_SOUND;
        setContentView(R.layout.create_picture_addsound);

        progressImageView = (ImageView)findViewById(R.id.progressImageView);
        prevButton = (Button)findViewById(R.id.prevButton);
        nextButton = (Button)findViewById(R.id.nextButton);
        recordSound = (Button)findViewById(R.id.recordSound);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (state == AddPictureActivityState.RECORD_SOUND) {
                enterAddNameState();
            }
            }
        });

        final Storage storage = new Storage(this);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(Constants.ADDED_IMAGE_NAME, imageName);
                result.putExtra(Constants.ADDED_IMAGE_PATH, storage.getTemporaryImageFile().getAbsolutePath());
                result.putExtra(Constants.ADDED_IMAGE_AUDIO_PATH, storage.getTemporaryAudioFile().getAbsolutePath());
                setResult(RESULT_OK, result);
                finish();
            }
        });

        recordSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchAudioRecordIntent();
            }
        });

        updatePrevNextButtons();
    }

    protected void updatePrevNextButtons() {
        switch (state) {
            case ADD_PICTURE:
                prevButton.setEnabled(false);
                nextButton.setEnabled(imageAdded);
                break;
            case ADD_NAME:
                prevButton.setEnabled(true);
                if (imageName.length() > 0) {
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }
                break;
            case RECORD_SOUND:
                prevButton.setEnabled(true);
                if (audioRecorded) {
                    nextButton.setEnabled(true);
                } else {
                    nextButton.setEnabled(false);
                }
                break;
        }
        updateProgressView();
    }

    protected void updateProgressView() {
        if (imageAdded && imageName.length() > 0 && audioRecorded) {
            if (state == AddPictureActivityState.ADD_PICTURE) {
                progressImageView.setImageResource(R.drawable.progress3step1);
            } else if (state == AddPictureActivityState.ADD_NAME) {
                progressImageView.setImageResource(R.drawable.progress3step2);
            } else {
                progressImageView.setImageResource(R.drawable.progress3step3);
            }
        } else if (imageAdded && imageName.length() > 0) {
            if (state == AddPictureActivityState.ADD_PICTURE) {
                progressImageView.setImageResource(R.drawable.progress2step1);
            } else if (state == AddPictureActivityState.ADD_NAME) {
                progressImageView.setImageResource(R.drawable.progress2step2);
            } else {
                progressImageView.setImageResource(R.drawable.progress2step3);
            }
        } else if (imageAdded) {
            if (state == AddPictureActivityState.ADD_PICTURE) {
                progressImageView.setImageResource(R.drawable.progress1step1);
            } else {
                progressImageView.setImageResource(R.drawable.progress1step2);
            }
        } else {
            progressImageView.setImageResource(R.drawable.progress0step1);
        }
    }

    /**
     * Start the image capture intent.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Storage storage = new Storage(this);
            File imageFile = storage.getTemporaryImageFile();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchPickPhotoIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.addpicture_photo_from_galery)), PICK_PHOTO_GALLERY);
    }

    private void dispatchAudioRecordIntent() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, RECORD_SOUND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Storage storage = new Storage(this);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            logger.info("Adding image from camera");
            loadImageFromFile(storage.getTemporaryImageFile());
            imageAdded = true;
        } else if (requestCode == PICK_PHOTO_GALLERY && data != null && data.getData() != null) {
            logger.info("Adding image from gallery");
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            final String imagePath = cursor.getString(0);
            try {
                FileUtils.copyFile(new File(imagePath), storage.getTemporaryImageFile());
                loadImageFromFile(storage.getTemporaryImageFile());
                imageAdded = true;
            } catch (IOException e) {
                showExceptionAlertAndFinish(e);
            }
        } else if (requestCode == RECORD_SOUND && resultCode == RESULT_OK) {
            logger.info("Adding audio recording");
            Uri recorderAudioUri = data.getData();
            File tempAudioPath = storage.getTemporaryAudioFile();
            try {
                FileUtils.copyFile(new File(getRealPathFromURI(recorderAudioUri)), tempAudioPath);
                // todo: make a good media player
                MediaPlayer player = new MediaPlayer();
                player.setDataSource(tempAudioPath.getAbsolutePath());
                player.prepare();
                player.start();
                audioRecorded = true;
            } catch (IOException e) {
                showExceptionAlertAndFinish(e);
            }

        }
        updatePrevNextButtons();
    }
    protected void showExceptionAlertAndFinish(Exception e) {
        logger.log(Level.SEVERE, e.getMessage());
        new AlertDialog.Builder(this)
                .setTitle("Exception")
                .setMessage(e.getMessage())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    protected void loadImageFromFile(File file) {
        // now load the real image
        Picasso.with(this)
                .load(file)
                .fit()
                .skipMemoryCache()
                .centerCrop()
                .into(imageView);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        logger.info("absolutepath audiopath in getRealPathFromURI : " + cursor.getString(column_index));
        return cursor.getString(column_index);
    }
}
