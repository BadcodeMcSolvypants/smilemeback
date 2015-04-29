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
package com.smilemeback.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.smilemeback.R;


public class IconViewSide extends IconView {

    public IconViewSide(Context context, AttributeSet attrs, boolean useDefaultImage) {
        super(context, attrs, useDefaultImage);
    }

    @Override
    protected void inflateLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.icon_view_side, this, true);
    }
}
