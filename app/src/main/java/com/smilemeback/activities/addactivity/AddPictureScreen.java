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
package com.smilemeback.activities.addactivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.smilemeback.R;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class AddPictureScreen extends Screen {
    private static String TAG = AddPictureScreen.class.getCanonicalName();

    protected static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static final int PICK_PHOTO_GALLERY = 2;

    protected Button fromCameraButton;
    protected Button fromDeviceButton;
    protected ImageView imageView;
    protected boolean imageSelected = false;

    public AddPictureScreen(AddActivity activity, @LayoutRes int layoutResId) {
        super(activity, layoutResId);
    }

    @Override
    public void onStart() {
        super.onStart();
        fromCameraButton = (Button)activity.findViewById(R.id.fromCameraButton);
        fromDeviceButton = (Button)activity.findViewById(R.id.fromDeviceButton);
        imageView = (ImageView)activity.findViewById(R.id.imageView);

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
    }

    @Override
    public boolean canGoForward() {
        return imageSelected;
    }

    @Override
    public boolean canGoBack() {
        return false; // it is the first screen, we cannot go back
    }

    /**
     * Start the image capture intent.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            try {
                Storage storage = new Storage(activity);
                File imageFile = storage.getTemporaryImageFile();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (StorageException e) {
                activity.showExceptionAlertAndFinish(e);
            }
        }
    }

    private void dispatchPickPhotoIntent() {
        Intent intent = new Intent();
        intent.setType("image/jpg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(
                Intent.createChooser(
                        intent,
                        activity.getString(R.string.addpicture_photo_from_galery)),
                PICK_PHOTO_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Storage storage = new Storage(activity);
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == activity.RESULT_OK) {
                Log.d(TAG, "Adding image from camera");
                loadTemporaryImageIntoView();
                imageSelected = true;
            } else if (requestCode == PICK_PHOTO_GALLERY && data != null && data.getData() != null) {
                Log.d(TAG, "Adding image from gallery");
                Uri uri = data.getData();
                Cursor cursor = activity.getContentResolver().query(
                        uri,
                        new String[] {android.provider.MediaStore.Images.ImageColumns.DATA},
                        null, null, null);
                cursor.moveToFirst();
                final String imagePath = cursor.getString(0);
                Log.d(TAG, "Gallery image is located at <" + imagePath + ">");
                FileUtils.copyFile(
                        new File(imagePath),
                        storage.getTemporaryImageFile());
                loadTemporaryImageIntoView();
                imageSelected = true;
            }
        } catch (StorageException | IOException e) {
            activity.showExceptionAlertAndFinish(e);
        }
    }

    private void loadTemporaryImageIntoView() throws StorageException {
        Storage storage = new Storage(activity);
        File tempFile = storage.getTemporaryImageFile();
        Log.d(TAG, "Temporary image is located at <" + tempFile.getAbsolutePath() + ">");
        activity.loadImageFromFile(tempFile, imageView);
    }
}