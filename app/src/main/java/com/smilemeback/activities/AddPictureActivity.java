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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.smilemeback.R;
import com.smilemeback.storage.Storage;
import com.smilemeback.storage.StorageException;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AddPictureActivity extends Activity {
    private static Logger logger = Logger.getLogger(AddPictureActivity.class.getCanonicalName());

    protected static final int REQUEST_IMAGE_CAPTURE = 1;
    protected static final int PICK_PHOTO_GALLERY = 2;
    protected AddPictureActivityState state = AddPictureActivityState.ADD_PICTURE;

    protected Button fromCameraButton;
    protected Button fromDeviceButton;
    protected Button prevButton;
    protected Button nextButton;
    protected ImageView imageView;

    protected boolean imageAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enterAddPictureState();
    }

    protected void enterAddPictureState() {
        setContentView(R.layout.create_picture_addpicture);
        fromCameraButton = (Button)findViewById(R.id.fromCameraButton);
        fromDeviceButton = (Button)findViewById(R.id.fromDeviceButton);
        imageView = (ImageView)findViewById(R.id.imageView);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Storage storage = new Storage(this);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadImageFromFile(storage.getTemporaryImageFile());
            imageAdded = true;
        } else if (requestCode == PICK_PHOTO_GALLERY && data != null && data.getData() != null) {
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
        }
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
        Picasso.with(this)
                .load(file)
                .fit()
                .centerCrop()
                .into(imageView);
    }
}
