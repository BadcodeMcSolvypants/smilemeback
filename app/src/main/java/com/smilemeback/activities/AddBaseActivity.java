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


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.smilemeback.R;
import com.smilemeback.activities.screens.Screen;
import com.smilemeback.misc.Dialogs;
import com.squareup.picasso.Picasso;

import org.javatuples.Triplet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Base activity for adding images and categories.
 */
public abstract class AddBaseActivity extends HomeEnabledActivity {
    private static String TAG = AddBaseActivity.class.getCanonicalName();

    protected Screen[] screens;
    protected int currentScreenIndex;
    protected Map<String, String> data;

    private final Map<Triplet<Integer, Integer, Integer>, Integer> progressMapping;

    public AddBaseActivity() {
        currentScreenIndex = 0;
        data = new HashMap<>();
        this.screens = getScreens();
        progressMapping = getProgressMapping();
        setResult(RESULT_CANCELED);
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
            setResultAndFinish();
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
     * Method for updating the progressbar, that shows the state of the images.
     */
    public void updateProgressBar() {
        // determine the current progress
        int max = screens.length;
        int current = currentScreenIndex;
        int completed = 0;
        for (completed=0 ; completed < screens.length ; ++completed) {
            if (!screens[completed].canGoForward()) {
                break;
            }
        }
        // set the appropriate image
        ImageView view = (ImageView)findViewById(R.id.progressImageView);
        view.setImageResource(progressMapping.get(triplet(max, completed, current)));
    }

    /**
     * Helper method for displaying an picture.
     * @param file The file containing the image.
     * @param view The view where the image will be loaded.
     */
    public void loadImageFromFile(File file, ImageView view) {
        // now load the real image
        Picasso.with(this).invalidate(file);
        Picasso.with(this)
                .load(file)
                .fit()
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
    public void setResultAndFinish() {
        Intent result = new Intent();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result.putExtra(entry.getKey(), entry.getValue());
        }
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onBackPressed() {
        // if user has inputted data, display a confirmation dialog
        if (data.size() > 0) {
            Dialogs.confirmation(this,
                    getString(R.string.addpicture_cancel_title),
                    getString(R.string.discard),
                    getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddBaseActivity.super.onBackPressed();
                        }
                    });
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Put together a (max, completed, current) mapping to progress bar resource images.
     * @return
     */
    private static Map<Triplet<Integer, Integer, Integer>, Integer> getProgressMapping() {
        Map<Triplet<Integer, Integer, Integer>, Integer> m = new HashMap<>();
        // for adding image
        m.put(triplet(3, 0, 0), R.drawable.progress0step1);
        m.put(triplet(3, 1, 0), R.drawable.progress1step1);
        m.put(triplet(3, 1, 1), R.drawable.progress1step2);
        m.put(triplet(3, 2, 0), R.drawable.progress2step1);
        m.put(triplet(3, 2, 1), R.drawable.progress2step2);
        m.put(triplet(3, 2, 2), R.drawable.progress2step3);
        m.put(triplet(3, 3, 0), R.drawable.progress3step1);
        m.put(triplet(3, 3, 1), R.drawable.progress3step2);
        m.put(triplet(3, 3, 2), R.drawable.progress3step3);
        // for adding category
        m.put(triplet(2, 0, 0), R.drawable.twostep_progress0step1);
        m.put(triplet(2, 1, 0), R.drawable.twostep_progress1step1);
        m.put(triplet(2, 1, 1), R.drawable.twostep_progress1step2);
        m.put(triplet(2, 2, 0), R.drawable.twostep_progress2step1);
        m.put(triplet(2, 2, 1), R.drawable.twostep_progress2step2);
        return m;
    }

    private static Triplet<Integer, Integer, Integer> triplet(int a, int b, int c) {
        return new Triplet<>(a, b, c);
    }
}
