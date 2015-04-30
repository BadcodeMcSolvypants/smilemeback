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

import android.support.annotation.LayoutRes;

public class AddAudioScreen extends Screen {

    public AddAudioScreen(AddActivity activity, @LayoutRes int layoutResId) {
        super(activity, layoutResId);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean canGoForward() {
        return false;
    }

    @Override
    public boolean canGoBack() {
        return true;
    }
}
