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
package com.smilemeback.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.smilemeback.activities.screens.Screen;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Base activity for adding images and categories.
 */
public abstract class AddBaseActivity extends Activity {
    private static String TAG = AddBaseActivity.class.getCanonicalName();

    protected Screen[] screens;
    protected int currentScreenIndex;
    protected Map<String, String> data;

    public AddBaseActivity() {
        currentScreenIndex = 0;
        data = new HashMap<>();
        this.screens = getScreens();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.screens[currentScreenIndex].onSetDefaultScreen(data);
    }

    /**
     * Go back to the previous screen.
     */
    public void goBack() {
        screens[currentScreenIndex].collectData(data);
        if (currentScreenIndex > 0) {
            currentScreenIndex--;
            screens[currentScreenIndex].onSetDefaultScreen(data);
        }
    }

    /**
     * Go to the next screen.
     */
    public void goForward() {
        screens[currentScreenIndex].collectData(data);
        if (currentScreenIndex == screens.length-1) { // is it the last screen?
            finish();
        } else {
            currentScreenIndex++;
            screens[currentScreenIndex].onSetDefaultScreen(data);
        }
    }

    public void showExceptionAlertAndFinish(Exception e) {
        Log.e(TAG, e.getMessage());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        screens[currentScreenIndex].onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Helper method for displaying an picture.
     * @param file The file containing the image.
     * @param view The view where the image will be loaded.
     */
    public void loadImageFromFile(File file, ImageView view) {
        // now load the real image
        Picasso.with(this)
                .load(file)
                .fit()
                .skipMemoryCache()
                .centerCrop()
                .into(view);
    }

    /**
     * Get the {@link com.smilemeback.activities.screens.Screen} instances used by
     * this activity
     * @return The list of screens used in the activity.
     */
    protected abstract Screen[] getScreens();

    /**
     * When finishing the activity, store all the collected user data
     * in the result.
     */
    @Override
    public void finish() {
        Intent result = new Intent();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result.putExtra(entry.getKey(), entry.getValue());
        }
        setResult(RESULT_OK, result);
        super.finish();
    }
}
