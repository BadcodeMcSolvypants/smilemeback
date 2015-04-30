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


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Base activity for adding images and categories.
 */
public abstract class AddActivity extends Activity {
    private static String TAG = AddActivity.class.getCanonicalName();

    protected Screen[] screens;
    protected int currentScreenIndex;

    public AddActivity() {
        currentScreenIndex = 0;
        this.screens = getScreens();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.screens[currentScreenIndex].setDefaultScreen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.screens[currentScreenIndex].onStart();
    }

    /**
     * Get the {@link com.smilemeback.activities.addactivity.Screen} instances used by
     * this activity
     * @return The list of screens used in the activity.
     */
    protected abstract Screen[] getScreens();

    public abstract void goBack();

    public abstract void goForward();

    protected void showExceptionAlertAndFinish(Exception e) {
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

    protected void loadImageFromFile(File file, ImageView view) {
        // now load the real image
        Picasso.with(this)
                .load(file)
                .fit()
                .skipMemoryCache()
                .centerCrop()
                .into(view);
    }
}
