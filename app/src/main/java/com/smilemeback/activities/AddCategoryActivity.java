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


import com.smilemeback.R;
import com.smilemeback.activities.addactivity.AddActivity;
import com.smilemeback.activities.addactivity.AddNameScreen;
import com.smilemeback.activities.addactivity.AddPictureScreen;
import com.smilemeback.activities.addactivity.Screen;

public class AddCategoryActivity extends AddActivity {

    public AddCategoryActivity() {
        super();
    }

    @Override
    protected Screen[] getScreens() {
        return new Screen[] {
                new AddPictureScreen(this, R.layout.create_picture_addpicture),
                new AddNameScreen(this, R.layout.create_picture_addname)
        };
    }

    @Override
    public void goBack() {

    }

    @Override
    public void goForward() {

    }
}
