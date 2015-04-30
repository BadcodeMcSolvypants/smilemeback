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
import android.support.annotation.LayoutRes;
import android.widget.Button;

import com.smilemeback.R;

/**
 * Single screen in {@link com.smilemeback.activities.addactivity.AddActivity} .
 * In a way similar to a {@link android.app.Fragment}, but better suited for
 * prev/next actions.
 */
public abstract class Screen {

    protected final AddActivity activity;
    private final @LayoutRes int layoutResId;
    private Button prevButton;
    private Button nextButton;

    /**
     * A {@link com.smilemeback.activities.addactivity.Screen} works very closely with
     * underlying {@link com.smilemeback.activities.addactivity.AddActivity} to provide
     * the necessary steps when entering information.
     *
     * Every screen manages basic user interface components defined by layout {@literal layoutResId}
     * using methos available in {@link com.smilemeback.activities.addactivity.AddActivity} .
     *
     * @param activity The underlying activity.
     * @param layoutResId The layout resource.
     */
    public Screen(final AddActivity activity, @LayoutRes int layoutResId) {
        this.activity = activity;
        this.layoutResId = layoutResId;
    }

    /**
     * Called when this screen is set the default one that should be displayed.
     */
    public void setDefaultScreen() {
        activity.setContentView(layoutResId);
    }

    /**
     * Called when the screen is started.
     * The method should display the contents of the screen.
     */
    public void onStart() {
        prevButton = (Button)activity.findViewById(R.id.prevButton);
        nextButton = (Button)activity.findViewById(R.id.nextButton);
    }

    /**
     * Method indicating if we can proceed to next step/screen.
     * This means that all necessary information by the {@link com.smilemeback.activities.addactivity.Screen}
     * is entered by the user.
     *
     * @return true if we can.
     */
    public abstract boolean canGoForward();

    /**
     * Method indicating if we can go back to previous step/screen.
     * This should be always true, except for the first screen.
     *
     * @return true, if we can go back to previous screen/step.
     */
    public abstract boolean canGoBack();

    /**
     * Method that tells the activity to go back to previous step.
     */
    public void goBack() {
        if (canGoBack()) {
            activity.goBack();
        }
    }

    /**
     * Method that tells the activity to go forward to next step/screen.
     */
    public void goForward() {
        if (canGoForward()) {
            activity.goForward();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
